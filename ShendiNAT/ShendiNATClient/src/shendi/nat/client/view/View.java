package shendi.nat.client.view;

import shendi.nat.client.control.Controller;

/**
 * 视图接口,需要自定义UI请实现此类.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public interface View {
	
	/**
	 * 视图被创建时调用,用于初始化.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 */
	void onCreate();
	
	/**
	 * 显示视图,用于启动显示界面逻辑,异步运行.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 */
	void show();
	
	/**
	 * 打开指定隧道,如果最开始没有隧道id则会调用此方法.<br>
	 * 需要接收用户输入的隧道id进行打开.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param control 隧道控制器.
	 */
	void open(Controller control);
	
	/**
	 * 用户访问的时候调用.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param info 用户的访问信息
	 */
	void userConnect(String info);
	
	/**
	 * 发送信息,实现应该将此信息显示给用户,
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param info 信息
	 */
	void sendInfo(String info);
	
}
