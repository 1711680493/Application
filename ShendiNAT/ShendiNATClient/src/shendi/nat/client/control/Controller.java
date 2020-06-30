package shendi.nat.client.control;

import shendi.kit.config.ConfigurationFactory;
import shendi.nat.client.bean.NATInfo;

/**
 * 隧道控制器,用于打开关闭隧道.<br>
 * 实现此类来制作指定协议的隧道控制器.<br>
 * 控制不需要经常使用,所以基本上是一次性的.<br>
 * 通过 {@link #create()} 获取当前控制器.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 * @see TCPController
 */
public abstract class Controller {
	
	/**
	 * 打开指定隧道,子类实现需要将解析后的信息存入 {@link NATInfo#open(String, int, int, String)}.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param id 隧道id.
	 * @return 打开是否成功.
	 */
	public abstract boolean open(String id);
	
	/**
	 * 关闭指定隧道,一般情况下不用知道关闭是否成功.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param id 隧道id.
	 */
	public abstract void close(String id);
	
	/**
	 * 创建控制器,通过配置文件 config.properties 中的 client.controller.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @return {@link Controller},当前程序使用的控制器
	 * @throws Exception 抛出的异常,出问题无非就是配置错误/反射错误/转换错误.
	 */
	public static Controller create() throws Exception {
		return ((Controller)Class.forName(ConfigurationFactory.getConfig("config").getProperty("client.controller")).getConstructor().newInstance());
	}
	
}
