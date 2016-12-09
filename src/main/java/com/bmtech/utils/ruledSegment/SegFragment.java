package com.bmtech.utils.ruledSegment;

import java.util.ArrayList;

import com.bmtech.utils.ruledSegment.affix.Lexicon;
import com.bmtech.utils.ruledSegment.affix.MatrixInfo;
import com.bmtech.utils.ruledSegment.affix.QueryInfo;
import com.bmtech.utils.ruledSegment.affix.SegRules;
import com.bmtech.utils.ruledSegment.affix.SegRules.RuleItem;

class SegFragment{
	public static final float SYMBOLEIDF = 0.0f;
	public static final String attr_en = "en";
	public static final String attr_un = "un";
	public static final String attr_nr = "nr";
	public static final String attr_na = "!";
	public static final String attr_w = "w";
	
	
	public final int fragId;
	private final int txtLen;
	private final int txtOff;
	public final boolean isCn;
	public final boolean isSymbol;
	SegResult header;
	SegResult tailer;
	private MatrixInfo wmap[][];
	final String txt;
	final boolean debug;
	public String toString() {
		return txt.substring(txtOff, txtOff + txtLen);
	}
	public SegFragment(String txt, int off, int len, boolean isCn, boolean isSymbol,
			int frgId, boolean debug) {
		this.txtOff = off;
		this.txtLen = len;
		this.isCn = isCn;
		this.isSymbol = isSymbol;
		this.fragId = frgId;
		this.txt = txt;
		this.debug = debug;
	}

	public void segment() {
		if(isCn) {
			this.cnSegment();
		}else {
			putResult(txtOff,(float)(2.5f*Math.log(txtLen)), txtLen, attr_en);
		}
	}

	private void putResult(int off, float idf, int len, String attr) {
		SegResult result = new SegResult(tailer, off, idf, len, attr, 
				isCn, isSymbol, txt);
		result.setPre(tailer);
		tailer = result;
		if (header == null){
			header = result;
		}
	}
	private void cnSegment() {
		wmap = new MatrixInfo[txtLen][txtLen];
		Lexicon lex = Lexicon.instance;
		for(int idx = 0; idx < txtLen; idx++) {
			QueryInfo wi = lex.get(txt.substring(idx + txtOff, idx + txtOff + 1));
			if(wi == null) {
				wmap[idx][idx]  = new MatrixInfo(
						txt.substring(idx + txtOff, idx + txtOff + 1), 0.5f, 0f, (byte) (MatrixInfo.SCWS_ZFLAG_UN | MatrixInfo.SCWS_WORD_FULL) , attr_un);
			}else {
				wmap[idx][idx]  = new MatrixInfo(wi, MatrixInfo.SCWS_WORD_FULL);
				if(wi.attr.equals("#")) {//TODO optimize using ENUM
					wmap[idx][idx].setSYMBOLFlag();
				}
			}
		}

		/* create word query table */
		for (int i = 0; i < txtLen; i++){
			int k = 0, j;
			for (j = i+1; j < txtLen; j++){
				QueryInfo query = lex.get(txt.substring(txtOff + i, txtOff + j + 1));
				if (query == null)
					break;
				if ((query.isWordFull()) && !query.attr.equals(attr_na)){
					wmap[i][j] = new MatrixInfo(query);
					wmap[i][i].setWHEADFlag();
					for (k = i+1; k <= j; k ++) {
						wmap[k][k].setWPARTFlag();
					}
				}

				if (!query.isWordPART())
					break;		
			}
			/* set nr2 to some short name */
			if (k-- > 0){
				if ((k == (i+1))){
					if (wmap[i][k].attr.equals(attr_nr)) {
						wmap[i][i].setNr2Flag();
					}
				}				

				if (k < j) {/* clean the PART flag for the last word */
					wmap[i][k].clearWordPart();
				}
			}
		}

		/* real do the segment */
		TokenLink tl = LinkTokens();
		while(true) {
			setWord(tl.idx, tl.getToValue());
			tl = tl.getNext();
			if(tl == null) {
				break;
			}
		}
	}
	private void setWord(int i, int j){
		QueryInfo item = wmap[i][j];
		if(item == null){
			return;
		}
		putResult(i + txtOff, item.idf, j - i + 1, item.attr);
	}	

