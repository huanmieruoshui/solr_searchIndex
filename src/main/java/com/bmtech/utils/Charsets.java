package com.bmtech.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import com.bmtech.utils.charDet.nsDetector;
import com.bmtech.utils.charDet.nsICharsetDetectionObserver;
import com.bmtech.utils.charDet.nsPSMDetector;
import com.bmtech.utils.log.BmtLogger;

public class Charsets {
	public static final String UTF8_STR = "utf8";
	public static final String GBK_STR = "gbk";
	public static final String BIG5_STR = "big5";
	public static final String GB2312_STR = "gb2312";
	public static final String ISO8859_1_STR = "ISO8859_1";

	public static final Charset UTF8_CS = Charset.forName(UTF8_STR);
	public static final Charset GBK_CS = Charset.forName(GBK_STR);
	public static final Charset BIG5_CS = Charset.forName(BIG5_STR);
	public static final Charset GB2312_CS = Charset.forName(GB2312_STR);
	public static final Charset ISO8859_1_CS = Charset.forName(ISO8859_1_STR);

	public static  enum EnCoder{
		UTF8, GBK, BIG5, GB2312, ISO8859_1;
		public final String toString(){
			switch(this){
			case UTF8:
				return UTF8_STR;
			case GBK:
				return GBK_STR;
			case BIG5:
				return BIG5_STR;
			case GB2312:
				return GB2312_STR;
			case ISO8859_1:
				return ISO8859_1_STR;
			default:
				return GBK_STR;
			}
		}
	}

	public static Charset getCharset(byte[] bs){
		if(bs == null||bs.length == 0) {
			return GBK_CS;
		}
		return getCharset(new ByteArrayInputStream(bs));
	}
	public static Charset getCharset(InputStream ips){
		try {
			Charset cs = getCharset_inner(ips);
			if(cs != null){
				String name = cs.toString().toLowerCase();
				if(name.startsWith("big")){
					return GBK_CS;
				}else if(name.startsWith("gb")) {
					return GBK_CS;
				}
			}
			return cs;
		} catch (IOException e) {
			BmtLogger.instance().log(e, "when parse char for " + ips);
		}
		return null;
	}

	private static Charset getCharset_inner(InputStream ips) throws IOException{
		boolean found = false;

		nsDetector det = new nsDetector(nsPSMDetector.ALL) ;
		Observer obs = new Observer();
		det.Init(obs);

		BufferedInputStream imp = new BufferedInputStream(ips);

		byte[] buf = new byte[1024] ;
		int len;

		boolean isAscii = true ;

		while( (len=imp.read(buf,0,buf.length)) != -1) {
			if (isAscii){
				isAscii = det.isAscii(buf, 0, len);
			}
			if (!isAscii){// DoIt if non-ascii and not done yet.
				det.DoIt(buf,len, false);
			}
			if(null != obs.charset){
				return obs.charset;
			}
		}
		det.DataEnd();

		if (isAscii) {
			return ISO8859_1_CS;
		}

		if (!found) {
			String prob[] = det.getProbableCharsets() ;
			if(prob != null && prob.length > 0){
				for(String s : prob) {
					if(s != null) {
						if(s.startsWith("GB")) {
							return Charsets.GBK_CS;
						}
					}
				}
				try{
					return Charset.forName( prob[0]);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}

		return null;
	}
	private static class Observer implements nsICharsetDetectionObserver{
		private Charset charset;
		@Override
		public void Notify(String charset) {
			try{
				this.charset = Charset.forName(charset);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

}
