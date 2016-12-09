package com.bmtech.utils.systemWatcher.innerAction;

import java.io.IOException;
import java.net.Socket;

import com.bmtech.utils.systemWatcher.WatcherAction;

public final class GCAction extends WatcherAction{
		public GCAction(String key) {
			super(GC, key);
		}

		@Override
		public void run(String[] paras, Socket clientSocket) throws IOException {
			Runtime runtime = Runtime.getRuntime();
			String reply = 
				"maxMemory " + runtime.maxMemory()/1000000.0 + "m " +
				"totalMemory " + runtime.totalMemory()/1000000.0 + "m " + 
				"freeMemory " + runtime.freeMemory()/1000000.0 + "m " +
				"still can use " + 
				(runtime.maxMemory() - runtime.totalMemory() + runtime.freeMemory())/1000000.0 + "m ";;
			System.gc();
			reply = 
				reply + "\n" +
				"maxMemory " + runtime.maxMemory()/1000000.0 + "m " +
				"totalMemory " + runtime.totalMemory()/1000000.0 + "m " + 
				"freeMemory " + runtime.freeMemory()/1000000.0 + "m " +
				"still can use " + 
				(runtime.maxMemory() - runtime.totalMemory() + runtime.freeMemory())/1000000.0 + "m ";

			this.writeBack(reply, clientSocket);
		}
	}