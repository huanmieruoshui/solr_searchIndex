/*
 * Get file from disk or network using http protocal
 * mainly used to get html file
 * getBin can be used to get the binary file without transform from
 * diffiren charset
 * 
 */
package com.bmtech.utils.io;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.bmtech.utils.Charsets;
import com.bmtech.utils.ZipUnzip;
import com.bmtech.utils.log.BmtLogger;
import com.bmtech.utils.log.LogLevel;


public class FileGet {
	private Map<String,List<String>> headInfo=null;
	private URL url=null;
	private File file=null;
	private String Charset="gbk";
	private BufferedInputStream bis=null;
	private String retString=null;
	private int ConnectionTimeout=3000;
	private int ReadTimeout=30000;

	static Proxy proxy;
	@Deprecated
	public void setConnectionTimeout(int Timeout){
		if(Timeout<500)
			return;
		this.ConnectionTimeout=Timeout;
	}
	@Deprecated
	public void setReadTimeout(int Timeout){
		if(Timeout<500)
			return;
		this.ReadTimeout=Timeout;
	}
	public FileGet(URL url,int connectTimeout, int readTimeout,String charset) throws IOException{
		this.ReadTimeout = readTimeout;
		this.ConnectionTimeout = connectTimeout;
		this.url=url;	
		this.Charset =charset;
		headInfo=new HashMap<String,List<String>>();
		urlGet();
	}

