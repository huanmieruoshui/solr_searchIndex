package com.bmtech.utils.systemWatcher;

import java.io.File;

import com.bmtech.utils.Misc;
import com.bmtech.utils.io.LineWriter;
import com.bmtech.utils.log.BmtLogger;
import com.bmtech.utils.security.BmAes;

public class ShellGen {
	final String suffix = ".bat";
	File baseFile = new File(System.getProperty("user.dir"));
	final File startFile;
	final File stopFile;
	final File sysInfoFile;
	final File RestartFile;
	final File ForceStopFile;
	final File watchConfigFile;
	final File log4jFile;
	final File bmtLogConfig;
	final boolean isWinSys = Misc.isWinSys();
	final String REM = isWinSys ? "REM " : "#";
	ShellGen(){
		baseFile = baseFile.getAbsoluteFile();
		BmtLogger.instance().log("baseFile : " + baseFile);

		startFile  = new File(baseFile, "start" + suffix);
		stopFile = new File(baseFile, "stop" + suffix);
		sysInfoFile  = new File(baseFile, "sysInfo" + suffix);
		RestartFile = new File(baseFile, "restart" + suffix);
		ForceStopFile = new File(baseFile, "config/watcher/ForceStop.cfg");;
		watchConfigFile = new File(baseFile, "config/watcher/remoteInfo.cfg");
		log4jFile = new File(baseFile, "config/log4j/log4j.properties");
		bmtLogConfig = new File(baseFile, "config/bmtLog/level.tch");
	}
	void makeBmtLogConfig(boolean override) {
		File file = bmtLogConfig;
		String infos[];

		infos = new String[] {
				"2" ,
		};
		make(file,  infos, override, true);

	}
	
	void makeStart(boolean override) {
		File file = startFile;
		String infos[];
		String cmdShell = "nohup  java -Xmx128m -Dfile.encoding=gbk  -cp ./:bin/:./*:lib/* CLASS_NAME &";
		if(isWinSys) {
			 cmdShell = "java -Xmx128m -Dfile.encoding=gbk  -cp ./;bin/;./*;lib/* CLASS_NAME";
		}
		infos = new String[] {
				"cd \"" + baseFile + "\"" ,
				cmdShell
		};

		if(make(file,  infos, override)) {
			BmtLogger.instance().log(
			"Warning: you must edit start file, replace CLASS_NAME with your class " );
		}
	}
	void makeStop(boolean override) {
		File file = stopFile;
		String infos[];

		infos = new String[] {
				"cd \"" + baseFile + "\"" ,
				"java -cp ./:./bin/:lib/* com.bmtech.utils.systemWatcher.RemoteCmdLine ./config/watcher/ForceStop.cfg" 
		};
		if(isWinSys){
			infos[1] = "java -cp ./;./bin/;lib/* com.bmtech.utils.systemWatcher.RemoteCmdLine ./config/watcher/ForceStop.cfg" ;
		}
		make(file,  infos, override);

		file = new File(baseFile, "config/watcher/ForceStop.cfg");
		if(!file.exists()) {
			BmtLogger.instance().log("Warning: stop config does not exist! file: " + file);
		}
	}
	void makeRestart(boolean override) {
		if(isWinSys){
			BmtLogger.instance().log("Warning: there is no restart.bat for win-sys, skip!");
			return;
		}
		File file = RestartFile;
		String infos[];
		String cmdStop = "./stop";
		String cmdStart = "./start";
		if(isWinSys) {
			cmdStop = "stop";
			cmdStart = "start";
		}
		infos = new String[] {
				"cd \"" + baseFile + "\"" ,
				cmdStop + suffix , 
				"sleep 60", 
				cmdStart + suffix
		};
		make(file,  infos, override);
		file = new File(baseFile, "config/watcher/ForceStop.cfg");
		if(!file.exists()) {
			BmtLogger.instance().log("Warning: stop config does not exist! file: " + file);
		}
	}
	void makeSysInfo(boolean override) {
		File file = sysInfoFile;
		String infos[];
		/**sysInfo*/

		infos = new String[] {
				"cd \"" + baseFile + "\"" ,
				"for((x = 1;x<4;x++))" , 
				" do" , 
				" java -cp ./:./*:lib/* com.bmtech.utils.systemWatcher.RemoteCmdLine" , 
				"done"
		};
		if(this.isWinSys){
			infos[3] = " java -cp ./;./*;lib/* com.bmtech.utils.systemWatcher.RemoteCmdLine" ;
			infos[1] = infos[3];
			infos[2] = infos[3];
			infos[4] = infos[3];
		}
		make(file,  infos, override);
	}
	void makeWatchConfig(boolean override) {
		if(!override) {
			if(ForceStopFile.exists() &&
					watchConfigFile.exists()) {
				BmtLogger.instance().log("skip " + this.ForceStopFile.getName());
				BmtLogger.instance().log("skip " + this.watchConfigFile.getName());
				return;
			}
		}

		String infos[];
		while(true) {
			int port = readInt("port:", 0);
			if(port < 4096) {
				BmtLogger.instance().log("can not use port less than 4096 ");
				continue;
			}else {
				String enc = read(" ENC:");
				if(enc == null || enc.length() < 1) {
					BmtLogger.instance().log("can not use empty encrypt code ");
					continue;
				}else {
					//force stop config

					infos = new String[] {
							"[main]",
							"addr=127.0.0.1", 
							"port = " + port,  
							"enc= " + enc, 
							"cmd= Force Stop"
					};
					make(ForceStopFile,  infos, true);

					//sysinfo config

					infos = new String[] {
							"[main]",
							"addr=127.0.0.1", 
							"port = " + port,  
							"enc= " + enc
					};
					make(watchConfigFile,  infos, override);

					long toEnd = (System.currentTimeMillis()/24/60/60/1000 +
							360 + (int)(Math.random() * 10000)%90)*24L*60*60*1000;
					byte[] bs = BmAes.encrypt(enc, System.getProperty("user.dir")+'*' + toEnd);
					infos = new String[] {Misc.bytesToStr(bs)};
					File pfFile = new File(baseFile, "config/profile/"+port+".pf");

					make(pfFile,  infos, override, true);
					break;
				}
			}

		}
	}
	
