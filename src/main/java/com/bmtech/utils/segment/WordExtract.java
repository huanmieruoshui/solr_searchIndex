package com.bmtech.utils.segment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.bmtech.utils.io.LineReader;
import com.bmtech.utils.log.BmtLogger;
import com.bmtech.utils.log.LogLevel;

public abstract class WordExtract {

	public static final Short state_is = 0;//iif this is a word
	public static final Short state_can = 1;//iif this is a word and can construct a longer word
	public static final Short state_may = 2;//iff this can construct a longger word but not a word now
	protected HashMap<String, Short> map = new HashMap<String, Short>();
	final int maxLen;
	public WordExtract(int maxLen){
		this.maxLen = maxLen;
		loadLexicon();
	}
	protected abstract void loadLexicon();
	protected boolean addItem(String item) {
		String t;
		Short s;
		int len;
		if(item.length() == 0) {
			return false;
		}
		item = item.toUpperCase().trim();
		if (item.length() <= maxLen) {
			t = item;
			s = map.get(t);
			if(s == null)
				map.put(t, state_is);
			else if(s == state_may)
				map.put(t, state_can);
			len = item.length();

			for(int x = 1; x < len; x ++){
				t = item.substring(0, x);
				s = map.get(t);
				if(s == null){
					map.put(t, state_may);
				}else if(s == state_is){
					map.put(t, state_can);
				}else if(s == state_may){

				}
			}
		
			return true;
		}else {
			return false;
		}
	}
	/**
	 * load lexion to hashtable
	 */
	protected void load(String file){
		String line = null;
		BmtLogger.instance().log(LogLevel.Debug, "Loading Lexicon ...");
		try {
			LineReader lr = new LineReader(file, "utf8");
			int i = 0;
			while ((line = lr.readLine())  !=  null) {
				if (line.indexOf("#")  ==  -1) {
					if(addItem(line)) {
						i++;
					}
				}
			}
			lr.close();
			BmtLogger.instance().log(LogLevel.Debug, "total %d words loaded",i);
		} catch (IOException e) {
			BmtLogger.instance().log(LogLevel.Error, "Loading Lexicon failuer");
			e.printStackTrace();

		}
	}

	public ArrayList<String> segment(String input){
		ArrayList<String>lst = new ArrayList<String>();
		input = input.toUpperCase();
		for(int x = 0; x < input.length(); x ++){
			final int startPos = x;
			for(int y = startPos + 1; y < input.length(); y ++){
				String sub = input.substring(startPos, y);
				Short type = map.get(sub);
				if(type == null){
					break;
				}else if(type == state_is){
					lst.add(sub);
					break;
				}else if(type == state_can){
					lst.add(sub);
				}
			}
		}
		return lst;
	}
	
	public static void main(String[]a){
		WordExtract w = new WordExtract(20){

			@Override
			protected void loadLexicon() {
				this.addItem("你好");
				this.addItem("你好大啊");
				this.addItem("好大");
				this.addItem("T恤");
			}
			
		};
		String word = "你的真的好大啊你好大啊你老了啊t恤衫";
		ArrayList<String> ret = w.segment(word);
		for(String x : ret){
			System.out.println(x);
		}
	}

}
