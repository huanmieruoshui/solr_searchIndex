package com.bmtech.utils.ruledSegment.affix;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;



import com.bmtech.utils.Charsets;
import com.bmtech.utils.c2j.cTypes.int32_t;
import com.bmtech.utils.io.FileGet;
import com.bmtech.utils.io.LineWriter;
import com.bmtech.utils.log.BmtLogger;

public class Lexicon{


	public static Lexicon instance;
	static {
		try {
			instance = new Lexicon();
		} catch (IOException e) {
			BmtLogger.instance().log(e, "when load ruledSegment lexicon");
			throw new RuntimeException(e);
		}
	}
	HashMap<String, QueryInfo>words = new HashMap<String, QueryInfo>();
	String tag;
	int ver;
	int base;
	int prime;
	int fsize;
	float check;
	String unused;
	final byte[]input;
	boolean echoLoadInfo = true;
	HashSet<String>set = new HashSet<String>();
	
	public QueryInfo get(String word) {
		return words.get(word);
	}
	private Object[] WordInfo(int offset, byte[]buff) {
		offset = offset + 16;
		int len = 0xff&buff[offset];
		offset ++;

		byte [] wbyte = new byte[len];
		System.arraycopy(buff, offset, wbyte, 0, len);
		String word = new String(wbyte, Charsets.GBK_CS);

		offset += len;

		int32_t tmp = new int32_t(0);
		tmp.fromBytes(buff, offset);
		offset += 4;
		float tf = Float.intBitsToFloat((int) tmp.getValue());


		tmp.fromBytes(buff, offset);
		offset += 4;
		float idf = Float.intBitsToFloat((int) tmp.getValue());


		byte flag = buff[offset];
		offset ++;

		byte [] attr = new byte[3];
		System.arraycopy(buff, offset, attr, 0, 3);
		offset += 3;

		String v = new String(attr).trim().intern();
		set.add(v);
		QueryInfo wi = new QueryInfo(word, tf, idf, flag, v);
		words.put(wi.word, wi);
		Object []ret = new Object[2];
		ret[0] = offset;
		ret[1] = wi;

		return ret;
	}
	Lexicon() throws IOException{
		String path = this.getClass().getResource("/").getPath();
		File f = new File(path + "/config/ruled_segment/dict.xdb");
		this.input = FileGet.getBytes(f);
		//		byte[] delimter = new byte[] {'\r', '\n', ' ', '\t'};

		parse();
	}
	private void parse() {
		tag = new String(input, 0, 3);
		ver = input[3];
		int32_t tmp = new int32_t(0);
		tmp.fromBytes(input, 4);
		base = (int) tmp.getValue();

		tmp.fromBytes(input, 8);
		prime = (int) tmp.getValue();

		tmp.fromBytes(input, 12);
		fsize = (int) tmp.getValue();

		tmp.fromBytes(input, 16);
		int fvalue = (int) tmp.getValue();
		this.check = Float.intBitsToFloat(fvalue);

		this.unused = new String(input, 20, 32);

		int crt = 32;
		for(int x = 0; x < this.prime; x ++) {
			tmp.fromBytes(input, crt);
			crt += 4;

			tmp.fromBytes(input, crt);
			crt += 4;
		}
		do {
			Object [] obj = WordInfo(crt, input);
			crt = (Integer)obj[0];
		}while(crt < input.length);
	}

	public static void main(String [] a) throws IOException {

		Lexicon  pl = Lexicon.instance;
		Iterator<Entry<String, QueryInfo>> itr = pl.words.entrySet().iterator();
		LineWriter lw = new LineWriter("words.txt", Charsets.GBK_CS);
		while(itr.hasNext()) {
			lw.writeLine(itr.next().getValue());
		}
		lw.close();
		System.out.println(pl.set);
		System.out.println(pl.words.size());
	}
}