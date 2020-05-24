package shendi.web.server.http;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 默认的 http 响应.<br>
 * 可以进行重定向等操作.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public class DefaultHttpResponse extends Response<BufferedOutputStream> {
	/**
	 * 数据头
	 */
	private StringBuilder head = new StringBuilder();
	
	/**
	 * 数据体
	 */
	private byte[] body = new byte[0];
	
	/**
	 * 默认状态 200
	 */
	private int status = 200;
	
	/**
	 * 响应是否还有效.默认为true,有效.<br>
	 * 在为 false 则不应在继续执行操作.
	 */
	public boolean isResponse = true;
	
	/**
	 * 是否还允许输出.
	 */
	private boolean isWrite = true;
	
	public static void main(String[] args) throws IOException {
		Socket socket = new Socket("www.baidu.com",80);
		OutputStream output = socket.getOutputStream();
		output.write(("GET / HTTP/1.1\r\n").getBytes());
		output.write(("HOST: www.baidu.com \r\n").getBytes());
		output.write("\r\n".getBytes());
		InputStream input = socket.getInputStream();
		byte[] bytes = new byte[1048576];
		int len = -1;
		while ((len = input.read(bytes)) != -1) {
			System.out.println(new String(bytes,0,len));
		}
		socket.close();
	}
	
	public DefaultHttpResponse(BufferedOutputStream output) {
		super(output);
	}

	@Override
	public void write(String data) {
		//响应失效直接出错提示
		if (!isWrite) {
			status = 500;
			throw new IllegalStateException("你不应在已经重定向的响应中执行write!");
		}
		//添加文字数据 先将字符串转数组
		byte[] bData = data.getBytes();
		byte[] temp = body;
		body = new byte[temp.length + bData.length];
		System.arraycopy(temp, 0, body, 0, temp.length);
		System.arraycopy(bData, 0, body, temp.length, bData.length);
	}

	@Override
	public void write(byte[] data) {
		//响应失效直接出错提示
		if (!isWrite) {
			status = 500;
			throw new IllegalStateException("你不应在已经重定向的响应中执行write!");
		}
		byte[] temp = body;
		body = new byte[temp.length + data.length];
		System.arraycopy(temp, 0, body, 0, temp.length);
		System.arraycopy(data, 0, body, temp.length, data.length);
	}
	
	@Override
	public void write(byte[] data, int offset, int len) {
		//响应失效直接出错提示
		if (!isWrite) {
			status = 500;
			throw new IllegalStateException("你不应在已经重定向的响应中执行write!");
		}
		byte[] temp = body;
		body = new byte[temp.length + len - offset];
		System.arraycopy(temp, 0, body, 0, temp.length);
		System.arraycopy(data, offset, body, temp.length, len);
	}
	
	/**
	 * 设置响应状态.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param status
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * 完成响应,会将数据头 数据体都输出到客户端.<br>
	 * 此方法只会执行一次.(第二次则不作操作).<br>
	 * 在 映射类 中不应该调用此方法.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 */
	public void finish() {
		if (isResponse) {
			//改变状态
			isResponse = false;
			//获取响应状态,例如404等跳固定模板 这里只处理404 和 500 可更改为页面
			if (404 == status) {
				body = "<h1>找不到你所要的页面</h1>".getBytes();
			} else if (500 == status) {
				body = "<h1>您的请求出错了,请稍后再试.</h1>".getBytes();
			}
			//响应数据 返回状态--请求头--换行--请求体
			byte[] data = ("HTTP/1.0 " + status + " OK\r\n" + head + "\r\n").getBytes();
			byte[] temp = data;
			data = new byte[temp.length + body.length];
			System.arraycopy(temp, 0, data, 0, temp.length);
			System.arraycopy(body, 0, data, temp.length, body.length);
			try {
				output.write(data);
				output.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 添加请求头.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param name 请求名
	 * @param value 请求值
	 */
	public void addHeader(String name,String value) {
		head.append(name + ':' + value + "\r\n");
	}

	/**
	 * 重定向.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param path 路径
	 */
	public void redirect(String path) {
		status = 302;
		addHeader("Location", path);
		//数据体清空
		body = new byte[0];
		//关闭任何的输出
		isWrite = false;
	}
	
	@Override
	public void setCharacter(String encode) {
		super.setCharacter(encode);
		//改变响应头
		addHeader("Content-Type", "text/html;charset=" + encode);
	}
	
}
