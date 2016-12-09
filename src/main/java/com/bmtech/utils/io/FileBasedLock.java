package com.bmtech.utils.io;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import com.bmtech.utils.log.BmtLogger;
import com.bmtech.utils.log.LogLevel;

/**
 * file lock to prevent reentrant
 * @author Fisher@Beiming
 *
 */
public class FileBasedLock extends Thread{
	public static final File LOCK_PATH = new File("./_f_locks_/");
	File file;
	FileLock fLock;
	FileChannel fileChannel;
	final String id;
	public FileBasedLock(String id) throws IOException{
		if(!LOCK_PATH.exists()) {
			LOCK_PATH.mkdirs();
		}
		file = new File(LOCK_PATH, id+".lock_");
		if(!file.exists()){
			file.createNewFile();
		}
		this.id = id;
	}
	/**
	 * try to lock on this 
	 * @return
	 * @throws IOException 
	 */
	public synchronized boolean tryLock() throws IOException{
		if(!file.canWrite()) {
			BmtLogger.instance().log("can not write file: %s", 
					file.getCanonicalFile());
			return false;
		}
		RandomAccessFile raFile = new RandomAccessFile(file,"rw");

		fileChannel = raFile.getChannel();
		if(fileChannel == null)
			return false;
		fLock = fileChannel.tryLock();
		if(fLock != null)
			return true;
		return false;
	}
	public synchronized void releaseLock(){
		if(fLock != null){
			if(fLock.isValid()){
				try {
					fLock.release();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			fLock = null;
		}
		if(fileChannel!=null){
			if(fileChannel.isOpen()){
				try {
					fileChannel.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			fileChannel = null;
		}
		synchronized(started) {
			started.notifyAll();
		}
	}
	public void finalize(){
		this.releaseLock();
		BmtLogger.instance().log(LogLevel.Warning,
				"file lock is released by VM :%s",
				id);
	}
	public void block() {
		while(true) {
			try {
				Thread.sleep(60*60*1000);
			} catch (InterruptedException e) {
			}finally {
				BmtLogger.instance().log("lock still holded");
			}
		}
	}
	private Boolean started = false;
	public void nonBlock() {
		if(started) {
			return;
		}
		synchronized(started) {
			if(!started) {
				this.start();
			}
		}
	}
	public void run() {
		synchronized(started){
			try {
				started.wait();
				BmtLogger.instance().log(LogLevel.Warning, "noblock model exit");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
