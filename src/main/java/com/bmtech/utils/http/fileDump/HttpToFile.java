package com.bmtech.utils.http.fileDump;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import com.bmtech.utils.Misc;

public class HttpToFile extends DumpAPI{
	final String urlPrefix;
	final String urlSuffix;
	final Iterator<String>itr;
	public HttpToFile(
			String urlPrefix, String urlSuffix, 
			Iterator<String>itr, 
			int minAllowLen,
			File toDir) {
		super(minAllowLen, toDir);
		urlPrefix = (urlPrefix == null? "" : urlPrefix.trim());
		urlSuffix = (urlSuffix == null? "" : urlSuffix.trim());
		this.urlPrefix = urlPrefix;
		this.urlSuffix = urlSuffix;
		
		
		this.itr = itr;
		if(!toDir.exists()) {
			toDir.mkdirs();
		}
	}
	

	@Override
	public boolean hasNextURL() throws IOException {
		return itr.hasNext();
	}


	@Override
	public void makeNextFile() throws IOException {
		String next = itr.next();
		this.nextURL = new URL(this.urlPrefix + next + this.urlSuffix);
		String name = Misc.formatFileName(next, '_');
		this.nextFile = new File(toDir, name);
	}


	@Override
	public void makeNextURL() throws IOException {
		String next = itr.next();
		this.nextURL = new URL(this.urlPrefix + next + this.urlSuffix);
		String name = Misc.formatFileName(next, '_');
		this.nextFile = new File(toDir, name);
	}

}
