package com.bmtech.utils.systemWatcher;

import java.io.IOException;

public abstract class BmtDemonService extends BmtDamonService{

	public BmtDemonService(int port, String enc, String sysName)
			throws IOException {
		super(port, enc, sysName);
	}

}
