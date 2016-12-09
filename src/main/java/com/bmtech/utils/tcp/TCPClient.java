package com.bmtech.utils.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.bmtech.utils.c2j.Bytable;
import com.bmtech.utils.c2j.Struct;
import com.bmtech.utils.c2j.cTypes.U8;
import com.bmtech.utils.log.BmtLogger;
/**
 * this is a TCP client 
 * @author liying1
 *
 */

public class TCPClient {
	protected Socket skt;
	InputStream ips;
	OutputStream ops;
	private InetSocketAddress sa;
	int connectTimeout=10000;
	String remoteIp;
	public TCPClient(String ip,int port) throws IOException{
		this(ip,port,10000);
	}
	public TCPClient(char[] ip, int port) throws IOException {
		this(new String(ip).trim(),port);
	}


	public TCPClient(String ip, int port, int connectTimeout) throws IOException {
		this(ip,port,connectTimeout,null);
	}
	public TCPClient(String ip, int port, int connectTimeout,InetSocketAddress local) throws IOException {
		this.remoteIp=ip;
		this.connectTimeout=connectTimeout;
		sa=new InetSocketAddress(InetAddress.getByName(ip), port);

		skt = new Socket();
		//skt.setReuseAddress(true);
		if(local!=null){
			skt.bind(local);
		}
		skt.connect(sa, connectTimeout);//链接到服务器，并指定超时时间
		this.ips=skt.getInputStream();
		this.ops=skt.getOutputStream();
	}
	public String getRemoteIp(){
		return this.remoteIp;
	}
	/**
	 * write <code>bs</code> to TCPSocket
	 * @param bs
	 * @throws IOException
	 */
	public void write(byte[]bs) throws IOException{
		OutputStream os=skt.getOutputStream();
		os.write(bs);	
		os.flush();
	}
	
	public void write(U8[]bss) throws IOException{
		byte[]bs=new byte[bss.length];
		Struct.arrayCopy(bss, bs);
		OutputStream os=skt.getOutputStream();
		os.write(bs);
		os.flush();
	}
	/**
	 * write a Bytable instance to TCPSokect
	 * @param ba
	 * @throws Exception
	 */
	public void write(Bytable ba) throws  IOException{
		this.write(ba.toBytes());
	}
	/**
	 * read a Bytable instance from TCPSokect
	 * @param ba
	 * @throws Exception
	 */
	public int readFull(Bytable ba) throws  IOException{
		int size=ba.sizeOf();
		byte[]bs=new byte[size];
		int ret=this.readFull(bs);
		ba.fromBytes(bs);
		return ret;
	}
	/**
	 * read data from InputStream to <code>bs</code>
	 * @param bs
	 * @return
	 * @throws IOException
	 */
	public int readFull(byte[]bs) throws IOException{
		InputStream ips=skt.getInputStream();
		int offset=0, len=bs.length;
		while(true){
			int readed=ips.read(bs,offset,len-offset);
			if(readed < 0)
				return offset;
			else{
				offset+=readed;
				if(offset < bs.length)
					continue;
				else{
					return offset;
				}
				
			}
		}
	}
	/**
	 * read <code>max</code> bytes to the OutputStream
	 * @param ops
	 * @param max
	 * @return
	 * @throws IOException
	 */
	public long read(OutputStream ops,long max) throws IOException{
		InputStream ips=skt.getInputStream();
		long pos=0;
		int b;
		while(true){
			b=ips.read();
			if(b==-1)
				break;
			ops.write(b);
			pos++;
			if(pos>=max)
				break;
		}

		return pos;
	}
	/**
	 * read data to a OutputStream util the end of the InputStreamReader reached
	 * @param ops
	 * @return
	 * @throws IOException
	 */
	public long read(OutputStream ops) throws IOException{
		return read(ops,Long.MAX_VALUE);
	}
	public void close(){
		if(this.skt!=null){
			try {
				this.skt.close();
			} catch (IOException e) {
				BmtLogger.instance().log(e, "when close socket %s", skt);
			}
			this.skt=null;
		}
	}
	public int read() throws IOException {
		return this.ips.read();
	}
	/**
	 * get the socket wrapped by this client
	 * @return
	 */
	public Socket getSocket(){
		return this.skt;
	}
	
	public void finalize() {
		this.close();
	}
	public InetSocketAddress getSa() {
		return sa;
	}
}
