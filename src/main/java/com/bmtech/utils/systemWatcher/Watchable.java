package com.bmtech.utils.systemWatcher;


public interface Watchable {
	
	public String run(String []paras) throws Exception;
	public String getCmd();
}