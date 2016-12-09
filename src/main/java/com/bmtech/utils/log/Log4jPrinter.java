package com.bmtech.utils.log;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Log4jPrinter implements LogPrinter{
	public final Logger logger;
	public Log4jPrinter(String name){
		logger = Logger.getLogger(name);
//		AsyncAppender appender = new AsyncAppender();
//		appender.setBlocking(false);
//		logger.addAppender(appender);
	}

	@Override
	public void print(LogInfo info) {
		Logger logger;
		if(info.getLogName() != null) {
			logger = Logger.getLogger(info.getLogName());
		}else {
			logger = this.logger;
		}
		Level lev;
		switch(info.lev.lev){
		case LogLevel.FineValue:
		case LogLevel.DebugValue:
			lev = Level.DEBUG;
			break;
		case LogLevel.WarnValue:
			lev = Level.WARN;
			break;
		case LogLevel.ErrorValue:
			lev = Level.ERROR;
			break;
		case LogLevel.UrgenValue:
			lev = Level.FATAL;
			break;
		default:
			lev = Level.INFO;
		}
		if(info.e != null){
			logger.log(lev, info.why + ", Exception is : " + info.e.toString());
		}else{
			logger.log(lev, info.why);
		}
	}
}
