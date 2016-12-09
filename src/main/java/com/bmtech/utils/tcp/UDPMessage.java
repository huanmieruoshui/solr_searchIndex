package com.bmtech.utils.tcp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class UDPMessage {
	static final int defaultTimeout=3000;
	UDPHeader header;
	byte[]body;

	private UDPMessage(){}
	/**
	 * instance a new UDPMessage
	 * @param header ,the UDPHeader to use, if null, we will create a new one
	 * @param msg the msg's body
	 */
	public UDPMessage(UDPHeader header,byte[]body){
		if(header==null)
			header=UDPHeader.instance();
		else
			this.header=header;
		this.body=body;
	}
	/**
	 * instance a new UDPMessage
	 * @param header ,the UDPHeader to use, if null, we will create a new one
	 */
	public UDPMessage(UDPHeader header){
		if(header==null){
			this.header=UDPHeader.instance();
		}
	}
	public UDPHeader getHeader(){
		return this.header;
	}
	public byte[]getBody(){
		return this.body;
	}
	public void setBody(byte[]bs){
		this.body=bs;
	}
	public byte[]toBytes(){
		if(body==null)
			return header.toBytes();
		else{
			byte[]bs=new byte[UDPHeader.size()+body.length];
			System.arraycopy(header.toBytes(), 0, bs, 0, UDPHeader.size());
			System.arraycopy(body, 0, bs,UDPHeader.size(),body.length);
			return bs;
		}
	}
	public DatagramPacket toPackage(InetAddress address, int port){
		byte[]bs=this.toBytes();
		return new DatagramPacket(bs,bs.length,address,port);
	}
	public static UDPMessage fromPackage(byte[]data) throws IOException{
		if(data.length<UDPHeader.size()){
			throw new IOException("size is too small "+data.length );
		}
		UDPMessage msg=new UDPMessage();
		msg.header=UDPHeader.newInstance(data);
		msg.body=new byte[data.length-UDPHeader.size()];
		System.arraycopy(data, UDPHeader.size(),msg.body, 0, msg.body.length);
		return msg;
	}
	public static UDPMessage sendMsg(UDPMessage msg,InetAddress address, int port,int recvSize) throws IOException{
		return sendMsg(msg,address, port,recvSize,defaultTimeout) ;
	}
	public static UDPMessage sendMsg(UDPMessage msg,int recvSize, int timeout) throws IOException{
		return sendMsg(msg, null, -1, recvSize, timeout);
	}
	public static UDPMessage sendMsg(UDPMessage msg,int recvSize) throws IOException{
		return sendMsg(msg,  recvSize, defaultTimeout);
	}
	public static UDPMessage sendMsg(UDPMessage msg,InetAddress address, int port,int recvSize
			,int timeout) throws IOException{
		DatagramSocket socket = new DatagramSocket();
		DatagramPacket packet = msg.toPackage(address, port);
		socket.setSoTimeout(timeout);
		socket.send(packet);
		byte[]recBuffer=new byte[recvSize+UDPHeader.size()];
		packet =new DatagramPacket(recBuffer,recvSize,
				packet.getAddress(),packet.getPort());
		socket.receive(packet);
		return fromPackage(recBuffer);
	}
	public static void main(String[]a) throws UnknownHostException, IOException {
		sendMsg(new UDPMessage(UDPHeader.instance(), new byte[1024]), InetAddress.getByName("192.168.1.1"), 2000, 5000);
	}
}
