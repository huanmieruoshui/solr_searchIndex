package com.bmtech.utils.http.fileDump;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import com.bmtech.utils.http.HttpCrawler;
import com.bmtech.utils.http.HttpHandler;
import com.bmtech.utils.log.BmtLogger;
import com.bmtech.utils.log.LogLevel;

public abstract class HttpToFile_low_API {
	protected final int minAllowLen;
	protected URL nextURL;
	protected File nextFile;
	private int minWait = 3000;//
	private HttpHandler hdl;
	public HttpToFile_low_API(int minAllowLen) {
		this.minAllowLen = minAllowLen;
	}
	/**
	 * make nextUrl and nextFie
	 * @return
	 * @throws IOException 
	 */
	public abstract boolean hasNext() throws IOException;

	/**
	 * 
	 * @return false means skip this one, true means ok crawlled
	 * @throws IOException
	 */
	public boolean next() throws IOException {
		BmtLogger.instance().log(LogLevel.Info, "to file %s from %s",
				this.nextFile.getName(), this.nextURL);
		if(nextFile.exists()) {
			if(this.nextFile.length() >= minAllowLen) {
				BmtLogger.instance().log(LogLevel.Warning,
						"Skip file %s, minAllowLen = %s, fileLen = %s, from %s",
						this.nextFile.getName(), minAllowLen, this.nextFile.length(),
						this.nextURL);
				return false;
			}
		}
		HttpCrawler crl = new HttpCrawler(this.nextURL, hdl);
		crl.connect();
		crl.dumpTo(this.nextFile);
		crl.close();
		BmtLogger.instance().log(LogLevel.Debug, "Ok file %s from %s",
				this.nextFile.getName(), this.nextURL);
		return true;
	}
	public void start(boolean stopWhenError) {
		while(true) {
			try {
				if(hasNext()) {
					break;
				}
				
				next();
				if(minWait > 0) {
					Thread.sleep(this.minWait);
				}
			} catch (Exception e) {
				BmtLogger.instance().log(e, "when doing file %s from %s",
						this.nextFile.getName(), this.nextURL);
				if(stopWhenError) {
					break;
				}
			}
		}
	}
	public void setHdl(HttpHandler hdl) {
		this.hdl = hdl;
	}
	public HttpHandler getHdl() {
		return hdl;
	}
	public void setMinWait(int minWait) {
		this.minWait = minWait;
	}
	public int getMinWait() {
		return minWait;
	}
}