	void makeLog4j(boolean override) {
		File file = log4jFile;
		String infos[];
		String token = "loger";
		if(!override) {
			if(file.exists()) {
				BmtLogger.instance().log("skip " + file.getName());
				return;
			}
		}
		while(true) {
			token = read("sysName:");
			if(token == null || token.length() == 0) {
				continue;
			}else {
				break;
			}
		}

		infos = new String[] {
				"log4j.rootLogger= debug, " + token,
				"log4j.logger.org.booster= debug,"+ token,
				"",
				"log4j.appender.stdout=org.apache.log4j.ConsoleAppender",
				"log4j.appender.stdout.layout=org.apache.log4j.PatternLayout",
				"log4j.appender.stdout.layout.ConversionPattern=%d %p [%c] - %m%n",
				"",
				"log4j.appender." + token + "=org.apache.log4j.RollingFileAppender",
				"log4j.appender." + token + ".File=./logs/" + token + ".log",
				"log4j.appender." + token + ".MaxFileSize=10000KB",
				"log4j.appender." + token + ".MaxBackupIndex=2500",
				"log4j.appender." + token + ".layout=org.apache.log4j.PatternLayout",
				"log4j.appender." + token + ".layout.ConversionPattern=%d %p [%c] - %m%n",
		};
		make(file,  infos, override);
	}
	private boolean make(File file, String[] infos, boolean override) {
		return make(file, infos, override, false);
	}
	private boolean make(File file, String[] infos, boolean override, boolean direct) {
		if(file.exists()) {
			if(!override) {
				BmtLogger.instance().log("skip " + file.getName());
				return true;
			}
			BmtLogger.instance().log("overriding " + file.getName());
		}else {
			BmtLogger.instance().log("creating  " + file.getName());
		}

		try {
			file.setExecutable(true, true);
			file.setReadable(true, true);
			file.setWritable(true, true);
			File par = file.getParentFile();

			if(!par.exists()) {
				BmtLogger.instance().log("mkdir " + par);
				par.mkdirs();
			}
			LineWriter lw = new LineWriter(file, false);
			if(direct) {
				for(String s : infos) {
					lw.write(s);
				}
				lw.flush();
			}else {
				lw.writeLine(REM+SystemWatcher.VERSION+" auto gen");
				lw.writeLine(REM);
				lw.writeLine(REM + "block start");
				lw.writeLine(REM);
				lw.writeLine("");
				for(String s : infos) {
					lw.writeLine(s);
				}
				lw.writeLine("");
				lw.writeLine(REM);
				lw.writeLine(REM + "block end");
				lw.flush();
				lw.close();
			}

			file.setExecutable(true, true);
			file.setReadable(true, true);
			file.setWritable(true, true);
			BmtLogger.instance().log("OK make file " + file );
		}catch(Exception e) {
			BmtLogger.instance().log("error make file " + file + ", ERROR:"+ e);
			return false;
		}
		return true;
	}
	void makeAll(boolean override) {
		this.makeLog4j(override);
		this.makeWatchConfig(override);
		this.makeSysInfo(override);
		this.makeStart(override);
		this.makeBmtLogConfig(override);
		this.makeStop(override);
		this.makeRestart(override);
	}
	void test() {
		if(!startFile  .exists()){
			BmtLogger.instance().log("warning not found file:" + startFile  );
		}else{
			BmtLogger.instance().log("ok file :" + startFile  );
		}
		if(!stopFile .exists()){
			BmtLogger.instance().log("warning not found file:" + stopFile );
		}else{
			BmtLogger.instance().log("ok file :" + stopFile );
		}
		if(!sysInfoFile  .exists()){
			BmtLogger.instance().log("warning not found file:" + sysInfoFile  );
		}else{
			BmtLogger.instance().log("ok file :" + sysInfoFile  );
		}
		if(!RestartFile .exists()){
			BmtLogger.instance().log("warning not found file:" + RestartFile );
		}else{
			BmtLogger.instance().log("ok file :" + RestartFile );
		}
		if(!ForceStopFile .exists()){
			BmtLogger.instance().log("warning not found file:" + ForceStopFile );
		}else{
			BmtLogger.instance().log("ok file :" + ForceStopFile );
		}
		if(!watchConfigFile .exists()){
			BmtLogger.instance().log("warning not found file:" + watchConfigFile );
		}else{
			BmtLogger.instance().log("ok file :" + watchConfigFile );
		}
		if(!log4jFile .exists()){
			BmtLogger.instance().log("warning not found file:" + log4jFile );
		}else{
			BmtLogger.instance().log("ok file :" + log4jFile );
		}
	}
	void help() {
		String help[] = new String[] {
				"! as force override",
				"(!)start -- make start shell",
				"(!)stop -- make stop shell",
				"(!)restart -- make restart shell",
				"(!)sysInfo --make sysInfo shell",
				"(!)log4j -- make config/log4j/log4j.properties",
				"(!)watchconfig -- make config/watcher/ForceStop.cfg and  remoteInfo.cfg",
				"(!)bmtLog -- make bmtLog",
				"(!)all -- make all files above",
				"test -- check all files above and some special files needed",
				"help (h) -- print help infos",
				"quit (q) -- exist",
		};
		for(String s : help) {
			System.out.print("\t");
			BmtLogger.instance().log(s);
		}
	}

