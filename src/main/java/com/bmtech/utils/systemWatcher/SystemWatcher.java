package com.bmtech.utils.systemWatcher;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.bmtech.utils.Misc;
import com.bmtech.utils.io.FileGet;
import com.bmtech.utils.log.BmtLogger;
import com.bmtech.utils.log.LogLevel;
import com.bmtech.utils.security.BmAes;
import com.bmtech.utils.systemWatcher.innerAction.ChangeLogExceptionAction;
import com.bmtech.utils.systemWatcher.innerAction.ChangeLogLevelAction;
import com.bmtech.utils.systemWatcher.innerAction.EchoAction;
import com.bmtech.utils.systemWatcher.innerAction.GCAction;
import com.bmtech.utils.systemWatcher.innerAction.ShutDownAction;
import com.bmtech.utils.systemWatcher.innerAction.SysInfoAction;
import com.bmtech.utils.systemWatcher.innerAction.SysPropAction;
import com.bmtech.utils.tcp.TCPServer;

/**
 * a TCP based System stopper,
 * @author Fisher@Beiming
 *
 */
public class SystemWatcher {
	public static final String VERSION = "bmWatcher 1.1";
	public static final String WatherLogName="sysWatcher";

	private Map<String, WatcherAction>actions =
		 Collections.synchronizedMap(new HashMap<String, WatcherAction>());

