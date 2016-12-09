package com.bmtech.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Properties;

import com.bmtech.utils.log.BmtLogger;


public class Misc {
	public static String bytesToStr(byte[]bs) {
		StringBuilder sb = new StringBuilder();
		for(byte b : bs) {
			sb.append((char)((b&0x0f) + 65));
			sb.append((char)(((b&0xf0) >> 4) + 65));
		}
		return sb.toString();
	}

	public static byte []strToBytes(String a) {

		int len = a.length()/2;
		byte[] b = new byte[len];
		int pos;
		for(int i = 0; i < len; i++) {
			pos = i * 2;
			b[i] = (byte) (a.charAt(pos) - 65 + ((a.charAt(pos+1) - 65) << 4));
		}
		return b;
	}
	public static boolean isWinSys() {
		Properties prop = System.getProperties();

		String os = prop.getProperty("os.name");
		if(os != null) {
			return os.toLowerCase().startsWith("windows");
		}
		return false;
	}
	public static ArrayList<String> getMACAddresses() {
		ArrayList<String>ret = new ArrayList<String>();
		String address = "";
		String os = System.getProperty("os.name");
		if (os != null) {
			if (os.startsWith("Windows")) {
				try {
					ProcessBuilder pb = new ProcessBuilder("ipconfig", "/all");
					Process p = pb.start();
					BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
					String line;
					while ((line = br.readLine()) != null) {
						if (line.indexOf("Physical Address") != -1) {
							int index = line.indexOf(":");
							address = line.substring(index + 1);
							ret.add(address.trim());
						}
					}
					br.close();
				} catch (IOException e) {

				}
			}else if(os.startsWith("Linux")){
				try {
					ProcessBuilder pb = new ProcessBuilder("ifconfig");
					Process p = pb.start();
					BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
					String line;
					while((line=br.readLine())!=null){
						int index=line.indexOf("HWaddr");
						if(index!=-1){
							address=line.substring(index+6);
							ret.add(address.trim());
						}
					}
					br.close();
				} catch (IOException ex) {
					BmtLogger.instance().log(ex, "when get mac address");
				}
			}
		}
		return ret;
	}
	public static String getMACAddress() {
		return getMACAddresses().get(0);
	}


	public static String substring(String str,
			String prefix, String sufix){
		return getSubString(str,
				prefix, sufix);
	}
	/**
	 * 
	 * @param str
	 * @param prefix perfix, null be viewed as from the begining
	 * @param sufix suffix, null be view as to the end
	 * @return null returned if nothing found
	 */
	public static String getSubString(String str,
			String prefix, String sufix){
		if(str==null)
			return null;
		int st,ed,stLen;
		if(prefix==null){
			st=0;
			stLen=0;
		}else{
			st=str.indexOf(prefix);
			stLen=prefix.length();
		}
		if(st == -1)
			return null;

		if(sufix==null)
			ed=str.length();
		else
			ed=str.indexOf(sufix,st+stLen);
		if(ed==-1)
			return null;
		return str.substring(st+stLen,ed);
	}

	public static ArrayList<String>subs(String str, char prefix,char suffix){
		ArrayList<String>lst = new ArrayList<String>();
		int pos=0,end;
		while(true){
			pos = str.indexOf(prefix,pos);
			if(pos == -1)
				break;
			pos+=1;
			end = str.indexOf(suffix, pos);
			if(end == -1)
				break;
			lst.add(str.substring(pos,end));
			pos = end + 1;
		}
		return lst;
	}
	public static double atod(String str){
		int start=-1;
		int st = 0;
		if(str == null)
			throw new NumberFormatException(str);
		for(;st<str.length();st++){
			char c = str.charAt(st);
			if(c >= '0' && c <= '9'){
				break;
			}			
		}
		if(st >=  str.length())
			throw new NumberFormatException(str);
		start = st;
		st += 1;
		for(;st<str.length();st++){
			char c = str.charAt(st);
			if(c >= '0' && c <= '9'){
				continue;
			}else
				break;
		}
		int ed = st;

		if(st < str.length()){
			if(str.charAt(st) == '.'){
				st++;
				for(;st<str.length();st++){
					char c = str.charAt(st);
					if(c >= '0' && c <= '9'){
						continue;
					}else
						break;
				}
			}
		}
		if(st - ed >1){
			ed = st;
		}
		return Double.parseDouble(str.substring(start,ed));
	}

	/**
	 * works just like c api atoi, 
	 * search for good formatted slice and convert to int
	 * @param str
	 * @return
	 */

	public static int atoi(String str)throws NumberFormatException{
		int start=-1;
		int st = 0;
		if(str == null)
			throw new NumberFormatException(str);
		for(;st<str.length();st++){
			char c = str.charAt(st);
			if(c >= '0' && c <= '9'){
				break;
			}			
		}
		if(st >=  str.length())
			throw new NumberFormatException(str);
		start = st;
		st += 1;
		for(;st<str.length();st++){
			char c = str.charAt(st);
			if(c >= '0' && c <= '9'){
				continue;
			}else
				break;
		}
		return Integer.parseInt(str.substring(start,st));
	}

