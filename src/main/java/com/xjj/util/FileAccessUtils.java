package com.xjj.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
			result = result.substring(0,result.indexOf("#"));
		}
		
		return result.trim();
	}
	
	/**
	 * 读取 INI 文件，注释以‘#’或‘;’开头，
	 *
	 * 支持行连接符号'\'（行末）；
	 * 支持global properties；
	 * 支持list格式，支持；
	 * 支持空行；
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
	 * [list section] # 我是只有value的section，判断第一个是否包含'='
	 * value1
	 * value2
	 * 
	 * @param fileName
	 * @return Map<sectionName, (name,value) Map或value List>
	 */
	public static Map<String, Object> readIniFile(String fileName){
		Map<String, Object> result = new HashMap<>();
		
		String globalPropertiesKey = "global"; //Map中存储的global properties的key 
		
		File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempStr = null;
            // 一次读入一行（非空），直到读入null为文件结束
            while ((tempStr = reader.readLine()) != null) {
            	tempStr = removeIniComments(tempStr); //去掉尾部的注释、去掉首尾空格
            	
            	if(tempStr.equals("")){
            		continue;
            	}
            	
            	//处理缺省的section
            	
            	
            	//TODO
            	//处理重复的name，转为List
            	//处理重复的section
            	//处理行连接符'\'
            	//处理等号前后的空格
            	
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
	
	public static void main(String[] args) {
		ArrayList<String> readByLines = readByLines("D:/httphosts.txt");
		
		System.out.println(readByLines);
	}
}
