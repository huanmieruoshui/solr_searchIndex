package com.bmtech.utils.http;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.bmtech.utils.Charsets;
import com.bmtech.utils.ZipUnzip;
import com.bmtech.utils.http.HttpHandler.Prop;
import com.bmtech.utils.log.BmtLogger;
import com.bmtech.utils.log.LogLevel;

public class HttpCrawler implements Runnable{

	protected HttpURLConnection conn ;
	protected URL url;
	private Map<String, List<String>> headInfo;
	protected InputStream ips;
	public final HttpHandler handler;
	private OutputStream ops ;
	private int totalWritten = -1;
	private int totalReaded = -1;
	private int httpCode = 0;
	private boolean connected = false;
	private boolean follow = true;
	private byte[]postDatas;
	public void setPostDatas(byte[]bs) {
		this.postDatas = bs;
	}
	public String toString(){
		return "crawling " + url;
	}
	public HttpCrawler(RURL url){
		this(url, null);
	}

	public HttpCrawler(RURL url, HttpHandler handler){
		this(url.getURL(), handler);

	}
	public HttpCrawler(URL url){
		this(url, null);
	}

	public HttpCrawler(URL url, HttpHandler handler){
		this.url = url;
		if(handler == null) {
			handler = HttpHandler.getCrawlHandler(true);
		}
		this.handler = handler;

	}
	public int getHttpCode() {
		return httpCode;
	}
	/**
	 * 
	 * @return false means connectted and nothing get
	 * @throws IOException
	 * 
	 */
	/**
	 * 打开HttpURLConnection连接
	 * @return
	 * @throws IOException
	 */
	public int connect() throws IOException{
		return connect(null);
	}
	/**
	 * 获得连接信息
	 * @param props
	 * @throws IOException
	 */
	private void connectInner(Prop[]props) throws IOException{
		
		if(handler.getProxy() != null){
			//与 openConnection() 类似，所不同是连接通过指定的代理建立；不支持代理方式的协议处理程序将忽略该代理参数并建立正常的连接
			conn = (HttpURLConnection) url.openConnection(handler.getProxy());
		}else
			//返回一个 URLConnection 对象，它表示到 URL 所引用的远程对象的连接。
			conn = (HttpURLConnection) url.openConnection();
		if(!follow) {
			conn.setInstanceFollowRedirects(false);
		}
		handler.applyTo(conn);

		if(props != null) {
			for(Prop p : props) {
				if(p != null) {
					conn.addRequestProperty(p.key, p.value);
				}
			}
		}
	}
	/**
	 * 打开HttpURLConnection连接
	 * @param props
	 * @return
	 * @throws IOException
	 */
	public synchronized int connect(Prop[]props) throws IOException{
		if(connected) {
			throw new IOException("already connected, if posted, you should not connect angin");
		}

		connectInner(props);
		connected = true;
		if(handler.isPost()) {
			throw new IOException("can not connect using post method, using post instead");
		}else {
			//打开到此 URL 引用的资源的通信链接
			conn.connect();
			return parseHttpHead();
		}

	}

	private int parseHttpHead() throws IOException {
		//返回头字段的不可修改的 Map。Map 键是表示 response-header 字段名称的 String。每个 Map 值为一个不可修改的 String 的列表，它们代表相应的字段值。
		headInfo = conn.getHeaderFields();
		collectHeader();
		String info = null;
		if(headInfo != null){
			httpCode = conn.getResponseCode();
			info = headInfo.get(null) + "";
		}
		if(handler.useCookie) {
			this.handler.setCookie(this.headInfo);
			this.handler.setCrawled(url);
		}
		if(httpCode < 200 || httpCode >= 400){
			throw new IOException("got http code " + info +" for url : " + this.getURL());
		}else {
			ips = new BufferedInputStream(conn.getInputStream());
		}
		return httpCode;
	}
	public InputStream getInputStream() throws IOException{
		return this.ips;
	}


