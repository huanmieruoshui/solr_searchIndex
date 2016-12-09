package com.bmtech.utils.http;

import java.io.CharArrayWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import com.bmtech.utils.Charsets;

public class RURL {
	private final URL url;
	public RURL(String url,Charset cs) throws MalformedURLException{
		url = rewrite(url,cs);
		this.url = new URL(url);
	}
	public RURL(URL url,Charset cs) throws MalformedURLException{
		this(url.toString(),cs);
	}
	public URL getURL() {
		return this.url;
	}
	public String toString() {
		return url.toString();
	}
	public boolean equals(Object o) {
		return url.equals(o);
	}
	public int hashCode() {
		return url.hashCode();
	}
	static final int caseDiff = ('a' - 'A');
	public static String rewrite(String s, Charset cs) {
		StringBuilder out = new StringBuilder();
		CharArrayWriter charArrayWriter = new CharArrayWriter();
		for (int i = 0; i < s.length();) {
			char c = s.charAt(i);
			if(!needRewrite(c)) {
				out.append(c);
				i++;
				continue;
			}
			do {    
				charArrayWriter.write(c);
				i++;
			} while (i < s.length() && needRewrite(c = s.charAt(i)));

			charArrayWriter.flush();
			String str = new String(charArrayWriter.toCharArray());
			byte[] ba = str.getBytes(cs);
			for (int j = 0; j < ba.length; j++) {
				out.append('%');
				char ch = Character.forDigit((ba[j] >> 4) & 0xF, 16);
				if (Character.isLetter(ch)) {
					ch -= caseDiff;
				}
				out.append(ch);
				ch = Character.forDigit(ba[j] & 0xF, 16);
				if (Character.isLetter(ch)) {
					ch -= caseDiff;
				}
				out.append(ch);
			}
			charArrayWriter.reset();
		}
		return out.toString().replace(" ", "%20");
	}

	private static boolean needRewrite(char c) {
		return c > 128;
	}
	public static void main(String[]a){
		String xxx = "x´ó¼ÒxºÃa";
		System.out.println(rewrite(xxx, Charsets.GBK_CS));
		System.out.println(rewrite(xxx, Charsets.UTF8_CS));
	}
}
