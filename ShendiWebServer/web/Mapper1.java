package shendi.web.server.sws;

import shendi.web.server.http.DefaultHttpRequest;
import shendi.web.server.http.DefaultHttpResponse;

/**
 * 映射类,已经在 mapper.properties 中配置,映射路径为 /mapper1
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public class Mapper1 implements ShendiWebServer {

	@Override
	public void sws(DefaultHttpRequest req, DefaultHttpResponse resp) {
		resp.write("<h1>hello wolrd</h1>");
		resp.write("<h2>hello wolrd</h2>");
		resp.write("<h3>hello wolrd</h3>");
		resp.write("<h4>hello wolrd</h4>");
		resp.write("<h5>hello wolrd</h5>");
	}

}
