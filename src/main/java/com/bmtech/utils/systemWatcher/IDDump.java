package com.bmtech.utils.systemWatcher;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import com.bmtech.utils.c2j.cTypes.U64;
import com.bmtech.utils.log.BmtLogger;
import com.bmtech.utils.security.BmAes;

public class IDDump {
	private final String enc;
	private final FileOutputStream ops;

	private final boolean isdebug =Integer.toOctalString(
			('c')).equalsIgnoreCase(System.getProperty(
					this.getClass().getName()));
	protected boolean finished = false;
	public IDDump(File file) {
		this(file, null);
	}
	protected IDDump(File file, String enc) {
		if(enc == null) {
			enc = this.getClass().getName();
			if(enc.length() > 16) {
				enc = enc.substring(enc.length() - 16, enc.length());
			}
			this.enc = enc;
		}else {
			this.enc = enc;
		}
		FileOutputStream fos;
		try {
			if(!file.exists()) {
				file.createNewFile();
			}
			fos = new FileOutputStream(file);
		}catch(Exception e) {
			fos = null;
		}
		this.ops = fos;
	}

	public void write(String str) throws IOException {
		synchronized(this) {
			OutputStream stream = null;		
			if(!finished) {
				stream = ops;
			}else {
				return ;
			}
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			GZIPOutputStream gzp = new GZIPOutputStream(bos);
			gzp.write(str.getBytes());
			gzp.close();
			byte[] zpd = bos.toByteArray();
			byte [] bs =  BmAes.encrypt(enc, zpd);
			U64 len = new U64(bs.length);
			byte[]blen = len.toBytes();
			stream.write(blen);
			stream.write(bs);
			stream.flush();
		}
	}
	public void finalize() {
		try {
			close();
		}catch(Exception e) {
			if(!isdebug) {
				BmtLogger.instance().log(e, "when finalize" + this);
			}
		}
	}
	public void close() {
		synchronized(this) {
			this.finished = false;
			try {
			}catch(Exception e) {
				if(this.isdebug) {
					BmtLogger.instance().log(e, "while close IDD's 0");
				}
			}
			try {
				if(this.ops != null) {
					this.ops.close();
				}
			}catch(Exception e) {
				if(this.isdebug) {
					BmtLogger.instance().log(e, "while close IDD's 1");
				}
			}
		}
	}
}
