package com.bmtech.utils.log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * this is the main class to report log infos
 * @author beiming
 *
 */
public class BmtLogger{
	int maxWaitTime = 20;
	int splitSize = 16;
	private boolean printException = false;
	class ShutDownHook extends Thread{
		public void run(){
			for(int x = 0; x < maxWaitTime; x++){
				int nowsize = queue.size();
				if(nowsize > 0){
					try {
						sleep(splitSize);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}else{
					break;
				}
			}
		}
	}
	class LogFlusher extends Thread{
		LogFlusher(){
			this.setDaemon(true);
			Runtime.getRuntime().addShutdownHook(new ShutDownHook());
		}
		public void run() {
			setLogLevel(BmtLogConfig.logLevel());
			if(BmtLogConfig.useWatchLogLevelChangeThread()){
				new Thread(){
					public void run(){
						while(true){
							try {
								sleep(10000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							setLogLevel(BmtLogConfig.logLevel());
						}
					}
				}.start();
			}
			while(true) {
				try {
					LogInfo li;
					li = queue.take();//.poll(300, TimeUnit.MILLISECONDS);
					if(li != null) {
						LogPrinter myPrint = lp;
						if(null == myPrint){
							myPrint = StandardLogPrinter.instance();
						}
						if(printException){
							if(li.e != null){
								try{
									li.e.printStackTrace();
								}catch(Exception e){
									e.printStackTrace();
								}
							}
						}
						myPrint.print(li);

					}
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	private int level = LogLevel.Debug.lev;
	private static BmtLogger instance = new BmtLogger();
	private LogPrinter lp;
	final BlockingQueue<LogInfo> queue;
	private final Object lock = new Object();
	private BmtLogger() {
		String str = System.getProperty("bmt_log_queue_size");
		int queueSize = 1024;
		if(str != null) {
			int vqueueSize = Integer.parseInt(str.trim());
			queueSize = vqueueSize;
		}
		queue = new LinkedBlockingQueue<LogInfo>(queueSize);
		LogFlusher logFlusher = new LogFlusher();
		logFlusher.start();

	}

	public static BmtLogger instance() { 
		return instance; 
	}

	/**
	 * add info level logInfo
	 * @param who
	 * @param why
	 */
	public void log(String why, Object...args){
		log(LogLevel.Info, why, args);
	}
	/**
	 * add info level logInfo using LogLevel
	 * @param why
	 * @param level
	 */
	public void log(LogLevel level, String why, Object...args) {
		log(level,  (Throwable)null, why, args);
	}
	/**
	 * add a error level logInfo
	 * @param who
	 * @param why
	 */
	public void log(Throwable e, String why, Object...args){
		log(LogLevel.Error, e, why, args);
	}

	/**
	 * add a logInfo at level<code>lev</code>
	 * @param who
	 * @param why
	 * @param lev
	 */
	public void log(LogLevel lev, Throwable e, String why, Object...args){
		logWithName(null, lev, e, why, args);
	}
	public void logWithName(String logName, LogLevel lev,  String why, Object...args){
		logWithName(logName, lev, null, why, args);
	}
	public void logWithName(String logName, Throwable e, String why, Object...args){
		logWithName(logName, LogLevel.Warning, e, why, args);
	}
	public void logWithName(String logName, LogLevel lev, Throwable e, String why, Object...args){
		if (this.level > lev.lev)
			return;//skip low level logs
		try{
			why = String.format(why, args);
			LogInfo rep = new LogInfo(why, lev, e);
			rep.setLogName(logName);
			try {
				synchronized(lock) {
					queue.add(rep);
				}
			}catch(IllegalStateException ill) {
				StandardLogPrinter.instance().print(rep);
			}

		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	/**
	 * set the log printer. As default, a consol writer will be used
	 * @param lp
	 */

	public void setPrinter(LogPrinter lp) {
		this.lp = lp;
	}

	public LogPrinter getPrinter() {
		return this.lp;
	}
	/**
	 * set the log level, default is Debug
	 * @param level
	 */
	public void setLogLevel(LogLevel level) {
		this.level = level.lev;
	}

	public String getLogLevel() {
		LogLevel ll = LogLevel.getLogLevel(this.level);
		if(ll == null) {
			return "Unknow log level";
		}else {
			return ll.logString();
		}
	}
	public static void main(String[]a) throws InterruptedException {
		for(int x = 0; ; x++){
			BmtLogger.instance.log(LogLevel.Debug, "Debug");
			BmtLogger.instance.log(LogLevel.Info, "Info");
			BmtLogger.instance.log(LogLevel.Warning, "Warning");
			Thread.sleep(100);
		}

	}

	public void setPrintException(boolean printException) {
		this.printException = printException;
	}

	public boolean isPrintException() {
		return printException;
	}
}


