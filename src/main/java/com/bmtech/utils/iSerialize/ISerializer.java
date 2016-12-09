package com.bmtech.utils.iSerialize;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

import com.bmtech.utils.Misc;

/**
 * trimer's format just like:
 * [trime_length:trimer_name:para_length:para_value:para_length:para_value],
 * all the length values are composed of 8 character
 * @author Fisher@Beiming
 *
 */
public class ISerializer {
	public static final String RAW_STHEADER = "STHEADER";
	public static final String STHEADER = "[00000008:STHEADER:";
	public static final int STHEADER_MODEL_LEN = STHEADER.length() + 8 + 1 + 8 + 1;
	public static final int NumLen = 8;

	private ArrayList<Attribute>lst;
	public ISerializer(InputStream ips) throws Exception{
		parse(this, ips);
	}
	public ISerializer(String str) throws Exception{
		parse(this, str);
	}
	public ISerializer(){
		lst = new ArrayList<Attribute>();
	}
	public void addAttribute(Attribute att) {
		lst.add(att);
	}
	public void addAttribute(Object key) {
		Attribute att = new Attribute(key);
		lst.add(att);
	}
	public void addAttribute(Object key, Object value) {
		Attribute att = new Attribute(key, value);
		lst.add(att);
	}
	public void addAttribute(Object key, Object ... values) {
		Attribute att = new Attribute(key);
		for(Object o : values) {
			att.addValue(o);
		}
		lst.add(att);
	}
	public byte[]toDefineBytes(){
		return this.toString().getBytes();
	}
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(Attribute p : lst) {
			sb.append(p.toDefString());
		}
		Attribute p = new Attribute(RAW_STHEADER);
		p.addValue(String.format("%08d", sb.length()));
		return p.toDefString() + sb.toString();

	}
	public static ISerializer parse(String str)throws Exception {
		return parse(null, str);
	}
	public static ISerializer parse(ISerializer ret, String str)throws Exception {
		byte [] bs = str.getBytes();
		ByteArrayInputStream bis = new ByteArrayInputStream(bs);
		return parse(ret, bis);
	}
	public static ISerializer parse(InputStream ips)throws Exception {
		return parse(null, ips);
	}
	public static ISerializer parse(ISerializer ret, InputStream ips)throws Exception {
		if(ret == null) {
			ret = new ISerializer();
		}
		byte [] bs = new byte[STHEADER_MODEL_LEN];
		ips.read(bs);
		String str = new String(bs);
		int sPos = str.indexOf(STHEADER);
		if(sPos != 0) {
			throw new RuntimeException("Not STHEADER fond");
		}
		ParseTool tool = new ParseTool(str, 0, STHEADER_MODEL_LEN);
		Attribute p = tool.parse().get(0);
		int len = Integer.parseInt(p.paras.get(0));
		bs = new byte[len];
		int readed = Misc.tryFillBuffer(ips, bs);
		if(readed != len) {
			throw new Exception("can not read enough byte(" +
					+ readed + ") from ips " + ips);
		}
		str = new String(bs);
		tool = new ParseTool(str, 0, len);
		ArrayList<Attribute> atts = tool.parse();
		ret.lst = atts;
		return ret;
	}
	public static String toDefineString(String o){
		String str = o;
		str = encode(str);
		int len = str.length();
		if(len >99999999){
			throw new RuntimeException("too long string!");
		}
		return String.format("%08d:%s", len, str);
	}
	public static class Attribute{
		String name;
		ArrayList<String> paras;
		private void setName(Object name) {
			if(name == null) {
				this.name = "";
			}else {
				this.name = name.toString();
			}
		}
		public Attribute(Object name, Object value) {
			setName(name);
			paras = new ArrayList<String>();
			addValue(value);
		}
		public Attribute(String name, ArrayList<String> paras) {
			setName(name);
			this.paras = paras;
		}
		public Attribute(Object name) {
			setName(name);
			this.paras = new ArrayList<String>();
		}
		public ArrayList<String> getValues() {
			return this.paras;
		}
		public String getValue(int idx) {
			synchronized(paras) {
				if(paras.size() > idx) {
					return paras.get(idx);
				}
				return null;
			}
		}
		public String getValue() {
			synchronized(paras) {
				if(paras.size() == 0) {
					return null;
				}
				return paras.get(0);
			}
		}
		public void addValue(Object o) {
			if(o == null) {
				this.paras.add("");
			}else {
				this.paras.add(o.toString()); 
			}
		}
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(name);
			sb.append(":");
			sb.append(' ');
			sb.append(' ');
			for(String s : paras) {
				sb.append('\'');
				sb.append(s);
				sb.append('\'');
				sb.append(',');
				sb.append(' ');
			}
			sb.setLength(sb.length() - 2);
			return sb.toString();
		}
		public String toDefString() {
			StringBuilder sb = new StringBuilder();
			sb.append('[');
			sb.append(toDefineString(name));
			for(String s : paras) {
				sb.append(':');
				sb.append(toDefineString(s));
			}
			sb.append(']');
			return sb.toString();
		}
		public String getName() {
			return name;
		}
	}
	//parse all attributes
	private static class ParseTool{
		private int pos = 0;
		private String def;
		final int endPosition ;
		private ParseTool(String defineString, int pos, int len){
			this.def = defineString;
			this.pos = pos;
			this.endPosition = pos + len;
		}

		private ParseTool(String defineString){
			this(defineString, 0 , defineString.length());
		}

		//parse a attribute
		private class AttParser{
			String name;
			ArrayList<String> paras = new ArrayList<String>();

			public Attribute toPara() {
				Attribute att = new Attribute(decode(name));
				for(String s : paras) {
					att.addValue(decode(s));
				}
				return att;
			}

			void parse() throws Exception{
				if('[' !=  def.charAt(pos)){
					throw new RuntimeException(
							String.format("TrimerFormat can not parse String %s, position is %d. '[' is expected",
									def, pos));
				}
				pos ++;

				//find paras
				while(true){
					String sLen,sValue;
					sLen = def.substring(pos, pos + NumLen);
					int len;
					pos += NumLen;

					if(def.charAt(pos) != ':'){
						throw new Exception("format error, for : %s");
					}
					len = Integer.parseInt(sLen);
					pos ++;
					sValue = def.substring(pos, pos + len);
					if(this.name == null){
						this.name = sValue;
					}else{
						paras.add(sValue);
					}
					pos = pos + len;
					if(def.charAt(pos) == ']'){
						pos ++;
						break;
					}else{
						if(def.charAt(pos) == ':'){
							pos++;
							continue;
						}else{
							throw new Exception("format error, for : " + def);
						}
					}
				}		
			}
		}

		private ArrayList<Attribute> parse() throws Exception{
			ArrayList<Attribute>ret = new ArrayList<Attribute>();
			while(pos < this.endPosition - 1){
				if('[' == def.charAt(pos)){
					AttParser pone = new AttParser();
					pone.parse();
					ret.add(pone.toPara());
				}else {
					throw new RuntimeException("args define error : " + def);
				}
			}
			if(pos != this.endPosition) {
				throw new RuntimeException("args define error, end mismatch : " + def);
			}

			return ret;
		}
	}

	private static String encode(String str) {
		if(str == null)
			str = "";
		try {
			return URLEncoder.encode(str, "utf8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return str;
	}
	private static String decode(String str) {
		if(str == null)
			str = "";
		try {
			return URLDecoder.decode(str, "utf8");
		} catch (UnsupportedEncodingException e) {
		}
		return str;
	}
	public ArrayList<Attribute> getAttributes() {
		return this.lst;
	}

}
