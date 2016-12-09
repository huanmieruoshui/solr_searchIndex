package com.bmtech.utils.systemWatcher;

import java.io.File;
import java.io.IOException;

import com.bmtech.utils.Consoler;
import com.bmtech.utils.io.ConfigReader;
import com.bmtech.utils.log.BmtLogger;
import com.bmtech.utils.log.LogLevel;

public class RemoteCmdLine {
	/**
	 * @param args[0] config_path<br>
	 * @param args[1] cmd_value<br>
	 * if args[1] not set, check config, if still no cmd got,
	 * ask user enter cmd
	 * @param args
	 */
	public static void main(String[]args) {
		try {
			CMD(args);
		} catch (Exception e) {
			System.out.println("ERROR:" + e);
		}
	}
	static void CMD(String[]args) throws IOException {
		String addr = null;
		int port = 0;
		String cmd = null;
		String enc = null;
		File conf;
		if(args.length > 0) {
			conf= new File(args[0]);
		}else {
			conf= new File("config/watcher/remoteInfo.cfg");
		}
		ConfigReader cr = null;

		if(!conf.exists()) {
			BmtLogger.instance().log(LogLevel.Warning, "not found config %s", conf);
		}else {
			cr = new ConfigReader(conf, "main");
		}

		if(cr != null) {
			addr = cr.getValue("addr");
		}

		if(addr == null) {
			addr = Consoler.readString("remote host(no input as 127.0.0.1):");
			if(addr.length() == 0) {
				addr = "127.0.0.1";
			}
		}
		if(cr != null) {
			port = cr.getInt("port");
		}
		while(true) {
			if(port != 0) {
				break;
			}
			port = Consoler.readInt("port : ", 0);
		}

		if(args.length > 1) {
			cmd = args[1];
			cmd = cmd.trim();
			if(cmd.length() == 0) {
				cmd = null;
			}
		}

		if(cmd == null && cr != null) {
			cmd = cr.getValue("cmd");
		}
		if(cmd == null) {
			cmd = Consoler.readString("cmd(^ as CR):");
		}
		cmd = cmd.trim();
		if(cmd.length() == 0) {
			System.out.println("no CMD to execute!");
			return;
		}
		cmd = cmd.replace("^", "\n");
		if(cr != null) {
			enc = cr.getValue("enc");
		}
		if(enc == null) {
			enc = Consoler.readString("enc:");
		}

		RemoteWatchClient clt = new RemoteWatchClient(
				addr, 
				port, 
				enc);
		System.out.println("connected!");
		String ret = clt.writeCommand(cmd);
		System.out.println("------got reply-----------\n'" + ret +"'");
	}
}