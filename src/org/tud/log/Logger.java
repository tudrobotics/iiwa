package org.tud.log;

public class Logger {

	
	private static LogLevel level = LogLevel.INFO;
	
	public enum LogLevel {
		NONE, TRACE, INFO, WARNING , ERROR
	}
	public static void setLevel(LogLevel level) {
		Logger.level = level;
	}
	public static LogLevel getLevel() {
		return level;
	}
	public static void trace(String message) {
		if(level.compareTo(LogLevel.TRACE) <= 0) System.out.println("[TRACE] "+message);
	}
	public static void info(String message) {
		if(level.compareTo(LogLevel.INFO) <= 0 ) System.out.println("[INFO] "+message);
	}
	public static void warn(String message) {
		if(level.compareTo(LogLevel.WARNING) <= 0 ) System.out.println("[WARN] "+message);
	}
	public static void error(String message) {
		if(level.compareTo(LogLevel.ERROR) <= 0) System.out.println("[ERROR] "+message);
	}
}
