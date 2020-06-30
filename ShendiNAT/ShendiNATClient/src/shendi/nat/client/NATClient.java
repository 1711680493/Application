package shendi.nat.client;

import shendi.kit.config.ConfigurationFactory;
import shendi.kit.log.Log;
import shendi.nat.client.bean.NATInfo;
import shendi.nat.client.control.Controller;
import shendi.nat.client.view.View;

/**
 * Start client application.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public class NATClient {
	
	/**
	 * 启动程序.<br>
	 * 通过读取配置文件调用指定 View 类 和 ClientProtocol 类.<br>
	 * 初始化隧道信息.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param args 参数,需要一个隧道id.
	 * @throws Exception 异常直接抛出去.
	 */
	public static void main(String[] args) throws Exception {
		// 将我的日志类不显示,我们通过视图类来进行显示输出
		Log.setIsLog(false);
		
		String id = args.length > 0 ? args[0] : null;
		// 开启视图.
		String view = ConfigurationFactory.getConfig("config").getProperty("client.view");
		View v = ((View)Class.forName(view).getConstructor().newInstance());
		v.onCreate();
		
		NATInfo.getInfo().setView(v);
		
		// 创建隧道控制器
		Controller c = Controller.create();
		
		// 有隧道 id,直接打开.
		if (id != null) {
			if (!c.open(id)) {
				String info = "隧道 [" + id + "] 打开失败";
				Log.printErr(info);
				v.sendInfo(info);
			}
		} else {
			// 根据用户输入隧道id进行打开.
			v.open(c);
		}
		
		// 显示视图.
		new Thread(() -> v.show()).start();;
	}
	
}
