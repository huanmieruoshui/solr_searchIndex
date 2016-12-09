package com.bmtech.utils.http;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;

import com.bmtech.utils.Charsets;
import com.bmtech.utils.KeyValuePair;
import com.bmtech.utils.log.BmtLogger;
import com.bmtech.utils.log.LogLevel;

public class URLBuilder {
	public final Charset cs ;
	public final String csName;
	private ArrayList<KeyValuePair<String,String>>pairs = new ArrayList<KeyValuePair<String,String>>();
	public final URL path;
	public URLBuilder(URL path, Charset cs) {
		this.path = path;
		if(cs == null) {
			this.cs = Charsets.GBK_CS;
		}else {
			this.cs = cs;
		}
		this.csName = this.cs.name();
	}
	public URLBuilder(String path, Charset cs) throws MalformedURLException {
		this(path == null?null : new URL(path), cs);
	}
	public URLBuilder(String path, String cs) throws MalformedURLException {
		this(path, cs == null?null:Charset.forName(cs.trim()));
	}
	public URLBuilder(URL path, String cs) {
		this(path, cs == null?null:Charset.forName(cs.trim()));
	}
	public URLBuilder(URL path) {
		this(path, (Charset)null);
	}
	public URLBuilder(String path) throws MalformedURLException {
		this(path, (Charset)null);
	}
	public void addPara(KeyValuePair<String,String>pair) throws IOException {
		this.addPara(pair.key, pair.value);
	}
	public synchronized void addPara(String key, String value) {
		if(key == null) {
			BmtLogger.instance().log(LogLevel.Warning, 
					"url para key can not be null");
			return ;
		}
		key = key.trim();
		for(int i = 0; i < pairs.size(); i++) {
			KeyValuePair<String,String> pair2 = pairs.get(i);
			if(pair2.key.trim().equals(key)) {
				this.pairs.set(i, new KeyValuePair<String,String>(key,value));
				return;//short cut
			}
		}
		this.pairs.add(new KeyValuePair<String,String>(key,value));
	}
	public String getParaString() {

		StringBuilder sb = new StringBuilder();
		for(KeyValuePair<String,String> pair: pairs) {
			if(sb.length() > 0) {
				sb.append('&');
			}
			try {
				sb.append(URLEncoder.encode(pair.key.trim(),
						csName));
			}catch(Exception e) {
				BmtLogger.instance().log(LogLevel.Error, "unsupport charset %s",csName);
				sb.append(pair.key.trim());
			}
			sb.append('=');
			if(null != pair.value) {
				try {
					String enc = URLEncoder.encode(pair.value.trim(),
							csName);
					sb.append(enc);
				}catch(Exception e) {
					BmtLogger.instance().log(LogLevel.Error, "unsupport charset %s",csName);
					sb.append(pair.value.trim());
				}
			}
		}
		return sb.toString();

	}
	public URL toURL() throws MalformedURLException {
		if(path == null) {
			throw new MalformedURLException("null path set");
		}
		URL url = new URL(path.toString() + '?' + this.getParaString());
		return url;
	}
	public String toString() {
		return "path:" + this.path + "\nparas:" + this.getParaString();
	}
}
