package com.bmtech.utils.log;


/**
 * LogLevel. Denotes the level of the loginfo
 * @author beiming
 *
 */

public class LogLevel{
	private  static final String FINE="FINE";
	private static final String DEBUG="DEBUG";
	private static final String INFO= "INFO";
	private static final String WARN= "WARN";
	private static final String ERROR="ERROR";
	private static final String URGEN="URGEN";
	private static final String UNDEF="UNDEF";
	final int lev;
	private LogLevel(int lev)	{
		this.lev = lev;
	}
	public boolean badThan(LogLevel lev){
		return this.lev > lev.lev;
	}
	/**
	 * test if this level if worse or equals lev
	 * @param lev
	 * @return
	 */
	public boolean accept(LogInfo info){
		return this.lev <= info.lev.lev;
	}
	public boolean equals(Object o) {
		if(o instanceof LogLevel) {
			return this.lev == ((LogLevel)o).lev;
		}
		return false;
	}
	public String logString()	{
		switch (lev)
		{
		case 1:
			return FINE;
		case 2:
			return DEBUG;
		case 4:
			return INFO;
		case 8:
			return WARN;
		case 16:
			return ERROR;
		case 32:
			return URGEN;
		}
		return UNDEF;
	}
	public String toString(){
		return logString();
	}
	public static final int FineValue = 1;
	public static final int DebugValue = 2;
	public static final int InfoValue = 4;
	public static final int WarnValue = 8;
	public static final int ErrorValue = 16;
	public static final int UrgenValue = 32;
	public static final  LogLevel Fine = new LogLevel(FineValue);
	public static final  LogLevel Debug = new LogLevel(DebugValue);
	public static final  LogLevel Info = new LogLevel(InfoValue);
	public static final  LogLevel Warning = new LogLevel(WarnValue);
	public static final  LogLevel Error = new LogLevel(ErrorValue);
	public static final  LogLevel Urgen = new LogLevel(UrgenValue);
	public static LogLevel getLogLevel(int lev) {
		if(lev == FineValue) {
			return Fine;
		}else if(lev == DebugValue){
			return Debug;
		}else if(lev == InfoValue){
			return Info;
		}else if(lev == WarnValue){
			return Warning;
		}else if(lev == ErrorValue){
			return Error;
		}else if(lev == UrgenValue){
			return Urgen;
		}else {
			return null;
		}
	}
}