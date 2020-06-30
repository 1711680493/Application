package shendi.nat.server.receive;

/**
 * 接收端接口.<br>
 * 实现此接口并更改config.properties中的server.receive来让你的接收端运行.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public interface ServerReceive {

	/**
	 * 当被创建时被调用.<br>
	 * 里面应该实现开启服务端进行接收处理的操作.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 */
	void onCreate();
	
}
