package com.bmtech.utils.log;


/**
 * LogPrint is used by BmtLogger to print logInfo.
 * by HiLogger.setPrinter(LogPrinter) we can print 
 * the log to anywhere we want
 * @author liying1
 *
 */
public interface LogPrinter {
	/**
	 * print this ReporterInfo
	 * @param info
	 */
	public void print(LogInfo info) ;

}
