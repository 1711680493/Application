package shendi.nat.client.protocol;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;

import shendi.kit.config.ConfigurationFactory;
import shendi.kit.log.Log;
import shendi.nat.client.bean.NATInfo;
import shendi.nat.client.util.ProtocolUtils;

/**
 * TCP 协议类.<br>
 * 创建此类对象可以实现一个 TCP 的 NAT 穿透服务端.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public class TCPClient extends ClientProtocol {

	/**
	 * 打开隧道的指定客户端(第一个连接的).
	 */
	private Socket client;
	
	/**
	 * 与客户端连接的输出流
	 */
	private BufferedOutputStream output;
	
	/**
	 * 与客户端连接的输入流
	 */
	private BufferedInputStream input;
	
	/**
	 * 包含现有的用户连接.
	 */
	private HashMap<String,Socket> sockets = new HashMap<>();
	
	@Override
	protected void onCreate() {
		// 这里有一点不合理的地方就是,我们在打开隧道的时候应该需要获取的是一串url,而不只是端口
		// url可以为域名,或者ip+端口的形式,这里只能指定一个服务端.
		try {
			client = new Socket(ConfigurationFactory.getConfig("config").getProperty("server.url").split(":")[0],NATInfo.getInfo().getServerPort());
			output = new BufferedOutputStream(client.getOutputStream());
			input = new BufferedInputStream(client.getInputStream());
		} catch (IOException e) {
			Log.printErr("在初始化客户端出错: " + e.getMessage());
			NATInfo.getInfo().getView().sendInfo("在初始化客户端出错,请重新打开隧道: " + e.getMessage());
		}
	}

	// data为未处理的数据,index是现在有效数据的下标.
	private byte[] data = new byte[1024];
	private int index = 0;
	
	@Override
	protected void run() {
		try {
			byte b = (byte) input.read();
			// 如果读到的数据为 -1 我们需要发送一下心跳包判断服务端是否存活
			if (b == -1) {
				try { output.write(0); } catch (IOException e) {
					// 关闭此服务端
					stop();
					Log.printErr("与服务器的连接已断线!");
					NATInfo.getInfo().getView().sendInfo("与服务器的连接已断线!");
					return;
				}
			}
			
			// 扩容
			if (index >= data.length) {
				byte[] temp = data;
				data = new byte[temp.length << 1];
				System.arraycopy(temp, 0, data, 0, temp.length);
			}
			
			data[index++] = b;
			
			if (ProtocolUtils.isData(data, index)) {
				// 转成字符串,进行解析,最后两个数据位去除
				String sData = new String(data,0,index-2);
				System.out.println(sData);
				String[] datas = sData.split(";");
				
				// 这一部分解析可以封装到协议类里,但是太麻烦,直接硬写了.
				switch (datas[0]) {
				// 新增用户连接,直接创建一个与内网服务端的连接,并与id存入map.
				case "new":
					if (datas.length < 2) { Log.printAlarm("TCP协议类数据格式new类型错误: " + sData); NATInfo.getInfo().getView().sendInfo("TCP协议类数据格式new类型错误: " + sData); break; }
					System.out.println(NATInfo.getInfo().getIp() + ":" + NATInfo.getInfo().getPort());
					Socket socket = new Socket(NATInfo.getInfo().getIp(),NATInfo.getInfo().getPort());
					sockets.put(datas[1], socket);
					System.out.println(datas[1] + "-已打开和内网服务端的连接.");
					// 开启线程执行接收内网服务端的数据
					new Thread(() -> {
						// 将 id 存起来,用于自己告诉客户端自己是谁
						String id = datas[1];
						try (BufferedInputStream input = new BufferedInputStream(socket.getInputStream())) {
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
								
								// 内网服务端将 Socket 关闭了,这里也直接将对应 Socket 关闭
								if (data.length == 0) {
									if (socket != null) socket.close();
									Log.print("内网服务端断开指定用户,id: " + id);
									NATInfo.getInfo().getView().sendInfo("内网服务端断开指定用户,id: " + id);
									break;
								}
								
								// 发送数据给服务端
								System.out.println(new String(ProtocolUtils.serverNAT("data", data)));
								output.write(ProtocolUtils.serverNAT("data", data));
								output.flush();
							}
						// 我们关闭指定用户方式为直接关闭 socket,所以就会出此异常,格外处理.
						} catch (SocketException e) {
							Log.printErr("用户离开了 id=" + id);
							NATInfo.getInfo().getView().sendInfo("用户离开了 id=" + id);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}).start();
					break;
				// 将数据发给内网服务端
				case "data":
					if (datas.length < 3) { Log.printAlarm("TCP协议类数据格式data类型错误: " + sData); NATInfo.getInfo().getView().sendInfo("TCP协议类数据格式data类型错误: " + sData); break; }
					BufferedOutputStream serverOutput = new BufferedOutputStream(sockets.get(datas[1]).getOutputStream());
					System.out.println("数据发往内网服务端: " + datas[2]);
					serverOutput.write(datas[2].getBytes());
					serverOutput.flush();
					break;
				// 关闭对应用户与内网服务端的连接
				case "close":
					if (datas.length < 2) { Log.printAlarm("TCP协议类数据格式close类型错误: " + sData); NATInfo.getInfo().getView().sendInfo("TCP协议类数据格式close类型错误: " + sData); break; }
					sockets.remove(datas[1]).close();
					break;
				// 默认,代表出错了
				default: Log.printAlarm("TCP协议类数据格式错误: " + sData); NATInfo.getInfo().getView().sendInfo("TCP协议类数据格式错误: " + sData);
				}
				// 此条数据被解析完成,开始下一条.
				index = 0; data = new byte[data.length];
			}
		} catch (IOException e) { stop(); Log.printErr("TCP协议类接收服务端数据出错: " + e.getMessage()); NATInfo.getInfo().getView().sendInfo("TCP协议类接收服务端数据出错: " + e.getMessage());}
	}

	@Override
	protected void onStop(String id) {
		try { if (input != null) input.close(); } catch (IOException e) { Log.printErr("TCP 隧道 input 关闭出错: " + e.getMessage()); NATInfo.getInfo().getView().sendInfo("TCP 隧道 output 关闭出错: " + e.getMessage()); }
		try { if (output != null) output.close(); } catch (IOException e) { Log.printErr("TCP 隧道 output 关闭出错: " + e.getMessage()); NATInfo.getInfo().getView().sendInfo("TCP 隧道 output 关闭出错: " + e.getMessage()); }
		try { if (client != null) client.close(); } catch (IOException e) { Log.printErr("TCP 隧道 socket 关闭出错: " + e.getMessage()); NATInfo.getInfo().getView().sendInfo("TCP 隧道 socket 关闭出错: " + e.getMessage()); }
		// 关闭所有用户的连接
		sockets.forEach((k,v) -> { try { v.close(); sockets.remove(k); } catch (IOException e) { e.printStackTrace(); } });
		// 推荐进行一次 GC
		System.gc();
	}
	
}
