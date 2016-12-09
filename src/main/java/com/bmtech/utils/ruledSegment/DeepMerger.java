package com.bmtech.utils.ruledSegment;

import com.bmtech.utils.ruledSegment.affix.SegRules;

public class DeepMerger extends ResultMerger{
	protected int maxMergeSize = 4;
	boolean canMerge(SegResult seg) {
		if(seg.isSymbol || !seg.isCn) {
			return false;
		}
		if(seg.attr.equals("nr") || seg.attr.equals("ns")) {
			return false;
		}
		if(seg.strValue.length() >= maxMergeSize ) {
			return false;
		}
		return true;
	}
	boolean canMergeBetweenSymbol(SegResult seg) {
		if(seg.isSymbol) {
			return false;
		}
		if(seg.attr.equals("nr") || seg.attr.equals("ns")) {
			return false;
		}
		if(seg.strValue.length() >= maxMergeSize ) {
			return false;
		}
		return true;
	}

	SegResult inSegmentMerge(SegResult header) {
		int minMergeScore = 2;
		//first merge: merge until can not merge
		//find good point to merge : 
		// -->if two tokens are both len-less-than-maxMergeSize,
		// -->both of them are not ns nor nr 
		// -->caculated value are non-less-than zero
		// -->if there is mergence in this round, continue merge, else break 
		boolean hasMerge = true;
		int totalLen = 0;
		while(hasMerge) {
			hasMerge = false;// set to false, if there is mergence, open it
			SegResult cursor = header;
			int lastRate = minMergeScore;
			SegResult segPoint = null;
			int totLen = 0;
			while(cursor != null) {
				if(!cursor.isSymbol) {
					totLen += cursor.strValue.length();
				}
				if(canMerge(cursor)) {
					SegResult next = cursor.next;
					if(next == null)
						break;
					if(canMerge(next)){
						int rate = SegRules.instance.attrRatio(cursor, next);
						if(rate > lastRate) {
							segPoint = cursor;
						}
					}
				}
				cursor = cursor.next;
			}
			if(segPoint != null) {
				hasMerge = true;
				SegResult segr = mergeTwoPiont(segPoint);
				if(segPoint == header) {
					header = segr;
				}	
				cursor = segr;
			}
			totalLen = totLen;
		}
		//last check! 

		if(!header.isSymbol && totalLen > 0 && totalLen <= maxMergeSize
				&& header.next != null){//one token
			//remove symobl and 
			SegResult cursor = header;
			StringBuilder sb = new StringBuilder();
			SegResult sr = null;
			while(cursor != null) {
				sb.append(cursor.strValue);
				sr = cursor;
				cursor = cursor.next;
			}
			String sv = sb.toString().trim();
			header = new SegResult(null, sv,
					1.0f, sr.attr,
					sr.isCn, sr.isSymbol); 
		}
		return header;
	}

	@Override
	public void mergeFragment(SegFragment fg) {
		if(fg.header == null) {
			return;
		}
		fg.header = inSegmentMerge(fg.header);
		SegResult tailer = fg.header;
		while(true) {
			if(null != tailer.next) {
				tailer = tailer.next;
			}else {
				break;
			}
		}
		fg.tailer = tailer;
	}
	@Override
	public SegResult mergeResultList(SegResult header) {
		header = mergeEnglish(header);
		SegResult cursor = header;
		while(cursor != null) {
			if(cursor.isSymbol) {
				cursor = cursor.next;
				continue;
			}
			if(cursor.next == null) {
				break;
			}
			//find a may-merge piont
			SegResult sr = this.mergeBetweenSymbol(cursor);
			if(cursor == header) {
				header = sr;
			}
			cursor = sr;
			//move to next symbol
			while(cursor != null) {
				if(cursor.isSymbol) {
					break;
				}
				cursor = cursor.next;
			}
		}
		return header;
	}


	private SegResult mergeEnglish(SegResult header) {
		SegResult sr = header;
		while(sr != null) {
			if(!sr.isCn && !sr.isSymbol) {
				SegResult next = sr.next;
				StringBuilder sb = new StringBuilder();
				sb.append(sr.strValue);
				boolean needInLink = false;
				boolean needSymbol = true;
				while(next != null) {
					if(next.isCn && !next.isSymbol){
						break;
					}else {
						if(next.strValue.equals(" ")) {
							break;
						}else { 
							if(needSymbol) {
								sb.append(' ');
							}
							if(next.isSymbol){
								needSymbol = false;
							}else {
								sb.append(next.strValue);
								needInLink = true;
								needSymbol = true;
							}
							next = next.next;
						}
					}
				}
				if(needInLink) {
					SegResult newRs = new SegResult(sr.pre ,sb.toString().trim(), 
							sr.idf, sr.attr, false, false);
					newRs.setNext(next);
					if(sr == header) {
						header = newRs;
					}
					sr = newRs;
				}
			}
			sr = sr.next;
		}
		return header;
	}
	
	protected SegResult mergeBetweenSymbol(final SegResult from) {
		SegResult cursor = from;
		StringBuilder sb = new StringBuilder();
		boolean canMerge = true;
		while(cursor != null) {
			if(cursor.isSymbol) {
				break;
			}else if(this.canMergeBetweenSymbol(cursor)) {
				if(sb.length() + cursor.strValue.length() > this.maxMergeSize) {
					canMerge = false;
					break;
				}else {
					sb.append(cursor.strValue);
					cursor = cursor.next;
				}
			}else {
				canMerge = false;
				break;
			}
		}
		if(canMerge) {
			String sv = sb.toString().trim();
			SegResult ret = new SegResult(null, sv,
					1.0f, from.attr,
					from.isCn, false);
			ret.setNext(cursor);
			return ret;
		}else {
			return from;
		}

	}
}
