package shendi.web.server.sws;

import shendi.web.server.http.DefaultHttpRequest;
import shendi.web.server.http.DefaultHttpResponse;

/**
 * 执行登录的接口.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public class Login implements ShendiWebServer {

	@Override
	public void sws(DefaultHttpRequest req, DefaultHttpResponse resp) {
		//现在的req中有账号和密码 全存 attribute中 进行转发
		req.setAttribute("account", req.getParameter("account"));
		req.setAttribute("pwd", "我把密码改了,密码不会给你看见的");
		req.setAttribute("info", "我是Login");
		//转发
		req.forward("/loginResult", req, resp);
	}

}
