package com.bmtech.utils.superTrimer;

import java.util.ArrayList;

public abstract class SuperTrimer {

	public abstract void setArgs(ArrayList<String> paras) throws Exception;

	public abstract String toDefineStr();
	
	/**
	 * trim the input String.
	 * @param input
	 * @return never null, even if the input is null.
	 * if not find the pattern in input, it may  print warning log 
	 */
	public abstract String trim(String input);
	
	
	public abstract String getName();
	
	public String toDefineString(Object o){
		String str = o.toString();
		int len = str.length();
		if(len >99999999){
			throw new RuntimeException("too long string!");
		}
		return String.format("%08d:%s", len, str);
	}
	
}
