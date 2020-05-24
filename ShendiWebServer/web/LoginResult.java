package shendi.web.server.sws;

import shendi.web.server.http.DefaultHttpRequest;
import shendi.web.server.http.DefaultHttpResponse;

/**
 * 登录结果处理.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public class LoginResult implements ShendiWebServer {

	@Override
	public void sws(DefaultHttpRequest req, DefaultHttpResponse resp) {
		//是从 Login 转发过来的,现在有的值 是 account,pwd,和Login接口添加的 info
		resp.write("<body>");
		resp.write("<h1>信息全是转发过来的.req 与 resp 和转发之前的一致</h1>");
		resp.write("账号=" + req.getAttribute("account") + "<br />");
		resp.write("密码=" + req.getAttribute("pwd") + "<br />");
		resp.write("信息=" + req.getAttribute("info") + "<br />");
		resp.write("</body>");
	}

}
