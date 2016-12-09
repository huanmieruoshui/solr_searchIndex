package com.bmtech.utils.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.bmtech.utils.Misc;
import com.bmtech.utils.http.HttpCrawler;
import com.bmtech.utils.io.TchFileTool;
/**
 * 
 * @author Fisher@Beiming
 *
 */
public class UserProfile {
	private File file = new File("config/profile/userInfo.inf");
	public static final UserProfile instance = new UserProfile();
	UserInfo ui;
	private UserProfile(){
		if(!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		if(!file.exists()) {
			makeNewProfile();
		}
	}
	public UserInfo getUserInfo() {
		if(ui == null) {
			try {
				ui = getUserInfoInner();
			} catch (IOException e) {
				JOptionPane.showConfirmDialog(null, "读取 user profile 失败",
						"ERROR！", JOptionPane.OK_OPTION);
			}
		}
		return ui;
	}

	private UserInfo getUserInfoInner() throws IOException {
		ArrayList<String> keys = Misc.getMACAddresses();

		if(!file.exists()) {
			return null;
		}
		FileInputStream fw = new FileInputStream(file);
		byte []bs = new byte[1024];
		int readed = 0;
		while(true) {
			int r = fw.read(bs, readed, bs.length - readed);
			if(r == -1)
				break;
			readed += r;
			if(readed >= bs.length)
				break;
		}
		fw.close();
		String ret = null;
		for(String key : keys) {
			try{
				ret = BmAes.decrypt(key, bs, 0, readed);
				break;
			}catch(Exception e) {
				continue;
			}
		}
		if(ret == null) {
			throw new RuntimeException("profile check fail");
		}
		return UserInfo.parse(ret);
	}
	public void makeNewProfile() {
		while(true) {
			JPasswordField  pwd  =  new  JPasswordField(); 
			JTextField  user  =  new  JTextField(); 
			user.setText("your_name");
			user.setSelectionStart(0);
			user.setSelectionStart(10);
			Object[]  message  =  { "输入您的用户名和密码: ",  user, pwd}; 
			int ret = JOptionPane.showConfirmDialog(
					null, message, null, JOptionPane.OK_OPTION);
			if(ret == JOptionPane.OK_OPTION) {
				if("your_name".equals(user.getText().trim())){
					continue;
				}
				if(pwd.getPassword().length == 0) {
					continue;
				}
			}else {
				break;
			}
			String u = user.getText();
			String p = new String(pwd.getPassword());
			UserInfo newUi = new UserInfo(u, Md5.toString(p.getBytes()));

			try {
				if(!regAddress(newUi)) {
					continue;
				}else {
					this.ui = newUi;
					JOptionPane.showConfirmDialog(null, "新user profile文件创建成功",
							"OK！", JOptionPane.PLAIN_MESSAGE);
				}
			} catch (IOException e) {
				JOptionPane.showConfirmDialog(null, e.toString(),
						"失败！", JOptionPane.PLAIN_MESSAGE);
			}
			break;
		}
	}
	private boolean regAddress(UserInfo userInfo) throws IOException {
		JPasswordField  pwd  =  new  JPasswordField(); 
		Object[]  message  =  { "输入管理员密码: ",  pwd}; 
		JOptionPane.showConfirmDialog(null, message, null, JOptionPane.PLAIN_MESSAGE);
		boolean ok = false;
		String pass = new String(pwd.getPassword());
		ok = (26000 == pass.hashCode()*pass.hashCode()%65536);
		if(!ok) {
			JOptionPane.showMessageDialog(null, 
					"管理员密码错误！请联系管理员",
					"验证失败！",
					JOptionPane.YES_OPTION);
			return false;
		}
		if(userInfo == null) {
			return false;
		}
		if(!this.okUser(userInfo, pass)) {
			JOptionPane.showMessageDialog(null, 
					"用户名密码验证失败",
					"验证失败！",
					JOptionPane.YES_OPTION);
			return false;
		}
		String s = Misc.getMACAddress();
		byte []bs = BmAes.encrypt(s, userInfo.toString());
		FileOutputStream fw = new FileOutputStream(file, false);
		fw.write(bs);
		fw.flush();
		fw.close();
		return true;
	}

	private boolean okUser(UserInfo ui,String pass) {
		try {
			String u = TchFileTool.get("config/profile/", "server") + "/hasUser.jsp?user="+ui.user+"&pass="+ui.pass+"&prv="+pass;
			URL url = new URL(u);
			HttpCrawler crl = new HttpCrawler(url);
			crl.connect();
			String ret = crl.getString();
			ret = ret.trim();
			if(ret.equals("0")) {
				return false;
			}
			return true;
		}catch(Exception e) {
		}
		return false;
	}

}