	public class BetterWayFinder{
		SegRules rules = SegRules.instance;


		TokenLink headerToken = null;
		private TokenLink betterWay = null;
		double betterValue = 0.0f;

		final int maxIdx ;
		final int from, to;


		public BetterWayFinder(int from, int to) {
			this.from = from;
			this.to = to;
			maxIdx = to;//
			this.headerToken = new TokenLink(from, null);
		}
		void find(final TokenLink link) {

			for(int nowIdx = maxIdx; nowIdx >= link.idx; nowIdx --) {
				if(wmap[link.idx][nowIdx] == null)
					continue;
				if(!wmap[link.idx][nowIdx].isWordFull()) {
					continue;
				}
				//find a road
				link.setTo(nowIdx);
				if(nowIdx == maxIdx) {
					makeLink();
					link.next = null;
				}else {//recursive to find next
					TokenLink newLink = new TokenLink(nowIdx + 1, link);
					link.next = newLink;
					newLink.pre = link;
					find(newLink);
				}
			}
		}

		double makeLink() {
			double ret = 1.0f;
			TokenLink theader = headerToken.copy();
			TokenLink tmp = theader;
			int cnt = 0;
			while(true) {
				cnt ++;
				if(tmp.idx == tmp.to) {
					cnt ++;
				}
				if(tmp.next == null) {
					ret *= tmp.score * tmp.weight;
					break;
				}
				tmp.next = tmp.next.copy();
				tmp.next.pre = tmp;

				//get weight1
				tmp.score = 1;
				//					tmp.score = rules.attrRatio(wmap[tmp.idx][tmp.to], wmap[tmp.next.idx][tmp.next.to]); 

				ret *= tmp.weight* tmp.score;
				tmp = tmp.next;
			}
			if(cnt  > 1) {
				ret = ret / Math.pow(cnt - 1, 5);
			}
			boolean swap = false;
			if(betterWay == null) {
				swap = true;
			}else {
				if(ret > this.betterValue ) {
					swap = true;
				}
			}
			if(swap) {
				this.betterValue = ret;
				this.betterWay = theader;
			}
			if(debug) {
				String vh ;
				if(swap) {
					vh = String.format("---- %d:%.3f\t%s" , cnt, ret, theader);
				}else {
					vh = String.format("     %d:%.3f\t%s" , cnt, ret, theader);
				}
				System.out.println(vh);
			}
			return ret;
		}

		public TokenLink getBetterWay() {
			find(this.headerToken);
			return betterWay;
		}
	}
	public class TokenLink{
		public TokenLink pre;
		private TokenLink next;
		public final int idx;
		private int to;
		double weight;//tf * lengthNorm
		int score = 1;//score(myAttr, nextAttr)
		TokenLink(int idx, TokenLink pre){
			this.pre = pre;
			this.idx = idx;
		}
		/**
		 * before this method called, the link is not finished yet
		 * @param to
		 * @return
		 */
		double setTo(int to) {
			return setTo(to, wmap[idx][to].tf);
		}
		double setTo(int to, double tf) { 
			this.to = to;

			this.weight =  tf;
			if(to > idx) {
				this.weight *= Math.pow(to - idx, 4);
			}
			return this.weight;
		}
		public TokenLink copy() {
			TokenLink cp = new TokenLink(idx, this.pre);
			cp.to = to;
			cp.next = next;
			cp.weight = weight;
			return cp;
		}

		public String strValue() {
			return txt.substring(txtOff + idx, txtOff + to + 1);
		}
		public String toString(boolean appendLasts) {

			StringBuilder sb = new StringBuilder();
			TokenLink tmp = this;
			while(true) {
				sb.append(wmap[tmp.idx][tmp.to].word);
				sb.append('/');
				sb.append(wmap[tmp.idx][tmp.to].attr);
				String str = String.format(" %02.2f:%d", tmp.weight, tmp.score);
				sb.append(str);
				sb.append(' ');
				tmp = tmp.next;
				if(tmp == null || !appendLasts) {
					break;
				}
			}
			return sb.toString();

		}
		public String toString() {
			return toString(true);
		}

		public TokenLink getNext() {
			return this.next;
		}

