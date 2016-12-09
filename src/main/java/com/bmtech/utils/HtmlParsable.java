package com.bmtech.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.bmtech.utils.io.FileGet;

public abstract class HtmlParsable {
	public static final File configBase = new File("./config/HtmlParsable/");
	public static abstract class TokenParser{
		public final String key;
		public TokenParser(String key){
			this.key = key;
		}
		public String toString() {
			return "TokenParser "+this.getClass().getName()+", for key " + key;
		}
		public abstract String parse()throws Exception;
	}
	protected ArrayList<TokenParser>tokenParsers = new ArrayList<TokenParser>();
	protected Map<String, String>parseResult = Collections.synchronizedMap(new HashMap<String, String>());
	public final String html;
	public HtmlParsable(InputStream ips, Charset cs) throws IOException {
		ByteArrayOutputStream bais = new ByteArrayOutputStream();
		byte[]buf = new byte[4096];
		while(true) {
			int readed = ips.read(buf);
			if(readed == -1)
				break;
			bais.write(buf, 0, readed);
		}
		html = new String(bais.toByteArray(), cs);
	}
	/**
	 * reg parse into this parser. TokenParsers reg into this parser,
	 * parsers reged into this parser will be called as order
	 * @param p
	 */
	public void regTokenParser(TokenParser p) {
		this.tokenParsers.add(p);
	}
	public void parse() throws Exception {
		 for(TokenParser tp : tokenParsers) {
			 String value = tp.parse();
			 this.setValue(tp.key, value);
		 }
	}
	public HtmlParsable(File file, Charset cs) throws IOException {
		this(new FileInputStream(file), cs);
	}
	public void setValue(String key, String value) {
		this.parseResult.put(key, value);
	}
	
	public abstract void regTokenParsers(Object...a)throws Exception;
	public void regTokenParsers() throws Exception {
		regTokenParsers(new Object[]{});
	}
	public String getValue(String key) {
		return this.parseResult.get(key);
	}
	/**
	 * get keys set into this parser
	 * @return
	 */
	public Set<String> keys() {
		return this.parseResult.keySet();
	}
	/**
	 * get value collections
	 * @return
	 */
	public Collection<String> values() {
		return this.parseResult.values();
	}
	
	public void debugParse()  {
		for(TokenParser tp : tokenParsers) {
			try {
			 String value = tp.parse();
			 System.out.println(tp + " parse  : \n'" + tp.key + "'\nvalue='" + value + "'");
			 this.setValue(tp.key, value);
			 System.out.println("in class value is '" + this.getValue(tp.key) +"'");
			}catch(Exception e) {
				System.out.println(tp + " parse error : " + e);
				e.printStackTrace();
			}
		 }
	}
	public String substring(String name) throws IOException{
		return substring(this.html, name);
	}
	public String substring(String html, String name) throws IOException{
		String[] startEnd = getToken(name);
		return Misc.getSubString(html, startEnd[0], startEnd[1]);
	}
	public static String[] getToken(String name) throws IOException{
		String start = new String(FileGet.getBytes(new File(configBase, name + ".start")));
		String end = new String(FileGet.getBytes(new File(configBase, name + ".end")));
		return new String[]{
			start, end	
		};
	}
}