	public static final String OK = "OK";
	public static final int MAX_BLOCK_SIZE_IN_BYTES = 1400;
	protected TCPServer server;
	protected final String key;
	protected Integer connected = 0;
	protected Boolean hasStarted = false;
	public final String sysName;
	protected final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
	protected final ThreadPoolExecutor pool;
	protected final long expireTime;
	private final boolean isCheckVer = false;
	public SystemWatcher(int port, String key) throws IOException {
		this(port, key, null);

	}
	public SystemWatcher(int port, String key, String sysName) throws IOException {
		this.key = key;
		this.sysName = sysName;
		BmtLogger.instance().logWithName(WatherLogName,LogLevel.Warning,"start watcher using port %s",port);
		//�����̳߳�
		/**
		 * corePoolSize - ������������߳��������������̡߳�
		 * maximumPoolSize - �������������߳�����
		 * keepAliveTime - ���߳������ں���ʱ����Ϊ��ֹǰ����Ŀ����̵߳ȴ���������ʱ�䡣
		 * unit - keepAliveTime ������ʱ�䵥λ��
		 * workQueue - ִ��ǰ���ڱ�������Ķ��С��˶��н������� execute �����ύ�� Runnable ����
		 */
		pool = new ThreadPoolExecutor(10, 100, 100, TimeUnit.SECONDS, queue){
			protected void afterExecute(Runnable r, Throwable t) {//�ر�Socket�Ŀͻ���
				ActionDealor run = (ActionDealor) r;
				try {
					run.clientSocket.close();
				} catch (IOException e) {
					BmtLogger.instance().log(e, "when close socket");
				}
			}
			protected void beforeExecute(Thread t, Runnable r) {
			}
		};
		//isCheckVerĬ��false
		if(isCheckVer) {
			File pFile = new File("config/profile/"+port+".pf");
			if(!pFile.exists()) {//����ļ�������
				throw new IOException(pFile+" not exist!");
			}
			byte[]pf ;
			//��ȡ�ļ��ĵ�һ������
			//IJMBGLFHPAODFCFCGFKNOHOHGMKDHLINMLHEGEBKFDOOHFDINADICLNEPAHHLJOHECGNMCGECENLNOLOBEBHFCDJOOECFJJE
			String tmp = FileGet.getStr(pFile);//��ȡ�ļ�����
			pf = Misc.strToBytes(tmp);
			pf = BmAes.decrypt(key.getBytes(), pf);
			String str = new String(pf);//usr/qlm/searchExp*1392940800000
			int starPos = str.indexOf("*");//18
			if(starPos == -1) {
				throw new IOException("Not good run.pf format");
			}
			String toDay = str.substring(starPos + 1);//1392940800000
			str = str.substring(0, starPos);///usr/qlm/searchExp
			//��ȡ�û��ĵ�ǰ����Ŀ¼������java�Դ���ϵͳ����
			String user_dir = System.getProperty("user.dir");//��ǰ��Ŀ·��F:\\qianlima_test\\LuceneTest
			File udir = new File(user_dir);
			File encDir = new File(str);
			//�жϵ�ǰ����Ŀ¼��509903.pf�����õ�Ŀ¼�Ƿ�һֱ
			if(str.trim().length() == 0 || !encDir.equals(udir)) {
				//�������Ŀ¼�͵�ǰ��ĿĿ¼����ȣ��׳��쳣
				throw new IOException("Not match user dir, this may disorder the system");
			}
			try {
				//�������ļ��е�509903.pf��ʱ�䣬����Ϊ����ʱ��
				expireTime = Long.parseLong(toDay);
			}catch(Exception e) {
				throw new IOException("unkown expire day");
			}
			if(expireTime < System.currentTimeMillis()) {
				//�������ʱ�䣬С�ڵ�ǰʱ�䣬�ܳ��쳣
				throw new IOException("has expired! please update BMFrame:" + new java.util.Date(expireTime));
			}
		}else {
			expireTime = Long.MAX_VALUE;//����ʱ��
		}
		//ServerSocket����50903�˿�
		server = new TCPServer(port) {
			//��ServerSocket���ص�socket���뵽ActionDealor����ʼִ�У�ActionDealor�����run����
			@Override
			public void doYourJob(final Socket clientSocket) {//socket ��������socket
				ActionDealor ad = new ActionDealor(clientSocket);
				pool.execute(ad);
			}
		};
		//��ʵ����WatcherAction������࣬��ӵ�Map��key=cmd��value=������� 
		this.regAction(new ShutDownAction(key));//cmd=Force Stop  key=key
		this.regAction(new SysInfoAction(key, sysName));//cmd=sysinfo key =key
		this.regAction(new GCAction(key)); //cmd=gc
		this.regAction(new ChangeLogLevelAction(key));//cmd=CH_LOG_LEV 
		this.regAction(new ChangeLogExceptionAction(key));//cmd=CH_LOG_ExcP
		this.regAction(new EchoAction(key));//cmd=echo
		this.regAction(new SysPropAction(key));//cmd=sysprop
		this.regAction(new WatcherAction(WatcherAction.HELP, key) {//cmd=HELP
			@Override
			public void run(String[] paras, Socket clientSocket)throws Exception {
				String reply = actions().toString();
				this.writeBack(reply, clientSocket);
			}
		});

		this.regAction(new WatcherAction("axp", key) {//cmd=axp key=keys
			@Override
			public void run(String[] paras, Socket clientSocket)
			throws Exception {
				long exp = expireTime - System.currentTimeMillis();
				exp = exp/24/60/60/100;
				double leftDay = exp/10.0;
				if(leftDay < 30) {
					this.writeBack("Urgent!", clientSocket);
				}
				this.writeBack(leftDay + "", clientSocket);
			}
		});
	}
	public Set<String> actions(){
		return this.actions.keySet();
	}

	public void regAction(final Watchable watcher) {
		regAction(new WatcherAction(watcher.getCmd(), key) {

			@Override
			public void run(String[] paras, Socket clientSocket)
			throws Exception {
				String reply = watcher.run(paras);
				writeBack(reply, clientSocket);
			}

		});
	}
	public void regAction(WatcherAction act) {
		if(this.actions.containsKey(act.cmd.toLowerCase())) {
			BmtLogger.instance().logWithName(WatherLogName,LogLevel.Warning,"%s already registed, skip", act.cmd);
			return;
		}
		this.actions.put(act.cmd.toLowerCase(), act);
	}
	public ShutDownAction getShutDownAction() {
		return (ShutDownAction) this.actions.get(WatcherAction.STOP.toLowerCase());
	}

	public synchronized void start() {
		if(this.hasStarted) {
			BmtLogger.instance().logWithName(WatherLogName,LogLevel.Warning,"has already started! skip! listen at %s",this.server.getPort());
		}else {
			BmtLogger.instance().logWithName(WatherLogName,LogLevel.Info,"try starting.... listen at %s",this.server.getPort());
			server.start();
		}
	}
	/**
	 * get a new StopHook and reg it to StopAction.<br>
	 * @param maxWait 0 or less means wait exist signal
	 * @return
	 */
	public StopHook regStopHook(long maxWait ) {
		StopHook sh = new StopHook(maxWait);
		this.getShutDownAction().addStopHook(sh);
		return sh;
	}

