package com.bmtech.utils.systemWatcher;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.bmtech.utils.c2j.Bytable;
import com.bmtech.utils.tcp.UDPHeader;

/**
 * udp Message sender.<br>
 * this class has several static methods to send UDP message, all of them will
 * first wrap the UDP message with a UDPHeader.when Receive, they also first unwrapp
 * the UDPHead
 * @author liying1
 *
 */
public class SendMessage {
	static final int CommonMsgSize=1500;
	/**
	 * send a UDP message to specialfied destination.
	 * @param ip`
	 * @param port
	 * @param bs
	 * @throws IOException
	 */
	public static void sendMessage(String ip,int port,byte[] bs) throws IOException{
		send(ip,port,bs,-1,false,DefaultTimeout);
	}

	static int DefaultTimeout=3000;
	public static  byte[] send(String ip,int port,byte[]msgBytes,
			int receiveSize,boolean answer,int timeout) throws IOException{
		ip=ip.trim();
		
		byte[]bss=new byte[msgBytes.length+UDPHeader.size()];
		UDPHeader sHeader=UDPHeader.instance().next(0);
		System.arraycopy(sHeader.toBytes(), 0, bss, 0, UDPHeader.size());
		System.arraycopy(msgBytes, 0, bss, UDPHeader.size(), msgBytes.length);

		DatagramPacket packet =new DatagramPacket(bss,bss.length,InetAddress.getByName(ip),
				port);
		
		DatagramSocket socket = new DatagramSocket();
		socket.setSoTimeout(timeout);
		socket.send(packet);
		
		if(!answer){
			socket.close();
			return null;
		}

		if(receiveSize==0)
			return null;

		byte[]recBuffer=new byte[receiveSize+UDPHeader.size()];
		packet =new DatagramPacket(recBuffer,receiveSize,InetAddress.getByName(ip),
				port);
		socket.receive(packet);
		byte[]header=new byte[UDPHeader.size()];
		System.arraycopy(recBuffer, 0,header, 0,  UDPHeader.size());
		UDPHeader rHeader=UDPHeader.newInstance(header);
		if(!rHeader.match(sHeader)){
			throw new IOException("seq not match");
		}

		byte []ret=new byte[receiveSize];
		System.arraycopy(recBuffer, UDPHeader.size(),ret, 0,  ret.length);
		socket.close();
		return ret;
	}


	/**
	 * send a message and wait for reply. The default timeout is 3 seconds
	 * @param ip
	 * @param port
	 * @param bs the data to send 
	 * @return return bytes received for this UDP message.
	 * @throws IOException
	 */
	 
	public static  byte[] sendWithAnswer(char[] ip,int port,byte[]bs) throws IOException{
		return  send(new String(ip),port,bs,bs.length,true,DefaultTimeout);
	}

	/**
	 *  send a message and wait for reply. Wait timeout is in ms
	 * @param ip
	 * @param port
	 * @param bs data to send
	 * @param timeout in ms
	 * @return return bytes received for this UDP message.
	 * @throws IOException
	 */
	public static  byte[] sendWithAnswer(char[] ip,int port,byte[]bs,int timeout) throws IOException{
		return  send(new String(ip),port,bs,bs.length,true,timeout);
	}

	public static  byte[] sendWithAnswer(String ip,int port,byte[]bs) throws IOException{
		return  send(new String(ip),port,bs,bs.length,true,DefaultTimeout);
	}
	public static  byte[] sendWithAnswer(String ip,int port,Bytable bs) throws IOException{
		byte[]bss=bs.toBytes();
		return  send(new String(ip),port,bss ,bss.length,true,DefaultTimeout);
	}	
}
