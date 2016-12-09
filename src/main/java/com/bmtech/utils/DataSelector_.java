//package com.bmtech.utils;
//
//import java.util.HashMap;
//import java.util.Iterator;
//
//public class DataSelector_ extends Thread{
//	public class KeyRegedException extends Exception{
//		/**
//		 * 
//		 */
//		private static final long serialVersionUID = 1L;
//
//		KeyRegedException(Object o){
//			super(o.toString());
//		}
//	}
//	/**
//	 * data Reader
//	 * @author liying1
//	 *
//	 */
//	private class Reader extends Thread{
//		private int timeo;
//		private SelectableData data;
//		public Reader(int timeout){
//			this.timeo = timeout;
//
//		}
//
//		public void run(){
//			if(timeo == 0){
//				return ;
//			}
//			try{
//				if(timeo < 0)
//					wait();
//				else{
//					wait(timeo);
//				}
//			}catch(Exception e){
//				e.printStackTrace();
//			}
//
//		}
//
//		void setData(SelectableData data) {
//			this.data = data;
//			synchronized(this){
//				this.notifyAll();
//			}
//		}
//
//		SelectableData getData() {
//			return data;
//		}
//	}
//	class SelectableData {
//		Object key;
//		Object value;
//		SelectableData prv, next;
//		long st;
//		public SelectableData(Object key,Object value){
//			this.key = key;
//			this.value = value;
//			st = System.currentTimeMillis();
//		}
//		public Object getkey(){
//			return this.key;
//		}
//		boolean isTimeouted(){
//			if(dataTimeout < 0)
//				return false;
//			return (System.currentTimeMillis() - st)> dataTimeout;
//		}
//	}
//
//	private Object lock = new Object();
//	private SelectableData head = new SelectableData("HEADER_KEY","HEADER_VALUE");
//	private HashMap<Object,SelectableData> map = new HashMap<Object,SelectableData>();
//	private HashMap<Object,Reader> readers = new HashMap<Object,Reader>();
//	int chkIteval = 1000;
//	int dataTimeout = -1;
//	/**
//	 * 
//	 * @param dataTimeout  in seconds! we only promise that, not timeOutted data will
//	 * be cached in Selector! if timeouted, the data may removed from it.
//	 * 0 means no cache; <0 means cache infinitely
//	 */
//	public DataSelector_(int dataTimeout){
//		//init head
//		head.next = head;
//		head.prv = head;
//		this.dataTimeout = dataTimeout*1000;
//		if(dataTimeout > 0)
//			this.start();
//	}
//	public void run(){
//		while(true){
//			try {
//				sleep(1000);
//			} catch (InterruptedException e) {
//				break;
//			}
//
//			try{
//				synchronized(lock){
//
//					while(true){//not head
//						SelectableData data = head.next;
//						if(data == head)
//							break;
//						if(data.isTimeouted()){
//							this.outLink(data);
//						}else{
//							break;
//						}
//
//					}
//				}
//			}catch(Exception e){
//				e.printStackTrace();
//			}
//
//		}
//	}
//	public void regValue(Object key,Object value){
//		SelectableData data = new SelectableData(key, value);
//		synchronized(lock){
//			Reader reader = readers.get(key);
//			if(reader !=null){
//				reader.setData(data);
//			}else{
//				if(dataTimeout > 1){
//					inLink(data);
//					map.put(key, data);
//				}
//			}
//		}
//	}
//
//	private void inLink(SelectableData data){
//		if(data!=null){
//			head.prv.next = data;
//			data.prv = head.prv;
//			head.prv = data;
//			data.next = head;
//
//		}
//	}
//	private void outLink(SelectableData data){
//		if(data != null){
//			data.prv.next = data.next;
//			data.next.prv = data.prv;
//			data.prv = null;
//			data.next = null;
//		}
//	}
//	/**
//	 * reader a data
//	 * @param key
//	 * @param timeout, 0 return immeditately, <0 infinitely wait
//	 * @return
//	 * @throws KeyRegedException 
//	 */
//	public Object select(Object key, int timeout) throws KeyRegedException{
//		Reader reader;
//		if(key == null)
//			throw new KeyRegedException("null key is not allowed");
//		synchronized(lock){
//			SelectableData data = map.get(key);
//			if(data != null){
//				outLink(data);
//				map.remove(key);
//				if(data.isTimeouted()){
//					return null;
//				}
//				return data.value;
//			}
//			if(timeout == 0)
//				return null;
//			reader = new Reader(timeout);
//			if(!regReader(key, reader)){
//				throw new KeyRegedException(key);//be sure the reader is reged
//			}
//		}
//
//		try{
//			if(timeout == 0){
//				return null;
//			}
//			while(true){
//				try{
//					if(timeout < 0){
//						synchronized(reader){
//							reader.wait(1000000);
//						}
//					}else{
//						synchronized(reader){
//							reader.wait(timeout);
//						}
//						break;
//					}
//				}catch(Exception e){
//					e.printStackTrace();
//					break;
//				}
//			}
//
//		}catch(Exception e){				
//		}finally{
//			synchronized(lock){
//				unregReader(key);
//			}
//		}
//		SelectableData data = reader.getData();
//		if(data == null)
//			return null;
//		else{
//			return data.value;
//		}
//	}
//	private void unregReader(Object key){
//		synchronized(readers){
//			readers.remove(key);
//		}
//	}
//	/**
//	 * return false if others has also regged on this key
//	 * @param key
//	 * @param reader
//	 * @return
//	 */
//	private boolean regReader(Object key, Reader reader){
//		synchronized(readers){
//			Reader r = this.readers.get(key);
//			if(r == null){
//				readers.put(key, reader);
//				return true;
//			}
//		}
//		return false;
//	}
//	boolean isClosed = false;
//	public void close(){		
//		synchronized(lock){
//			if(isClosed)
//				return;
//			this.interrupt();
//			this.map.clear();
//
//			SelectableData data = head.next, tmp;
//			while(data != head){
//				tmp = data.next;
//				data.prv = null;
//				data.next = null;
//				data = tmp;
//			}
//			head.prv = head;
//			head.next = head;
//			isClosed = true;
//			synchronized(readers){
//				Iterator<Reader> itr = this.readers.values().iterator();
//				while(itr.hasNext()){
//					Reader rd =itr.next();
//					synchronized(rd){
//						rd.notifyAll();
//					}
//				}
//				readers.clear();
//			}
//		}
//	}
//	public boolean isClosed(){
//		return this.isClosed;
//	}
//}
