package shendi.nat.server;

import shendi.kit.config.ConfigurationFactory;
import shendi.nat.server.receive.ServerReceive;

/**
 * Start this application.<br>
 * 开启这个程序,英文注释看起来很美好.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public class NATServer {
	
	public static void main(String[] args) throws Exception {
		// Start receive server
		String receiveServer = ConfigurationFactory.getConfig("config").getProperty("server.receive");
		((ServerReceive)Class.forName(receiveServer).getConstructor().newInstance()).onCreate();
	}
	
}