	public URL getURL(){
		return this.url;
	}

	/**
	 * simply wrap the {@link #dumpTo(File)}
	 * @param path
	 * @return
	 * @throws IOException 
	 */
	public int dumpTo(String path) throws IOException{
		if(path == null)
			return -1;
		File f=new File(path);
		return dumpTo(f);
	}
	/**
	 * get the bytes and write them to this file
	 * @param file
	 * @return total number of data write to file,-1 will be returned if error occur
	 * @throws IOException 
	 */
	public int dumpTo(File file) throws IOException{
		if(ips==null)
			return -1;
		FileOutputStream fos = new FileOutputStream(file);
		BufferedOutputStream ops=new BufferedOutputStream(fos);
		try{
			this.dumpTo(ops);
		}finally{
			ops.close();
			fos.close();
		}
		return this.totalWritten;
	}
	/**
	 * get file's string using Charset to encode bytes to String
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	public String getString(Charset charset) throws IOException	{
		byte[]bs = this.getBytes();

		if(charset == null){
			charset = Charsets.getCharset(bs);
			if(charset == null){
				charset = Charsets.GBK_CS;
			}
		}
		return new String(bs, charset);
	}
	/**
	 * get the String use {@link #handler}'s charset
	 * @return
	 * @throws IOException
	 */
	public String getString() throws IOException	{
		return getString((Charset)null);
	}
	/**
	 * get http head info for this connection
	 * @return the connection head info
	 */
	public Map<String,List<String>> getHeadInfo(){
		return headInfo;
	}
	public static final int PlainText_Encoding = 0;
	public static final int Gzip_Encoding = 1;
	public static final int Deflate_Encoding = 2;
	/**
	 * 1 gzip
	 * 2 deflate
	 * others plain text
	 * @return
	 */
	public int getContentType(){

		Map<String, List<String>> map = getHeadInfo();
		if(map == null)
			return PlainText_Encoding;

		String encode = conn.getContentEncoding();
		if(encode == null)
			return PlainText_Encoding;
		encode = encode.toLowerCase();
		if(encode.indexOf("gzip") != -1){
			return Gzip_Encoding;
		}else if(encode.indexOf("deflate") != -1) {
			return Deflate_Encoding;
		}
		return 0;
	}
	public int rawDataDumpTo(OutputStream ops) throws IOException{
		byte[] buf = new byte[4096];
		int totalReaded = 0;
		int readZero = 0;
		while(true){			
			int readed = ips.read(buf);
			if(readed == -1)
				break;
			if (readed < -1)
				throw new IOException("unexpected read byte size " + readed);
			if (readed == 0){
				readZero++;
				if (5 >= readZero)
					continue;
				throw new IOException("too many cntZeroRead match,  total try=" + readZero + ", has read " + totalReaded + ", for url " + this.url);
			}
			readZero = 0;
			totalReaded += readed;
			ops.write(buf, 0, readed);
		}
		this.totalReaded = totalReaded;
		return totalReaded;
	}

	/**
	 * 
	 * @param ops
	 * @return
	 * @throws Exception 
	 */
	public void dumpTo(OutputStream ops) throws IOException{
		int contentType = getContentType();
		if(contentType == Gzip_Encoding){
			File tmpFile = File.createTempFile("http-client-html", ".tmp");
			tmpFile.deleteOnExit();
			FileOutputStream tmpFos = new FileOutputStream(tmpFile);

			this.rawDataDumpTo(tmpFos);
			tmpFos.close();

			FileInputStream  tmpFis = new FileInputStream(tmpFile);
			int[]arr = new int[1];
			try {

				ZipUnzip.unGzip(tmpFis, ops, arr);
			}catch(EOFException e) {
				BmtLogger.instance().log(e, "unzip fail！ try use fragements, for url %s", this.url);
			}
			tmpFis.close();
			tmpFile.delete();

			ops.flush();
			this.totalWritten = arr[0];
		}else if(contentType == Deflate_Encoding) {
			ByteArrayOutputStream baos =
				new ByteArrayOutputStream();
			this.rawDataDumpTo(baos);
			byte[]bs = baos.toByteArray();
			bs = WebInflate.inflate(bs);

			ops.write(bs);
			ops.flush();
			this.totalWritten = bs.length;

		}else{
			this.totalWritten = this.rawDataDumpTo(ops);
			ops.flush();
		}
	}

