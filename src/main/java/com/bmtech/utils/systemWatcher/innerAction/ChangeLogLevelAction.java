package com.bmtech.utils.systemWatcher.innerAction;

import java.io.IOException;
import java.net.Socket;

import com.bmtech.utils.log.BmtLogger;
import com.bmtech.utils.log.LogLevel;
import com.bmtech.utils.systemWatcher.WatcherAction;

public class ChangeLogLevelAction extends WatcherAction{
 
	public ChangeLogLevelAction(String key) {
		super(Change_Log_LEVLE, key);
	}

	@Override
	public void run(String[] paras, Socket clientSocket) throws IOException {
		/**
		 * paras format: CH_LOG_LEV\cmdid
		 * @return
		 * 0 get crt info
		 * 2 to debug
		 * 4 to fine
		 * ....
		 */

		int id = Integer.parseInt(paras[1]);
		String to = "unknown level cmd:"+id;
		LogLevel lev = LogLevel.getLogLevel(id);
		if(id == 0) {
			to = "crtLogLevel:" + BmtLogger.instance().getLogLevel();
		}else {
			if(lev != null) {
				to = "change to logLevel: " + id;
				BmtLogger.instance().setLogLevel(lev);
			}else {
				to = "unknown logLevel: " + lev;
			}
		}
		this.writeBack(to, clientSocket);
		return;
	}
}