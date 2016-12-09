package com.bmtech.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

public class ZipFileOutputStream extends OutputStream{
	final GZIPOutputStream ops;
	final FileOutputStream fos;
	public ZipFileOutputStream(File zipFile) throws IOException{
		fos = new FileOutputStream(zipFile);
		ops = new GZIPOutputStream(fos);
	}
	@Override
	public void write(int b) throws IOException {
		ops.write(b);
	}

	public void flush()throws IOException{
		fos.flush();
	}
	public void close()throws IOException{
		this.flush();
		ops.close();
		fos.close();

	}
}