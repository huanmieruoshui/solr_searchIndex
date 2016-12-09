package com.bmtech.utils.ruledSegment.affix;
public class QueryInfo{
	public static final byte SCWS_WORD_FULL = 0x01; // ����: ����
	public static final byte SCWS_WORD_PART = 0x02; // ����: ǰ�ʶ�
	public static final byte SCWS_WORD_USED = 0x04; // ����: ��ʹ��
	public static final byte SCWS_WORD_RULE = 0x08; // ����: �Զ�ʶ���
	
	public static final byte SCWS_ZFLAG_UN = 0x00; // δ֪�ַ�
//	public static final byte SCWS_ZFLAG_PUT = 0x02; // ����: ��ʹ��
//	public static final byte SCWS_ZFLAG_N2 = 0x04; // ����: ˫������ͷ
	public static final byte SCWS_ZFLAG_NR2 = 0x20; // ����: ��ͷ��Ϊ˫������
	public static final byte SCWS_ZFLAG_WHEAD = 0x10; // ����: ��ͷ
//	public static final byte SCWS_ZFLAG_WPART = 0x20; // ����: ��β�����
	public static final byte SCWS_ZFLAG_ENGLISH = 0x40; // ����: �����м��Ӣ��
	public static final byte SCWS_ZFLAG_SYMBOL = (byte) 0x80 ;   // ����: ����ϵ��

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