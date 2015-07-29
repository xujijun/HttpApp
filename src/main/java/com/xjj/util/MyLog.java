package com.xjj.util;

public class MyLog {
	private static String getTimeString() {
		return DateUtils.getCurrentDateString(DateUtils.DFHHmmssSSS);
	}
	
	public static void info(String format, Object... args) {
		System.out.println(getTimeString() + " [INFO] " + String.format(format, args));
	}
	
	public static void warn(String format, Object... args) {
		System.out.println(getTimeString() + " [WARN] " + String.format(format, args));
	}
	
	public static void error(String format, Object... args) {
		System.out.println(getTimeString() + " [ERROR] " + String.format(format, args));
	}
}
