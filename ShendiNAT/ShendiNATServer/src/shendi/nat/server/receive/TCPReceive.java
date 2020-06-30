package shendi.nat.server.receive;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

import shendi.kit.config.ConfigurationFactory;
import shendi.kit.log.Log;
import shendi.nat.server.bean.NATInfo;
import shendi.nat.server.bean.NATState;
import shendi.nat.server.protocol.ServerProtocol;
import shendi.nat.server.util.ProtocolUtils;

/**
 * TCP 接收端.<br>
 * 接收到消息后进行处理,如果消息是正确的则会创建指定服务端.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public class TCPReceive implements ServerReceive {
	
	/**
	 * TCP 服务端套接字.
	 */
	private ServerSocket server;
	
	/**
	 * 当前用户开启的端口.<br>
	 * 隧道id=隧道信息的形式.
	 */
	private static final HashMap<String,NATInfo> NAT_INFOS = new HashMap<>();
	
	/**
	 * 分配的,隧道id
	 */
	private static int id = 0;
	
	public TCPReceive() throws IOException {
		// 创建服务端,绑定指定端口,端口从配置文件获取
		String port = ConfigurationFactory.getConfig("config").getProperty("server.port");
		server = new ServerSocket(Integer.parseInt(port));
	}
	
	@Override public void onCreate() {
		while (true) {
			try {
				Socket socket = server.accept();
				// 开启线程
				new Thread(() -> {
					try (BufferedInputStream input = new BufferedInputStream(socket.getInputStream());
							BufferedOutputStream output = new BufferedOutputStream(socket.getOutputStream())) {
						// 接收的数据不会大于 1024 字节
						byte[] data = new byte[1024];
						int len = input.read(data);
						
						// 处理数据
						if (len > 0) {
							String sData = new String(data,0,len);
							String[] datas = sData.split(";");
							
							// 获取到请求 IP 地址
							String requestIp = socket.getInetAddress().getHostAddress();
							
							// 加锁,避免高并发出问题
							synchronized (NAT_INFOS) {
								// 获取到数据类型 按照正常思路来,数组越界也就等于数据是错误的
								switch (datas[0]) {
								// 添加隧道,增加一个id进入map里然后返回数据就ok了.
								case "add":
									// 格式为 add;ip;端口;协议
									// 这里的 ip 和端口都是客户端的.
									String ip = datas[1];
									int port = Integer.parseInt(datas[2]);
									int serverPort = Integer.parseInt(datas[3]);
									String protocol = datas[4];
									
									// 判断端口是否占用,占用则提醒,
									try {
										new ServerSocket(serverPort).close();
										// 返回信息,返回格式为 ok;id;端口
										NAT_INFOS.put(String.valueOf(id), new NATInfo(ip, port, serverPort, protocol));
										
										StringBuilder b = new StringBuilder();
										b.append("ok;"); b.append(id); b.append(';'); b.append(serverPort);
										output.write(b.toString().getBytes()); output.flush();
										Log.print("添加了客户端: " + id + '-' + ip + ';' + port);
										id++;
									} catch (IOException e) {
										Log.printAlarm("TCP接收器新增隧道失败,端口被占用,客户请求 ip: " + requestIp);
										output.write("error;端口被占用,请更换端口".getBytes()); output.flush();
									}
									break;
								// 开启隧道,先判断有无隧道,有则创建服务端改变隧道信息.
								case "open":
									String id = datas[1];
									if (NAT_INFOS.containsKey(id)) {
										// 如果已经在线则提示ok,否则创建指定隧道类并启动.
										NATInfo info = NAT_INFOS.get(id);
										
										StringBuilder b = new StringBuilder();
										b.append("ok;"); b.append(info.getIp()); b.append(';'); b.append(info.getPort());
										b.append(';'); b.append(info.getServerPort()); b.append(';'); b.append(info.getProtocol());
										
										if (info.getState() == NATState.在线) {
											output.write(b.toString().getBytes()); output.flush();
											Log.printAlarm("TCP接收器开启隧道,隧道已经是开启的,客户请求 ip: " + requestIp);
										} else {
											// 通过配置文件获取到指定协议类,如果没有则出错
											// 这里最好是将用户拉黑... 因为一般都是同过不正当渠道...
											String pClass = ConfigurationFactory.getConfig("protocol").getProperty(info.getProtocol());
											if (pClass == null) { Log.printAlarm("TCP接收器开启隧道,没有指定协议！协议为: " + info.getProtocol() + ",客户请求 ip: " + requestIp); return; }
											try {
												ServerProtocol server = (ServerProtocol) Class.forName(pClass).getConstructor().newInstance();
												// 设置隧道服务器
												info.setServer(server);
												
												output.write(b.toString().getBytes()); output.flush();
												
												server.start(info);
												Log.print("隧道被打开了: " + id + '-' + info.getIp() + ':' + info.getPort());
											} catch (ClassNotFoundException e) { Log.printErr("TCP接收器开启隧道,类找不到: " + pClass + ",客户请求 ip: " + requestIp);
											} catch (Exception e) { Log.printErr("TCP接收器开启隧道,出大问题了!!!客户请求 ip: " + requestIp + "\n错误信息: " + e.getMessage()); }
										}
									} else {
										output.write("error;没有此id".getBytes()); output.flush();
										Log.printAlarm("TCP接收器开启隧道没有此id,客户请求 ip: " + requestIp);
									}
									break;
								// 关闭隧道,无任何返回.执行完后调用一次 GC.
								case "close":
									id = datas[1];
									if (NAT_INFOS.containsKey(id)) {
										NATInfo info = NAT_INFOS.get(id);
										if (info.getState() == NATState.离线) { Log.printAlarm("TCP接收器关闭隧道已经是关闭的,客户请求 ip: " + requestIp); }
										else if (info.getServer() == null) { Log.printAlarm("TCP接收器关闭隧道没有此id,客户请求 ip: " + requestIp); }
										else { info.getServer().stop(); info.setServer(null); }
									} else { Log.printAlarm("TCP接收器关闭隧道没有此id,客户请求 ip: " + requestIp); }
									System.gc();
									break;
								default: Log.printAlarm("TCP 接收器接收到不合法数据: " + new String(data));
								}
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
						Log.printErr("接收到数据,执行解析操作失败: " + e.getMessage());
					} finally {
						try {
							if (socket != null)	socket.close();
						} catch (IOException e) { Log.printErr("Socket关闭失败: " + e.getMessage()); }
					}
				}).start();
			} catch (IOException e) { Log.printErr("接收到数据,获取Socket失败: " + e.getMessage()); }
		}
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		Socket socket = new Socket("127.0.0.1",9999);
		BufferedOutputStream output = new BufferedOutputStream(socket.getOutputStream());
		BufferedInputStream input = new BufferedInputStream(socket.getInputStream());
		byte[] b = new byte[1024];
		// 添加
		output.write("add;127.0.0.1;8888;10000;tcp".getBytes());
		output.flush();
		// 获取到隧道id
		int len = input.read(b);
		String addReceive = new String(b,0,len);
		String id = addReceive.split(";")[1];
//		System.out.println(addReceive);
		// 将之前的关闭,因为一个请求只能做一次操作
		output.close();
		input.close();
		socket.close();
//		if (true) return;
		socket = new Socket("127.0.0.1",9999);
		output = new BufferedOutputStream(socket.getOutputStream());
		input = new BufferedInputStream(socket.getInputStream());
		// 打开
		output.write(("open;"+id).getBytes());
		output.flush();
		len = input.read(b);
		System.out.println(new String(b,0,len));
		socket.close();
		
		// 将之前的关闭,因为一个请求只能做一次操作
		output.close();
		input.close();
		socket.close();
		
		/*
		socket = new Socket("127.0.0.1",9999);
		output = new BufferedOutputStream(socket.getOutputStream());
		// 关闭
		output.write(("close;"+id).getBytes());
		output.flush();
		output.close();
		socket.close();
		*/
		
		// 测试 服务端 客户端 用户访问
		// 8888是内网端口,我们要打开, 然后通过 10000 来访问到8888
		// 这里是等于创建 内网的服务端
		ServerSocket server = new ServerSocket(8888);
		new Thread(() -> {
			try (Socket s = server.accept();
					OutputStream sOutput = s.getOutputStream()) {
				// 服务端发送的数据不用什么协议
				sOutput.write("我是内网的服务器,hello, world".getBytes());
				sOutput.flush();
				
				InputStream sInput = s.getInputStream();
				byte[] sByte = new byte[1024];
				int sLen = sInput.read(sByte);
				System.out.println("内网服务端接收到数据: " + new String(sByte,0,sLen));
				
				server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
		
		// 客户端
		// 10000端口是打开的服务端,我们这里创建连接 当做客户端
		// 可以通过打开的时候获取到端口和ip
		socket = new Socket("127.0.0.1", 10000);
		input = new BufferedInputStream(socket.getInputStream());
		output = new BufferedOutputStream(socket.getOutputStream());
		
		// 用户端 开启后发送数据
		new Thread(() -> {
			try {
				Socket userSocket = new Socket("127.0.0.1", 10000);
				// 接收数据,开启后就创建了连接,连接被创建就会收到服务端发来的数据
				InputStream userInput = userSocket.getInputStream();
				byte[] userByte = new byte[1024];
				int userLen = userInput.read(userByte);
				System.out.println("用户接收到数据: " + new String(userByte,0,userLen));
				
				OutputStream userOutput = userSocket.getOutputStream();
				userOutput.write("我是用户,hello, world".getBytes());
				userOutput.flush();
				userSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
		
		// 处理客户端的接收操作
		b = new byte[1024];
		len = input.read(b);
		System.out.println("客户端接收到数据(新用户来了): " + new String(b,0,len));
		// 上面这个数据应该是创建的数据格式,我们创建一个新Socket与内网服务端连接
		// new;id,id应该为0,第一个用户
		Socket serverUserSocket = new Socket("127.0.0.1",8888);
		InputStream serverUserInput = serverUserSocket.getInputStream();
		b = new byte[1024];
		len = serverUserInput.read(b);
		// 接收到内网服务端发的数据直接发给服务端->服务端发给用户]
		System.out.println("客户端接收到内网服务端数据,并发送给服务端: " + new String(b,0,len));
		output.write(ProtocolUtils.serverNAT("data", 0,new String(b,0,len)));
		output.flush();
		
		// 接收用户->服务端发来的数据
		// 这里粘包了,没有问题,我们写客户端的时候会进行处理.
		b = new byte[1024];
		len = input.read(b);
		String userData = new String(b,0,len);
		System.out.println("客户端接收到数据,并发给内网服务端: " + userData);
		OutputStream serverUserOutput = serverUserSocket.getOutputStream();
		{
			byte[] bytes = userData.split(";")[2].getBytes();
			serverUserOutput.write(new String(bytes,0,bytes.length - 2).getBytes());
		}
		serverUserOutput.close();
		serverUserInput.close();
		serverUserSocket.close();
		
//		while(true);
		output.close();
		input.close();
		socket.close();
	}
	
}
