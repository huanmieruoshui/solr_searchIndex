package com.bmtech.utils.systemWatcher.innerAction;

import java.io.IOException;
import java.net.Socket;

import com.bmtech.utils.log.BmtLogger;
import com.bmtech.utils.systemWatcher.WatcherAction;

public class ChangeLogExceptionAction extends WatcherAction{
 
	public ChangeLogExceptionAction(String key) {
		super(Change_Log_ExcP, key);
	}

	@Override
	public void run(String[] paras, Socket clientSocket) throws IOException {

		String to, prmt = " use ture/false para switch to change" ;
		if("true".equalsIgnoreCase(paras[1])){
			BmtLogger.instance().setPrintException(true);
		}else if("false".equalsIgnoreCase(paras[1])){
			BmtLogger.instance().setPrintException(false);
		}
		to = "now is  " + BmtLogger.instance().isPrintException() + ". "+ prmt;
		this.writeBack(to, clientSocket);
		return;
	}
}