package com.bmtech.utils.systemWatcher;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.bmtech.utils.log.BmtLogger;
import com.bmtech.utils.log.LogLevel;
import com.bmtech.utils.security.BmAes;
import com.bmtech.utils.tcp.TCPClient;

public class RemoteWatchClient {
	TCPClient clt;
	private String encKey;
	public RemoteWatchClient(String address, int port, String enc) throws IOException{
		clt = new TCPClient(address, port, 1000);
		this.encKey = enc;
	}
	public RemoteWatchClient(int port, String enc) throws IOException{
		this("127.0.0.1", port, enc);
	}
	
	public void close(){
		if(clt != null){
			clt.close();
		}
	}
	public String writeCommand(String cmd) throws IOException {
		
		byte []bCmd = BmAes.encrypt(encKey, cmd);
		clt.write(bCmd);
		ByteArrayOutputStream bops = new ByteArrayOutputStream();
		long readed = clt.read(bops);
		if(readed < 1) {
			return "*** nothing read ***";
		}
		return BmAes.decrypt(encKey, bops.toByteArray(), 0, (int)readed);
	}
	public void stop() throws IOException {
		BmtLogger.instance().logWithName(SystemWatcher.WatherLogName,
				LogLevel.Warning,
				"try stop %s:%s", 
				clt.getSa().getHostName(), 
				clt.getSa().getPort()
		);
		String ret = writeCommand(WatcherAction.STOP);
		BmtLogger.instance().logWithName(SystemWatcher.WatherLogName,
				LogLevel.Warning,
				"from %s:%s, got reply '%s'", 
				clt.getSa().getHostName(), 
				clt.getSa().getPort(),
				ret		
		);
	}
	public void finalize() {
		close();
	}
	
}
