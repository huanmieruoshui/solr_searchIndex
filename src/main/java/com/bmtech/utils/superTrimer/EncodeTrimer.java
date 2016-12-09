package com.bmtech.utils.superTrimer;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;

import com.bmtech.utils.Charsets;
import com.bmtech.utils.log.BmtLogger;

public class EncodeTrimer extends SuperTrimer{
	public static final String NAME = "_URLEnc_";
	private Charset cs;
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void setArgs(ArrayList<String> paras) throws Exception {

	}
	public String toString() {
		return toDefineStr();
	}
	@Override
	public String toDefineStr() {
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		sb.append(toDefineString(this.getName()));
		sb.append(']');
		return sb.toString();
	}

	@Override
	public String trim(String input) {
		if(cs == null) {
			cs = Charsets.GBK_CS;
		}
		try {
			return URLEncoder.encode(input, cs.name());
		}catch(Exception e) {
			BmtLogger.instance().log(e, cs + ":" + input);
		}
		return input;
	}

	public void setCs(Charset cs) {
		this.cs = cs;
	}

	public Charset getCs() {
		return cs;
	}

}
