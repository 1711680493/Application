package shendi.game.whoisthespy.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import shendi.game.whoisthespy.player.Player;
import shendi.game.whoisthespy.player.PlayerState;
import shendi.game.whoisthespy.player.PlayerType;
import shendi.game.whoisthespy.room.GameState;
import shendi.game.whoisthespy.room.Room;

/**
 * 夜晚执行的操作
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public class GameBlockState implements RoomGameState {
	private static GameWhiteState whiteState = new GameWhiteState();
	
	public GameBlockState() {
		GameWhiteState.setBlockState(this);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void switchState(Room room) {
		//休息一秒
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//提示晚上来临
		room.sendInfo("系统", Room.INFO_SYSTEM, "夜晚来临.");
		//睡眠一秒 提示进行操作
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		room.startPlayers.forEach((k,v) -> {
			if (PlayerType.警察 == v.getType()) {
				room.sendInfoByUser(k, "请选择你要搜查的目标");
			} else if (PlayerType.杀手 == v.getType()) {
				room.sendInfoByUser(k, "请选择你要击杀的目标");
			}
		});
		//倒计时20秒 执行结果
		room.sendInfo("系统", Room.INFO_SYSTEM, "距离白天还有20秒.");
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		room.gameState = GameState.白天;
		try {
			Thread.sleep(1000);;
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		//执行夜晚的操作
		{
			//获取警察 杀手选择的目标 并执行操作
			HashMap<String,Integer> officeSelection = new HashMap<>();
			HashMap<String,Integer> killSelection = new HashMap<>();
			room.startPlayers.forEach((k,v) -> {
				if (v.getType() == PlayerType.警察) {
					if (v.getObjects().containsKey("getIdentity")) {
						String sessionId = (String) v.getObjects().get("getIdentity");
						if (officeSelection.containsKey(sessionId)) {
							officeSelection.put(sessionId,officeSelection.get(sessionId) + 1);
						} else {
							officeSelection.put(sessionId,1);
						}
						v.getObjects().remove("getIdentity");
					}
				} else if (v.getType() == PlayerType.杀手) {
					if (v.getObjects().containsKey("kill")) {
						String sessionId = (String) v.getObjects().get("kill");
						if (killSelection.containsKey(sessionId)) {
							killSelection.put(sessionId,killSelection.get(sessionId) + 1);
						} else {
							killSelection.put(sessionId,1);
						}
						v.getObjects().remove("kill");
					}
				}
			});
			//通知警察被查询的身份
			{
				String officeSelect = null;
				int officeMax = -1;
				//获取投票最多的用户
				{
					Set<String> keys = officeSelection.keySet();
					for (String key : keys) {
						int value = officeSelection.get(key);
						if (value > officeMax) {
							officeSelect = key;
							officeMax = value;
						}
					}
				}
				Player officeSelectPlayer = room.startPlayers.get(officeSelect);
				if (officeSelect != null) {
					//所有警察都获取此用户身份
					room.sendInfo("系统", Room.INFO_TOGETHER + PlayerType.警察,officeSelectPlayer.getId() + " 已经被查,身份是:" + officeSelectPlayer.getType());
					Set<String> keys = room.startPlayers.keySet();
					for (String key : keys) {
						Player player = room.startPlayers.get(key);
						if (player.getType() == PlayerType.警察) {
							room.sendInfoByUser(key, officeSelectPlayer.getId() + " 已经被查,身份是:" + officeSelectPlayer.getType());
							if (player.getObjects().containsKey("identitys")) {
								((List<String>)(player.getObjects().get("identitys"))).add(officeSelect);
							} else {
								List<String> list = new ArrayList<>();
								list.add(officeSelect);
								player.getObjects().put("identitys",list);
							}
						}
					}
				}
			}
			//执行杀手操作
			{
				String killSelect = null;
				int killMax = -1;
				//获取投票最多的用户
				{
					Set<String> keys = killSelection.keySet();
					for (String key : keys) {
						int value = killSelection.get(key);
						if (value > killMax) {
							killSelect = key;
							killMax = value;
						}
					}
				}
				//击杀玩家
				if (killSelect != null) {
					Player killPlayer = room.startPlayers.get(killSelect);
					killPlayer.setState(PlayerState.死亡);
					room.sendInfo("系统",Room.INFO_SYSTEM,killPlayer.getId() + " 被杀害,身份是“" + killPlayer.getType() + "”");
					//判断游戏结果 如果场上没有警察 平民则胜利
					boolean officeExists = false;
					boolean peopleExists = false;
					Collection<Player> values = room.startPlayers.values();
					for (Player player : values) {
						if (player.getType() == PlayerType.警察 && player.getState() == PlayerState.存活) {
							officeExists = true;
						} else if (player.getType() == PlayerType.平民 && player.getState() == PlayerState.存活) {
							peopleExists = true;
						}
					}
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
					}
				}
			}
		}
		room.roomGameState = whiteState;
		room.switchGameState();
	}

}
