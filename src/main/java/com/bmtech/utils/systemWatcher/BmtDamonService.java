package com.bmtech.utils.systemWatcher;

import java.io.IOException;
import java.text.SimpleDateFormat;

import org.apache.log4j.PropertyConfigurator;

import com.bmtech.utils.io.TchFileTool;
import com.bmtech.utils.log.BmtLogHelper;
import com.bmtech.utils.log.BmtLogger;
import com.bmtech.utils.log.Log4jPrinter;
import com.bmtech.utils.log.LogLevel;
/**
 * the sub-class works as:<br>
 * init<br>
 * do your work {@link #work()}<br>
 * {@link #block()}
 */
public abstract class BmtDamonService {
	public final SystemWatcher watcher;
	final String envName;
	public final BmtLogHelper logd;
	public BmtDamonService(int port, String enc, String sysName,String log4jConfigPath) throws IOException {
		//配置log4j
		if(sysName != null) {
			PropertyConfigurator.configureAndWatch(log4jConfigPath);
			BmtLogger.instance().setPrinter(new Log4jPrinter(sysName));
			logd = new BmtLogHelper(sysName);
		}else{
			logd = new BmtLogHelper("daemon");
		}
		watcher = SystemWatcher.regWatcher(port, enc, sysName + "");
		//读取一行config文件下，envName文件中的内容，返回87
		String envNameStr = TchFileTool.get("config", "envName");
		this.envName = envNameStr == null ? "unknowEnv_" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()) : envNameStr;
		BmtLogger.instance().log(LogLevel.Info, "envName is %s", this.envName);
		System.setProperty("BmtDamonService_env", envName);
	}
	public static String getEnv(){
		return System.getProperty("BmtDamonService_env");
	}
	/**
	 * use log4j, assuming log4j configed at "./config/log4j/log4j.properties"
	 * @param port
	 * @param enc
	 * @param sysName
	 * @throws IOException 
	 */
	public BmtDamonService(int port, String enc,String sysName) throws IOException {
		this(port, enc, sysName, "./config/log4j/log4j.properties");
	}
	/**
	 * recommended method.
	 * equals:<code>
	 * <br> {@link #work(Object[])}; <br>{@link #block()}<br>
	 * </code>
	 * @param paras
	 * @throws Exception
	 */
	protected void startDamon(Object[]paras) throws Exception{
		work(paras);
		block();
	}
	/**
	 * work thread.
	 * instead calling this method,
	 * it is recommended to use {@link #startDamon(Object[])} 
	 * @param paras
	 * @throws Exception
	 */
	protected abstract void work(Object[]paras)throws Exception ;

	public void block() {
		watcher.block();
		//unreachable code, protect toRun not be collected by GC
	}

	protected void shutdown() throws IOException {
		RemoteWatchClient clt = new RemoteWatchClient(watcher.server.getPort(),
				watcher.getShutDownAction().key);
		clt.writeCommand(watcher.getShutDownAction().cmd);
	}
}
