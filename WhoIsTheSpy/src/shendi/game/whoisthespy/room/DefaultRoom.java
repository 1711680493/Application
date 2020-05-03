package shendi.game.whoisthespy.room;

import java.util.Set;

import shendi.game.whoisthespy.player.Player;
import shendi.game.whoisthespy.player.PlayerType;

/**
 * 默认房间(警杀模式)
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public class DefaultRoom extends Room {
	
	public DefaultRoom(String sessionId) {
		//默认六个人
		super(sessionId, 6);
	}

	@Override
	protected void start() {
		//分配职位 一警察 一杀手
		int police = (int)(Math.random() * 100 % 6) + 1;
		int slayer = (int)(Math.random() * 100 % 6) + 1;
		//警察杀手不能同一人
		while (police == slayer) slayer = (int)(Math.random() * 100 % 6) + 1;
		Set<Integer> keys = getPlayers().keySet();
		for (int key : keys) {
			Player player = new Player(key);
			if (key == police) {
				//警察
				player.setType(PlayerType.警察);
			} else if (key == slayer) {
				//杀手
				player.setType(PlayerType.杀手);
			}
			startPlayers.put(getPlayers().get(key), player);
		}
		//提醒用户的身份
		startPlayers.forEach((k,v) -> {
			sendInfoByUser(k, "游戏开始了！你的身份是:" + v.getType());
		});
		//开始游戏逻辑
		switchGameState();
	}
	
	@Override
	protected void stop() {
		//清除状态
		startPlayers.clear();
	}

	@Override
	protected Player getPlayer(String sessionId) {
		return startPlayers.get(sessionId);
	}

}
