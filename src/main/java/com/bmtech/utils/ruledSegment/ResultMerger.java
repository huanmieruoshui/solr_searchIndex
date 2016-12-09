package com.bmtech.utils.ruledSegment;


public abstract class ResultMerger {
	
	ResultMerger(){
	}
	
	/**
	 * merge the SegFragment
	 */
	public abstract void mergeFragment(SegFragment frg);

	/**
	 * merge the linked list, return the merged linked-list head point<br>
	 * this method is called  after all segments are merged and befor the result is
	 * return
	 * @param header
	 * @return
	 */
	public abstract SegResult mergeResultList(SegResult header);
	
	protected SegResult mergeTwoPiont(SegResult segPoint) {
		StringBuilder sb = new StringBuilder();
		sb.append(segPoint.strValue);
		sb.append(segPoint.next.strValue);
		SegResult newSr = new SegResult(segPoint.pre, sb.toString(),
				segPoint.next.idf, segPoint.next.attr,
				segPoint.isCn, segPoint.isSymbol);

		newSr.setNext(segPoint.next.next);
		return newSr;

	}
}