	public FileGet(URL url,int connectTimeout, int readTimeout) throws IOException{
		this(url, connectTimeout, readTimeout, "gbk");
	}
	public FileGet(URL url,String Charset) throws IOException{
		this(url, 1000, 3000, "gbk");
	}
	public FileGet(File file,String Charset) throws IOException{
		this.file=file;
		this.Charset=Charset;
		fileGet();
	}
	public FileGet(String str) throws IOException{
		if(((str.length()>=7)&&(str.substring(0,7).compareToIgnoreCase("http://")==0))||
				((str.length()>=8)&&(str.substring(0,8).compareToIgnoreCase("https://")==0))){
			try {
				URL url=new URL(str);
				this.url=url;	
				urlGet();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		else{
			File file=new File(str);
			this.file=file;
			fileGet();			
		}
	}
	public FileGet(URL url) throws IOException{
		this.url=url;	
		headInfo=new HashMap<String,List<String>>();
		urlGet();

	}
	public FileGet(File file) throws IOException{
		this.file=file;
		fileGet();

	}
	/**
	 * simply wrap the {@link #getBin(File)}
	 * @param s
	 * @return
	 */
	public int getBin(String s){
		if(s==null)
			return -1;
		File f=new File(s);
		return getBin(f);
	}
	/**
	 * get the bytes and write them to this file
	 * @param file
	 * @return total number of data readed,-1 will be returned if error occur
	 */
	public  int getBin(File file){
		if(bis==null)
			return -1;
		BufferedOutputStream bos=null;
		int i=0;
		try {
			bos=new BufferedOutputStream(new FileOutputStream(file));
		} catch (IOException e1) {
			e1.printStackTrace();
			return -1;
		}
		byte[]buffer=new byte[4096];
		int total=0;
		while(true){
			try {
				i=bis.read(buffer);
				if(i!=-1) {
					total+=i;
					bos.write(buffer,0,i);
				} else {
					bos.flush();
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
				return -1;
			}

		}
		try {
			try{
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			bis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return total;

	}
	/**
	 * get file's string using Charset to encode bytes to String
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	public String get(Charset charset) throws IOException	{
		if(retString!=null)
			return retString;
		if(bis==null)
			return null;
		byte[]bs=this.getBin();

		if(bs==null)
			return null;
		return new String(bs,charset);
	}
	public String get() throws IOException	{
		byte []bs = getBin();
		Charset cs = Charsets.getCharset(bs);
		return new String(bs, cs);
	}

	public Map<String,List<String>> getHeadInfo(){
		return headInfo;
	}

	private void fileGet() throws IOException{
		if(!file.canRead()){
			throw new IOException("can not read:" + file.getCanonicalPath());
		}
		FileInputStream fis;
		fis = new FileInputStream(file);
		BufferedInputStream bis=new BufferedInputStream(fis);
		this.bis=bis;		
		this.url = file.toURI().toURL();//根据文件的抽象路径名构造一个URL
	}

	private void urlGet() throws IOException{
		if(this.url==null){
			BmtLogger.instance().log(LogLevel.Error, "URL NULL ERROR");
			return;
		}
		BufferedInputStream bis=null;
		HttpURLConnection urlcnt=null;

		if(proxy!=null)
			urlcnt=(HttpURLConnection) url.openConnection(proxy);//.openConnection();
		else urlcnt=(HttpURLConnection) url.openConnection();
		/**
		 * 
		 * Accept: * /*
		 * Referer: http://www.133.net/
		 * Accept-Language: zh-cn
		 * User-Agent: Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; .NET CLR 2.0.50727; .NET CLR 3.0.04506.648; .NET CLR 3.5.21022; CIBA)
		 * Accept-Encoding: gzip, deflate
		 * 
		 */
		urlcnt.addRequestProperty("Accept", "*/*");
		urlcnt.addRequestProperty("Referer", "http://www.baidu.com");
		urlcnt.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 5.0; Windows XP)");
		urlcnt.addRequestProperty("Accept-Language", "zh-cn");
		urlcnt.addRequestProperty("Accept-Encoding", "gzip");


		urlcnt.setConnectTimeout(ConnectionTimeout);
		urlcnt.setReadTimeout(ReadTimeout);

		urlcnt.connect();
		headInfo=urlcnt.getHeaderFields();
		bis=new BufferedInputStream(urlcnt.getInputStream());
		this.bis=bis;
		return;

	}
	public URL getURL() {
		return this.url;
	}

	public boolean save(File file){
		boolean ret=false;
		if(retString==null)
			return false;
		try {
			FileOutputStream fos=new FileOutputStream(file);
			fos.write(retString.getBytes(Charset));
			fos.close();

			ret=true;
		}
		catch (IOException e) {
			e.printStackTrace();
			return ret;
		}
		return ret;
	}

	public void setCharset(String str){
		this.Charset=str;
	}
	public void save(String string) {
		File file =new File(string);
		save(file);
	}
	public static Proxy setProxy(String host,int port) throws UnknownHostException{
		return proxy=new Proxy(Proxy.Type.HTTP,new InetSocketAddress(InetAddress.getByName(host) , port) );
	}
	public static Proxy setProxy(){
		try {
			File f =new File("c:/windows/setProxy");
			if(f.exists())
				return setProxy("172.16.2.101", 80);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static void unProxy() {
		proxy=null;
	}

	public byte[]getBin() throws IOException{
		int bufferSize=4096;
		ByteArrayOutputStream bob=new ByteArrayOutputStream();
		try{
			while(true){
				byte[]buf=new byte[bufferSize];
				int readed=bis.read(buf);
				if(readed==-1)
					break;
				if(readed==0)
					continue;
				bob.write(buf, 0, readed);
			}
			bis.close();
		}catch(IOException e){
			bis.close();
			throw e;
		}
		byte []bs = bob.toByteArray();
		if(isGzip()){
			bs = ZipUnzip.unGzip(bs);
		}
		return bs;
	}
	public void close(){
		if(this.bis!=null)
			try {
				bis.close();
			} catch (IOException e) {
				e.printStackTrace();
				bis=null;
			}
	}

	public boolean isGzip(){
		Map<String, List<String>> map = getHeadInfo();
		if(map == null)
			return false;
		Set<Entry<String, List<String>>>  set = map.entrySet();

		for(Entry<String, List<String>> e : set){
			if(e.getKey() == null)
				continue;
			if(0 == "Content-Encoding".compareToIgnoreCase(e.getKey())){
				String value =e.getValue().toString().toLowerCase();
				if(value.indexOf("gzip") != -1){
					return true;
				}
			}
		}
		return false;
	}
	
	public static String getStr(File file) throws IOException {
		return new FileGet(file).get();
	}
	public static String getStr(String str) throws IOException {
		return new FileGet(str).get();
	}
	public static  byte[] getBytes(File file) throws IOException{
		FileGet fg = new FileGet(file);
		byte[]ret = fg.getBin();
		fg.close();
		return ret;
	}
}