		public int getToValue() {
			return this.to;
		}
		public boolean hasNoRule() {
			if(to != idx) {
				return true;
			}
			if(wmap[idx][idx].noRule) {
				return true;
			}
			return false;
		}
	}

	public TokenLink LinkTokens() {
		int idx = 0, nowEnd = 0;
		int minPart = 16;
		ArrayList<TokenLink>links = new ArrayList<TokenLink>();
		for (; idx < wmap.length; idx++){
			if (wmap[idx][idx].isWPARTFlag()) {
				continue;
			}

			if (idx > nowEnd && (idx - nowEnd) > minPart) {
				BetterWayFinder gf = 
					new BetterWayFinder(nowEnd, idx - 1);
				TokenLink tl = gf.getBetterWay();
				links.add(tl);

			}else {
				continue;
			}

			nowEnd = idx;
			if (!wmap[idx][idx].isWHEADFlag()){
				TokenLink tl = new TokenLink(nowEnd, null);
				tl.setTo(nowEnd);
				links.add(tl);
				nowEnd++;
			}
		}

		/* the lastest zone */
		if (idx > nowEnd) {
			BetterWayFinder gf = 
				new BetterWayFinder(nowEnd, idx - 1);
			TokenLink tl = gf.getBetterWay();
			links.add(tl);
		}
		TokenLink header = null;
		TokenLink tail = null;
		for(TokenLink tl : links) {
			if(header == null) {
				header = tl;

			}else {
				tail.next = tl;
				tl.pre = tail;
			}
			tail = tl;
			//move tail to tail
			while(tail.next != null)
				tail = tail.next;
		}
		mergeRoundOne(header);
		mergeRoundTwo(header);
		return header;
	}
	/**
	 * from + 2 <= to
	 * @param from
	 * @param to
	 * @return
	 */
	RuleItem ruleMatch(TokenLink now , TokenLink next, final int ruleLen) {
		final int from = now.idx + txtOff; 
		final int len = next.to - now.idx + 1;
		final int to = from + len;
		final int toMatchLen = len - ruleLen;
		if(toMatchLen < 1) {
			return null;
		}
		//check prefix

		String strV = txt.substring(from,  from + ruleLen);
		RuleItem ri = rules.getItemRule(strV);
		if(ri != null && ri.isPrefix()) {

			final int min = ri.zmin > 0 ? ri.zmin : 1;

			boolean canCheck = false;
			if(min <= toMatchLen ) {
				if(ri.zmax > 0) {
					if(ri.zmax >= toMatchLen){
						canCheck = true;
					}
				}else {
					canCheck = true;
				}
			}
			if(canCheck) {

				int ch;
				int matchedLen = 0;
				for (ch = 0; ch < toMatchLen; ch ++){
					if(!ri.matches(txt.substring(from + ruleLen + ch , from + ruleLen + ch + 1)))
						break;
					matchedLen ++;
				}
				if(matchedLen == toMatchLen) {
					if(worthSwitch(now, next, ri))
						return ri;
				}
			}
		}
		//check suffix
		if(next.idx != next.to)
			return null;
		strV = txt.substring(to - ruleLen, to);
		ri = rules.getItemRule(strV);
		if(ri != null && ri.isSuffix()) {

			final int min = ri.zmin > 0 ? ri.zmin : 1;
			boolean canCheck = false;
			if(min <= toMatchLen ) {
				if(ri.zmax > 0) {
					if(ri.zmax >= toMatchLen){
						canCheck = true;
					}
				}else {
					canCheck = true;
				}
			}
			if(canCheck) {
				int ch;
				int matchedLen = 0;
				for (ch = 0; ch < toMatchLen; ch ++){
					if(!ri.matches(txt.substring(from + ch ,from + ch + 1)))
						break;
					matchedLen ++;
				}
				if(matchedLen == toMatchLen) {
					if(worthSwitch(now, next, ri))
						return ri;
				}
			}
		}

		return null;
	}
	boolean worthSwitch(TokenLink t1, TokenLink t2, RuleItem ri) {
		double oldWeight = 1;
		double newWeight = 1;
		if(ri.attr.equals("nr")) {
			if(t1.to > t1.idx && wmap[t1.idx][t1.to].attr.equals("nr")
					||(wmap[t2.idx][t2.to].attr.equals("nr"))){
				//					return false;
			}else {
				return false;
			}
		}
		oldWeight *= wmap[t1.idx][t1.to].tf * wmap[t2.idx][t2.to].tf;
		newWeight *= ri.tf*ri.tf;
		return newWeight >= oldWeight ;
	}
	SegRules rules = SegRules.instance;
	void mergeRoundTwo(TokenLink header) {
		TokenLink now = header;
		while(now != null) {
			TokenLink next = now.next;
			if(next == null) {
				break;
			}
			int len = next.to - now.idx + 1;
			if( len == 3 || len == 4) {
				//three or  words
				RuleItem ri = ruleMatch(now, next, 1);
				if(ri == null) {
					ri = ruleMatch(now, next, 2);
				}
				if(ri != null) {
					merge(now, next, ri);
				}
			}
			now = now.next;
		}

	}
	void mergeRoundOne(TokenLink header) {
		SegRules rules = SegRules.instance;

		TokenLink tail = header;
		TokenLink now = header;
		while(now != null) {
			tail = now;
			if(now.next == null) {
				break;
			}
			RuleItem ri = rules.getItemRule(now.strValue());
			if (ri != null) {
				int clen = ri.zmin > 0 ? ri.zmin : 1;
				if (ri.isPrefix()){	
					TokenLink tmp = now.next, tmpOk = null;
					int ch;
					for (ch = 1; ch <= clen; ch ++){
						if (tmp == null || tmp.hasNoRule())
							break;
						if(!ri.matches(tmp.strValue()))
							break;
						tmpOk = tmp;
						tmp = tmp.next;
					}

					if (ch > clen) {//min match!
						while (tmp != null){

							if ((ri.zmax == 0 && ri.zmin > 0) || (ri.zmax > 0 && (clen >= ri.zmax))) 
								break;

							if (tmp.hasNoRule()) 
								break;

							if(!ri.matches(tmp.strValue())) {
								break;
							}
							tmpOk = tmp;
							clen++;
							tmp = tmp.next;
						}
						merge(now, tmpOk, ri);
					}
				}
			}
			now = now.next;
		}

		//checkSuffix
		now = tail;
		while(now != null) {
			tail = now;
			if(now.pre == null) {
				break;
			}
			RuleItem ri = rules.getItemRule(now.strValue());
			if (ri != null) {
				int clen = ri.zmin > 0 ? ri.zmin : 1;
				if (ri.isSuffix()){
					TokenLink tmp = now.pre, tmpOk = null;
					int ch;
					for (ch = 1; ch <= clen; ch ++){

						if (tmp == null || tmp.hasNoRule())
							break;
						if(!ri.matches(tmp.strValue()))
							break;
						tmpOk = tmp;
						tmp = tmp.pre;
					}

					if (ch > clen) {//min match!
						while (tmp != null){

							if ((ri.zmax == 0 && ri.zmin > 0) || (ri.zmax > 0 && (clen >= ri.zmax))) 
								break;

							if (tmp.hasNoRule()) 
								break;

							if(!ri.matches(tmp.strValue())) {
								break;
							}
							tmpOk = tmp;
							clen++;
							tmp = tmp.pre;
						}

						//ok try merge
						merge(tmpOk, now , ri);
						now = tmpOk;
					}
				}
			}
			now = now.pre;
		}
	}
	private void merge(TokenLink from, TokenLink to, 
			RuleItem ri) {
		if(debug) {
			boolean may = from.next == to;
			System.out.println(String.format("Merge '%s' +%s'%s' £¨ %s£©", 
					from.toString(false), may?" ":" ... + ", to.toString(false), ri));
		}
		byte flag = 
			MatrixInfo.SCWS_WORD_RULE | MatrixInfo.SCWS_WORD_FULL;
		wmap[from.idx][to.to] = new MatrixInfo(
				txt.substring(from.idx, to.to + 1),
				ri.tf, ri.idf, flag, ri.attr);

		wmap[from.idx][to.to].setWHEADFlag();
		from.setTo(to.to);
		from.next = to.next;
		if(from.next != null) {
			from.next.pre = from;
		}
		if(from.pre != null) {
			from.pre.next = from;
		}
	}
}