package com.bmtech.utils.tcp;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

import com.bmtech.utils.log.BmtLogger;

public abstract class TCPServer extends Thread{
	private final int port;
	private  boolean stop = false;
	protected final ServerSocket listenSocket;
	protected ExecutorService  exec;
	public TCPServer(int port) throws IOException{
		this.port = port;
		listenSocket = new ServerSocket(port);
	}
	public abstract void doYourJob(Socket clientSocket);
	protected final void tryStop() {
		if(!this.stop) {
			this.stop = true;
			if(this.listenSocket != null) {
				try {
					this.listenSocket.close();
				} catch (IOException e) {
					BmtLogger.instance().log(e, "when stop tcp server, port = %d",this.port);
				}finally {

				}
			}
		}
	}
	/**
	 * start service
	 * here,listenSocket listen one port,when connect query is accepted,
	 * TC let  Hiconnection to manipulate the socket,and then keep listening 
	 */
	public void run() {//搜索器的线程启动
		try{
			/**setReuseAddress(true)方法执行后，可以允许多个DatagramSocket
			绑定到相同的IP地址和端口，那么发送到此IP地址和端口的数据能够被复制到多个DatagramSocket，
			也就是说能够实现多播的功能
			**/
			listenSocket.setReuseAddress(true);
			do{
				try{
					Socket linked = listenSocket.accept();
					linked.setReuseAddress(true);
					doYourJob(linked);//调用SearchHost中的server
				}catch(Exception e){
					BmtLogger.instance().log(e, "tcpServer got excetion");
				}
			}while(!stop);
		} catch(IOException e) {
			BmtLogger.instance().log(e, "Listen socket:"+e.getMessage());
		}
		this.stop=true;
	}
	public int getPort() {
		return this.port;
	}
}


