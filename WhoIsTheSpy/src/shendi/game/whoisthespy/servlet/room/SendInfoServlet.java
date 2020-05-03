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
 * 房间内发送消息,状态200为发送成功,其余则失败.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
@WebServlet("/send")
public class SendInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//获取当前session
		String sessionId = req.getSession().getId();
		//获取房间id
		String roomId = req.getParameter("roomId");
		if (roomId == null || !RoomManager.getRooms().containsKey(Integer.parseInt(roomId))) {
			resp.setStatus(201);
			return;
		}
		String infoType = req.getParameter("infoType");
		if (infoType == null) {
			resp.setStatus(201);
			return;
		}
		//获取信息
		String info = req.getParameter("info");
		//获取一个房间的消息
		Room room = RoomManager.getRooms().get(Integer.parseInt(roomId));
		room.sendInfo(sessionId, infoType, info);
	}

}
