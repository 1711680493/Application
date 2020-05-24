package shendi.web.server.http;

/**
 * 响应类,继承此类来实现对一个请求的响应.<br>
 * 为了扩展,这里使用一个泛型来作为输出流的类型(后期可能会有不同的流等).
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public abstract class Response<T> {
	/**
	 * 数据读取的输出流
	 */
	protected T output;
	
	/**
	 * 数据解析时的编码 默认为UTF-8
	 */
	protected String encode = "UTF-8";
	
	/**
	 * 初始化输出流.<br>
	 * @param output 输入流,用于响应数据.
	 */
	public Response(T output) {
		this.output = output;
	}
	
	/**
	 * 设置读取解析的编码.<br>
	 * 实现的子类请在输出数据的时候使用变量 encode 来进行处理编码.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param encode 编码
	 */
	public void setCharacter(String encode) {
		this.encode = encode;
	}
	
	/**
	 * 输出数据,请统一编码规范.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param data 要输出的数据体.
	 */
	public abstract void write(String data);
	
	/**
	 * 输出指定字节数组.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param data 字节数组.
	 */
	public abstract void write(byte[] data);
	
	/**
	 * 输出指定字节数组,如果数组内有无效数据,则使用此.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param data 字节数组.
	 * @param offset 起始偏移
	 * @param len 读取的长度
	 */
	public abstract void write(byte[] data,int offset,int len);
}