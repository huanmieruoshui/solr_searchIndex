package com.bmtech.utils.segment;

import java.util.HashMap;
public class CnSegment extends Segment{
	private static CnSegment reversed ;
	private static CnSegment noReversed;
	HashMap<String, Short> map;

	private  boolean reverse;
	private CnSegment(boolean reverse) {
		this.reverse = reverse;
		map = new HashMap<String, Short>();
		load("config/segment/lexicon", map, reverse);
	}
	/**
	 * get a instance of of this class
	 * @return
	 */
	public synchronized static  CnSegment instance(boolean reverse) {
		if(reverse) {
			if(reversed ==null) {
				reversed = new CnSegment(true);
			}
			return reversed;
		}else {
			if(noReversed ==null) {
				noReversed = new CnSegment(false);
			}
			return  noReversed;
		}
	}

	/**
	 * segment the input source.
	 * the segment is like:
	 * %我们你还好吗？亲爱的 my darling.gei you a mp3, please come room356_1 at 1 pm, you have a dollor such as 12563.2$
	 * the result is:
	 *  我们 你 还好吗 亲爱 的 my darling gei you a mp3 please come room356 1 at 1 pm you have a dollor such as 12563 2
	 *  for null, empty string will returned
	 */
	public TokenHandler segment(String source){
		return Segment.segment(source, map, reverse);
	}

}