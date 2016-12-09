package com.bmtech.utils.iSerialize;

import java.io.InputStream;

public interface ISerialize {

	public ISerializer toSerialize();

	public void loadFromSerialize(InputStream ips);
}
