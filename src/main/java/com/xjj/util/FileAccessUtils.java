package com.xjj.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 读取文件
 * @author XuJijun
 *
 */
public class FileAccessUtils {

	/**
	 * 按行 读取文件
	 * @param fileName
	 * @return
	 */
	public static ArrayList<String> readByLines(String fileName) {
		ArrayList<String> result = new ArrayList<>();
		
		File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
        	
            String tempString = null;
            // 一次读入一行（非空），直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
            	tempString = tempString.trim();
            	if(!tempString.equals("")){
            		result.add(tempString);
            	}
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
		
		return result;
	}
	
	/**
	 * 去除ini文件中的注释，以";"或"#"开头
	 * @param source
	 * @return
	 */
	private static String removeIniComments(String source){
		String result = source;
		
		if(result.contains(";")){
			result = result.substring(0, result.indexOf(";"));
		}
		
		if(result.contains("#")){
			result = result.substring(0, result.indexOf("#"));
		}
		
		//去除UTF-8的BOM！！！用notepad编辑的话，在文件的第一个字符就是这个！！！
		if(result.startsWith("\uFEFF")){
			result = result.substring(1);
		}
		
		return result.trim();
	}
	
	/**
	 * 读取 INI 文件，注释以‘#’或‘;’开头，
	 *
	 * 支持行连接符号'\'（行末）；
	 * 支持global property；
	 * 支持list格式，支持；
	 * 支持空行、name/value前后的空格；
	 * 如果有重名，去最后一个；
	 * 
	 * 格式（例子）如下
	 * 
	 * # 我是注释
	 * 
	 * name0=value0  # 我是global properties
	 * name10=value10
	 * 
	 * [normal section] # 我是普通的section
	 * name1=value1 # 我是name和value
	 * 
	 * [list section] # 我是只有value的section，以第一个是否包含'='为标准
	 * value1
	 * value2
	 * 
	 * @param fileName
	 * @return Map<sectionName, object> object是一个Map（存放name=value对）或List（存放只有value的properties）
	 */
	public static Map<String, Object> readIniFile(String fileName){
		Map<String, List<String>> listResult = new HashMap<>();
		Map<String, Object> result = new HashMap<>();
		
		String globalSection = "global"; //Map中存储的global properties的key
		
		File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
        	//reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"windows-1256"));
            String str = null;
            String currentSection = globalSection; //处理缺省的section
            List<String> currentProperties = new ArrayList<>();
            boolean lineContinued = false;
            String tempStr = null;
            
            //一次读入一行（非空），直到读入null为文件结束
            //先全部放到listResult<String, List>中
            while ((str = reader.readLine()) != null) {
            	str = removeIniComments(str).trim(); //去掉尾部的注释、去掉首尾空格
            	
            	if("".equals(str)||str==null){
            		continue;
            	}

            	//如果前一行包括了连接符'\'
            	if(lineContinued == true){
            		str = tempStr + str;
            	}
            	
            	//处理行连接符'\'
            	if(str.endsWith("\\")){
            		lineContinued = true;
            		tempStr = str.substring(0,str.length()-1);
            		continue;
            	}else {
            		lineContinued = false;
				}
            	
            	//是否一个新section开始了
            	if(str.startsWith("[") && str.endsWith("]")){
            		String newSection = str.substring(1, str.length()-1).trim();

            		//如果新section不是现在的section，则把当前section存进listResult中
            		if(!currentSection.equals(newSection)){
            			listResult.put(currentSection, currentProperties);
            			currentSection = newSection;
            			
            			//新section是否重复的section
            			//如果是，则使用原来的list来存放properties
            			//如果不是，则new一个List来存放properties
            			currentProperties=listResult.get(currentSection);
            			if(currentProperties==null){
            				currentProperties = new ArrayList<>();
            			}
            		}
            	}else{
            		currentProperties.add(str);
            	}
            }
            
            //把最后一个section存进listResult中
            listResult.put(currentSection, currentProperties);
            
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        
        //从listResult<String, List>中，看各个list中的元素是否包含等号“=”，如果包含，则拆开并放到Map中
        //整理后，把结果放进result<String, Object>中
        for(String key : listResult.keySet()){
        	List<String> tempList = listResult.get(key);
        	
        	//空section不放到结果里面
        	if(tempList==null||tempList.size()==0){
        		continue;
        	}
        	
        	if(tempList.get(0).contains("=")){ //name=value对，存放在MAP里面
        		Map<String, String> properties = new HashMap<>();
        		for(String s : tempList){
        			int delimiterPos = s.indexOf("=");
        			//处理等号前后的空格
        			properties.put(s.substring(0,delimiterPos).trim(), s.substring(delimiterPos+1, s.length()).trim());
        		}
        		result.put(key, properties);
        	}else{ //只有value，则获取原来的list
        		result.put(key, listResult.get(key));
        	}
        }
        
		return result;
	}
	
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		//ArrayList<String> readByLines = readByLines("D:/httphosts.txt");
		//System.out.println(readByLines);
		
		Map<String, Object> ini = readIniFile("D:/test.ini");
		for(String k : ini.keySet()){
			System.out.println(k + ini.get(k));
		}
		
		System.out.println(((Map<String, String>)ini.get("myInfo")).get("myName"));
		
	}
}
