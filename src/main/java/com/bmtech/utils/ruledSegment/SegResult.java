package com.bmtech.utils.ruledSegment;
public class SegResult{
	public final float idf;
	public final String attr;
	public final boolean isCn;
	public final boolean isSymbol;
	SegResult next = null;
	SegResult pre;
	public final String strValue;
	public SegResult(SegResult pre ,int off, float idf, int len, String attr,
			boolean isCn, boolean isSymbol, String txt) {
		this(pre, txt.substring(off, off + len), idf, attr, isCn, isSymbol);
	}
	public SegResult(SegResult pre ,String str, float idf, String attr,
			boolean isCn, boolean isSymbol) {
		this.idf = idf;
		this.attr = attr;
		this.isCn = isCn;
		this.isSymbol = isSymbol;
		this.strValue = str;
		this.setPre(pre);
	}
	public void setNext(SegResult next){
		this.next = next;
		if(next != null) {
			next.pre = this;
		}
	}
	public void setPre(SegResult pre) {
		this.pre = pre;
		if(pre != null) {
			pre.next = this;
		}
	}
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(strValue);
		sb.append("	");
		sb.append("attr:");
		sb.append(attr);

		sb.append("	");
		sb.append("idf:");
		sb.append(idf);
		if(next != null) {
			sb.append("\n");
			sb.append(next);
		}

		return sb.toString();
	}
}