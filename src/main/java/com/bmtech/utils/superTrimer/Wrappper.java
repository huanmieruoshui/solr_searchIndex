package com.bmtech.utils.superTrimer;

import java.util.ArrayList;


/**
 * By define the prefix and suffix, we FIND the String inside it.
 * 
 * <br>the define String is[find:length_of_prefix:prefix:length_of_suffix:suffix]
 * <br>
 * 
 * @author Fisher@Beiming
 *
 */
public class Wrappper extends SuperTrimer{
	public static final String NAME = "wrapper";
	private ArrayList<String> paras;
	public String getPrefix() {
		return paras.get(0);
	}
	public String getSuffix() {
		return paras.get(1);
	}
	@Override
	public void setArgs(ArrayList<String> paras)throws Exception {
		if(paras.size() != 2){
			throw new Exception("only 2 or more paras accepted, now set is :" + paras.toString());
		}	
		this.paras = paras;
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
		sb.append(':');
		sb.append(toDefineString(paras.get(1)));
		sb.append(']');
		return sb.toString();
	}

	@Override
	public String trim(String input) {
		return this.paras.get(0) + input + this.paras.get(1);
	}

	public String toString(){
		return String.format("[wrapper: prefix = '%s', suffix = '%s']", 
				paras.get(0), paras.get(1));
	}

}