	public StopHook regStopHook() {
		StopHook sh = new StopHook(0);
		this.getShutDownAction().addStopHook(sh);
		return sh;
	}

	/**
	 * block this thread, never exist!
	 * may be used when need block 
	 */
	public void block() {
		BmtLogger.instance().logWithName(WatherLogName,LogLevel.Warning,"WatherLogName blocking start"
		);
		while(true) {
			try {
				Thread.sleep(60*60*1000);
			} catch (InterruptedException e) {
				BmtLogger.instance().log(e, "sleep error");
			}
		}
	}
	/**
	 * reg and start the watcher
	 * @param port
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public static SystemWatcher regWatcher(int port, String key, String sysName) throws IOException {
		SystemWatcher stp = new SystemWatcher(port, key, sysName);
		stp.start();
		return stp;
	}


	class ActionDealor implements Runnable{
		final Socket clientSocket;
		ActionDealor(Socket clientSocket){
			this.clientSocket =  clientSocket;
		}
		public void run() {
			InetSocketAddress address = null;
			try {
				//���ش��׽������ӵĶ˵�ĵ�ַ�����δ�����򷵻� null��
				address =(InetSocketAddress) clientSocket.getRemoteSocketAddress();
				BmtLogger.instance().logWithName(WatherLogName,
						LogLevel.Urgen,
						"linked in socket, dst address = %s:%d",
						address.getHostName(),
						address.getPort()
				);
				clientSocket.setSoTimeout(1000);// ���� SO_TIMEOUT �����á�
				//���ش��׽��ֵ�������
				InputStream  is = clientSocket.getInputStream();
				byte[]bs = new byte[MAX_BLOCK_SIZE_IN_BYTES];//��ʼ��1400������
				int read = is.read(bs);//�����������뵽bs byte���飬��������һ�������ֽڣ������������ĩβ���򷵻� -1�� 
				if(read == -1) {
					BmtLogger.instance().logWithName(WatherLogName,
							LogLevel.Error,
							"read bytes %d", read);
					return;
				}
				//��ȡkey��0��ʼ��ȡread���ȵ�bs�У�Ȼ��ת��String���ͷ���
				String cmd = BmAes.decrypt(key, bs, 0, read);
				final String cmds[] = cmd.split("\n");
				//����cmdȥactions���ҷ��������Ĺ۲���
				final WatcherAction act = actions.get(cmds[0].toLowerCase());
				if("true".equals(System.getProperty("logcmd"))) {
					BmtLogger.instance().logWithName(WatherLogName,LogLevel.Urgen,
							"dst address = %s:%d, cmd='%s'",address.getHostName(),address.getPort(),cmd.replace('\n', '^'));
				}
				if(act != null) {
					//����ע���WatcherAction��ִ�в�ͬ������
					act.run(cmds, clientSocket);
				}else {
					BmtLogger.instance().logWithName(WatherLogName,LogLevel.Warning,"Unkonwn command got '%s', dst address = %s:%d",
							cmd,address.getHostName(),address.getPort());
					
					WatcherAction wa = new WatcherAction(WatcherAction.Echo, key) {

						@Override
						public void run(String[] paras, Socket clientSocket) throws IOException {
							this.writeBack( "bad cmd " + paras[0], clientSocket);
						}
					};
					//clientSocket��������������cmds
					wa.run(cmds, clientSocket);
				}
			} catch (Exception e) {
				BmtLogger.instance().logWithName(WatherLogName,
						e, "got error when del socket", this
				);
			}finally {
				if(null != clientSocket) {
					try {
						if(!clientSocket.isClosed()) {
							clientSocket.close();
						}
					}catch(Exception e) {
						BmtLogger.instance().logWithName(WatherLogName,
								LogLevel.Warning,
								e,
						"got error when close socket");
					}
				}
			}
		}
	}

}