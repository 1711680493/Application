package shendi.web.server;

import shendi.kit.config.ConfigurationFactory;
import shendi.kit.log.Log;

/**
 * 所有 服务端类的 父类.<br>
 * 如果你需要实现自己的功能类等,继承此类实现 {@link #server()} 来执行自己的服务器.<br>
 * 并且需要更改 config.properties 配置中的 server.class 为你自己的类(类的全路径)
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public abstract class Server {
	/**
	 * 当前服务端的端口.
	 */
	protected int port;
	
	/**
	 * 连接的最大数量.
	 */
	protected int connectMax;
	
	/**
	 * 当前的连接数量
	 */
	protected int connectNum;
	
	/**
	 * 通过指定端口来创建 Server.
	 * @param port 端口号
	 * @param connectMax 连接的最大数量
	 */
	public Server(int port,int connectMax) {
		this.port = port;
		this.connectMax = connectMax;
	}
	
	/**
	 * 此方法由 main 调用.<br>
	 * 将循环调用实现类的 server() 方法.<br>
	 * 拥有指定冷却间隔(睡眠),如果需要更改,请修改 config.properties 中的 server.call.time 的值(1000为一秒)
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 */
	public final void start() {
		while (true) {
			//为了避免无请求 但一直开启服务端接收,所以则限制指定数量的连接.
			if (connectNum < connectMax) {
				//连接数量 + 1
				connectNum++;
				//这里使用的 lambda 就是一种简写法.此行代码实际功能就是开启线程 调用 server 方法
				new Thread(() -> {
					server();
					//执行完 连接数量 - 1
					connectNum--;
				}).start();
			}
			//执行冷却
			try {
				Thread.sleep(Integer.parseInt(ConfigurationFactory.getConfig("config").getProperty("server.call.time")));
			} catch (NumberFormatException | InterruptedException e) {
				Log.printErr("调用 处理冷却 出错(对程序整体无影响): " + e.getMessage());
			}
		}
	}
	
	/**
	 * 处理请求,并作出响应.<br>
	 * 此方法将在格外的线程被调用(多线程).<br>
	 * 请勿在此方法内执行耗时操作(死循环等),避免程序开销过高.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 */
	protected abstract void server();
}
