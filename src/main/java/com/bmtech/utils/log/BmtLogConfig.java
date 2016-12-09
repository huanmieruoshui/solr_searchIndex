package com.bmtech.utils.log;

import java.io.File;

import com.bmtech.utils.io.TchFileTool;

public class BmtLogConfig {
	public static LogLevel logLevel(File path) {
		return LogLevel.getLogLevel(TchFileTool.getInt(path, "level", LogLevel.Debug.lev));
	}
	public static LogLevel logLevel() {
		return logLevel(new File("./config/bmtLog/"));
	}
	
	public static boolean useWatchLogLevelChangeThread(File path) {
		return TchFileTool.getInt(path, "useWatchLogLevelChangeThread", 0) == 1;
	}
	public static boolean useWatchLogLevelChangeThread() {
		return useWatchLogLevelChangeThread(new File("./config/bmtLog/"));
	}
	public static void main(String[]a){
		System.out.println(useWatchLogLevelChangeThread());
	}
}
