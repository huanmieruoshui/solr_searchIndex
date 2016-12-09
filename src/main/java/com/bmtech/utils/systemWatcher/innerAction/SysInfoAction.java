package com.bmtech.utils.systemWatcher.innerAction;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

import com.bmtech.utils.systemWatcher.WatcherAction;

public final class SysInfoAction extends WatcherAction{
	final String SysName;
	final long startTime;
	public SysInfoAction(String key, String SysName) {
		super(SYSINFO, key);
		this.SysName = SysName;
		this.startTime = System.currentTimeMillis();
	}

	@Override
	public void run(String[] paras, Socket clientSocket) throws IOException {
		File baseFile = new File("");
		baseFile = baseFile.getAbsoluteFile();
		
		double passedSeconds = (System.currentTimeMillis() - startTime)/1000.0;
		double passedHour = (System.currentTimeMillis() - startTime)/60/60/10;
		passedHour = passedHour /100.0;
		
		String reply = 
			"SysName:" + SysName + "\n" +
			"uptime:" + passedHour + " Hour (" +passedSeconds + " seconds)\n" +
			"work at:" + baseFile.getAbsolutePath() + "\n" +
			"maxMemory " + Runtime.getRuntime().maxMemory()/1000000.0 + "m " +
			"totalMemory " + Runtime.getRuntime().totalMemory()/1000000.0 + "m " + 
			"freeMemory " + Runtime.getRuntime().freeMemory()/1000000.0 + "m " +
			"still can use " + 
			(Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() + Runtime.getRuntime().freeMemory())/1000000.0 + "m ";;

		this.writeBack(reply, clientSocket);
	}
}