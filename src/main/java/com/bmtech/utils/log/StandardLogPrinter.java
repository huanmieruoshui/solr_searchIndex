package com.bmtech.utils.log;

import java.io.PrintStream;

/**
 * print log to screen using standard outputStream
 * @author liying1
 *
 */
public class StandardLogPrinter implements LogPrinter{
	private PrintStream standard= System.out;
	private PrintStream error= System.err;
	private StandardLogPrinter(){		
	}
	private static final StandardLogPrinter instance=new StandardLogPrinter();
	public static final StandardLogPrinter instance(){
		return instance;
	}
	@Override
	public synchronized void print(LogInfo info) {
		if(info.getLevel().badThan(LogLevel.Info)){
			this.error.println(info);
			if(info.e != null){
				info.e.printStackTrace();
			}
		}else{
			this.standard.println(info);
			if(info.e != null){
				info.e.printStackTrace();
			}
		}		
	}

}
