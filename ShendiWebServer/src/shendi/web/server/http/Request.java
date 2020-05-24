package shendi.web.server.http;

import java.io.UnsupportedEncodingException;

/**
 * 请求类,继承此类来实现对一个请求的处理.<br>
 * 为了扩展,这里使用一个泛型来作为输入流的类型(后期可能会有不同的流等).
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public abstract class Request<T> {
	/**
	 * 数据读取的输入流
	 */
	protected T input;
	
	/**
	 * 数据解析时的编码 默认为UTF-8
	 */
	protected String encode = "UTF-8";
	
	/**
	 * 请求的所有数据的字节形式.
	 */
	protected byte[] datas;
	
	/**
	 * 初始化输入流.<br>
	 * 在这里应该将数据都读取到字节数组 {@link #datas} 中.
	 * @param input 输入流,用于读取请求头等.
	 */
	public Request(T input) {
		this.input = input;
	}
	
	/**
	 * 设置读取解析的编码.<br>
	 * 实现的子类请在解析的时候使用变量 encode 来进行处理编码.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param encode 编码
	 */
	public void setCharacter(String encode) {
		this.encode = encode;
	}
	
	/**
	 * 获取此请求的数据的字节数组形式
	 */
	public byte[] getData() {
		return datas;
	}
	
	/**
	 * 获取此请求的数据的字符串形式
	 */
	public String getDataString() {
		try {
			return new String(datas,encode);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 获取读取流.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @return 默认为 BufferedInputStream.
	 */
	public T getInputStream() {
		return input;
	}
}
