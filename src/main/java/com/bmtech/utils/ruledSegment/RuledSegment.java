package com.bmtech.utils.ruledSegment;

import java.util.ArrayList;
import java.util.Iterator;

import com.bmtech.utils.Consoler;
import com.bmtech.utils.ruledSegment.affix.Lexicon;
import com.bmtech.utils.ruledSegment.affix.SegRules;


public class RuledSegment{
	public static final int MAXCNMAT = 256;
	boolean debug = "true".equals(System.getProperty("_ruledSeg_debug_"));
	private int seq = 1;
	

	private ArrayList<SegFragment>fragments = new ArrayList<SegFragment>();
	private String txt;//查询的关键字

	public RuledSegment(String txt) {
		this.txt = txt;
	}
	public Iterator<SegResult> segment(ResultMerger merger) {
		segToFragment();
		for(SegFragment frg : fragments) {
			frg.segment();
		}
		if(merger != null) {

			for(SegFragment fg : fragments) {
				if(fg.header == null) {
					continue;
				}
				merger.mergeFragment(fg);
			}
		
		}
		SegResult header = null, tailer = null;
		for(SegFragment fg : fragments) {
			if(fg.header == null) {
				continue;
			}
			if(header == null) {
				header = fg.header;
				tailer = fg.tailer;
			}else {
				tailer.setNext(fg.header);
				tailer = fg.tailer;
			}
		}
		if(merger != null) {
			header = merger.mergeResultList(header);
		}
		
		final SegResult iheader = header;
		return new Iterator<SegResult>() {
			protected SegResult cursor = iheader;
			@Override
			public boolean hasNext() {
				if(cursor == null) {
					return false;
				}
				return true;
			}

			@Override
			public SegResult next() {
				SegResult ret = (cursor);
				cursor = cursor.next;
				return ret;
			}

			@Override
			public void remove() {
				if(hasNext()) {
					next();
				}
			}
		};
	}
	
	
	private synchronized int getNewFragId() {
		return seq ++;
	}
	private void segToFragment() {
		int wstart = 0; 
		boolean lastSymbol = true;
		final int end = txt.length();
		for(int x = 0; x < end; x ++) {
			char ch = txt.charAt(x);
			if(Character.isLetterOrDigit(ch)) {//确定指定字符是否为字母,字符或数字
				if(lastSymbol) {
					wstart = x;
				}
				lastSymbol = false;
			}else {
				if(!lastSymbol) {//put last value
					int wlen = x - wstart;
					segmentByCharRange(wstart, wlen);
				}
				SegFragment frg = new SegFragment(txt, x, 1, ch > 127, true,this.getNewFragId(), debug);
				this.fragments.add(frg);
				lastSymbol = true;
			}
		}

		if(!lastSymbol) {//put last value
			int wlen = end - wstart;
			segmentByCharRange(wstart, wlen);
		}
	}

	
	private void segmentByCharRange(final int xstart, int xlen) {

		int wstart = xstart;
		boolean lastCn = false;
		final int end = xlen + xstart;

		int x = xstart;
		int cnCnt = 0;
		for(; x < end; x ++) {
			char ch = txt.charAt(x);
			if(ch > 128) {//判断是否为特殊符号和字母，128及一下为特殊符号和字母
				if(!lastCn) {
					if(x > xstart) {//如果不是第一个字符
						SegFragment frg = new SegFragment(txt, wstart, x - wstart, lastCn,false, this.getNewFragId(), debug);
						this.fragments.add(frg);
					}
					wstart = x;
				}
				lastCn = true;
				cnCnt ++;
				if(cnCnt > MAXCNMAT) {//如果txt的连续字符大于256
					SegFragment frg = new SegFragment(txt, wstart, x - wstart, lastCn, false,this.getNewFragId(), debug);
					this.fragments.add(frg);
					wstart = x;
					cnCnt = 1;
				}
			}else {
				cnCnt = 0;
				if(lastCn) {
					SegFragment frg = new SegFragment(txt, wstart, x - wstart, lastCn, false,this.getNewFragId(), debug);
					this.fragments.add(frg);
					wstart = x;
				}
				lastCn = false;
			}
		}

		//check last block
		if(x > xstart) {
			SegFragment frg = new SegFragment(txt, wstart, x - wstart, lastCn, false,
					this.getNewFragId(), debug);
			this.fragments.add(frg);
		}
	}

	public static Iterator<SegResult> segment(String txtStr, ResultMerger merger) {
		String txt;
		if(txtStr == null) {
			txt = "";
		}else {
			txt = txtStr;
		}
		RuledSegment agt = new RuledSegment(txt);
		return agt.segment(merger);
	}
	

	public static void main(String[] args) {
		if(SegRules.instance == null || Lexicon.instance == null) {
			throw new RuntimeException("Error! load rules and lexicon");
		}

		String str = "电 梯";
		System.setProperty("_ruledSeg_debug_", "true");
		while(true) {
			RuledSegment rs = new RuledSegment(str);
			
			Iterator<SegResult> itr = rs.segment(new DeepMerger());
			System.out.println("----------------------------");
			boolean needAppendSpl = false;
			StringBuilder sb = new StringBuilder();
			while(itr.hasNext()) {
				SegResult res = itr.next();
				if(needAppendSpl) {
					sb.append("/");
				}
				if(res.isSymbol) {
					needAppendSpl = false;
				}else {
					sb.append(res.strValue.replace(" ", "*"));
					needAppendSpl = true;
				}
			}
			System.out.println(sb);
			str = Consoler.readString("\nnew to seg：");
		}
	}


}