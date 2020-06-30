package shendi.nat.server.protocol;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;

import shendi.kit.log.Log;
import shendi.nat.server.bean.NATInfo;
import shendi.nat.server.util.ProtocolUtils;

/**
 * TCP 协议类.<br>
 * 创建此类对象可以实现一个 TCP 的 NAT 穿透服务端.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public class TCPServer extends ServerProtocol {

	/**
	 * TCP 服务端.
	 */
	private ServerSocket server;
	
	/**
	 * 打开隧道的指定客户端(第一个连接的).
	 */
	private Socket client;
	
	/**
	 * 与客户端连接的输出流
	 */
	private BufferedOutputStream cOutput;
	
	/**
	 * 与客户端连接的输入流
	 */
	private BufferedInputStream cInput;
	
	/**
	 * 包含现有的用户连接.
	 */
	private HashMap<Integer,Socket> sockets = new HashMap<>();
	
	/**
	 * 用户的 id 标识.
	 */
	private int userId;
	
	@Override
	protected void onCreate(NATInfo info) {
		try {
			server = new ServerSocket(info.getServerPort());
			// 等待客户端连接(第一个连接),当然不避免客户端不连接的情况(恶意访问)
			// 对于不连接的 就造成了DDOS攻击,因为 accept() 是异步阻塞的,所以需要设置阻塞超时时间.
			// 超时会抛出 java.net.SocketTimeoutException.
			server.setSoTimeout(1000);
			client = server.accept();
			cInput = new BufferedInputStream(client.getInputStream());
			cOutput = new BufferedOutputStream(client.getOutputStream());
			
			// 将时间改回
			server.setSoTimeout(0);
			
			// 客户端只有一个,所以需要一个线程单独处理客户端发送数据.
			// 设置名称方便以后出问题调试.
			Thread t = new Thread(() -> {
				// data为未处理的数据,index是现在有效数据的下标.
				byte[] data = new byte[1024];
				int index = 0;
				
				try {
					// 记录上一次接收到的字节,用于判断结尾.
					byte upData = 0;
					
					while (true) {
						byte b = (byte) cInput.read();
						// 如果读到的数据为 -1 我们需要发送一下心跳包判断客户端是否存活
						if (b == -1) {
							try { cOutput.write(0); } catch (IOException e) {
								// 关闭此服务端
								stop();
								Log.printErr("客户端被关闭了,端口为: " + info.getServerPort() +",错误为: " + e.getMessage());
								break;
							}
						}
						
						// 扩容
						if (index >= data.length) {
							byte[] temp = data;
							data = new byte[temp.length << 1];
							System.arraycopy(temp, 0, data, 0, temp.length);
						}
						
						data[index++] = b;
						
						if (upData == -2 && b == -3) {
							// 转成字符串,进行解析,最后两个数据位去除
							String sData = new String(data,0,index-2);
							String[] datas = sData.split(";");
							
							switch (datas[0]) {
							// 将数据发给指定用户
							case "data":
								if (datas.length < 3) { Log.printAlarm("TCP协议类数据格式data类型错误 端口为: " + info.getPort() + ",数据为: " + sData); break; }
								BufferedOutputStream userOutput = new BufferedOutputStream(sockets.get(Integer.parseInt(datas[1])).getOutputStream());
								System.out.println("发送数据给用户 " + datas[2]);
								userOutput.write(datas[2].getBytes());
								userOutput.flush();
								break;
							// 默认,代表出错了
							default: Log.printAlarm("TCP协议类数据格式错误 端口为: " + info.getPort() + ",数据为: " + sData);
							}
							// 此条数据被解析完成,开始下一条.
							index = 0; upData = 0; data = new byte[data.length];
						}
						upData = b;
					}
				} catch (IOException e) { stop(); e.printStackTrace(); Log.printErr("TCP协议类接收客户端数据出错: " + e.getMessage()); }
			});
			t.setName("TCP服务端线程: " + info.getServerPort());
			t.start();
		} catch (SocketTimeoutException e) {
			// 关闭隧道
			Log.printAlarm("连接被关闭了: " + e.getMessage());
			stop();
		} catch (IOException e) { Log.printErr("TCP协议类onCreate初始化出错: " + e.getMessage()); }
	}

	@Override
	protected void run() {
		try {
			// 接收请求,将请求转发到客户端(客户端将请求转发到客户服务器).
			Socket socket = server.accept();
			// 新用户连接,
			new Thread(() -> {
				try (BufferedInputStream input = new BufferedInputStream(socket.getInputStream())) {
					// 存储此用户
					sockets.put(userId, socket);
					
					// 通知客户端有新用户
					cOutput.write(ProtocolUtils.serverNAT("new", userId)); cOutput.flush();
					System.out.println("有新用户连接了: " + userId);
					
					// 当前id.
					int id = userId;
					
					// 将用户发送的数据转发给客户端..
					// 一方关闭就会出异常,就直接将socket关闭
					while (true) {
						byte[] data = new byte[0];
						byte[] bytes = new byte[1024];
						int len = -1;
						while ((len = input.read(bytes)) != -1) {
							byte[] temp = data;
							data = new byte[temp.length + len];
							System.arraycopy(temp, 0, data, 0, temp.length);
							System.arraycopy(bytes, 0, data, temp.length, len);
						}
						
						// 用户将 Socket 关闭了,这里也直接将对应 Socket 关闭
						if (data.length == 0) {
							if (socket != null) socket.close();
							cOutput.write(ProtocolUtils.serverNAT("close", userId)); cOutput.flush();
							cOutput.flush();
							Log.print("用户关闭了连接,用户id: " + userId);
							break;
						}
						System.out.println("发送数据给客户端: " + new String(ProtocolUtils.serverNAT("data",id,data)));
						cOutput.write(ProtocolUtils.serverNAT("data",id,data));
						cOutput.flush();
					}
				} catch (IOException e) {
					e.printStackTrace();
					Log.printErr("客户端关闭了连接或用户处理出错: " + e.getMessage());
				}
				userId++;
			}).start();
		} catch (IOException e) { Log.printErr("与用户打开连接出错: " + e.getMessage()); }
		
	}

	@Override
	protected void onStop() {
		try { if (cInput != null) cInput.close(); } catch (IOException e) { Log.printErr("TCP协议类onStop 关闭input出错: " + e.getMessage()); }
		try { if (cOutput != null) cOutput.close(); } catch (IOException e) { Log.printErr("TCP协议类onStop 关闭output出错: " + e.getMessage()); }
		try { if (client != null) client.close(); } catch (IOException e) { Log.printErr("TCP协议类onStop 关闭client出错: " + e.getMessage()); }
		try { if (server != null) server.close(); } catch (IOException e) { Log.printErr("TCP协议类onStop 关闭server出错: " + e.getMessage()); }
	}

}
