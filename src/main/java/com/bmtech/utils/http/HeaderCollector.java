package com.bmtech.utils.http;

import java.net.URL;
import java.util.List;
import java.util.Map;

public interface HeaderCollector {
	public void write(URL url , Map<String, List<String>> header)throws Exception;
}
