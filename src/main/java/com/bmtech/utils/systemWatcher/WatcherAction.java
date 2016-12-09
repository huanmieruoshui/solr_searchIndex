package com.bmtech.utils.systemWatcher;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import com.bmtech.utils.security.BmAes;

public abstract class WatcherAction{
	public static final String STOP = "Force Stop";
	public static final String SYSINFO = "sysinfo";
	public static final String GC = "gc";
	public static final String Echo = "echo";
	public static final String SysProperty = "sysprop";
	public static final String Change_Log_LEVLE = "CH_LOG_LEV";
	public static final String Change_Log_ExcP = "CH_LOG_ExcP";
	public static final String HELP = "HELP";
	
	protected String cmd;
	protected final String key;
	
	public WatcherAction(String cmd, final String key){
		this.cmd = cmd.trim().toLowerCase();
		this.key = key;
	}
	
	public void writeBack(String reply, Socket clientSocket ) throws IOException {
		if(reply == null) {
			reply = "***null value**";
		}
		byte [] bRet = BmAes.encrypt(key, reply);
		OutputStream ops = clientSocket.getOutputStream();
		ops.write(bRet);
		ops.flush();
	}
	
	public String getCmd() {
		return cmd;
	}
	public abstract void run(String []paras, Socket clientSocket) throws Exception;

}