	/**
	 * format org to a validate file name
	 * @param org
	 * @return
	 */
	public static String toFileNameFormat(String org){
		if(org == null || org.length() == 0){
			return System.currentTimeMillis() + "";
		}
		char cs[] = org.toCharArray();
		char c;
		int len = cs.length;
		for(int i = 0; i < len; i++){
			c = cs[i];
			if(c == '\\'||
					c == '/'||
					c == ':'||
					c == '*'||
					c == '?'||
					c == '"'||
					c == '|'||
					c == '<'||
					c == '>'){
				cs[i] = '^';
			}
		}
		return new String(cs);
	}

	public static void shutDownMachine(int seconds)throws IOException{
		Runtime  rt=Runtime.getRuntime(); 
		String cmd= "c:\\windows\\system32\\shutdown   -s   -t  " + seconds;
		rt.exec(cmd); 
	}
	/**
	 * try to fill the bs, return the bytes readed
	 * @param ips
	 * @param bs
	 * @return
	 * @throws IOException
	 */
	public static int tryFillBuffer(InputStream ips,
			byte[]bs) throws IOException {
		return tryFillBuffer(ips, bs, 0, bs.length);
	}
	public static int tryFillBuffer(InputStream ips, byte[]bs, 
			int offset, int len) throws IOException {
		int readed = 0;

		while(true) {
			int tRead = ips.read(bs, readed + offset, len - readed);
			if (tRead == -1){
				if (readed != 0)
					break;
				readed = -1;
				break;
			}
			readed += tRead;
			if(readed >= len)
				break;
		}

		return readed;
	}
	public static void del(File file){
		if(file.isDirectory()){
			BmtLogger.instance().log("remove %s...", file);
			File[]fs=file.listFiles();
			for(int i=0;i<fs.length;i++){
				del(fs[i]);
			}
			file.delete();
		}else
			file.delete();
	}

	public static String formatFileName(String str, char badCharReplacer) {
		String goodName = str.replaceAll("\\p{Space}+", " ").trim();
		char [] cs = goodName.toCharArray();
		char []fbd = new char[] {'\\', '/', ':', '*', '?', '\"', '<', '>' };
		StringBuilder sb = new StringBuilder();
		char toRep ;
		for(char c : cs) {
			toRep = c;
			for(char cx : fbd) {
				if(c == cx) {
					toRep = badCharReplacer;
					break;
				}
			}
			sb.append(toRep);
		}
		goodName = sb.toString().trim();
		if(goodName.length() == 0) {
			return "" + badCharReplacer; 
		}
		return sb.toString();
//		goodName = goodName.replace('\\', badCharReplacer).replace("/", badCharReplacer).replace(":", badCharReplacer).replace("*", badCharReplacer);
//		goodName = goodName.replace("?", badCharReplacer).replace("\"", badCharReplacer).replace("<", badCharReplacer).replace(">", badCharReplacer).replace(">", badCharReplacer);
//		return goodName;
	}
	public static void main(String[]a) throws IOException {
		File f;
		f = new File("/0.");
		f.createNewFile();
		
		File f2 = new File("/0");
		System.out.println(f2.equals(f));
		
		
		String s = "POSIX character classes (US-ASCII only) \r\n" + 
				"\\p{Lower} A lower-case alphabetic character: [a-z] \r\n" + 
				"\\p{Upper} An upper-case alphabetic character:[A-Z] \r\n" + 
				"\\p{ASCII} All ASCII:[\\x00-\\x7F] \r\n" + 
				"\\p{Alpha} An alphabetic character:[\\p{Lower}\\p{Upper}] \r\n" + 
				"\\p{Digit} A decimal digit: [0-9] \r\n" + 
				"\\p{Alnum} An alphanumeric character:[\\p{Alpha}\\p{Digit}] \r\n" + 
				"\\p{Punct} Punctuation: One of !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~ \r\n" + 
				"\\p{Graph} A visible character: [\\p{Alnum}\\p{Punct}] \r\n" + 
				"\\p{Print} A printable character: [\\p{Graph}\\x20] \r\n" + 
				"\\p{Blank} A space or a tab: [ \\t] \r\n" + 
				"\\p{Cntrl} A control character: [\\x00-\\x1F\\x7F] \r\n" + 
				"\\p{XDigit} A hexadecimal digit: [0-9a-fA-F] \r\n" + 
				"\\p{Space} A whitespace character: [ \\t\\n\\x0B\\f\\r] \r\n" + 
				"";
		System.out.println(formatFileName(s, '~'));
	}
	public static int randInt(int from, int to){
		//如果to-from=0，返回 from
		int itv = to - from;
		if(itv == 0)
			return from;
		//否则,将to-from的值乘以一个0.0到1.0之间的随机值，然后加from后返回
		int it  = (int) (Math.random() * itv);
		return (from + it);
	}
}