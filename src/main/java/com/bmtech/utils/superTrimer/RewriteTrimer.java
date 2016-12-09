package com.bmtech.utils.superTrimer;

import java.util.ArrayList;


public class RewriteTrimer extends SuperTrimer{
	public static final String NAME = "rewrite";
	private String toValue;


	@Override
	public void setArgs(ArrayList<String> paras)throws Exception {
		if(paras.size() != 1){
			throw new Exception("only 1  paras accepted, now set is :" + paras.toString());
		}	
		this.toValue = paras.get(0);
		if(toValue == null) {
			toValue = "";
		}
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
		sb.append(toDefineString(toValue));
		sb.append(']');
		return sb.toString();
	}

	@Override
	public String trim(String input) {
		return this.toValue;
	}

	public String toString(){
		return String.format("[rewrite: tovalue = '%s']", 
				toValue);
	}


	public String getValue() {
		return this.toValue;
	}


}
