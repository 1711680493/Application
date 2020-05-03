package shendi.game.whoisthespy.game;

import shendi.game.whoisthespy.room.Room;

public interface RoomGameState {
	/**
	 * 切换房间游戏状态.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param room 房间
	 */
	void switchState(Room room);
}
