package com.bmtech.utils.io;

import java.io.File;

import com.bmtech.utils.log.BmtLogger;


/**
 * this class define a model to serialize data:
 * using a file to keep a special token,
 * only read/write this token
 * @author beiming
 *
 */
public class TchFileTool {
	String path;
	File base;
	static final String fileSuffix=".tch";
	public TchFileTool(String path){
		this(new File(path));
	}
	public TchFileTool(File dir){
		this.path =dir.getAbsolutePath();
		this.base=dir;
		if(!base.exists()){
			base.mkdirs();
		}
	}
	/**
	 * read id.tch file first line
	 * @param id File name
	 * @return
	 */
	public String read(String id){
		File file=new File(base,id+fileSuffix);
		if(file.exists()){
			try{
				LineReader lr=new LineReader(file);
				String ret=lr.readLine();
				lr.close();
				return ret;
			}catch(Exception e){
				BmtLogger.instance().log(e, "can not read a touch action, id = %s",
						id);
				return null;
			}
		}else{
			return null;
		}
	}
	/**
	 * read id.tch file first line, if null or length==0 return dft 
	 * @param id file name
	 * @return the number readed, if nothing read or format is not
	 * a number, return 0
	 */
	public int readInt(String id, int dft){
		String str = this.read(id);
		if(str == null){
			return dft;
		}else{
			str = str.trim();
			if(str.length() == 0)
				return dft;
			try{
				int i = Integer.parseInt(str);
				return i;
			}catch(Exception e){
				BmtLogger.instance().log(e, "can not parse a touched number, id = %s, token = %s",id,str);
				return dft;
			}
		}
	}
	public boolean write(String id,String token){
		File file=new File(base,id+".tch");

		try{
			File par = file.getParentFile();
			if(par !=null){
				if(!par.exists()){
					par.mkdirs();
				}
			}
			LineWriter lw=new LineWriter(file, false);
			lw.writeLine(token);
			lw.close();
			return true;
		}catch(Exception e){
			BmtLogger.instance().log(e, "can not make a touch action, id = %s, token = %s",
					id,
					token);
			return false;
		}
	}
	public boolean writeInt(String id,int token){
		return this.write(id, token+"");
	}
	/**
	 * get the int key from TchFile, if file is not exist, return 0
	 * @param path
	 * @param id
	 * @return
	 */
	public static int getInt(File path,String id, int dft){
		TchFileTool tft=new TchFileTool(path);
		return tft.readInt(id, dft);
	}
	/**
	 * 读取path目录下的id文件，中第一行的内容，并将其返回
	 * @param path
	 * @param id
	 * @return
	 */
	public static String get(File path,String id){
		TchFileTool tft=new TchFileTool(path);
		return tft.read(id);
	}
	/**
	 * get the int key from tch file , if file is not exist, return 0
	 * @param path File Path，if not exist create
	 * @param id file name
	 * @param dft if file not exit or read length ==0 return dft
	 * @return
	 */
	public static int getInt(String path,String id, int dft){
		TchFileTool tft=new TchFileTool(path);
		return tft.readInt(id, dft);
	}
	public static String get(String path,String id){
		TchFileTool tft=new TchFileTool(path);
		return tft.read(id);
	}
	public static boolean put(File path,String id,String token){
		TchFileTool tft=new TchFileTool(path);
		return tft.write(id, token);
	}

	public static boolean putInt(File path,String id,int token){
		TchFileTool tft=new TchFileTool(path);
		return tft.writeInt(id, token);
	}

	public static boolean put(String path,String id,String token){
		TchFileTool tft=new TchFileTool(path);
		return tft.write(id, token);
	}
	public static boolean multyPut(String path, String key,
			String[]args) {
		TchFileTool tft=new TchFileTool(path);
		boolean ret = true;
		for(int i = 0; i < args.length; i++) {
			if(i == 0) {
				ret = ret & tft.write(key, args[0]);
			}else {
				ret = ret & tft.write(key + "_" + i, args[0]);
			}
		}
		return false;
	}
	public static void multiGet(String path, String key, String[]args) {
		TchFileTool tft=new TchFileTool(path);

		for(int i = 0; i < args.length; i++) {
			if(i == 0) {
				args[i] = tft.read(key);
			}else {
				args[i] = tft.read(key + "_" + i);
			}
		}
	}

	public static String[] multiGet(String path, String key, 
			int numToRead) {
		String []ret = new String[numToRead];
		multiGet(path, key, ret);
		return ret;
	}

	public static boolean putInt(String path,String id,int token){
		TchFileTool tft=new TchFileTool(path);
		return tft.writeInt(id, token);
	}
}
