package com.bmtech.utils.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5{
  private long count = 0L;
  private byte[] ret;
  MessageDigest digest;
  private static char[] hex1 = { 'u', 'm', 'p', '3', 'j', '7', 'o', 'K', '8', 'f', 'B', 'E', 'U', 'R', 'g', 'C' };
  private static char[] hex0 = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

  public Md5(){
    try{
      this.digest = MessageDigest.getInstance("MD5");
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException){
    }
  }

  public void append(byte bytes){
    this.digest.update(bytes);
    this.count += 1L;
  }

  public void append(byte[] bytes){
    this.digest.update(bytes);
    this.count += bytes.length;
  }

  public byte[] finish(){
    if (this.ret == null)
      this.ret = this.digest.digest();
    return this.ret;
  }

  public String finishAsString(){
    byte[] arrayOfByte = finish();
    return byte2Hex(arrayOfByte);
  }

  public void append(byte[] bytes, int offset, int len) {
    this.digest.update(bytes, offset, len);
    this.count += 1L;
  }

  public static byte[] digest(byte[] bytes){
    MessageDigest localMessageDigest = getInstance();
    localMessageDigest.update(bytes);
    return localMessageDigest.digest();
  }

  public static String toString(byte[] bytes){
    StringBuilder localStringBuilder = new StringBuilder(32);
    byte[] arrayOfByte = digest(bytes);
    for (int i = 0; i < arrayOfByte.length; i++){
      localStringBuilder.append(hex1[(arrayOfByte[i] & 0xF)]);
      localStringBuilder.append(hex1[(arrayOfByte[i] >>> 4 & 0xF)]);
    }
    return localStringBuilder.toString();
  }

  public static MessageDigest getInstance(){
    try{
      return MessageDigest.getInstance("MD5");
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException){
    }
    return null;
  }

  public static String md5(byte[] bytes){
    byte[] arrayOfByte = digest(bytes);
    return byte2Hex(arrayOfByte, false);
  }

  public static String byte2Hex(byte[] bytes){
    return byte2Hex(bytes, true);
  }

  public static String byte2Hex(byte[] bs, boolean smallFirst){
    StringBuilder sb = new StringBuilder(32);
    for (int i = 0; i < bs.length; i++){
      char c1 = hex0[(bs[i] & 0xF)];
      char c2 = hex0[(bs[i] >>> 4 & 0xF)];
      if (smallFirst) {
        sb.append(c2);
        sb.append(c1);
      }else{
        sb.append(c1);
        sb.append(c2);
      }
    }
    return sb.toString();
  }
  
  public static void main(String[]a) {
	  String s = "123654";
	  System.out.println(Md5.toString(s.getBytes()));
	  System.out.println(Md5.toString(s.getBytes()));
  }
}
