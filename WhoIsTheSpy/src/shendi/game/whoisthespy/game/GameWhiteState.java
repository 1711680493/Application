package shendi.game.whoisthespy.game;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import shendi.game.whoisthespy.player.Player;
import shendi.game.whoisthespy.player.PlayerState;
import shendi.game.whoisthespy.player.PlayerType;
import shendi.game.whoisthespy.room.GameState;
import shendi.game.whoisthespy.room.Room;

/**
 * 白天执行的操作
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public class GameWhiteState implements RoomGameState {
	private static GameBlockState blockState;
	
	public static void setBlockState(GameBlockState blockState) {
		GameWhiteState.blockState = blockState;
	}
	
	@Override
	public void switchState(Room room) {
		//休息一秒
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//提示早上来临
		room.sendInfo("系统", Room.INFO_SYSTEM, "早上了,请开始投票.");
		//睡眠一秒 提示进行操作
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		room.startPlayers.forEach((k,v) -> {
			room.sendInfoByUser(k, "请投票.");
		});
		//倒计时50秒 执行结果
		room.sendInfo("系统", Room.INFO_SYSTEM, "距离夜晚还有50秒.");
		try {
			Thread.sleep(50000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		room.gameState = GameState.夜晚;
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		//执行白天的操作
		{
			//获取所有的投票
			HashMap<String,Integer> selection = new HashMap<>();
			room.startPlayers.forEach((k,v) -> {
				if (v.getObjects().containsKey("selection")) {
					String sessionId = (String) v.getObjects().get("selection");
					if (selection.containsKey(sessionId)) {
						selection.put(sessionId,selection.get(sessionId) + 1);
					} else {
						selection.put(sessionId,1);
					}
					v.getObjects().remove("selection");
				}
			});
			String select = null;
			int selectMax = -1;
			//获取投票最多的用户
			{
				Set<String> keys = selection.keySet();
				for (String key : keys) {
					int value = selection.get(key);
					if (value > selectMax) {
						select = key;
						selectMax = value;
					}
				}
			}
			//处决用户
			if (select != null) {
				Player player = room.startPlayers.get(select);
				//将用户状态改为死亡
				player.setState(PlayerState.死亡);
				//通知其他用户
				room.sendInfo("系统", Room.INFO_SYSTEM, "玩家 " + player.getId() + " 被处决,身份是 “" + player.getType() + "”");
				//休息两秒
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				//获取职业状态(是否全灭)
				boolean officeExists = false;
				boolean killExists = false;
				boolean peopleExists = false;
				Collection<Player> values = room.startPlayers.values();
				for (Player p : values) {
					if (p.getType() == PlayerType.警察 && p.getState() == PlayerState.存活) {
						officeExists = true;
					} else if (p.getType() == PlayerType.杀手 && p.getState() == PlayerState.存活) {
						killExists = true;
					} else if (p.getType() == PlayerType.平民 && p.getState() == PlayerState.存活) {
						peopleExists = true;
					}
				}
				//判断结果
				if (!officeExists || !peopleExists) {
					//提示游戏结束 一秒后跳出结果界面并停止游戏
					room.startPlayers.forEach((k,v) -> {
						if (v.getType() == PlayerType.杀手) {
							room.sendInfoByUser(k, "|stop|杀手胜利,警察或平民全灭.");
						} else {
							room.sendInfoByUser(k, "|stop|失败,警察或平民全灭.");
						}
					});
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					room.switchState();
					return;
				} else if (!killExists) {
					//提示游戏结束 一秒后跳出结果界面并停止游戏
					room.startPlayers.forEach((k,v) -> {
						if (v.getType() == PlayerType.杀手) {
							room.sendInfoByUser(k, "|stop|杀手失败,杀手全灭.");
						} else {
							room.sendInfoByUser(k, "|stop|胜利,杀手全灭.");
						}
					});
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					room.switchState();
					return;
				}
			}
		}
		room.roomGameState = blockState;
		room.switchGameState();
	}

}
