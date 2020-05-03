package shendi.game.whoisthespy.servlet.room;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import shendi.game.whoisthespy.room.Room;
import shendi.game.whoisthespy.room.RoomManager;

/**
 * 用于获取目前房间里的消息
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
@WebServlet("/info")
public class InfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String sessionId = req.getSession().getId();
		//获取要查询的房间id 和 频道
		String roomId = req.getParameter("roomId");
		if (roomId == null || "undefined".equals(roomId) || !RoomManager.getRooms().containsKey(Integer.parseInt(roomId))) {
			resp.getOutputStream().write("没有房间号！".getBytes("UTF-8"));
			resp.setStatus(201);
			return;
		}
		String infoType = req.getParameter("infoType");
		if (infoType == null) {
			resp.getOutputStream().write("没有频道类型！".getBytes("UTF-8"));
			resp.setStatus(201);
			return;
		}
		//获取一个房间的消息
		Room room = RoomManager.getRooms().get(Integer.parseInt(roomId));
		resp.getOutputStream().write(room.getInfo(sessionId,infoType).toString().getBytes("UTF-8"));
	}
	
}
