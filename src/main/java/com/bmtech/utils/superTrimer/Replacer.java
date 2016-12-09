package com.bmtech.utils.superTrimer;

import java.util.ArrayList;

import com.bmtech.utils.log.BmtLogger;
import com.bmtech.utils.log.LogLevel;
/**
 * 
 * @author Fisher@Beiming
 *
 */
public class Replacer extends SuperTrimer{
	public static final String NAME = "_Replace_";
	boolean isReg;
	private String org;
	private String replacement;
	public boolean isReg() {
		return this.isReg;
	}
	public String getOrg() {
		return this.org;
	}
	public String getReplacement() {
		return this.replacement;
	}
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void setArgs(ArrayList<String> paras) throws Exception {
		//isreg:org:replacement:
		if(paras.size() != 3){
			throw new Exception(String.format("% need three paras, but we get %s", 
					NAME, paras));
		}
		if(paras.get(0).compareToIgnoreCase("true") == 0){
			isReg = true;
		}else if(paras.get(0).compareToIgnoreCase("false") == 0){
			isReg = false;
		}else{
			throw new Exception(String.format("%s need first para error: %s", 
					NAME, paras.get(0)));
		}
		this.org = paras.get(1);
		this.replacement = paras.get(2);
	}

	@Override
	public String toDefineStr() {

		StringBuilder sb = new StringBuilder();
		sb.append('[');
		sb.append(toDefineString(this.getName()));
		sb.append(':');
		sb.append(toDefineString(this.isReg));
		sb.append(':');
		sb.append(toDefineString(this.org));
		sb.append(':');
		sb.append(toDefineString(this.replacement));
		sb.append(']');
	
		return sb.toString();
	}

	@Override
	public String trim(String input) {
		if(input == null)
			return "";
		if(this.isReg){
			try{
				return input.replaceAll(this.org, this.replacement);
			}catch(Exception e){
				BmtLogger.instance().log(LogLevel.Error, "Replacer throws PatternSyntaxException, reg = %s, rep = %s",
						this.org, this.replacement);
				return input;
			}
		}else{
			return input.replace(this.org, this.replacement);
		}
	}

	public String toString(){
		return String.format("[%s: is regular replacement = %s, target = %s, replacement = %s]", 
				NAME, this.isReg, this.org, this.replacement);
	}
	
}
