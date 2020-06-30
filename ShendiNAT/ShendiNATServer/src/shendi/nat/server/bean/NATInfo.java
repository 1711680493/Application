package shendi.nat.server.bean;

import shendi.nat.server.protocol.ServerProtocol;

/**
 * 穿透信息,包含需要访问的ip和端口,状态等.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public class NATInfo {
	/**
	 * 内网电脑的 ip.
	 */
	private String ip;
	
	/**
	 * 内网电脑的端口.
	 */
	private int port;
	
	/**
	 * 服务端对应的端口(经济原因,这里应是域名)
	 */
	private int serverPort;
	
	/**
	 * 使用的什么协议.
	 */
	private String protocol;
	
	/**
	 * 当前穿透(隧道)状态.
	 */
	private NATState state = NATState.离线;

	/**
	 * 协议对应的服务端.
	 */
	private ServerProtocol server;
	
	/**
	 * 创建穿透信息.
	 * @param ip 内网电脑的ip
	 * @param port 内网电脑的端口
	 * @param serverPort 对应的服务器的端口
	 * @param protocol 使用的什么协议
	 */
	public NATInfo(String ip, int port, int serverPort,String protocol) {
		this.ip = ip;
		this.port = port;
		this.serverPort = serverPort;
		this.protocol = protocol;
	}

	public String getIp() { return ip; }

	public int getPort() { return port; }
	
	public int getServerPort() { return serverPort; }

	public String getProtocol() { return protocol; }
	
	public NATState getState() { return state; }
	public void setState(NATState state) { this.state = state; }
	
	public ServerProtocol getServer() { return server; }
	public void setServer(ServerProtocol server) { this.server = server;}
	
}
