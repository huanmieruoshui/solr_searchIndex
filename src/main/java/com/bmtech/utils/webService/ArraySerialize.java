package com.bmtech.utils.webService;

import java.util.ArrayList;

public class ArraySerialize {
	static final String stFlag = "_ASSTFLAG_";
	static final String edFlag = "_ASEDFLAG_";

	public static final ArrayList<String>deSerialize(String ipt)throws Exception{
		if(ipt.startsWith(stFlag) && ipt.endsWith(edFlag)) {
			;
		}else {
			throw new Exception("format error");
		}
		int x = ipt.indexOf("]");
		String body = ipt.substring(x + 1, ipt.length() - edFlag.length());
		String Head = ipt.substring(stFlag.length()+ 1, x);
		String lenStr[] = Head.split(",");
		ArrayList<Integer>lst = new ArrayList<Integer>();
		int totLen = 0;
		for(String s : lenStr) {
			int v = Integer.parseInt(s.trim());
			if(v > 0) {
				totLen += v;
			}
			lst.add(v);
		}
		//check length matchs
		if(body.length() != totLen) {
			throw new Exception("length NOT match while deserialize...");
		}
		//now toString
		int nowStart = 0;
		int nowEnd = 0;
		String tmp;
		ArrayList<String> ret = new ArrayList<String>();
		for(int ix : lst) {
			if(ix < 0) {
				ret.add(null);
				continue;
			}else {
				nowEnd = nowStart + ix;
				tmp = body.substring(nowStart, nowEnd);
				ret.add(tmp);
				//re-caculate nowStart
				nowStart = nowEnd;
			}
		}
		return ret;

	}



	ArrayList<Object>orgObj = new ArrayList<Object>();
	public ArraySerialize() {

	}
	public void append(Object o) {
		this.orgObj.add(o);
	}
	public String toString() {
		ArrayList<Integer>lst = new ArrayList<Integer>();
		StringBuilder body = new StringBuilder();
		/**
		 * for null object, the length is -1, 
		 */
		for(Object o : orgObj) {
			if(o == null) {
				lst.add(-1);

			}else {
				String sValue  = o.toString();
				lst.add(sValue.length());
				body.append(sValue);
			}
		}
		return stFlag+lst.toString()+ body.toString()+edFlag;
	}
	
}
