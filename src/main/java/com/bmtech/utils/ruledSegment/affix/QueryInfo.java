package com.bmtech.utils.ruledSegment.affix;
public class QueryInfo{
	public static final byte SCWS_WORD_FULL = 0x01; // 多字: 整词
	public static final byte SCWS_WORD_PART = 0x02; // 多字: 前词段
	public static final byte SCWS_WORD_USED = 0x04; // 多字: 已使用
	public static final byte SCWS_WORD_RULE = 0x08; // 多字: 自动识别的
	
	public static final byte SCWS_ZFLAG_UN = 0x00; // 未知字符
//	public static final byte SCWS_ZFLAG_PUT = 0x02; // 单字: 已使用
//	public static final byte SCWS_ZFLAG_N2 = 0x04; // 单字: 双字名词头
	public static final byte SCWS_ZFLAG_NR2 = 0x20; // 单字: 词头且为双字人名
	public static final byte SCWS_ZFLAG_WHEAD = 0x10; // 单字: 词头
//	public static final byte SCWS_ZFLAG_WPART = 0x20; // 单字: 词尾或词中
	public static final byte SCWS_ZFLAG_ENGLISH = 0x40; // 单字: 夹在中间的英文
	public static final byte SCWS_ZFLAG_SYMBOL = (byte) 0x80 ;   // 单字: 符号系列

	public static final byte SCWS_ZFLAG_WPART = SCWS_WORD_PART;
	public final String word;
	public final float tf;
	public final float idf;
	protected byte flag = 0;
	public final String attr ;
	public QueryInfo(String word, float tf, float idf, byte flag, String attr) {
		this.word = word;
		this.tf = tf;
		this.idf = idf;
		this.flag = flag;
		this.attr = attr;
	}
	
	public QueryInfo cloneMe(byte setFlag) {
		return new QueryInfo(word, tf, idf, setFlag, attr);
	}
	public QueryInfo cloneMe() {
		return cloneMe(flag);
	}

	public void setWordFull() {
		flag |= SCWS_WORD_FULL;
	}
	
	public boolean isWordFull() {
		return (flag & SCWS_WORD_FULL) != 0;
	}
	
	public void setWordPART() {
		flag |= SCWS_WORD_PART;
	}
	
	public boolean isWordPART() {
		return (flag & SCWS_WORD_PART) != 0;
	}
	
	public void setWordUsed() {
		flag |= SCWS_WORD_USED;
	}
	
	public boolean isWordUsed() {
		return (flag & SCWS_WORD_USED) != 0;
	}
	
	public void setWordRule() {
		flag |= SCWS_WORD_RULE;
	}
	
	public boolean isWordRule() {
		return (flag & SCWS_WORD_RULE) != 0;
	}

	public void clearWordPart() {
		byte b = ~SCWS_WORD_PART;
		flag &= b;
//		flag ^= SCWS_WORD_PART;
	}
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		sb.append("'");
		sb.append(word);
		sb.append("'");
		sb.append(" ,tf:");
		sb.append(tf);

		sb.append(" ,idf:");
		sb.append(idf);
		
		sb.append(" ,flag:");
		sb.append(Integer.toBinaryString(0xff&flag));


		sb.append(" ,attr:");
		sb.append(attr);

		return sb.toString();

	}
}