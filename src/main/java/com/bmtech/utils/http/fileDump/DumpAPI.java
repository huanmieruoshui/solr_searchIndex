package com.bmtech.utils.http.fileDump;

import java.io.File;
import java.io.IOException;

public abstract class DumpAPI extends HttpToFile_low_API{
	final File toDir;
	public DumpAPI(
			int minAllowLen,
			File toDir) {
		super(minAllowLen);
		
		if(!toDir.exists()) {
			toDir.mkdirs();
		}
		this.toDir = toDir;
	}
	public abstract boolean hasNextURL()throws IOException;
	public abstract void makeNextURL() throws IOException;
	public abstract void makeNextFile() throws IOException;
	
	@Override
	public boolean hasNext() throws IOException {
		if(hasNextURL()) {
			return true;
		}
		return false;
	}

}
