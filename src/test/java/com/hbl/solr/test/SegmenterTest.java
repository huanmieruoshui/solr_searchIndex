package com.hbl.solr.test;

import java.util.Iterator;

import org.junit.Test;

import com.bmtech.utils.ruledSegment.DeepMerger;
import com.bmtech.utils.ruledSegment.RuledSegment;
import com.bmtech.utils.ruledSegment.SegResult;

public class SegmenterTest {

	@Test
	public void testSegmenter() {
		String kw = "安装完以后";
		RuledSegment rs = new RuledSegment(kw);
	    Iterator itr = rs.segment(new DeepMerger());
	    while (itr.hasNext()) {
	    	SegResult res = (SegResult)itr.next();
	    	if (!res.isSymbol) {
	    		System.out.println(res.strValue);
	    	}
	    }
	}
}
