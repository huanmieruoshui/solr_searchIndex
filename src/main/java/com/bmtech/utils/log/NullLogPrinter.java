package com.bmtech.utils.log;


/**
 * print log to screen using standard outputStream
 * @author liying1
 *
 */
public class NullLogPrinter implements LogPrinter{
	private NullLogPrinter(){		
	}
	private static final NullLogPrinter instance=new NullLogPrinter();
	public static final NullLogPrinter instance(){
		return instance;
	}
	@Override
	public void print(LogInfo info) {
		
	}

}
