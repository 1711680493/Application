package shendi.web.down;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 文件的下载
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
@WebServlet("/down")
public class DownFileServlet extends HttpServlet {
	private static final long serialVersionUID = -2878549582574220473L;

	/** 当前项目下的data目录 */
	public static final String PATH = DownFileServlet.class.getResource("/").getPath().substring(1) + "../../../data/";
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 这里可以自己设置一个文件的根路径,比如就在项目根目录的data文件夹下
		// 这里获取路径的方法可以自行百度,比如兼容性之类的,也可直接使用我开发的工具包 https://github.com/1711680493/ShendiKit
		String uPath = req.getParameter("path");
		if (uPath == null) return;
		
		// 这里会有个bug,比如说我传递的uPath为 ../ 这样的结构 根目录就会改变
		// 所以我们要判断一下
		if (uPath.contains("../")) return;
		
		File file = new File(PATH + uPath);
		System.out.println(file.getPath());
		if (file.exists()) {
			FileInputStream input = new FileInputStream(file);
			// 这个是java9的api,直接读取这个文件的所有数据
			byte[] data = input.readAllBytes();
			// 设置响应头,告诉浏览器下载文件
			resp.setHeader("Content-Disposition", "form-data; name=\"file\"; filename=\"" + file.getName() + "\"");
			resp.getOutputStream().write(data);
			// 这里为了方便,直接这样关闭,正常使用一般需要try
			input.close();
		} else {
			resp.setStatus(404);
		}
	}
	
}
