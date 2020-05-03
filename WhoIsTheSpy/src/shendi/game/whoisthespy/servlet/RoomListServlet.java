package shendi.game.whoisthespy.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import shendi.game.whoisthespy.room.RoomManager;

/**
 * 获取现有的房间列表信息
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
@WebServlet("/rooms")
public class RoomListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		JsonArray rooms = new JsonArray();
		//遍历列表
		RoomManager.getRooms().forEach((k,v) -> {
			//输出room信息
			JsonObject info = new JsonObject();
			info.addProperty("id", k);
			info.addProperty("playerNumber", v.getPlayers().size());
			info.addProperty("type", v.type);
			rooms.add(info);
		});
		resp.getOutputStream().write(rooms.toString().getBytes("UTF-8"));
	}
}
