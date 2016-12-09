package com.bmtech.utils.superTrimer;

import java.util.ArrayList;

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
public class Findor extends SuperTrimer{
	public static final String NAME = "_findor_";
	public static final String REVERSE = "R";
	public static final String DIRECTLY = "D";
	private ArrayList<String> paras;
	boolean reverse = false;
	public boolean isReverse() {
		return this.reverse;
	}
	public String getPrefix() {
		return this.paras.get(0);
	}
	public String getSuffix() {
		return this.paras.get(1);
	}
	@Override
	public void setArgs(ArrayList<String> paras)throws Exception {
		if(paras.size() < 2){
			throw new Exception("only 2 or more paras accepted, now set is :" + paras.toString());
		}	
		this.paras = paras;
		if(paras.size() > 2){
			if(REVERSE.compareToIgnoreCase(paras.get(2)) == 0){
				this.reverse = true;
			}else if(DIRECTLY.compareToIgnoreCase(paras.get(2)) == 0){
				this.reverse = false;
			} else{
				BmtLogger.instance().log(LogLevel.Error, "the third para %s can not be recognized, in %s",
						paras.get(2), this.toString());
			}
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
		sb.append(toDefineString(paras.get(0)));
		sb.append(':');
		sb.append(toDefineString(paras.get(1)));
		if(this.reverse){
			sb.append(':');
			sb.append(toDefineString(REVERSE));
		}else{
			sb.append(':');
			sb.append(toDefineString(DIRECTLY));
		}
		sb.append(']');
		return sb.toString();
	}

	@Override
	public String trim(String input) {
		if(input == null)
			return "";
		String para0 = paras.get(0);
		String para1 = paras.get(1);
		para0 = para0.replace("\\n", "\n");
		para1 = para1.replace("\\n", "\n");
		

		int pos1,pos2;
		if(!this.reverse){
			if(para0.length() == 0){
				pos1 = 0;
			}else{
				pos1 = input.indexOf(para0); 
			}
			if(pos1 == -1){
				if(input.trim().length() > 0)
					BmtLogger.instance().log(LogLevel.Debug, "can not find the prefix in %s",
							this.toString());
				return "";
			}
			if(para1.length() == 0){
				pos2 = input.length();
			}else{
				pos2 = input.indexOf(para1, pos1 + para0.length() + 1);//FIXME a bug
			}
			if(pos2 == -1){
				BmtLogger.instance().log(LogLevel.Debug, "can not find the suffix in %s",
						this.toString());
				return "";
			}

		}else{
			if(para1.length() == 0){
				pos2 = input.length();
			}else{
				pos2 = input.lastIndexOf(para1);
			}
			if(pos2 == -1){
				BmtLogger.instance().log(LogLevel.Debug, "can not find the suffix in %s",
						this.toString());
				return "";
			}

			if(para0.length() == 0)
				pos1 = 0;
			else 
				pos1 = input.lastIndexOf(para0, pos2-1);
			if(pos1 == -1){
				BmtLogger.instance().log(LogLevel.Debug, "can not find the prefix in %s",
						this.toString());
				return "";
			}

		}
		return input.substring(pos1 + para0.length(), pos2);

	}

	public String toString(){
		return String.format("[Findor: prefix = '%s', suffix = '%s', third para = '%s']", 
				paras.get(0), paras.get(1), paras.size() > 2? paras.get(2) : "");
	}



}
