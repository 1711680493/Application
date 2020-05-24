package shendi.web.server;

import java.lang.reflect.InvocationTargetException;

import shendi.kit.config.ConfigurationFactory;

/**
 * Start web server.<br>
 * 开启网络服务器.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public class StartWebServer {
	/**
	 * 这里使用反射开启服务器,目前使用的为 Socket(BIO).<br>
	 * 为了扩展,后期可能会用到 NIO 或者新的一系列的技术.<br>
	 * 所以这里通过读取 配置文件 + 反射 获取指定类来开启服务端.<br>
	 * 如果需要实现自己的服务器处理请继承 {@link Server} 类.<br>
	 * 并更改配置文件config.properties 中的 server.class 的值.<br>
	 * config.properties 中的 server.connect.max 为现有连接的最大数量(开启了等待接收 不代表有用户)
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param args default
	 * @see Server
	 * @see DefaultServer
	 */
	public static void main(String[] args) {
		try {
			//创建指定 Server 对象
			Server server = (Server) Class.forName(ConfigurationFactory.getConfig("config").getProperty("server.class"))
					.getDeclaredConstructor(int.class,int.class)
					.newInstance(Integer.parseInt(ConfigurationFactory.getConfig("config").getProperty("server.port")),
							Integer.parseInt(ConfigurationFactory.getConfig("config").getProperty("server.connect.max")));
			//启动
			server.start();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException
				| ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
