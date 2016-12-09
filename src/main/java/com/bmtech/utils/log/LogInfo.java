package com.bmtech.utils.log;

import java.sql.Date;
import java.text.SimpleDateFormat;
/**
 * the LogInfo holding the info of the log
 * @author liying1
 *
 */
public class LogInfo{
	public final Object  why;
	public final  Throwable e;
	public final long when;
	public final LogLevel lev;
	private static final SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
	private String logName;
	LogInfo(Object why){
		this(why, LogLevel.Info);
	}
	LogInfo(Object why, LogLevel lev){
		this(why, lev, null);
	}
	LogInfo(Object why, LogLevel lev, Throwable e){
		this.why = why;
		this.lev = lev;
		this.when = System.currentTimeMillis();
		this.e = e;
//		sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
	}
	public LogLevel getLevel(){
		return lev;
	}
	public String toString(){
		StringBuilder sb=new StringBuilder();
		sb.append(sdf.format(new Date(this.when)));
		sb.append(' ');
		sb.append(lev.toString());
		sb.append(' ');
		if(logName != null) {
			sb.append('[');
			sb.append(logName);
			sb.append(']');
			sb.append(' ');
		}
		sb.append('-');
		sb.append(' ');
		sb.append(why);
		return sb.toString();
	}
	void setLogName(String logName) {
		this.logName = logName;
	}
	public String getLogName() {
		return logName;
	}

}