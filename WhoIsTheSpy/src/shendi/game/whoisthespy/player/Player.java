package shendi.game.whoisthespy.player;

import java.util.HashMap;

/**
 * 代表一个玩家.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public class Player {
	
	/**
	 * 在房间里的编号
	 */
	private int id;
	
	/**
	 * 角色类型,默认平民.
	 */
	private PlayerType type = PlayerType.平民;

	/**
	 * 角色状态,默认存活.
	 */
	private PlayerState state = PlayerState.存活;
	
	/**
	 * 用于存当前角色的一些数据 比如,警察存储查了用户的身份,警察和杀手都可以存投票的数据...
	 */
	private HashMap<String,Object> objects = new HashMap<>();
	
	public Player(int id) {
		this.id = id;
	}
	
	public PlayerState getState() {
		return state;
	}
	public void setState(PlayerState state) {
		this.state = state;
	}
	
	public PlayerType getType() {
		return type;
	}
	public void setType(PlayerType type) {
		this.type = type;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public HashMap<String, Object> getObjects() {
		return objects;
	}
}
