package com.bmtech.utils.security;
public class UserInfo{
	public static final String USER = "user";
	public static final String PASSWORD = "password";
	public final String user;
	public final String pass;
	public UserInfo(String user, String pass){
		this.user = user;
		this.pass = pass;
	}
	public String toString() {
		return USER + ":" + this.user + "\n" + PASSWORD + ":" +this.pass;
	}
	public static UserInfo parse(String str) {
		if(str == null) {
			return null;
		}
		String[]strs = str.split("\n");
		if(strs.length != 2)
			return null;
		if(strs[0].startsWith(USER+":")) {
			strs[0] = strs[0].substring(USER.length() + 1);
		}else {
			return null;
		}
		if(strs[1].startsWith(PASSWORD + ":")) {
			strs[1] = strs[1].substring(1 + PASSWORD.length());
		}else {
			return null;
		}
		return new UserInfo(strs[0], strs[1]);
	}
}