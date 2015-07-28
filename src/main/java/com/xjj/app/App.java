package com.xjj.app;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xjj.http.HttpHelper;
import com.xjj.http.HttpResult;
import com.xjj.util.DateUtils;
import com.xjj.util.FileAccessUtils;
import com.xjj.util.RandomUtils;
import com.xjj.util.RegexUtils;


public class App {
	private static String getTimeString() {
		return DateUtils.getCurrentDateString(DateUtils.DFHHmmssSSS);
	}
	
	private static void logMsg(String format, Object... args){
		System.out.println(getTimeString() + " [myLog] " + String.format(format, args));
	}
	
	private static String[] userAgents = {"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.124 Safari/537.36",
			"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36 SE 2.X MetaSr 1.0",
			"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36"};
	
	public static void main(String[] args) {
		int howLongMinutes = 35;	//Minutes
		int maxIntervalMinutes = 1; //Minutes
		String hostFileName = "D:" + File.separator + "httphosts.txt";
		
		if(args.length > 0){ //First Param: How long 
			howLongMinutes = Integer.parseInt(args[0]);
		}

		if(args.length > 1){ //Second Param: Interval
			maxIntervalMinutes = Integer.parseInt(args[1]);
		}

		if(args.length > 2){ //Third Param: File Path and Name
			hostFileName = args[2];
		}
		
		Map<String, String> headers = HttpHelper.defaultHeaders;
		
		List<String> hosts = FileAccessUtils.readByLines(hostFileName);
		Map<String, Integer> urlHitCount = new HashMap<>(hosts.size());
		for(String host : hosts){
			urlHitCount.put(host, 0);
		}
		
		logMsg("Program started, will last for %s minutes, with maximum interval %s minutes", howLongMinutes, maxIntervalMinutes);
		
		long endTime = System.currentTimeMillis() + howLongMinutes*60*1000;
		long maxInterval = maxIntervalMinutes*60*1000;
		int succCount = 0;
		int failCount = 0;
		int bingoCount = 0;
		
		while ( System.currentTimeMillis() <= endTime) {
			String url = RandomUtils.getRandomElement(hosts);
			
			String userAgent = RandomUtils.getRandomElement(userAgents);
			headers.put("User-Agent",userAgent);
			HttpResult result = HttpHelper.doGet(url, headers);
			if(result.getCode()==200){
				String websiteHitCount = RegexUtils.getFirstMatch(result.getMsg(), "\\d+人阅读");
				websiteHitCount = RegexUtils.findFirstNumber(websiteHitCount);
				
				if(websiteHitCount.endsWith("99")){
					websiteHitCount += " ~ Bingo";
					bingoCount ++;
				}

				succCount ++;
				logMsg("%s request succeeded, read count: %s. No.%s", url, websiteHitCount, succCount);
				urlHitCount.put(url, urlHitCount.get(url)+1);
				
				int hitCount = Integer.parseInt(websiteHitCount);
				if(hitCount >= 999){
					hosts.remove(url);
					logMsg("WARNING: hitCount greater than 999! url=%s", url);
				}
			}else {
				failCount ++;
				logMsg("%s %s", url, result.toString());
			}
			
			long interval = Math.abs(RandomUtils.getRandomLong(maxInterval));
			
			if(System.currentTimeMillis()+interval > endTime){
				break;
			}
			
			long currentTimeInSecond = System.currentTimeMillis()/1000;
			logMsg("Time to the end: %s'%s\". Next request in %s seconds...", (endTime/1000-currentTimeInSecond)/60, (endTime/1000-currentTimeInSecond)%60, interval/1000);
			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		for(Map.Entry<String, Integer> entry : urlHitCount.entrySet()){
			System.out.println(entry.getKey() + " : " + entry.getValue());
		}
		logMsg("Completed. Succeeded: %s, Failed: %s, Bingo: %s", succCount, failCount, bingoCount);
	}	
}
