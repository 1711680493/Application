package shendi.web.server.sws;

import shendi.web.server.http.DefaultHttpRequest;
import shendi.web.server.http.DefaultHttpResponse;

/**
 * 所有的映射类应该实现此类.<br>
 * 目前只做一种操作,后续完善.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public interface ShendiWebServer {
	
	/**
	 * 处理后端操作.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param req 请求
	 * @param resp 响应
	 */
	public void sws(DefaultHttpRequest req,DefaultHttpResponse resp);
	
}
