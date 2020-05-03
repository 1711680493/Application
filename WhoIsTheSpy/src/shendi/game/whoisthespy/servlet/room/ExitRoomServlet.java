package shendi.game.whoisthespy.servlet.room;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import shendi.game.whoisthespy.room.RoomManager;

/**
 * 退出房间.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
@WebServlet("/exitRoom")
public class ExitRoomServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String sessionId = req.getSession().getId();
		//获取房间id
		String roomId = req.getParameter("roomId");
		if (roomId == null || "undefined".equals(roomId)) {
			resp.sendRedirect("./html/main.html");
			return;
		}
		//清除此玩家在房间内的信息,并退出
		int id = Integer.parseInt(roomId);
		if (RoomManager.getRooms().containsKey(id)) {
			RoomManager.getRooms().get(id).exit(sessionId);
		}
		resp.sendRedirect("./html/main.html");
	}

}
