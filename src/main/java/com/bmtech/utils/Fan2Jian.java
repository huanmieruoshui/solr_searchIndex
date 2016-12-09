package com.bmtech.utils;

import java.io.IOException;
import java.util.Hashtable;

import com.bmtech.utils.io.LineReader;
import com.bmtech.utils.log.BmtLogger;


public class Fan2Jian {
	Hashtable<Character,Character> ht=new Hashtable<Character,Character>();
	private static final Fan2Jian instance = new Fan2Jian();
	private Fan2Jian(){
		this.load();
	}

	private void load(){
		LineReader lr;
		try {
			lr = new LineReader("config/f2j/cov2.txt");
			while(true){
				String line=lr.readLine();
				if(line==null)
					break;
				ht.put(line.charAt(0), line.charAt(2));
			}
			BmtLogger.instance().log(ht.size()+" words loaded for gbk2gb2312");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public char convert(char ch){
		Character c=ht.get(ch);
		if(c==null){
			return ch;
		}else{
			return c;
		}
	}
	public String convert(String str){
		if(str == null)
			return null;
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<str.length();i++){
			sb.append(convert(str.charAt(i)));
		}
		return sb.toString();
	}

	public static Fan2Jian instance() {
		return instance;
	}

}
