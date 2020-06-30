package shendi.nat.client.view;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import shendi.kit.config.ConfigurationFactory;
import shendi.kit.log.Log;
import shendi.nat.client.bean.NATInfo;
import shendi.nat.client.bean.NATState;
import shendi.nat.client.control.Controller;

/**
 * 控制台视图.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public class ConsoleView implements View {

	/**
	 * 用于与用户交互的扫描器.
	 */
	private Scanner sc;
	
	@Override
	public void onCreate() { sc = new Scanner(System.in); }

	@Override
	public void show() {
		while (true) {
			// 通过现在隧道状态来分界面
			String input;
			// 隧道在线
			if (NATState.在线 == NATInfo.getInfo().getState()) {
				sendInfo("* * * * * * 隧道在线 * * * * * *");
				sendInfo("exit\t\t退出");
				sendInfo("* * * * * * * * * * * * * * * *");
				input = sc.nextLine();
				// 过滤掉空字符
				while ("".equals(input)) input = sc.nextLine();
				String[] data = input.split(" ");
				switch (data[0]) {
				case "exit":
					System.exit(0);
					break;
				}
				
			// 隧道离线
			} else if (NATState.离线 == NATInfo.getInfo().getState()) {
				sendInfo("* * * * * * 隧道离线 * * * * * *");
				sendInfo("add [ip] [内网服务端端口] [需要的外网服务端端口] [隧道协议]\t添加隧道");
				sendInfo("open [隧道id]\t\t\t\t打开隧道");
				sendInfo("exit\t\t\t\t\t退出");
				sendInfo("* * * * * * * * * * * * * * * *");
				input = sc.nextLine();
				// 过滤掉空字符
				while ("".equals(input)) input = sc.nextLine();
				
				String[] data = input.split(" ");
				switch (data[0]) {
				// 添加隧道操作,这里不做封装了,因为添加隧道不应在此处进行.
				case "add":
					{
						if (data.length < 5) System.out.println("添加隧道需要的参数不够(空格隔开)");
						String[] url = ConfigurationFactory.getConfig("config").getProperty("server.url").split(":");
						try (Socket socket = new Socket(url[0],Integer.parseInt(url[1]));
								BufferedOutputStream output = new BufferedOutputStream(socket.getOutputStream());
								BufferedInputStream i = new BufferedInputStream(socket.getInputStream())) {
							output.write(("add;" + data[1] + ';' + data[2] + ';' + data[3] +';' + data[4]).getBytes());
							output.flush();
							// 获取到隧道id
							byte[] b = new byte[1024];
							int len = i.read(b);
							String addReceive = new String(b,0,len);
							String id = addReceive.split(";")[1];
							System.out.println("添加成功,你的隧道id为: " + id);
						} catch (IOException io) {
							io.printStackTrace();
						}
					}
					break;
				case "open":
					if (data.length > 1 && !"".equals(data[1])) {
						try {
							if (!Controller.create().open(data[1])) {
								String info = "隧道 [" + data[1] + "] 打开失败";
								Log.printErr(info);
								NATInfo.getInfo().getView().sendInfo(info);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else { System.out.println("使用open命令的方法为: open 隧道id"); }
					break;
				case "exit":
					System.exit(0);
					break;
				}
			} else {
				System.err.println("控制台出现未知错误!");
				// 等待用户输入后退出
				try {
					Runtime.getRuntime().exec("cmd /c pause");
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.exit(0);
			}
		}
	}
	
	@Override
	public void open(Controller control) {
		System.out.print("请输入隧道 id: ");
		String id = sc.next();
		
		if (id == null || "".equals(id)) {
			System.out.println("隧道id不能为空,请重新打开!");
		} else {
			if (!control.open(id)) {
				String info = "隧道 [" + id + "] 打开失败";
				Log.printErr(info);
				NATInfo.getInfo().getView().sendInfo(info);
			}
		}
	}
	
	@Override
	public void userConnect(String info) {
		System.err.println(info);
	}

	@Override
	public void sendInfo(String info) {
		System.out.println(info);
	}

}
