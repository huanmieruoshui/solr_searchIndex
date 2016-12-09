package com.bmtech.utils.systemWatcher;

import java.io.File;
import java.util.ArrayList;

import com.bmtech.utils.KeyValuePair;
import com.bmtech.utils.io.ConfigReader;
import com.bmtech.utils.log.BmtLogger;
import com.bmtech.utils.log.LogLevel;

public class SerivceBatchTest {
	static String cmd = "echo^hello!";
	public static void main(String[] args) {
		if(args.length > 0) {
			cmd = args[0];
		}
		BmtLogger.instance().log("using test cmd : %s", cmd);
		ConfigReader cr = new ConfigReader("config/watcher/remoteInfo.cfg", "tests");
		ArrayList<KeyValuePair<String, String>> lst = cr.getAllConfig();
		for(KeyValuePair<String, String> pair : lst) {
			check(pair);
		}
	}

	private static void check(KeyValuePair<String, String> pair) {
		BmtLogger.instance().log("check %s----------------------------------",
				pair.value);
		File cfgFile = new File(pair.value);
		if(!cfgFile.exists()) {
			BmtLogger.instance().log(
					LogLevel.Error,
					"not exists:%s", cfgFile);
			return;
		}
		if(cfgFile.isDirectory()) {
			BmtLogger.instance().log("%s is directory, check default cfg", cfgFile);
			cfgFile = new File(cfgFile, "/config/watcher/remoteInfo.cfg");
			if(!cfgFile.exists()) {
				BmtLogger.instance().log(
						LogLevel.Error,"not exists:%s", cfgFile);
				return;
			}
		}
		
		BmtLogger.instance().log("use config file : %s", cfgFile);

		RemoteCmdLine.main(new String[] {
				cfgFile.getAbsolutePath(),
				cmd
		});
	}
}
