package shendi.nat.client.bean;

import shendi.nat.client.protocol.ClientProtocol;
import shendi.nat.client.view.View;

/**
 * 穿透信息,包含需要访问的ip和端口,状态等.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public class NATInfo {
	/**
	 * 单例模式
	 */
	private NATInfo() {}
	private static final NATInfo INFO = new NATInfo();
	
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
	 * 协议对应的客户类.
	 */
	private ClientProtocol client;
	
	/**
	 * 当前视图
	 */
	private View view;
	
	/**
	 * 获取此类的单例对象.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @return {@link NATInfo}
	 */
	public static NATInfo getInfo() { return INFO; }
	
	/**
	 * 打开隧道的时候获取的信息.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param ip 内网ip
	 * @param port 内网服务端口
	 * @param serverPort 服务器端口
	 * @param protocol 通信协议
	 */
	public void open(String ip,int port,int serverPort,String protocol) {
		this.ip = ip;
		this.port = port;
		this.serverPort = serverPort;
		this.protocol = protocol;
		state = NATState.在线;
	}
	public String getIp() { return ip; }

	public int getPort() { return port; }
	
	public int getServerPort() { return serverPort; }

	public String getProtocol() { return protocol; }
	
	public NATState getState() { return state; }
	public void setState(NATState state) { this.state = state; }
	
	public ClientProtocol getClient() { return client; }
	public void setClient(ClientProtocol client) { this.client = client;}
	
	public View getView() { return view; }
	public void setView(View view) { this.view = view; }
}
