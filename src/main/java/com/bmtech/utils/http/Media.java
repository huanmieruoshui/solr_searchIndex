package com.bmtech.utils.http;

import java.io.UnsupportedEncodingException;

import com.bmtech.utils.c2j.Bytable;
import com.bmtech.utils.c2j.ToBytes;

public class Media implements Bytable{
		public final byte[]head = new byte[] {
				0x00 ,0x03 ,0x00 ,0x00 ,0x00 ,0x01 , 0x00, 0x17 
		};
		public final byte[] head2 = "DataAccessor.getIndexes".getBytes();
		public final byte[] pend1 = {
				0x00, 0x02, 0x2f, 0x31, 0x00, 0x00, 0x00, 0x00};//total len of last
		public final byte[] pend2 = {
				0x0a, 0x00, 0x00, 0x00, 0x03, 0x02, 0x00, 0x00};//byte len of words
		public final byte[]words;
		byte[]tailDate = new byte[28];

		public Media(String str) throws UnsupportedEncodingException {
			words = str.getBytes("utf8");
			pend1[pend1.length -1] = (byte) (pend2.length + words.length + tailDate.length);
			pend2[pend2.length -1] = (byte) words.length;
			tailDate[0] = 0x02;
			tailDate[1] = 0x00;
			tailDate[2] = 0x01;
			tailDate[3] = 0x30;
			tailDate[4] = 0x02; 
			tailDate[5] = 0x00;
			tailDate[6] = 0x15;
			byte []datebs = "2009-12-28|2010-01-26".getBytes();
			System.arraycopy(datebs, 0, this.tailDate, 7, datebs.length);
		}
		@Override
		public void fromBytes(byte[] bss) {

		}

		@Override
		public int sizeOf() {
			return this.head.length + this.head2.length + 
			this.pend1.length + this.pend2.length + this.words.length +
			this.tailDate.length;
		}

		@Override
		public byte[] toBytes() {
			ToBytes tb = new ToBytes(sizeOf());
			tb.next(head);
			tb.next(head2);
			tb.next(pend1);
			tb.next(pend2);
			tb.next(words);
			tb.next(tailDate);
			return tb.next();
		}

	}