	public static void main(String[] args) {
		ShellGen sgen = new ShellGen();	
		while(true) {
			String cmd = read("shellGen: ");
			boolean override = false;
			if(cmd.startsWith("!")) {
				override = true;
				cmd = cmd.substring(1);
			}
			if("quit".equalsIgnoreCase(cmd) ||
					"q".equalsIgnoreCase(cmd)) {
				BmtLogger.instance().log("quit!");
				break;
			}else if("help".equalsIgnoreCase(cmd) ||
					"h".equalsIgnoreCase(cmd)) {
				sgen.help();
			}else if("test".equalsIgnoreCase(cmd)) {
				sgen.test();
			}else if("start".equalsIgnoreCase(cmd)) {
				sgen.makeStart(override);
			}else if("stop".equalsIgnoreCase(cmd)) {
				sgen.makeStop(override);
			}else if("restart".equalsIgnoreCase(cmd)) {
				sgen.makeRestart(override);
			}else if("sysInfo".equalsIgnoreCase(cmd)) {
				sgen.makeSysInfo(override);
			}else if("log4j".equalsIgnoreCase(cmd)) {
				sgen.makeLog4j(override);
			}else if("watchconfig".equalsIgnoreCase(cmd)) {
				sgen.makeWatchConfig(override);
			}else if("bmtlog".equalsIgnoreCase(cmd)) {
				sgen.makeBmtLogConfig(override);
			}else if("all".equalsIgnoreCase(cmd)) {
				sgen.makeAll(override);
			}else {
				if(cmd.length() > 0) {
					BmtLogger.instance().log("unkonw cmd : " + cmd );
				}
			}
		}
	}
	private static String read(String prmt){
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return com.bmtech.utils.Consoler.readString(prmt);
	}
	private static int readInt(String prmt, int i) {
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return com.bmtech.utils.Consoler.readInt(prmt, i);
	}
}