	/**
	 * read all of the bytes out
	 * @return
	 * @throws IOException
	 */
	public byte[] getBytes() throws IOException{
		ByteArrayOutputStream ops = new ByteArrayOutputStream();
		this.dumpTo(ops);
		byte[] ret = ops.toByteArray();
		ops.close();
		return ret;
	}

	/**
	 * set OutputStream ops as the standard output.
	 * <br>by calling {@link #run()}, the data will be dump to ops and flushed
	 * @param ops
	 */
	public void setOutputStream(OutputStream ops) {
		this.ops = ops;
	}
	public void runAfterConnected() {
		try {
			if(!this.connected) {
				throw new IOException("Not connected yet, for url " + url);
			}
			if(httpCode == 200){
				synchronized(ops){
					this.dumpTo(ops);
				}
			}else {
				BmtLogger.instance().log(
						LogLevel.Error,
						"crawler got Error httpCode='%s', while crawling %s",
						httpCode, url);
			}
		} catch (Exception e) {
			BmtLogger.instance().log(
					LogLevel.Error,
					"crawler got exception '%s', while crawling %s",
					e.toString(), url);
			//e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			if(this.postDatas != null) {
				this.post(this.postDatas);
			}else {
				this.connect();
			}
			if(httpCode == 200){
				synchronized(ops){
					this.dumpTo(ops);
				}
			}else {
				BmtLogger.instance().log(
						LogLevel.Error,
						"crawler got Error httpCode='%s', while crawling %s",
						httpCode, url);
			}
		} catch (Exception e) {
			BmtLogger.instance().log(
					LogLevel.Error,
					"crawler got exception '%s', while crawling %s",
					e.toString(), url);
			//e.printStackTrace();
		}
	}

	/**
	 * get the total bytes written to the outputStream 
	 * @return the totalWritten. -1 means not start or http error
	 */
	public int getTotalWritten() {
		return totalWritten;
	}
	/**
	 * close the connection and other resources attached to it
	 * <br> it null throw exception
	 */
	public void close(){
		if(url!= null){
			synchronized(url){
				if(this.ips != null){
					try {
						ips.close();
					} catch (Exception e) {
						e.printStackTrace();
					}finally{
						ips=null;
					}
				}
				if(this.conn != null){
					try {
						conn.disconnect();
					} catch (Exception e) {
						BmtLogger.instance().log(e, "when close http connection");
					}finally{
						conn = null;
					}
				}
			}
		}
		this.connected = false;
	}
	public void finalize(){
		this.close();
	}


	//	public String getId() {
	//		return id;
	//	}
	//	public void setId(String id){
	//		this.id = id;
	//	}
	public int post(Prop[] props, String str) throws IOException{
		return post(props, str.getBytes());
	}
	public int post(String str) throws IOException{
		return post(null, str);
	}
	public int post(byte[]bs) throws IOException {
		return post(null, bs);
	}
	public int post(Prop[] props, byte[]bs) throws IOException {
		//		if(!handler.isPost()) {
		//			throw new IOException("must set HttpHandler to post state before post datas");
		//		}
		this.connectInner(props);
		conn.setDoOutput(true);// 打开写入属性 

		OutputStream ops = conn.getOutputStream(); 
		ops.write(bs);
		ops.flush(); 
		ops.close(); 
		connected = true;
		return parseHttpHead();
	}
	public int getTotalReaded() {
		return totalReaded;
	}
	public static HttpCrawler makeCrawler(URL url) throws IOException {
		HttpHandler handler = HttpHandler.getCrawlHandler(true);
		if (handler.isPost())
			throw new IOException("can not post using this method");
		return makeCrawler(url, handler, (byte[])null);
	}
	public static HttpCrawler makeCrawler(URL url, HttpHandler handler) throws IOException {
		if (handler.isPost())
			throw new IOException("can not post using this method");
		return makeCrawler(url, handler, (byte[])null);
	}
	public static HttpCrawler makeCrawler(URL url, 
			HttpHandler handler, byte[] bytes)throws IOException{
		return makeCrawler(url, handler, bytes, 3);
	}

	public static HttpCrawler makeCrawler(
			URL url, HttpHandler handler,
			byte[]toPost, int tryTimes) throws IOException {
		int maxCnt = tryTimes < 1 ? 3 : tryTimes;;
		int connectTime = 0;
		while(true) {
			if(connectTime >= maxCnt) {
				break;
			}
			connectTime ++;
			HttpCrawler crl = new HttpCrawler(url, handler);
			crl.follow = false;
			int code;
			if((toPost != null) || handler != null && handler.isPost()) {
				code = crl.post(toPost);
			}else {
				code = crl.connect();
			}
			if(code >=300 && code < 400) {
				//follow link
				handler = crl.handler;
				handler.setPost(false);
				List<String> lst = new ArrayList<String>();;//crl.headInfo.get("location");
				Iterator<Entry<String, List<String>>>  itr = 
					crl.headInfo.entrySet().iterator();
				while(itr.hasNext()) {
					Entry<String, List<String>> e = itr.next();
					String str = e.getKey();
					if(str == null) {
						continue;
					}
					if(str.equalsIgnoreCase("location")) {
						//						lst = e.getValue();
						lst.addAll(e.getValue());
						break;
					}
				}
				if(lst.size() == 0) {
					throw new IOException("got no trace url when got httpcode:" + code);
				}
				String u = lst.get(0);
				URL url2 = new URL(url, u);
				BmtLogger.instance().log(LogLevel.Debug, "redirect to '%s', org= '%s'",
						url2, url );
				url = url2;
				crl.close();
				continue;
			}else {
				return crl;
			}
		}

		throw new IOException("exceed max 3xx trace times : 3");
	}

	public static byte[]getBytes(String url) throws IOException{
		return getBytes(new URL(url), null);
	}
	public static byte[]getBytes(URL url) throws IOException{
		return getBytes(url, null);
	}
	public static byte[]getBytes(URL url, HttpHandler handler) throws IOException{
		HttpCrawler crl;
		if(handler == null) {
			crl = new HttpCrawler(url);
		}else {
			crl = new HttpCrawler(url, handler);
		}
		crl.connect();
		byte [] ret = crl.getBytes();
		crl.close();
		return ret;
	}
	public static String getString(String url) throws IOException{
		return getString(new URL(url), null);
	}
	public static String getString(URL url) throws IOException{
		return getString(url, null);
	}
	public static String getString(URL url, HttpHandler handler) throws IOException{
		HttpCrawler crl;
		if(handler == null) {
			crl = new HttpCrawler(url);
		}else {
			crl = new HttpCrawler(url, handler);
		}
		crl.connect();
		String ret = crl.getString();
		crl.close();
		return ret;
	}

	private void collectHeader() {
		try {
			HeaderCollector hc = getHc();
			if(hc != null) {
				hc.write(url, this.getHeadInfo());
			}
		}catch(Exception e) {
			BmtLogger.instance().log(e, "when collect header infos for %s",
					this.getURL());
		}
	}

	private static HeaderCollector hc;
	public static void setHc(HeaderCollector hc) {
		HttpCrawler.hc = hc;
	}
	public static HeaderCollector getHc(){
		return hc;
	}
	public void setFollow(boolean follow){
		this.follow = follow;
	}

	public boolean isFollow(){
		return this.follow;
	}

}
