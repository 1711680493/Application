package shendi.web.up;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

@WebServlet("/up")
public class UpServlet extends HttpServlet {
	private static final long serialVersionUID = -618843484075342697L;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 前端测试上传代码
//		String f = req.getParameter("file");
//		System.out.println(f);
//		byte[] data = req.getInputStream().readAllBytes();
//		System.out.println("开 " + new String(data) + " 尾");
		
		try {
			List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(req);
			for (FileItem item : items) {
				if ("file".equals(item.getFieldName())) {
					InputStream input = item.getInputStream();
					byte[] data = input.readAllBytes();
					input.close();
					
					// 将元素文件后缀获取
					String end = item.getName();
					end = end.substring(end.lastIndexOf('.'));
					// 文件保存路径,项目根目录下
					String path = "/" + UUID.randomUUID() + end;
					// 将文件输出,文件名使用 UUID 来随机生成
					FileOutputStream output = new FileOutputStream(UpServlet.class.getResource("/").getPath().substring(1) + "../../" + path);
					output.write(data);
					output.close();
					// 告诉前端图片在哪
					resp.getOutputStream().write(("/WebUpDownFile/" + path).getBytes());
					return;
				}
			}
		} catch (FileUploadException e) {
			e.printStackTrace();
		}
		
	}
	
}
