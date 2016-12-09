package com.bmtech.utils.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import com.bmtech.utils.iSerialize.ISerializer;
import com.bmtech.utils.iSerialize.ISerializer.Attribute;
/**
 * the construction of a Virtual folder likes:<br>
 * [name][]
 * @author Fisher@Beiming
 *
 */
public class VirtualFolder{
	private static final File path = new File("/");;
	protected final File file;
	private final HashMap<File, Long>sizeMap = 
		new HashMap<File, Long>();
	public VirtualFolder(File file){
		this.file = file;
	}
	public String toString() {
		return  "VirtualFolder: " + file.toString();
	}

	public boolean equals(Object o) {
		if(o == null || !(o instanceof VirtualFolder))
			return false;
		VirtualFolder v = (VirtualFolder) o;
		return file.equals(v.file);
	}
	/**
	 * test if this virtual fold contans a file
	 * @param file
	 * @return
	 */
	boolean contains(File file) {
		return this.sizeMap.containsKey(file);
	}
	public static  class VirtualFolderWriter{
		class StreamEntry{
			final boolean autoClose;
			final InputStream ips;
			final boolean isDir;
			final long toWrite;
			StreamEntry(InputStream ips, long toWrite){
				this.ips = ips;
				this.autoClose = false;
				if(ips == null) {
					this.toWrite = 0;
					this.isDir = true;
				}else {
					this.toWrite = toWrite;
					this.isDir = false;
				}
			}
			StreamEntry(File file) throws IOException{
				if(file.isDirectory()) {
					this.ips = null;
					this.autoClose = false;
					this.isDir = true;
					this.toWrite = 0;
				}else {
					this.ips = new FileInputStream(file);
					this.autoClose = true;
					this.isDir = false;
					this.toWrite = file.length();
				}
			}
			public void finalize() {
				if(autoClose) {
					if(ips != null) {
						try {
							ips.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		private Object lock = new Object();
		HashMap<File, StreamEntry>inputMap = 
			new HashMap<File, StreamEntry>();
		ArrayList<File>lists = new ArrayList<File>();
		final VirtualFolder folder;
		public VirtualFolderWriter(File file) {
			folder = new VirtualFolder(file);
		}
		/**
		 * add a single file to the vf 
		 * @param file
		 * @throws Exception
		 */
		public void addFile(File file) throws Exception {
			addOne(file, false);
		}
		public void addFolder(File file) throws Exception {
			addOne(file, true);
		}
		public void addOne(File file, boolean recursive) throws Exception {
			String name = file.getName();
			addOne(name, file, recursive);
		}
		public void addOne(String name, File file, boolean recursive) throws Exception {
			if(!file.exists()) {
				throw new Exception(file + " is not exist");
			}
			if(file.isDirectory()) {
				addDir(name);
				if(recursive) {
					File [] fs = file.listFiles();
					if(fs != null) {
						String subName;
						for(File ff : fs) {
							subName = name + "./" + ff.getName(); 
							addOne(subName, ff, recursive);
						}
					}
				}
			}else {
				StreamEntry ent = new StreamEntry(file);
				addOne(name, ent);
			}
		}
		public void addDir(String name) throws Exception {
			StreamEntry ent = new StreamEntry(null, 0);
			addOne(name, ent);
		}
		private void addOne(String name, StreamEntry ent) throws Exception {
			synchronized(lock) {
				File tFile = new File(path , name.toLowerCase());
				tFile = tFile.getCanonicalFile();
				if(folder.contains(tFile)) {
					throw new Exception(name + " has been used");
				}else {
					this.lists.add(tFile);
					inputMap.put(tFile, ent);
					folder.sizeMap.put(tFile, ent.toWrite);
				}
			}
		}

		public VirtualFolder flushToFolder() throws IOException {
			synchronized(lock) {			
				OutputStream ops = new FileOutputStream(folder.file);
				ISerializer is = new ISerializer();
				for(File f : this.lists) {
					long size = this.folder.sizeMap.get(f);
					String name = f.getAbsolutePath();
					int pos = name.lastIndexOf(":");
					if(pos != -1) {
						name = name.substring(pos + 1);
					}
					name = name.replace('\\', '/');
					if(!name.startsWith("/")) {
						name = '/' + name;
					}
					name = '.' + name;
					is.addAttribute(name, size,
							this.inputMap.get(f).isDir);
				}
				ops.write(is.toDefineBytes());
				//write meta ok
				byte []buffer = new byte[4096];
				for(File f : this.lists)  {
					long shouldRead = this.folder.sizeMap.get(f);
					StreamEntry ent = this.inputMap.get(f);
					if(ent.isDir)
						continue;
					long readed = 0;
					int tmp;
					while(true) {
						tmp = ent.ips.read(buffer);
						if(tmp == -1)
							break;
						readed += tmp;
						if(readed > shouldRead)
							break;
						ops.write(buffer, 0, tmp);
					}
					if(readed != shouldRead) {
						throw new IOException(
								String.format(
										"when write to %s need read %s, but get %s", 
										this.folder, shouldRead, readed));
					}
				}
				ops.flush();
				ops.close();
				this.inputMap.clear();
			}
			return folder;
		}
	}

	private Object lock = new Object();
	File folder;
	private boolean expanded = false;
	FileEntry []subs;
	class FileEntry{
		final File par;
		final String name;
		final boolean isDir;
		final long length;
		final File file;
		public FileEntry(File par,Attribute att) {
			this.par = par;
			name = att.getName();
			String sSize = att.getValue(0);
			String sIsDir = att.getValue(1);
			length = Long.parseLong(sSize);
			isDir = sIsDir.equals("true");
			file = new File(par, name);
		}
		public String toString() {
			String line = "%S %s %s";
			line = String.format(line, 
					isDir? "D":"-",	length, name
			);
			return line;
		}
	}
	public File expands() throws Exception {
		synchronized(lock) {
			if(expanded) {
				return this.folder;
			}
			while(true) {
				folder = new File("./_vt_tmp/" + file.getName() 
						+ '@' + System.currentTimeMillis());
				if(folder.exists()) {
					continue;
				}
				folder.mkdirs();
//				folder.deleteOnExit();
				break;
			}
			FileInputStream fis = new FileInputStream(file);
			ISerializer is = ISerializer.parse(fis);
			ArrayList<Attribute> atts = is.getAttributes();
			subs = new FileEntry[atts.size()];
			for(int i = 0; i < atts.size(); i++) {
				Attribute att = atts.get(i);
				FileEntry ent = new FileEntry(folder, att);
				subs[i] = ent;
			}
			byte[]buffer = new byte[4096];
			for(FileEntry ent : subs) {
				if(ent.isDir) {
					if(!ent.file.exists()) {
						ent.file.mkdirs();
					}
					continue;
				}else {
					if(!ent.file.getParentFile().exists()) {
						ent.file.getParentFile().mkdirs();
					}
					ent.file.createNewFile();
				}
				FileOutputStream ops = new FileOutputStream(ent.file);
				int written = 0;
				while(written < ent.length) {
					long len = ent.length - written;
					if(len > buffer.length) {
						len = buffer.length;
					}
					int rd = fis.read(buffer, 0, (int) len);
					if(rd == -1) {
						break;
					}
					ops.write(buffer, 0, rd);
					written += rd;
				}
				if(written != ent.length) {
					throw new IOException(String.format(
							"require %B, but get %s, for %s", 
							ent.length, written, ent.name
							));
				}
				ops.close();
				
			}
			expanded = true;
		}
		return this.folder;
	}
	public static void main(String[] args) throws Exception {
//		File f = new File("d:/testVirtualFolder");
//		VirtualFolderWriter vfw = new VirtualFolderWriter(new File("/v.txt"));
//		vfw.addOne(f, true);
//		VirtualFolder vf = vfw.flushToFolder();
		VirtualFolder vf = new VirtualFolder(new File("d:/testVirtualFolder"));
		vf.expands();
	}
}
