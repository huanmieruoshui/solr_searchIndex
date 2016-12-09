package com.bmtech.utils.segment;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

import com.bmtech.utils.Charsets;
import com.bmtech.utils.io.LineReader;
import com.bmtech.utils.log.BmtLogger;
import com.bmtech.utils.log.LogLevel;

public class SimpleFMM {
	public class SegHandler implements Iterator<String>{
		private final String input;
		private final boolean inLexicon;
		private int crtCursor = 0;
		private String buffered ;
		private int strlen ;
		private boolean finished = false;
		SegHandler(String input, boolean inLexicon){
			this.input = input == null? "" : input;
			this.inLexicon = inLexicon;
			this.strlen = input.length();
		}
		private boolean readNext() {
			//			buffered = null;
			if(crtCursor >= strlen) {
				finished = true;
				return false;
			}
			int maxCursor = strlen - crtCursor;
			if(maxCursor > maxLen) {
				maxCursor = maxLen;
			}
			boolean find = false;
			for(int i = maxCursor; i > 1; i --) {
				//1 or less should not be checked in lexicon
				String sub = input.substring(crtCursor, crtCursor + i);
				if(set.contains(sub)) {
					find = true;
					buffered = sub;
					crtCursor = crtCursor + i;
					break;
				}
			}
			if(find) {
				return true;
			}else {
				if(this.inLexicon) {
					crtCursor = crtCursor + 1;
					return false;
				}else {
					buffered = input.substring(
							crtCursor , crtCursor + 1);
					crtCursor = crtCursor + 1;
					return true;
				}
			}
		}
		@Override
		public boolean hasNext() {
			while(true) {
				if(finished) {
					return false;
				}
				if(readNext()) {
					return true;
				}
			}
		}
		@Override
		public String next() {
			return buffered;
		}
		@Override
		public void remove() {
			throw new RuntimeException("unsupport method #remove()");

		}

	}
	HashSet<String>set = new HashSet<String>();
	final int maxLen ;
	/**
	 * lexicon file must be UTF-8 format 
	 * @param lexionFile
	 * @param maxLen
	 * @throws IOException 
	 */
	public SimpleFMM(File lexiconFile, int maxLen) throws IOException {
		LineReader lr = new LineReader(lexiconFile, Charsets.UTF8_CS) ;
		BmtLogger.instance().log(LogLevel.Debug, "loading lexicon %s", lexiconFile);
		while(lr.hasNext()) {
			String line = lr.next();
			line = line.trim();
			if(line.startsWith("#")) {
				continue;
			}
			if(line.length() > maxLen) {
				BmtLogger.instance().log(LogLevel.Debug, 
						"skip too long word %s", line);
				continue;
			}
			this.set.add(line.intern());
		}	
		BmtLogger.instance().log(LogLevel.Debug, "loaded lexicon, accept %s word from %s",
				set.size(), lexiconFile);
		this.maxLen = maxLen;
	}
	public SegHandler segment(String input) {
		return segment(input, false);
	}
	/**
	 * 
	 * @param input
	 * @param inlexicon if true, only the word in lexicon will be returned
	 * @return
	 */

	public SegHandler segment(String input, boolean inlexicon) {
		return new SegHandler(input, inlexicon);
	}

}
