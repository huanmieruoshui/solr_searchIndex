package com.bmtech.utils.systemWatcher.innerAction;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import com.bmtech.utils.log.BmtLogger;
import com.bmtech.utils.log.LogLevel;
import com.bmtech.utils.systemWatcher.SystemWatcher;
import com.bmtech.utils.systemWatcher.WatcherAction;

public final class ShutDownAction extends WatcherAction{
		/**
		 * 初始化对象，继承WatcherAction，将cmd和key初始化
		 * @param key
		 */
		public ShutDownAction(String key) {
			super(STOP, key);
		}
		ArrayList<Runnable> stopHook = new ArrayList<Runnable>();
		public void addStopHook(Runnable nnn){
			stopHook.add(nnn);
		}

		@Override
		public void run(String[] paras, Socket clientSocket) {
			BmtLogger.instance().logWithName(SystemWatcher.WatherLogName, LogLevel.Warning,"stopping system");
			InetSocketAddress address = 
				(InetSocketAddress) clientSocket.getRemoteSocketAddress();

			BmtLogger.instance().logWithName(SystemWatcher.WatherLogName,LogLevel.Warning,"Stop command got, dst address = %s:%d",address.getHostName(),address.getPort());
			
			String reply = cmd + "-->OK" ;
			for(Runnable r : stopHook) {
				if(r != null) {
					BmtLogger.instance().logWithName(SystemWatcher.WatherLogName,LogLevel.Warning,"running stop hook %s", r);
					r.run();
				}else {
					BmtLogger.instance().logWithName(SystemWatcher.WatherLogName,LogLevel.Warning,
					"skip null stop hook");
				}
			}
			try {
				this.writeBack(reply, clientSocket);
				clientSocket.close();
			}catch(Exception e) {
				BmtLogger.instance().logWithName(SystemWatcher.WatherLogName,e,"got error by %s", cmd);
			}
			System.exit(0);
		}
	}