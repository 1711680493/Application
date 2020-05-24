package shendi.web.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;

import shendi.kit.config.ConfigurationFactory;
import shendi.kit.log.Log;
import shendi.web.server.http.DefaultHttpRequest;
import shendi.web.server.http.DefaultHttpResponse;
import shendi.web.server.sws.ShendiWebServer;
import shendi.web.server.util.InputStreamUtils;

/**
 * 默认的服务器类,基于TCP实现.<br>
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public class DefaultServer extends Server {
	/**
	 * 服务端套接字 TCP.
	 */
	private ServerSocket server;
	
	public DefaultServer(int port,int connectMax) {
		super(port,connectMax);
		try {
			this.server = new ServerSocket(port);
		} catch (IOException e) {
			//服务端开启失败则提示并退出
			Log.printErr("服务器开启失败！请检查端口是否占用！" + e.getMessage());
			System.exit(0);
		}
	}

	@Override
	public void server() {
		//接收客户端的套接字 TCP 使用了 java7 的 try-with-resources
		//也就是 在括号内开启的 东西 会自动帮你关闭,也就不用写 finally了
		//TODO 这里有个小问题,有很多线程都在执行 accept(异步阻塞) 所以就导致了可能同时accept,有一个为空
		//TODO 问题不大,就是增加了个线程的开销.
		try (Socket socket = server.accept();
				BufferedInputStream input = new BufferedInputStream(socket.getInputStream());
				BufferedOutputStream output = new BufferedOutputStream(socket.getOutputStream())) {
			/*在 Servlet 中有 Request 与 Response
			 * 我们在请求中有时候处理错误请求则需要直接返回,所以先建立响应.*/
			DefaultHttpResponse response = new DefaultHttpResponse(output);
			DefaultHttpRequest request = new DefaultHttpRequest(input,response);
			//处理请求响应
			call(request, response);
			//响应数据
			response.finish();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 执行调用操作,处理请求和响应.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param request 请求
	 * @param response 响应
	 */
	public static void call(DefaultHttpRequest request,DefaultHttpResponse response) {
		/* 请求中如果没有进行响应操作,
		 * 则判断是否有资源,没有资源则代表为请求我们自己写的映射(Servlet),
		 * 如果两者都没有,则返回404 */
		if (request.isResponse) {
			//将系统路径存一份 (web 目录)
			StringBuilder sPath = new StringBuilder();
			sPath.append(System.getProperty("user.dir"));
			sPath.append(ConfigurationFactory.getConfig("config").getProperty("server.url"));
			//获取访问的路径
			String path = sPath.toString();
			/* 判断是否为项目根(对应 config.properties 中的 server.index 配置项) */
			if ("/".equals(request.getUrl())) {
				path += ConfigurationFactory.getConfig("config").getProperty("server.index");
			} else {
				path += request.getUrl();
			}
			String mapper = null;
			/* 先判断是否为文件 并且存在. */
			File file = new File(path);
			if (file != null && file.exists() && file.isFile()) {
				//路径不能包含 mapper 文件路径.
				if (path.contains(ConfigurationFactory.getConfig("config").getProperty("server.mapper"))) {
					response.write("不能访问系统目录.");
				} else {
					//返回此文件 
					response.write(InputStreamUtils.getFile(file));
				}
			/* 判断是否为映射 请在 mapper.properties 中配置自己的映射文件 */
			} else if ((mapper = ConfigurationFactory.getConfig("mapper").getProperty(request.getUrl())) != null) {
				//避免异常
				try {
					mapper(mapper, sPath, request, response);
				} catch (Exception | Error e) {
					response.setStatus(500);
					e.printStackTrace();
				}
			} else {
				//两者都无,为404(找不到页面)
				response.setStatus(404);	
			}
		}
	}
	
	/**
	 * 执行映射文件.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param mapper 映射文件全路径名.
	 * @param sPath web文件夹路径
	 * @param request 请求
	 * @param response 响应
	 */
	private static void mapper(String mapper,StringBuilder sPath,DefaultHttpRequest request,DefaultHttpResponse response) {
		//将 sPath 路径改到 web 下的 mapper 路径 的指定文件
		sPath.append(ConfigurationFactory.getConfig("config").getProperty("server.mapper"));
		//全路径名为 xxx.xxx.xxx 所以需要将 . 改为 /
		sPath.append(mapper.replace(".","/"));
		/* 判断文件是否为 sws 或者 class 文件,并执行对应解析 */
		File file = new File(sPath + ".sws");
		if (file.exists()) {
			//这里简单做一下处理 重定向与转发 后期在完善
			try (BufferedInputStream fInput = new BufferedInputStream(new FileInputStream(file))) {
				//只获取第一行数据判断
				var data = InputStreamUtils.readLine(fInput);
				if (data == null || !data.startsWith("重定向(\"")) {
					response.setStatus(500);
				} else {
					data = data.substring(data.indexOf('"') + 1,data.indexOf(')') - 1);
					response.redirect(data);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if ((file = new File(sPath + ".class")).exists()) {
			/* 查找类,传递类所在位置 和 全路径名 */
			Class<?> sws = SwsClassLoader.sws.loadSwsClass(mapper,file.getPath());
			//不管出错,因为调用方已 try catch 了
			try {
				//反射调用方法
				ShendiWebServer swsObject = (ShendiWebServer) sws.getDeclaredConstructor().newInstance();
				swsObject.sws(request, response);
			} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				response.setStatus(500);
				e.printStackTrace();
			}
		} else {
			//映射文件不合格 直接500
			response.setStatus(500);
		}
	}
	
}
