package com.bmtech.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 一个粗糙的同步工具，应用系统应当自己约定使用的实例，并将需要同步的块封装到 #IRunnor实例中。<br>
 * 典型调用方式:<br>
 * new IRunnor(lockId){<br>
 *  public Object[] synRun(Object objs[]){<br>
 *  	//the block should be synchronized<br>
 *  	...<br>
 *  	...<br>
 *  	...<br>
 *  }.startRun();<br>
 * }<br>
 * @author fisher
 *
 */
public class VisitLock {
	public static interface ItfSynRunnor{
		/**
		 * to synchronized run, if it is called by #VisitLock.run(...)
		 * @param objs
		 * @return
		 * @throws Exception
		 */
		public Object[] synRun(Object objs[]) throws Exception ;
	}
	public static abstract class VLRunnor implements ItfSynRunnor{
		Object lockId;
		VLRunnor(Object lockId){
			this.lockId = lockId;
		}
		/**
		 * only this method should be called!
		 * @param objs
		 * @return
		 * @throws Exception
		 */
		public Object[] startRun(Object objs[]) throws Exception {
			return VisitLock.run(lockId, this, objs);
		}

	}
	private static Map<Object, VisitLock> map = Collections.synchronizedMap(new HashMap<Object, VisitLock>());

	public synchronized Object[] run(ItfSynRunnor runor, Object ...obj) throws Exception{
		return runor.synRun(obj);
	}
	public static Object[] run(Object lockId, ItfSynRunnor runor, Object ...obj) throws Exception {
		VisitLock lck = map.get(lockId);

		if(lck == null) {//avoid synchronize 
			synchronized(map) {
				lck = map.get(lockId);
				if(lck == null) {
					lck = new VisitLock();
					map.put(lockId, lck);
				}
			}
		}
		return lck.run(runor, obj);
	}

	public static void main(String args[]) {
		class vv{
			int num = 0;
			public String toString() {
				String ret =  num + "";
				num ++;
				return ret;
			}
		}
		final vv num = new vv();

		for(int x = 0; x < 6; x ++) {
			final int id = x;
			Thread t = new Thread() {
				public void run() {
					final boolean isPrint = (id) % 3 == 1;
					while(true) {
						try {
//							new IRunnor(Math.random()) {
							new VLRunnor(0) {
								@Override
								public Object[] synRun(Object[] objs) throws Exception {
									String s1 = ("id:" + id + "\torg:\t" + num );
									sleep(20);
									String s2 = s1 + ("\tto :\t" + num);
									
									if(isPrint) {
										System.out.println(s2);
									}
									return null;
								}

							}.startRun(null);
						} catch (Exception e1) {
							e1.printStackTrace();

						}
					}
				}
			};
			t.start();
		}
	}
}
