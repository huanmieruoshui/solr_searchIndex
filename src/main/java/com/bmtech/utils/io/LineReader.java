package com.bmtech.utils.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;

import com.bmtech.utils.Charsets;

public class LineReader extends BufferedReader implements Iterator<String>{
	private String strFile;
	ArrayList<LineFilter>forbidden=new ArrayList<LineFilter>();
	Charset cs = Charset.defaultCharset();
	public LineReader(String file) throws IOException{
		super(new FileReader(new File(file)));
		this.strFile=file;
	}
	public LineReader(File file) throws IOException{
		super(new FileReader(file));
		this.strFile=file.getAbsolutePath();
	}
	public LineReader(String file,String encoder) throws IOException{
		super(new InputStreamReader(new FileInputStream(file),encoder));
		this.strFile=file;
		this.cs = Charset.forName(encoder);
	}
	public LineReader(String file,Charset encoder) throws IOException{
		super(new InputStreamReader(new FileInputStream(file),encoder));
		this.strFile=file;
		this.cs = encoder;
	}
	public LineReader(File file,Charset encoder) throws IOException{
		super(new InputStreamReader(new FileInputStream(file),encoder));
		this.strFile=file.getAbsolutePath();
		this.cs = encoder;
	}
	public LineReader(File file,String encoder) throws IOException{
		super(new InputStreamReader(new FileInputStream(file),encoder));
		this.strFile=file.getAbsolutePath();
		this.cs = Charset.forName(encoder);
	}
	/**
	 * get the absolute path of the file
	 * @return
	 */
	public String getPath(){
		return new File(strFile).getAbsolutePath();
	}
	public String getFileName(){
		return new File(strFile).getName();
	}

	String line;
	boolean first = true;
	public synchronized String readLine() throws IOException {
		String s = super.readLine();
		if(first) {
			if(s != null) {
				if(this.cs.equals(Charsets.UTF8_CS)) {
					if(s.length() > 0) {
						int c = s.charAt(0);
						if(c == 0xfeff) {
							s = s.substring(1);
						}
					}
				}
			}
			first = false;
		}
		return s;
	}
	@Override
	public boolean hasNext() {
		try {
			line=this.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(line==null)
			return false;
		return true;
	}

	public String peer(){
		return line;
	}
	@Override
	public String next() {
		return line;
	}

	public void remove() {
		this.line = null;
	}

	/////////////new added code for easy use////////////////////////////////////////
	boolean emptyLineAcceptted=true;
	private class LineFilter{		
		String prefix;
		LineFilter(String prefix){
			this.prefix=prefix;
		}
		public boolean accept(String line) {
			if(line.startsWith(prefix))
				return false;
			return true;
		}		
	}
	public void forbidden(String prefix){
		if(prefix==null)
			return;
		if(prefix.length()==0)
			this.emptyLineAcceptted=false;
		else
			this.forbidden.add(new LineFilter(prefix));
	}
	/**
	 * trimed next line, first this line will be trimed and then check the validation,
	 * if true, return this line, else check the next line until find a validate line 
	 * or null readed
	 * @return
	 * @throws IOException
	 */
	public String iNextTrimedLine() throws IOException{
		String line;
		while(true){
			line=this.readLine();
			if(line==null)
				return null;
			line= line.trim();
			if(this.iValidateLine(line))
				return line;
		}
	}
	/**
	 * next line, search for the next line who is validate
	 * @return
	 * @throws IOException
	 */
	public String iNextLine() throws IOException{
		String line;
		while(true){
			line=this.readLine();
			if(line==null)
				return null;
			if(this.iValidateLine(line))
				return line;
		}
	}

	/**
	 * test if this line start with a invalidate prefix
	 * @param line
	 * @return true if this line is not forbiddened
	 */
	public boolean iValidateLine(String line){
		if(!emptyLineAcceptted)
			if(line.length()==0)
				return false;
		for(int i=0;i<this.forbidden.size();i++){
			if(this.forbidden.get(i).accept(line))
				continue;
			else return false;
		}
		return true;
	}

	public void finalize(){
		this.close();
	}

	public void close() {
		try {
			super.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
