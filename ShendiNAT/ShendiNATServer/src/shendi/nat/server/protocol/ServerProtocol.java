package shendi.nat.server.protocol;

import shendi.nat.server.bean.NATInfo;
import shendi.nat.server.bean.NATState;

/**
 * 隧道协议抽象类,继承此类来创建一个隧道协议.<br>
 * 创建的隧道协议需要在 protocol.properties 中进行配置.<br>
 * 配置形式为 协议名=类全路径.<br>
 * 如果要实现构造方法,请确保线程安全,请勿执行耗时操作.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public abstract class ServerProtocol {
	
	/**
	 * 死循环的标志位,true运行,false关闭.
	 */
	private boolean state = true;
	
	/**
	 * 隧道信息.
	 */
	private NATInfo info;
	
	/**
	 * 开启此协议.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param info 隧道的信息.
	 */
	public final void start(NATInfo info) {
		info.setState(NATState.在线);
		this.info = info;
		
		new Thread(() ->{
			onCreate(info);
			while (state) { run(); }
		}).start();
	}
	
	/**
	 * 关闭此协议,关闭后就代表此对象将被清理.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 */
	public final void stop() { info.setState(NATState.离线); state = false; onStop(); }
	
	/**
	 * 在被创建的时候调用.<br>
	 * 用于初始化.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param info 隧道的信息.
	 */
	protected abstract void onCreate(NATInfo info);
	
	/**
	 * 执行具体操作逻辑.<br>
	 * 此方法会被持续调用,请确保线程安全.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 */
	protected abstract void run();
	
	/**
	 * 被关闭时调用.<br>
	 * 用于关闭连接,恢复状态等.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 */
	protected abstract void onStop();
	
	/**
	 * 获取当前隧道信息.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @return {@link NATInfo}
	 */
	public NATInfo getInfo() { return info; }
}
