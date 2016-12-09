package com.bmtech.utils.superTrimer;

import java.util.ArrayList;

import com.bmtech.utils.log.BmtLogger;
import com.bmtech.utils.log.LogLevel;
/**
 * TC trim function:
 * @paras 
 * <br>0 trim suffix and prefix
 * <br>1 formatted trim, all white space will be trimed as one
 * <br>2 remove all white space
 * <br>3 remove \r \n
 * <br>
 * <br>
 * 
 * @author Fisher@Beiming
 *
 */
public class WhiteTrimer extends SuperTrimer{
	public static final String NAME = "_WhiteTrim_";

	int opCode = -1;
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void setArgs(ArrayList<String> paras) throws Exception {

		if(paras.size() != 1){
			throw new Exception(
					String.format("para number error, only need one for %s, but '%s'",
							NAME, paras));
		}

		if(paras.get(0).length() != 1){
			throw new Exception(
					String.format("unknown para error, for %s, but '%s'",
							NAME, paras.get(0)));
		}else{
			opCode = paras.get(0).charAt(0) - '0';
			if(opCode >= 0 &&opCode <= 3){
				;
			}else{
				throw new Exception(
						String.format("unknown para error, for %s, para is '%s'",
								NAME, paras.get(0)));
			}
		}
	}


	@Override
	public String toDefineStr() {
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		sb.append(toDefineString(this.getName()));
		sb.append(':');
		sb.append(toDefineString(opCode));
		sb.append(']');
		return sb.toString();
	}

	@Override
	public String trim(String input) {
		if(input == null)
			return "";
		switch(opCode){
		case 0:

			int len = input.length();
			int st = 0;

			while (((st < len) && (input.charAt(st) <= ' '))
					|| ((st < len) && input.charAt(st) == 12288)) {
				st++;
			}
			while (((st < len) && (input.charAt(len -1) <= ' '))
					|| ((st < len) &&(input.charAt(len -1) == 12288))){
				len--;
			}
			return ((st > 0) || (len < input.length())) ? input.substring(st, len) : input;

		case 1:
			StringBuilder sb = new StringBuilder();
			boolean state = false;
			for(int i = 0; i < input.length(); i ++){
				if(input.charAt(i) <= ' ' || input.charAt(i) == 12288){
					if(state){
						continue;
					}else{
						sb.append(' ');
						state = true;
					}
				}else{
					state = false;
					sb.append(input.charAt(i));
				}
			}
			return sb.toString();
		case 2:
			StringBuilder sb2 = new StringBuilder();
			char c;
			for(int i = 0; i < input.length(); i ++){
				c = input.charAt(i);
				if(c> ' ' && c != 12288){
					sb2.append(c);
				}
			}
			return sb2.toString();
		case 3:
			input = input.replace('\r', ' ');
			return input.replace('\n', ' ');


		}

		BmtLogger.instance().log(LogLevel.Error, "illegal opCode for %s, opCode = %d",
				NAME, opCode);
		return input;
	}

	public String toString(){
		return String.format("[%s: %s]", NAME, explain());
	}
	public final String explain(){
		switch(opCode){
		case 0: return "trim white spaces";
		case 1: return "trim multi-white-space-String to one";
		case 2: return "remove all whitespaces";
		case 3: return "replace \\r or \\n to a whitespace";
		}
		return "Unkown opCode " + opCode;
	}

	public int getOpCode() {
		return this.opCode;
	}

}
