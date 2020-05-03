package shendi.game.whoisthespy.servlet.room;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import shendi.game.whoisthespy.player.Player;
import shendi.game.whoisthespy.player.PlayerState;
import shendi.game.whoisthespy.player.PlayerType;
import shendi.game.whoisthespy.room.GameState;
import shendi.game.whoisthespy.room.Room;
import shendi.game.whoisthespy.room.RoomManager;

/**
 * 获取当前房间里的玩家的状态.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
@WebServlet("/roomInfo")
public class RoomInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//获取当前session
		String sessionId = req.getSession().getId();
		//获取房间id
		String roomId = req.getParameter("roomId");
		//获取房间信息
		Room room = RoomManager.getRooms().get(Integer.parseInt(roomId));
		if (room == null) {
			//返回房间号 -500为无房间
			resp.getOutputStream().write(String.valueOf(room).getBytes("UTF-8"));
			return;
		}
		//获取房间状态 游戏未开启输出的 操作什么都为无
		JsonArray array = new JsonArray();
		if (!room.state) {
			room.getPlayers().forEach((k,v) -> {
				JsonObject player = new JsonObject();
				player.addProperty("id", k);
				if (v.equals(sessionId)) player.addProperty("user", "我");
				else player.addProperty("user", v);
				array.add(player);
			});
		} else {
			//获取自己的信息
			Player my = room.startPlayers.get(sessionId);
			room.startPlayers.forEach((k,v) -> {
				JsonObject player = new JsonObject();
				//id
				player.addProperty("id", v.getId());
				//名称标识
				if (k.equals(sessionId)) {
					player.addProperty("user", "我");
					//获取自己的职业
					player.addProperty("identity",v.getType().toString());
				} else {
					player.addProperty("user", k);
					//其余人的职业一律未知 (警察可以获取到查询了的职业),已死亡的角色职业将暴露
					if (my != null && my.getType() == PlayerType.警察) {
						@SuppressWarnings("unchecked")
						List<String> identitys = (List<String>) my.getObjects().get("identitys");
						if (identitys != null && identitys.contains(k)) {
							player.addProperty("identity",v.getType().toString());
						} else {
							player.addProperty("identity","未知");
						}
					} else if (v.getState() == PlayerState.死亡) {
						player.addProperty("identity",v.getType().toString());
					} else {
						player.addProperty("identity","未知");
					}
				}
				//当前状态
				player.addProperty("gameState", v.getState().toString());
				//操作,和操作数量 用户死亡 或者为自己 则无操作选项和数量
				if (v.getState() == PlayerState.死亡 || k.equals(sessionId)) {
					player.addProperty("operation", "无");
					if (k.equals(sessionId)) {
						player.addProperty("operationNum",getOperationNum(k, room,PlayerType.平民));
					} else {
						player.addProperty("operationNum", "无");
					}
				} else {
					//旁观不能投票
					if (my == null) {
						player.addProperty("operation", "无");
						player.addProperty("operationNum",getOperationNum(k, room,PlayerType.平民));
					} else {
						//白天都有投票操作 并且只有投票操作
						if (room.gameState == GameState.白天) {
							//获取自己选中的是什么
							if (k.equals(my.getObjects().get("selection"))) {
								player.addProperty("operation", "<label><input type='radio' name='selection' checked='checked' onclick='selection(\"" + k + "\")' />投票</label>");
							} else {
								player.addProperty("operation", "<label><input type='radio' name='selection' onclick='selection(\"" + k + "\")' />投票</label>");
							}
							//获取此用户的投票数量
							player.addProperty("operationNum",getOperationNum(k, room,PlayerType.平民));
						} else {
							//晚上 自己的身份是警察则是查操作 杀手则是杀操作 平民则无
							switch (my.getType()) {
							case 平民:
								player.addProperty("operation", "无");
								player.addProperty("operationNum", "无");
								break;
							case 警察:
								//获取自己选中的是什么
								if (k.equals(my.getObjects().get("getIdentity"))) {
									player.addProperty("operation", "<label><input type='radio' name='getIdentity' checked='checked' onclick='getIdentity(\"" + k + "\")' />查询</label>");
								} else {
									player.addProperty("operation", "<label><input type='radio' name='getIdentity' onclick='getIdentity(\"" + k + "\")' />查询</label>");
								}
								//获取此用户的投票数量
								player.addProperty("operationNum",getOperationNum(k, room,PlayerType.警察));
								break;
							case 杀手:
								//获取自己选中的是什么
								if (k.equals(my.getObjects().get("kill"))) {
									player.addProperty("operation", "<label><input type='radio' name='kill' checked='checked' onclick='kill(\"" + k + "\")' />杀掉</label>");
								} else {
									player.addProperty("operation", "<label><input type='radio' name='kill' onclick='kill(\"" + k + "\")' />杀掉</label>");
								}
								//获取此用户的投票数量
								player.addProperty("operationNum",getOperationNum(k, room,PlayerType.杀手));
								break;
							}
						}
					}
				}
				array.add(player);
			});
		}
		resp.getOutputStream().write(array.toString().getBytes("UTF-8"));
	}
	
	/**
	 * 获取指定用户的操作数
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param sessionId 用户标识,获取此用户的操作数.
	 * @param room 房间
	 * @param type 代表获取的类型 平民则获取投票数,警察则获取用户的警投票数,杀手则获取杀投票数.
	 * @return 操作数量
	 */
	private int getOperationNum(String sessionId,Room room,PlayerType type) {
		Set<String> set = room.startPlayers.keySet();
		int num = 0;
		for (String session : set) {
			switch (type) {
			case 平民:
				if (sessionId.equals(room.startPlayers.get(session).getObjects().get("selection"))) num++;
				break;
			case 警察:
				if (sessionId.equals(room.startPlayers.get(session).getObjects().get("getIdentity"))) num++;
				break;
			case 杀手:
				if (sessionId.equals(room.startPlayers.get(session).getObjects().get("kill"))) num++;
				break;
			}
		}
		return num;
	}
	
}
