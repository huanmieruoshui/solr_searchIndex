package com.bmtech.utils.superTrimer;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bmtech.utils.log.BmtLogger;
import com.bmtech.utils.log.LogLevel;


/**
 * By define the prefix and suffix, we FIND the String inside it.
 * 
 * <br>the define String is[find:length_of_prefix:prefix:length_of_suffix:suffix]
 * <br>
 * 
 * @author Fisher@Beiming
 *
 */
public class RegFindor extends SuperTrimer{
	public static final String NAME = "_regfind_";
	private ArrayList<String> paras;
	Pattern pattern;
	public String getPattern() {
		return this.paras.get(0);
	}
	@Override
	public void setArgs(ArrayList<String> paras)throws Exception {
		if(paras.size() != 1){
			throw new Exception("only 1 para accepted, now set is :" + paras.toString());
		}	
		this.paras = paras;
		pattern = Pattern.compile(paras.get(0));
	}

	public String getName(){
		return NAME;
	}

	@Override
	public String toDefineStr() {
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		sb.append(toDefineString(this.getName()));
		sb.append(':');
		sb.append(toDefineString(paras.get(0)));
		sb.append(']');
		return sb.toString();
	}

	@Override
	public String trim(String input) {
		try {
			if(input == null)
				return "";
			Matcher mat = pattern.matcher(input);
			if(mat == null) {
				return "";
			}
			if(!mat.find()) {
				return "";
			}

			String ret = mat.group();
			if(ret == null) {
				return "";
			}
			ret = ret.replaceFirst(paras.get(0), "$1");
			return ret;
		}catch(Exception e) {
			BmtLogger.instance().log(LogLevel.Debug,  "regFindor fail: " + e);
			return "";
		}
		
	}

	public String toString(){
		return String.format("[regFindor: prefix = '%s']", 
				paras.get(0));
	}

}
