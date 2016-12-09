package com.bmtech.utils.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Properties;

public class LineWriter extends BufferedWriter{
	public LineWriter(File file, boolean append, Charset cs) throws IOException {
		super(new OutputStreamWriter(new FileOutputStream(file, append) ,cs));
	}
	boolean isWin = false;
	public LineWriter(File file,boolean append) throws IOException{
		super(new FileWriter(file,append));
		try {
			Properties prop = System.getProperties();

			String os = prop.getProperty("os.name");
			if(os != null) {
				isWin = os.toLowerCase().startsWith("win");
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	public LineWriter(String file,boolean append) throws IOException{
		this(new File(file),append);
	}


	public LineWriter(String file,Charset cs) throws IOException{
		this(new File(file), false, cs);
	}
	public LineWriter(String file,boolean append,Charset cs) throws IOException{
		this(new File(file), append, cs);
	}
	public LineWriter(String file,Charset cs,boolean append) throws IOException{
		this(new File(file), append, cs);
	}

	//	public void write(String str) throws IOException{
	//		super.write(str+"\n");
	//	}
//	public void writeLine(String str) throws IOException{
//		super.write(str);
//		super.write('\n');
//		if(isWin) {
//			super.write('\r');
//		}
//	}
	public void writeLine(Object o) throws IOException{
		String toWrite = null;
		if(o != null) {
			toWrite = o.toString();
		}else {
			toWrite = "null";
		}
		super.write(toWrite);
		
		if(isWin) {
			super.write('\r');
		}
		super.write('\n');
	}

	public void close(){
		try{
			super.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void finalize(){
		this.close();
	}
}
