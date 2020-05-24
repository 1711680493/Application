package shendi.web.server.http;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.HashMap;

import shendi.web.server.DefaultServer;
import shendi.web.server.util.InputStreamUtils;

/**
 * 默认的 http 请求处理.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public class DefaultHttpRequest extends Request<BufferedInputStream> {
	/**
	 * 是否需要响应 默认为true
	 */
	public boolean isResponse = true;
	
	/**
	 * 请求资源的信息(数据第一行).
	 */
	private String request;
	
	/**
	 * 所有的数据头信息.
	 */
	private HashMap<String,String> headers = new HashMap<>();
	
	/**
	 * 数据头的字符串表示形式.
	 */
	private StringBuilder headersText = new StringBuilder();
	
	/**
	 * 请求的参数.
	 */
	private HashMap<String,String> parameters = new HashMap<>();
	
	/**
	 * 响应,用于结果失败直接返回响应.
	 */
	private DefaultHttpResponse response;
	
	/**
	 * 请求类型
	 */
	private String method;
	
	/**
	 * 请求的路径.
	 */
	private String url;
	
	/**
	 * 请求中的参数.可用于转发.
	 */
	private HashMap<String,Object> attributes = new HashMap<>();
	
	public DefaultHttpRequest(BufferedInputStream input,DefaultHttpResponse response) {
		super(input);
		this.response = response;
		//读取数据头 并解析
		readHead();
	}
	
	/**
	 * 读取数据头.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 */
	protected void readHead() {
		try {
			/* 数据的第一行为请求的类型 资源路径
			 * 例如 GET / HTTP/1.1 可以看出 第一个为类型,第二个为路径,第三个为协议,空格分隔开来
			 * 如果没有此行,代表不是 HTTP 协议,直接返回 500.*/
			request = InputStreamUtils.readLine(input);
			/* 一个无效的请求,直接不管 */
			if (request == null) {
				response.setStatus(500);
				isResponse = false;
				return;
			}
			String types[] = request.split(" ");
			if (types == null || types.length < 2) {
				//返回 500
				response.write("<h1>请使用Http协议！<h1>".getBytes(encode));
				response.setStatus(500);
				isResponse = false;
				return;
			}
			/* 获取请求类型 */
			method = types[0].trim();
			/* 判断路径 这里只做对GET的支持,?后面为参数 */
			String[] paths = types[1].split("\\?");
			url = paths[0].trim();
			/* 处理参数 如果有 */
			if (paths.length > 1) {
				String[] ps = paths[1].split("\\&");
				for (var p : ps) {
					String[] content = p.split("=");
					if (content.length > 1) {
						//这里没有做空格处理
						parameters.put(content[0], content[1]);
					}
				}
			}
			/* 处理请求头 */
			String data = null;
			while ((data = InputStreamUtils.readLine(input)) != null) {
				//一个有效的数据头为键值对形式
				String[] map = data.split(":");
				if (map.length > 1) {
					headersText.append(data);
					String key = map[0].trim();
					String value = map[1].trim();
					//存入键值对
					headers.put(key, value);
				} else {
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取所有的数据头.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @return 数据头map
	 */
	public HashMap<String, String> getHeaders() {
		return headers;
	}
	
	/**
	 * 获取指定数据头.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param head 数据头名称
	 * @return 数据头的值,null则无.
	 */
	public String getHeader(String head) {
		return headers.get(head);
	}
	
	/**
	 * 获取所有的请求参数.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @return HashMap
	 */
	public HashMap<String, String> getParameters() {
		return parameters;
	}
	
	/**
	 * 获取指定的请求参数.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param name 参数名
	 * @return 参数值
	 */
	public String getParameter(String name) {
		return parameters.get(name);
	}
	
	/**
	 * 获取请求类型.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @return 请求的类型.
	 */
	public String getMethod() {
		return method;
	}
	
	/**
	 * 获取请求的相对路径.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @return 请求的资源路径.
	 */
	public String getUrl() {
		return url;
	}
	
	/**
	 * 获取数据头的字符串表示形式.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @return 数据头.
	 */
	public StringBuilder getHeadersText() {
		return headersText;
	}
	
	/**
	 * 获取请求资源信息.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @return 请求数据第一行.
	 */
	public String getRequest() {
		return request;
	}
	
	/**
	 * 获取此请求中所有的属性.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @return HashMap
	 */
	public HashMap<String, Object> getAttributes() {
		return attributes;
	}
	
	/**
	 * 根据指定属性名获取对应属性.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param name 属性名
	 * @return 属性值,不存在则为空
	 */
	public Object getAttribute(String name) {
		return attributes.get(name);
	}
	
	/**
	 * 设置属性.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param name 属性名
	 * @param value 属性值
	 */
	public void setAttribute(String name,Object value) {
		attributes.put(name, value);
	}
	
	/**
	 * 转发.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param path 转发的路径
	 * @param req 请求
	 * @param resp 响应
	 */
	public void forward(String path,DefaultHttpRequest req,DefaultHttpResponse resp) {
		//将路径替换
		url = path;
		//在此调用处理请求和响应的方法
		DefaultServer.call(req, resp);
	}
	
}
