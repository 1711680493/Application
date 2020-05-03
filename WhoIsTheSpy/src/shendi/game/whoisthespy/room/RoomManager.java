package shendi.game.whoisthespy.room;

import java.util.HashMap;
import java.util.Set;

/**
 * 房间管理.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 * @see Room
 */
public class RoomManager {
	/**
	 * 包含所有的房间列表
	 */
	private static HashMap<Integer,Room> rooms = new HashMap<>();
	
	/**
	 * 没有对象可言
	 */
	private RoomManager() {}
	
	/**
	 * 销毁一个房间.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param room 要销毁的房间.
	 */
	public static void destroy(Room room) {
		Set<Integer> set = rooms.keySet();
		for (int id : set) {
			if (rooms.get(id).equals(room)) {
				rooms.remove(id);
				break;
			}
		}
	}

	/**
	 * 创建房间,后期可以自行扩展,目前直接创建默认的.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param sessionId 创建房间的用户
	 * @return 200|房间id 为成功 500未知错误
	 */
	public static String create(String sessionId) {
		synchronized (rooms) {
			int size = rooms.size() + 1;
			//房间id,哪个没有往哪加(实现中间插入效果),如果没有中断则往最后添加
			for (int i = 1;i <= size;i++) {
				if (!rooms.containsKey(i)) {
					rooms.put(size, new DefaultRoom(sessionId));
					//将创建的玩家置于一号
					return 200 + "|" +size;
				}
			}
			return String.valueOf(500);
		}
	}
	
	/**
	 * 进入指定房间,返回进入的房间号,如果没有此房间则返回 -404,未知错误 -500.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param sessionId 进入房间的玩家.
	 * @param roomId 要进入的房间id号
	 * @return 返回确认无误的房间的id号,或者错误码.
	 */
	public static int joinRoom(String sessionId,String roomId) {
		if ("null".equals(roomId) || null == roomId) {
			//随便加入一个未满的房间
			Set<Integer> set = rooms.keySet();
			for (int id : set) {
				Room r = rooms.get(id);
				if (r.getPlayers().size() < r.MAX_PLAYER) {
					return id;
				}
			}
			//房间全部满人则创建一个新房间
			String result = create(sessionId);
			if ("500".equals(result)) {
				return -500;
			}
			return Integer.parseInt(result.split("\\|")[1]);
		} else {
			//将字符串转int
			int room = Integer.parseInt(roomId);
			//加入指定房间
			if (rooms.containsKey(room)) return room;
		}
		return -404;
	}
	
	public static HashMap<Integer, Room> getRooms() {
		return rooms;
	}
}
