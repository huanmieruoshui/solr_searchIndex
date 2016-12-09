package com.bmtech.utils.systemWatcher.innerAction;

import java.io.IOException;
import java.net.Socket;
import java.util.Properties;

import com.bmtech.utils.systemWatcher.WatcherAction;

public class SysPropAction extends WatcherAction{
	public SysPropAction(String key) {
		super(SysProperty, key);
	}

	@Override
	public void run(String[] paras, Socket clientSocket) throws IOException {
		if(paras.length == 1) {
			Properties pps = System.getProperties();
			String ret = pps.toString();
			this.writeBack( ret, clientSocket);
		}else if(paras.length == 2) {
			String prpt = System.getProperty(paras[1]);
			if(prpt == null) {
				prpt = "NO_THIS_PROPERTY";
			}
			this.writeBack(prpt, clientSocket);
		}else if(paras.length == 3) {
			System.setProperty(paras[1], paras[2]);
			this.writeBack( System.getProperties().toString(), clientSocket);
		}else {
			this.writeBack( "bad cmd size:" + paras.length, clientSocket);
		}
	}
}
