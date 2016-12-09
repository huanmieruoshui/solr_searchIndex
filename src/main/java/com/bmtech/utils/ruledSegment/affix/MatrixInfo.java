package com.bmtech.utils.ruledSegment.affix;


public final class MatrixInfo extends QueryInfo{
	public static final byte RuledFlag = SCWS_WORD_RULE | SCWS_WORD_FULL;

	public final boolean noRule;
	public MatrixInfo(String word, float tf, float idf, byte flag, String attr) {
		super(word, tf, idf, flag, attr);
		noRule = 
			isSYMBOLFlag() ||
			isENGLISHFlag() ||
			(isWHEADFlag() && ! isNr2Flag());
	}
	public MatrixInfo(QueryInfo wi, byte flag) {
		this(wi.word, wi.tf, wi.idf, flag, wi.attr);
	}
	public MatrixInfo(QueryInfo wi) {
		this(wi, wi.flag);
	}

	public void setUNFlag() {
		flag |= SCWS_ZFLAG_UN;
	}
	public void setNr2Flag() {
		flag |= SCWS_ZFLAG_NR2;
	}
	public void setWHEADFlag() {
		flag |= SCWS_ZFLAG_WHEAD;
	}
	public void setWPARTFlag() {
		flag |= SCWS_ZFLAG_WPART;
	}
	public void setENGLISHFlag() {
		flag |= SCWS_ZFLAG_ENGLISH;
	}
	public void setSYMBOLFlag() {
		flag |= SCWS_ZFLAG_SYMBOL;
	}

	public boolean isUNFlag() {
		return (flag & SCWS_ZFLAG_UN) != 0;
	}
	public boolean isNr2Flag() {
		return (flag & SCWS_ZFLAG_NR2) != 0;
	}
	public boolean isWHEADFlag() {
		return (flag & SCWS_ZFLAG_WHEAD) != 0;
	}
	public boolean isWPARTFlag() {
		return (flag & SCWS_ZFLAG_WPART) != 0;
	}
	public boolean isENGLISHFlag() {
		return (flag & SCWS_ZFLAG_ENGLISH) != 0;
	}
	public boolean isSYMBOLFlag() {
		return (flag & SCWS_ZFLAG_SYMBOL) != 0;
	}
}