package shendi.chinesechess.ai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import javax.swing.JLabel;

import shendi.chinesechess.view.Chess;
import shendi.chinesechess.view.defaultview.DefaultChess;

/**
 * 简单AI,循环搜寻有无可吃的棋,有则吃,否则随机一个棋子进行移动.<br>
 * TODO 不先急着吃,先将可以吃的记下来,然后随机选取一个进行吃棋.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public class DefaultAI extends AI {
	private boolean redBlack;
	private DefaultChess dChess;
	private String[][] scene;
	
	public DefaultAI(Chess chess) {
		super(chess);
		dChess = (DefaultChess) chess;
	}

	/**
	 * 人机的棋子.
	 */
	private HashMap<String,JLabel> myChesses = new HashMap<>();
	
	/**
	 * 玩家的棋子
	 */
	private HashMap<String,JLabel> playerChesses = new HashMap<>();
	
	/**
	 * 用于取随机数
	 */
	private Random random = new Random();
	
	@Override
	public void init(boolean redBlack) {
		this.redBlack = redBlack;
		scene = dChess.getScene();
		
		myChesses.clear();
		playerChesses.clear();
		
		for (int i = 0;i < scene.length;i++) {
			String[] xScene = scene[i];
			for (int j = 0;j < xScene.length;j++) {
				JLabel chess = dChess.getChess(xScene[j]);
				if (chess == null) continue;
				if (redBlack) {
					if (chess.getText().contains("红")) {
						myChesses.put(chess.getName(), chess);
					} else {
						playerChesses.put(chess.getName(), chess);
					}
				} else {
					if (chess.getText().contains("黑")) {
						myChesses.put(chess.getName(), chess);
					} else {
						playerChesses.put(chess.getName(), chess);
					}
				}
			}
		}
	}

	@Override
	public void playChess() {
		// 获取有无可吃的棋子
		Set<Entry<String, JLabel>> myChess = myChesses.entrySet();
		for (var my : myChess) {
			if (!my.getValue().isVisible()) {
				myChesses.remove(my.getKey());
				playChess();
				return;
			}
			Set<Entry<String, JLabel>> playerChess = playerChesses.entrySet();
			for (var player : playerChess) {
				JLabel vv = player.getValue();
				dChess.getGame().onClick(my.getValue(), redBlack);
				dChess.getGame().onClick(vv, redBlack);
				if (!vv.isVisible()) {
					playerChesses.remove(player.getKey());
					return;
				}
			}
		}
		// 没有可吃棋子,随机行走
		randomWalk(myChess,new ArrayList<String>());
	}
	
	/**
	 * 随机行走.<br>
	 * 思路很简单,通过随机点击对应棋子获取可走路线,然后循环地图上的可走路线进行行走.<br>
	 * 如果都没有就代表失败了(这种情况不存在的).
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param myChess	我的棋子
	 * @param list 		当前使用过什么棋子.
	 */
	private void randomWalk(Set<Entry<String, JLabel>> myChess,ArrayList<String> list) {
		
		int rm = random.nextInt(myChess.size());
		
		for (var my : myChess) {
			if (rm != 0) rm--;
			else {
				if (!list.contains(my.getKey())) {
					dChess.getGame().onClick(my.getValue(), redBlack);
					// 随便获取可走路线进行行走,没有可走路线换一个棋子
					ArrayList<JLabel> walks = new ArrayList<>();
					for (int i = 0;i < scene.length;i++) {
						String[] xScene = scene[i];
						for (int j = 0;j < xScene.length;j++) {
							JLabel nullChess = dChess.getNullChess(xScene[j]);
							if (nullChess != null && nullChess.isVisible()) {
								walks.add(nullChess);
							}
						}
					}
					if (walks.size() > 0) {
						dChess.getGame().onClick(walks.get(random.nextInt(walks.size())),redBlack);
						walks = null;
						return;
					}
				}
				// 换一个棋子
				list.add(my.getKey());
				randomWalk(myChess, list);
				break;
			}
		}
	}
	
}
