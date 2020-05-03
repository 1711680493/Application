package shendi.game.whoisthespy.servlet.room;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonArray;

import shendi.game.whoisthespy.room.Room;
import shendi.game.whoisthespy.room.RoomManager;

/**
 * 获取指定房间的最近事件.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
@WebServlet("/roomEvent")
public class RoomEventServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//获取当前session
		String sessionId = req.getSession().getId();
		//获取房间id
		String roomId = req.getParameter("roomId");
		//获取是否查看最后一条记录
		String isLast = req.getParameter("isLast");
		//获取房间信息
		Room room = RoomManager.getRooms().get(Integer.parseInt(roomId));
		//房间内没此用户则返回错误状态
		if (room == null || !room.getPlayers().containsValue(sessionId)) {
			resp.setStatus(201);
			return;
		}
		JsonArray array = room.getInfoByUser(sessionId,Boolean.parseBoolean(isLast));
		//返回房间号 -500为无房间
		resp.getOutputStream().write(array.toString().getBytes("UTF-8"));
	}
	
}
