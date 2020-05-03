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
 * 加入游戏
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
@WebServlet("/joinRoom")
public class JoinRoomServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//获取当前session
		String sessionId = req.getSession().getId();
		//获取要进入的房间id
		String roomId = req.getParameter("roomId");
		if (roomId == null) {
			return;
		}
		//加入房间
		Room room = RoomManager.getRooms().get(Integer.parseInt(roomId));
		room.join(sessionId);
	}
	
}
