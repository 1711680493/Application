package shendi.nat.client.control;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import shendi.kit.config.ConfigurationFactory;
import shendi.kit.log.Log;
import shendi.nat.client.bean.NATInfo;
import shendi.nat.client.protocol.ClientProtocol;
import shendi.nat.client.util.ProtocolUtils;

/**
 * 使用 TCP 协议的控制器,
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public class TCPController extends Controller {

	@Override
	public boolean open(String id) {
		String[] url = ConfigurationFactory.getConfig("config").getProperty("server.url").split(":");
		try (Socket socket = new Socket(url[0],Integer.parseInt(url[1]));
				InputStream input = socket.getInputStream();
				OutputStream output = socket.getOutputStream()) {
			output.write(ProtocolUtils.open(id));
			output.flush();
			
			// 接收服务端传来的数据并解析.
			byte[] data = new byte[0];
			byte[] bytes = new byte[1024];
			int len = -1;
			while ((len = input.read(bytes)) != -1) {
				byte[] temp = data;
				data = new byte[temp.length + len];
				System.arraycopy(temp, 0, data, 0, temp.length);
				System.arraycopy(bytes, 0, data, temp.length, len);
			}
			// 解析数据成功则打开与指定服务端的连接
			if (ProtocolUtils.analysisOpen(new String(data))) {
				try {
					ClientProtocol.create(NATInfo.getInfo().getProtocol()).start(id);
					return true;
				} catch (Exception e) {
					Log.printErr("打开隧道,创建客户端错误,请重新打开: " + e.getMessage());
					NATInfo.getInfo().getView().sendInfo("打开隧道,创建客户端错误,请重新打开: " + e.getMessage());
				}
			}
		} catch (IOException e) {
			Log.printErr("打开隧道错误: " + e.getMessage());
			NATInfo.getInfo().getView().sendInfo("打开隧道错误: " + e.getMessage());
		}
		return false;
	}

	@Override
	public void close(String id) {
		String[] url = ConfigurationFactory.getConfig("config").getProperty("server.url").split(":");
		try (Socket socket = new Socket(url[0],Integer.parseInt(url[1]));
				OutputStream output = socket.getOutputStream()) {
			output.write(ProtocolUtils.close(id));
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
			Log.printErr("关闭隧道错误: " + e.getMessage());
			NATInfo.getInfo().getView().sendInfo("关闭隧道错误: " + e.getMessage());
		}
	}
	
}
