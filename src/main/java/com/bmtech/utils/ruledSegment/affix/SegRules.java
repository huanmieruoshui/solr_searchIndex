package com.bmtech.utils.ruledSegment.affix;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;


import com.bmtech.utils.Charsets;
import com.bmtech.utils.KeyValuePair;
import com.bmtech.utils.Misc;
import com.bmtech.utils.io.LineReader;
import com.bmtech.utils.log.BmtLogger;
import com.bmtech.utils.ruledSegment.SegResult;

public class SegRules {
	public static final int SCWS_RULE_MAX =  32;
	public static final int SCWS_RULE_SPECIAL = 0x80000000;
	public static final int SCWS_RULE_NOSTATS = 0x40000000;

	/* flag: 0x00 ~ 0x4000 */
	public static final int SCWS_ZRULE_NONE = 0x00;
	public static final int SCWS_ZRULE_PREFIX = 0x01;
	public static final int SCWS_ZRULE_SUFFIX = 0x02;
	public static final int SCWS_ZRULE_INCLUDE = 0x04; /* with include */
	public static final int SCWS_ZRULE_EXCLUDE = 0x08; /* with exclude */
	public static final int SCWS_ZRULE_RANGE = 0x10; /* with znum range */

	public HashMap<String, RuleItem>name_items = new HashMap<String, RuleItem>();

	public HashMap<String, RuleItem>key_items = new HashMap<String, RuleItem>();
	public RuleAttr attr;
	public static SegRules instance;
	static {
		try {
			instance = new SegRules();
		} catch (IOException e) {
			BmtLogger.instance().log(e, "when init segRules ");
			throw new RuntimeException(e);
		}
	}

	public class RuleAttr {
		private char attr1[] = new char[2];
		private char attr2[] = new char[2];
		private char npath[] = new char[2];
		int ratio;

		RuleAttr next;
		public String toString() {
			return "RuleAttr: " +
			attr1[0] + attr1[1] +
			(npath[0] == 0xff ? "" : "("+(int)npath[0]+")") +
			"+" +
			attr2[0] + attr2[1] +
			(npath[1] == 0xff ? "" : "("+(int)npath[1]+")") +
			"=" +
			ratio;
		}

		private boolean match(char[] y, String att) {
			if(y[0] == '*' || y[0] == att.charAt(0)) {
				if(y[1] == 0) {
					return true;
				}else {
					if(att.length() > 1) {
						return y[1] == att.charAt(1);
					}
				}
			}
			return false;
		}

		//		public boolean match(MatrixInfo mi1, MatrixInfo mi2) {
		////			if (this.match(mi1.attr, mi2.attr)){ 
		////				if((npath[0] == 0xff || npath[0]== mi1.word.length()))
		////					if((npath[1]==0xff||npath[1]== mi2.word.length())) {
		////						return true;
		////					}
		////			}
		////			return false;
		//			return match(mi1.attr, mi2.attr, mi1.word.length(), mi2.word.length());
		//		}
		public boolean match(String attr1, String attr2, int len1 ,int len2) {
			if (match(this.attr1, attr1) && match(this.attr2, attr2)){ 
				if((npath[0] == 0xff || npath[0]== len1))
					if((npath[1]==0xff||npath[1]== len2)) {
						return true;
					}
			}
			return false;
		}
	}	
	public int attrRatio(MatrixInfo mi1, MatrixInfo mi2){
		return attrRatio(mi1.attr, mi2.attr, mi1.word.length(), mi2.word.length());
	}
	public int attrRatio(SegResult seg1, SegResult seg2){
		return attrRatio(seg1.attr, seg2.attr, seg1.strValue.length(), seg2.strValue.length());
	}
	public int attrRatio(String attr1, String attr2, int len1, int len2){
		RuleAttr ptr = attr; 
		while (ptr != null){
			if (ptr.match(attr1, attr2, len1, len2)){
				return ptr.ratio;
			}
			ptr = ptr.next;
		}
		return 1;
	}
	public class RuleItem {
		private final byte flag;
		public final int zmin;
		public final int zmax;
		public final String name;
		public final String attr;
		public final float tf;
		public final float idf;
		public final int bit;	/* my bit  */
		int inc;	/* include */
		int exc;	/* exclude */
		public RuleItem(String name, float tf, float idf, 
				String attr, int bit, byte flag, int zmin, int zmax) {
			this.name = name;
			this.tf = tf;
			this.idf = idf;
			this.attr = attr;
			this.bit = bit;
			this.flag = flag;
			this.zmin = zmin;
			this.zmax = zmax;
		}
		public boolean isPrefix() {
			return 0 != (flag & SCWS_ZRULE_PREFIX);
		}
		public boolean isSuffix() {
			return 0 != (flag & SCWS_ZRULE_SUFFIX);
		}
		public String toString() {
			return "RuleItem: " + name + ", " + 
			", flag: " + Integer.toString(flag, 2) +
			", zmin: " + zmin + 
			", zmax: " + zmax + 
			", attr: " + attr + 
			", tf= " + tf + 
			", idf= " + idf + 
			", bit= " + Integer.toString(bit, 2) + 
			", inc= " + Integer.toString(inc, 2) + 
			", exc= " + Integer.toString(exc, 2);
		}

