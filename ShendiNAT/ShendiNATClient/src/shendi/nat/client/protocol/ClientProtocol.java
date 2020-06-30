package shendi.nat.client.protocol;

import shendi.kit.config.ConfigurationFactory;
import shendi.nat.client.bean.NATInfo;
import shendi.nat.client.bean.NATState;

/**
 * 隧道协议抽象类,继承此类来创建一个隧道协议.<br>
 * 创建的隧道协议需要在 protocol.properties 中进行配置.<br>
 * 配置形式为 协议名=类全路径.<br>
 * 如果要实现构造方法,请确保线程安全,请勿执行耗时操作.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public abstract class ClientProtocol {
	
	/**
	 * 死循环的标志位,true运行,false关闭.
	 */
	private boolean state = true;
	
	/**
	 * 开启此协议.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param id 隧道id.
	 */
	public final void start(String id) {
		NATInfo.getInfo().setState(NATState.在线);
		
		new Thread(() ->{
			onCreate();
			while (state) { run(); }
			onStop(id);
		}).start();
	}
	
	/**
	 * 关闭此协议,关闭后就代表此对象将被清理.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 */
	public final void stop() { NATInfo.getInfo().setState(NATState.离线); state = false; }
	
	/**
	 * 在被创建的时候调用.<br>
	 * 用于打开隧道和初始化.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 */
	protected abstract void onCreate();
	
	/**
	 * 执行具体操作逻辑.<br>
	 * 此方法会被持续调用,请确保线程安全.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 */
	protected abstract void run();
	
	/**
	 * 被关闭时调用.<br>
	 * 用于关闭隧道,关闭连接,恢复状态等.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 */
	protected abstract void onStop(String id);
	
	/**
	 * 创建对应协议的客户端.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param protocol 客户端使用的协议.
	 * @return {@link ClientCodeException}
	 * @throws Exception 反射出异常时抛出.
	 */
	public static ClientProtocol create(String protocol) throws Exception {
		return ((ClientProtocol)Class.forName(ConfigurationFactory.getConfig("protocol").getProperty(protocol)).getConstructor().newInstance());
	}
}
