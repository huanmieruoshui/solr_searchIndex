package com.bmtech.utils.log;

public class BmtLogHelper {
	final String logName;
	public BmtLogHelper(String logName){
		this.logName = logName;
	}
	public void log(String why, Object...args){
		info(why, args);
	}
	/**
	 * add info level logInfo using LogLevel
	 * @param why
	 * @param level
	 */
	public void log(LogLevel level, String why, Object...args) {
		BmtLogger.instance().log(level,  (Throwable)null, why, args);
	}
	public void log(Throwable e, String why, Object...args){
		BmtLogger.instance().log(LogLevel.Error, e, why, args);
	}

	/**
	 * add a logInfo at level<code>lev</code>
	 * @param who
	 * @param why
	 * @param lev
	 */
	public void info(String why, Object...args){
		BmtLogger.instance().logWithName(logName, LogLevel.Info, why, args);
	}
	public void debug(String why, Object...args){
		BmtLogger.instance().logWithName(logName, LogLevel.Debug, why, args);
	}
	public void warn(String why, Object...args){
		BmtLogger.instance().logWithName(logName, LogLevel.Warning, why, args);
	}
	public void error(String why, Object...args){
		BmtLogger.instance().logWithName(logName, LogLevel.Error, why, args);
	}

	public void error(Throwable e, String why, Object...args){
		BmtLogger.instance().logWithName(logName, LogLevel.Error, e, why, args);
	}
}