		public boolean matches(String str){

			if ((flag & SCWS_ZRULE_INCLUDE) != 0) {
				if(!this.ruleInclude(str)){
					return false;
				}
			}

			if ((flag & SCWS_ZRULE_EXCLUDE) != 0) {
				if(this.ruleExclude(str)){
					return false;
				}
			}
			return true;
		}
		boolean checkInExclude(String str, int bit) {
			RuleItem ri = getItemRule(str);
			if ((ri != null)) {
				if ((ri.bit & bit) != 0){
					return true;
				}
			}
			return false;
		}
		boolean ruleInclude(String str) {
			return checkInExclude(str, this.inc);
		}
		boolean ruleExclude(String str) {
			return checkInExclude(str, this.exc);
		}
	}

	public SegRules() throws IOException{
		String path = this.getClass().getResource("/").getPath();
		File toLoad = new File(path + "/config/ruled_segment/rules/rules.ini");
		loadRule(toLoad);
	}


	void initAttrs(ArrayList<String>lst, int start, int end) {
		for(int x = start; x < end; x ++) {
			String line = lst.get(x);
			line = line.trim();
			if(line.length() == 0) {
				continue;
			}
			if(line.indexOf('=') == -1) {
				continue;
			}
			if(line.indexOf('+') == -1) {
				continue;
			}
			String[]ss = line.split("\\=");
			if(ss.length != 2) {
				continue;
			}
			String to = ss[1].trim();
			String added = ss[0].trim();
			String [] addStrs = added.split("\\+");
			if(addStrs.length != 2) {
				continue;
			}
			String add0 = addStrs[0].trim();
			String add1 = addStrs[1].trim();
			RuleAttr attr = new RuleAttr();
			attr.npath[0] = 0xff;
			attr.npath[1] = 0xff;
			attr.ratio = Integer.parseInt(to);
			if(attr.ratio < 1) {
				attr.ratio = 1;
			}
			attr.attr1[0] = add0.charAt(0);
			String s0 = Misc.getSubString(add0, "(", ")");
			if(s0 != null) {
				attr.npath[0] = (char) Integer.parseInt(s0.trim()); 
			}

			attr.attr2[0] = add1.charAt(0);
			String s1 = Misc.getSubString(add1, "(", ")");
			if(s1 != null) {
				attr.npath[1] = (char) Integer.parseInt(s1.trim()); 
			}
			if(this.attr == null) {
				this.attr = attr;
			}else {
				RuleAttr tmp = this.attr;
				do {
					if(tmp.next == null) {
						tmp.next = attr;
						break;
					}else {
						tmp = tmp.next;
					}
				}while(true);
			}
		}
	}
	void initRule(String name, final int bitValue, ArrayList<String>lst, int start, int end, HashMap<String, String[]> incs, HashMap<String, String[]> excs) {

		float tf = 5.0f;
		float idf = 3.5f;
		String attr = "un";
		int bit = bitValue;
		int zmin = 0;
		int zmax = 0;
		byte flag = 0;
		ArrayList<String>keyLines = new ArrayList<String>();
		boolean readByLine = true;
		for(int x = start + 1; x < end; x ++) {
			String line = lst.get(x).trim();
			if(line.length() == 0) {
				continue;
			}

			char c = line.charAt(0);
			if(c == ';') 
				continue;

			if(c == ':') {
				String toParse = line.substring(1).trim();
				if(toParse.indexOf("=") == -1) {
					continue;
				}
				KeyValuePair<String, String> pair = KeyValuePair.parseLine(toParse);

				if(pair.key.equals("line")) {
					readByLine = !pair.value.toUpperCase().startsWith("N");
				}else if(pair.key.equals("tf")) {
					tf = Float.parseFloat(pair.value); 
				}else if(pair.key.equals("idf")) {
					idf = Float.parseFloat(pair.value);
				}else if (pair.key.equals( "attr")) {
					attr = pair.value;
				}else if(pair.key.equals("znum")){
					if (pair.value.indexOf(',') != -1){
						String [] pp = pair.value.split("\\,");
						zmax = Integer.parseInt(pp[1].trim());
						zmin = Integer.parseInt(pp[0].trim());
					}else {
						zmin =Integer.parseInt(pair.value);				
					}
				}else if (pair.key.equals("type")){
					if(pair.value.startsWith("prefix")) {
						flag |= SCWS_ZRULE_PREFIX;
					}else if(pair.value.startsWith("suffix")) {
						flag |= SCWS_ZRULE_SUFFIX;
					}
				}else if (pair.key.equals("include")){
					flag |= SCWS_ZRULE_INCLUDE;
					incs.put(name, pair.value.split(","));
				}else if (pair.key.equals("exclude")){
					flag |= SCWS_ZRULE_EXCLUDE;
					excs.put(name, pair.value.split(","));
				}else {
					System.out.println("unknown key" + pair.key);
				}
				continue;
			}else {
				keyLines.add(line);
			}
		}

		RuleItem itm = new RuleItem(name, tf, idf, attr, 
				bit, flag, zmin, zmax);
		this.name_items.put(name, itm);
		for(String line : keyLines) {
			if(!readByLine) {
				for(int i = 0; i < line.length(); i++) {
					key_items.put(line.substring(i, i + 1), itm);

				}
			}else {
				key_items.put(line, itm);
			}
		}

	}
	public void loadRule(File fpath) throws IOException{
		HashMap<String, String[]>incs = new HashMap<String, String[]>();
		HashMap<String, String[]>excs = new HashMap<String, String[]>();
		if(!fpath.exists()) {
			throw new IOException("not found rule file " + fpath.getAbsolutePath());
		}
		LineReader lr = new LineReader(fpath, Charsets.UTF8_CS);
		ArrayList<String>itemDatas = new ArrayList<String>();
		ArrayList<Integer>itemStarts = new ArrayList<Integer>();

		while(lr.hasNext()) {
			String line = lr.next();

			if(line.startsWith("[")) {
				itemStarts.add(itemDatas.size());
			}
			itemDatas.add(line);
		}
		int itmIdx = 0;
		int rulePos = 0;
		for(itmIdx = 0; itmIdx < itemStarts.size();  itmIdx ++ ) {
			//get itemname
			int start = itemStarts.get(itmIdx);

			String name = itemDatas.get(start).trim();
			name = name.substring(1, name.length() - 1);

			int end;
			if(itmIdx == itemStarts.size() -1) {
				end = itemDatas.size();
			}else {
				end = itemStarts.get(itmIdx + 1);
			}

			if(name.equals("attrs")) {
				initAttrs(itemDatas, start, end);
			}else {
				int bitValue ;
				if(name.equals("special")) {
					bitValue = SCWS_RULE_SPECIAL;
				}else if(name.equals("nostats")) {
					bitValue = SCWS_RULE_NOSTATS;
				}else {
					bitValue = 1 << rulePos;
					rulePos ++;
				}
				initRule(name, bitValue, itemDatas, start, end,
						incs, excs);
			}
		}
		Iterator<Entry<String, String[]>> itr = incs.entrySet().iterator();
		while(itr.hasNext()) {
			Entry<String, String[]> e = itr.next();
			String key = e.getKey();
			String[]v = e.getValue();
			RuleItem att = this.name_items.get(key);
			for(String vv : v) {
				RuleItem attvv = this.name_items.get(vv);
				att.inc |= attvv.bit;

			}
		}

		itr = excs.entrySet().iterator();
		while(itr.hasNext()) {
			Entry<String, String[]> e = itr.next();
			String key = e.getKey();
			String[]v = e.getValue();
			RuleItem att = this.name_items.get(key);
			for(String vv : v) {
				RuleItem attvv = this.name_items.get(vv);
				att.exc |= attvv.bit;
			}
		}
	}


	public RuleItem getItemRule(String name) {
		return key_items.get(name);
	}

	public static void main(String[] args) throws IOException {

		SegRules holder = SegRules.instance;

		Iterator<Entry<String,SegRules.RuleItem>> itr = holder.key_items.entrySet().iterator();
		while(itr.hasNext()) {
			Entry<String,SegRules.RuleItem> ri = itr.next();
			System.out.println(ri.getKey() + "\t:\t" + ri.getValue());
		}
		RuleAttr attr = holder.attr;
		while(true) {
			System.out.println(attr);
			attr = attr.next;
			if(attr == null) {
				break;
			}
		}
	}


}
