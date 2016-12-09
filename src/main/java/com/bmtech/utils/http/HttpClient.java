package com.bmtech.utils.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.bmtech.utils.http.HttpHandler.Prop;
import com.bmtech.utils.log.BmtLogger;
import com.bmtech.utils.log.LogLevel;

public class HttpClient implements Runnable{

	public final URL url;
	public final HttpHandler handler;
	boolean connected = false;
	private int followTimes = 20;
	private byte[]postDatas;
	private OutputStream output;
	private HttpCrawler crl;
	private Prop[] props;

	public HttpClient(URL url){
		this(url, null);
	}

	public HttpClient(URL url, HttpHandler handler){
		this.url = url;
		if(handler == null) {
			handler = HttpHandler.getCrawlHandler();
		}
		this.handler = handler;
	}

	/**
	 * set datas to post, if not null setted, will use post to connect HTTP SERVER
	 * @param bs
	 */
	public void setPostDatas(byte[]bs) {
		this.postDatas = bs;
	}

	@Override
	public String toString(){
		return "HttpClient crawling " + url;
	}

	/**
	 * set times to follow 30x
	 * @param followTimes
	 */
	public void setFollowTimes(int followTimes) {
		this.followTimes = followTimes;
	}
	public int getFollowTimes() {
		return followTimes;
	}


	private HttpCrawler post() throws Exception {
		crl = new HttpCrawler(url, handler);
		crl.setFollow(false);
		crl.post(props, postDatas);
		return followRedirect();
	}

	private synchronized HttpCrawler connect() throws Exception{
		crl = new HttpCrawler(url, handler);
		crl.setFollow(false);
		crl.connect(props);
		return followRedirect();
	}

	public HttpCrawler getConnected() throws Exception {
		if(this.postDatas != null) {
			this.post();
		}else {
			this.connect();
		}
		return crl;
	}
	public void runWithException() throws Exception{
		getConnected();
		crl.dumpTo(output);
		crl.close();
	}
	public void run() {
		try {
			getConnected();
			crl.dumpTo(output);
			crl.close();
		}catch(Exception e) {
			BmtLogger.instance().log(e,
					"HttpClient reading fail for url %s", url);
		}
	}
	private HttpCrawler followRedirect() throws Exception {
		int nowTried = 0;
		try {
			while(true) {
				nowTried ++;
				int stat = crl.getHttpCode();

				if(stat >= 300 && stat <= 307 &&
						stat != 306 && stat != 304) {
					//follow link
					List<String> lst = new ArrayList<String>();
					Iterator<Entry<String, List<String>>>  itr = 
						crl.getHeadInfo().entrySet().iterator();
					while(itr.hasNext()) {
						Entry<String, List<String>> e = itr.next();
						String str = e.getKey();
						if(str == null) {
							continue;
						}
						if(str.equalsIgnoreCase("location")) {
							lst.addAll(e.getValue());
							break;
						}
					}
					if(lst.size() == 0) {
						throw new IOException("got no trace url when got httpcode:" + stat + " for url " + 
								crl.getURL());
					}
					String u = lst.get(0);

					if(nowTried > this.followTimes)
						break;
					//can trace we start trace now
					
					HttpCrawler newCrl = null;

					Exception followE = null;
					try {
						URL newUrl = new URL(crl.getURL(), u);
						BmtLogger.instance().log(LogLevel.Debug, "redirect to '%s', org= '%s'",
								newUrl, crl.getURL() );
						newCrl = new HttpCrawler(newUrl, handler);
						newCrl.setFollow(false);
						newCrl.connect();
					}catch(Exception e) {
						followE = e;
					}finally {
						if(newCrl != null) {
							crl.close();
							crl = newCrl;
						}
					}
					if(followE != null)
						throw followE;

				}else {
					return crl;
				}
			}
		}catch(Exception e) {
			BmtLogger.instance().log(LogLevel.Urgen, e, "auto redirect fail! error for url %s", url);
			throw e;
		}
		throw new IOException("exceed max trace times : " + nowTried);
	}
	public void setOutputStream(OutputStream output) {
		this.output = output;
	}
	public OutputStream getOutputStream() {
		return this.output;
	}
	/**
	 * get the url 
	 * @return
	 */
	public URL getURL() {
		return url;
	}
	public void close() {
		if(crl != null) {
			crl.close();
		}
	}

	public void setProps(Prop[] props) {
		this.props = props;
	}
	public Prop[] getProps() {
		return props;
	}

	public int getTotalWritten() {
		if(this.crl != null) {
			return crl.getTotalWritten();
		}
		return 0;
	}

	public int getHttpCode() {
		if(this.crl != null) {
			return crl.getHttpCode();
		}
		return 0;
	}
	/**
	 * get the HttpCrawler instance last connected
	 * @return
	 */
	public HttpCrawler getHttpCrawler() {
		return this.crl;
	}


}
