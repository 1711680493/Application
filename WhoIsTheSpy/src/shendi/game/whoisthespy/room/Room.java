package shendi.game.whoisthespy.room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import shendi.game.whoisthespy.game.GameBlockState;
import shendi.game.whoisthespy.game.RoomGameState;
import shendi.game.whoisthespy.player.Player;

/**
 * 代表一个游戏的房间.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public abstract class Room {
	/**
	 * 当前房间里的玩家
	 * k = 房间号,v = 玩家sessionId
	 */
	private HashMap<Integer,String> players = new HashMap<>();
	
	/**
	 * 房间开始的用户对应状态
	 */
	public volatile HashMap<String,Player> startPlayers = new HashMap<String,Player>();
	
	/**
	 * 房间游戏状态
	 */
	public RoomGameState roomGameState = new GameBlockState();
	
	/**
	 * 房间最大人数
	 */
	public final int MAX_PLAYER;
	
	/**
	 * 房间类型 后期可自行扩展
	 */
	public String type = "警杀经典";
	
	/**
	 * 一个房间的消息频道,消息类型-消息.
	 */
	private HashMap<String,List<String>> infos = new HashMap<>();
	
	/**
	 * 此房间的全部消息频道
	 */
	public static final String INFO_ALL = "all";
	/**
	 * 系统频道
	 */
	public static final String INFO_SYSTEM = "system";
	/**
	 * 阵容频道
	 */
	public static final String INFO_TOGETHER = "together";
	/**
	 * 旁观频道
	 */
	public static final String INFO_OBSERVER = "observer";

	/**
	 * 房间的状态 默认为等待
	 */
	public boolean state;
	
	/**
	 * 游戏状态
	 */
	public GameState gameState = GameState.夜晚;
	
	/**
	 * 默认最大人数为6人,满六人自动开始游戏.
	 * @param sessionId 创建者
	 * @param maxPlayer 最大人数
	 */
	public Room(String sessionId,int maxPlayer) {
		MAX_PLAYER = maxPlayer;
		players.put(1, sessionId);
		//给全部频道添加消息
		sendInfo("系统", INFO_SYSTEM, "房间已创建,集齐人数自动开始游戏.");
	}
	
	/**
	 * 发送消息到指定频道.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param sessionId 是谁发送的消息.
	 * @param infoType 频道类型
	 * @param info 消息内容
	 */
	public void sendInfo(String sessionId,String infoType,String info) {
		//游戏开始 类型公共 并且是晚上则不执行操作
		if (state && infoType.equals(INFO_ALL) && gameState == GameState.夜晚) {
			return;
		}
		//系统的则直接发送
		if (INFO_SYSTEM.equals(infoType)) {
			if (infos.containsKey(infoType)) {
				List<String> list = infos.get(infoType);
				list.add(sessionId + ":" + info);
			} else {
				List<String> list = new ArrayList<>();
				list.add(sessionId + ":" + info);
				infos.put(infoType,list);
			}
			if (infos.containsKey(INFO_ALL)) {
				List<String> list = infos.get(INFO_ALL);
				list.add(sessionId + ":" + info);
			} else {
				List<String> list = new ArrayList<>();
				list.add(sessionId + ":" + info);
				infos.put(INFO_ALL,list);
			}
			return;
		}
		//获取sessionId在房间里的位置 如果没有,则以sessionId命名
		if (players.containsValue(sessionId)) {
			Set<Integer> set = players.keySet();
			for (int k : set) {
				if (sessionId.equals(players.get(k))) {
					sessionId = String.valueOf(k);
					break;
				}
			}
		}
		if (infos.containsKey(infoType)) {
			List<String> list = infos.get(infoType);
			list.add(sessionId + ":" + info);
		} else {
			List<String> list = new ArrayList<>();
			list.add(sessionId + ":" + info);
			infos.put(infoType,list);
		}
	}
	
	/**
	 * 发送消息到指定玩家.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param sessionId 是谁发送的消息.
	 * @param info 消息内容
	 */
	public void sendInfoByUser(String sessionId,String info) {
		if (infos.containsKey(sessionId)) {
			List<String> list = infos.get(sessionId);
			list.add("系统:" + info);
		} else {
			List<String> list = new ArrayList<>();
			list.add("系统:" + info);
			infos.put(sessionId,list);
		}
	}
	
	/**
	 * 获取指定频道的信息集.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param sessionId 用以区分不同用户
	 * @param infoType 频道
	 * @return 频道内所有信息
	 */
	public JsonArray getInfo(String sessionId,String infoType) {
		JsonArray arr = new JsonArray();
		//如果消息类型为阵容则判断游戏是否开始 没开始则无
		if (!state && INFO_TOGETHER.equals(infoType)) {
			return arr; 
		} else if (state && INFO_TOGETHER.equals(infoType)) {
			//获取对应的阵容的聊天频道
			Player p = getPlayer(sessionId);
			if (p == null) infoType += INFO_OBSERVER;
			else infoType += getPlayer(sessionId).getType();
		}
		//有此类型则返回 否则返回空数组
		if (!infos.containsKey(infoType)) return arr;
		List<String> list = infos.get(infoType);
		list.forEach(str -> {
			JsonObject o = new JsonObject();
			o.addProperty("info", str);
			arr.add(o);
		});
		return arr;
	}
	
	/**
	 * 获取指定玩家的消息.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param sessionId 用以区分不同用户
	 * @param isLast 是否获取最后一条数据
	 * @return 频道内所有信息
	 */
	public JsonArray getInfoByUser(String sessionId,boolean isLast) {
		JsonArray arr = new JsonArray();
		//判断游戏是否开始 没开始则无
		if (!state) {
			return arr;
		}
		//有此类型则返回 否则返回空数组
		if (!infos.containsKey(sessionId)) return arr;
		List<String> list = infos.get(sessionId);
		if (isLast) {
			JsonObject o = new JsonObject();
			o.addProperty("info", list.get(list.size() - 1));
			arr.add(o);
		} else {
			list.forEach(str -> {
				JsonObject o = new JsonObject();
				o.addProperty("info", str);
				arr.add(o);
			});
		}
		return arr;
	}
	
	/**
	 * 加入房间.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param sessionId 哪个用户加入房间.
	 * @return 加入是否成功 成功则返回在房间的 200|在房间里的下标,500为未知错误
	 */
	public String join(String sessionId) {
		synchronized (players) {
			//超过房间最大人数不允许加入
			if (players.size() >= MAX_PLAYER) {
				return "房间已满人";
			}
			//有此玩家则不做操作
			if (players.containsValue(sessionId)) {
				return "此用户已存在!";
			}
			//加入房间,哪个地方有空位往哪加...
			for (int i = 1;i <= MAX_PLAYER;i++) {
				if (!players.containsKey(i)) {
					players.put(i,sessionId);
					//满人则直接开始游戏
					if (players.size() == MAX_PLAYER) {
						new Thread(() -> {
							switchState();
						}).start();
					}
					return "200|" + i;
				}
			}
			return "500";
		}
	}
	
	/**
	 * 退出房间.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param sessionId 要退出房间的用户.
	 */
	public void exit(String sessionId) {
		//房间内不包含此玩家直接重定向
		if (!players.containsValue(sessionId)) return;
		//如果房间里只剩下自己 则销毁此房间
		if (players.size() == 1) {
			//销毁当前房间
			RoomManager.destroy(this);
			return;
		}
		//从房间删除此玩家
		for (int i = 1;i <= MAX_PLAYER;i++) {
			if (players.containsKey(i)) {
				if (players.get(i).equals(sessionId)) {
					players.remove(i);
					return;
				}
			}
		}
	}

	/**
	 * 切换房间状态 只应在开始游戏 结束游戏的时候调用.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 */
	public void switchState() {
		state = !state;
		if (state) {
			start();
		} else { 
			//清空消息
			infos.clear();
			stop();
			//睡眠3秒 如果房间还是满人,则提示20秒后自动开始游戏
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (players.size() == MAX_PLAYER) {
				sendInfo("系统", INFO_SYSTEM, "房间将在20秒后自动开始游戏.");
				try {
					Thread.sleep(20000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				switchState();
			}
		}
	}
	
	/**
	 * 切换当前游戏状态行为
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 */
	public void switchGameState() {
		roomGameState.switchState(this);
	}
	
	/**
	 * 开启游戏,实现了此方法应该做角色类型分配操作.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 */
	protected abstract void start();
	
	/**
	 * 停止游戏.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 */
	protected abstract void stop();
	
	/**
	 * 获取正在游戏中的用户
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param sessionId 用户标识
	 * @return 正在游戏中的状态
	 */
	protected abstract Player getPlayer(String sessionId);
	
	/**
	 * 获取当前房间里的玩家.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @return 一个包含玩家在房间里的位置和sessionId的Hashmap
	 */
	public HashMap<Integer, String> getPlayers() {
		return players;
	}
}
