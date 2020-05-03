package shendi.game.whoisthespy.servlet.room.vote;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import shendi.game.whoisthespy.room.Room;
import shendi.game.whoisthespy.room.RoomManager;

/**
 * 白天投票选择
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
@WebServlet("/room/selection")
public class SelectionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String sessionId = req.getSession().getId();
		String roomId = req.getParameter("roomId");
		String selection = req.getParameter("selection");
		//判断房间号是否存在.
		if (roomId == null || !RoomManager.getRooms().containsKey(Integer.parseInt(roomId))) {
			return;
		}
		Room room = RoomManager.getRooms().get(Integer.parseInt(roomId));
		//判断是否包含此用户,不包含则不做操作
		if (!room.startPlayers.containsKey(sessionId)) {
			return;
		}
		//设置此用户选择的投票处决的用户
		room.startPlayers.get(sessionId).getObjects().put("selection", selection);
	}

}
