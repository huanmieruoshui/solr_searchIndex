package com.bmtech.utils.systemWatcher.innerAction;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import com.bmtech.utils.systemWatcher.WatcherAction;

public class EchoAction  extends WatcherAction{

	public EchoAction(String key) {
		super(Echo, key);
	}
	@Override
	public void run(final String[] paras, final Socket clientSocket) throws IOException {
		if(paras.length < 2) {
			this.writeBack( "?", clientSocket);
			return;
		}
		if(!paras[1].equals("!")) {
			this.writeBack(paras[1], clientSocket);
			return;
		}
		try {
			String cmdss[] = new String[paras.length -2];
			System.arraycopy(paras, 2, cmdss, 0, cmdss.length);

			ProcessBuilder pb = new ProcessBuilder(cmdss);
			Process p = pb.start();
			InputStream ips = p.getInputStream();
			ByteArrayOutputStream bios = new ByteArrayOutputStream();
			int c;
			while (true) {
				c= ips.read();
				if(c == -1)
					break;
				bios.write(c);
			}
			byte[] bs = bios.toByteArray();
			String ret = new String(bs, "gbk");
			writeBack(ret , clientSocket);
			p.waitFor();
		}catch(Exception e) {
			try {
				writeBack( e.toString() , clientSocket);
			} catch (IOException e1) {

			}
			e.printStackTrace();
		}
	}
}

