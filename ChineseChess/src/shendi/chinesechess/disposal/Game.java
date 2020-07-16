package shendi.chinesechess.disposal;

import java.lang.reflect.InvocationTargetException;

import javax.swing.JLabel;

import shendi.chinesechess.ai.AI;
import shendi.chinesechess.view.Chess;
import shendi.kit.config.ConfigurationFactory;

/**
 * 游戏逻辑处理接口.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public abstract class Game {
	/**
	 * 绘制棋子的类.
	 */
	protected Chess dChess;
	
	/**
	 * 象棋AI.
	 */
	protected AI ai;
	
	/**
	 * 当前是哪一方下棋,true红方,false黑方.
	 */
	private boolean gameRedBlack;
	
	/**
	 * 玩家是哪一方
	 */
	private boolean playerWhat;
	
	/**
	 * 人机是哪一方
	 */
	private boolean aiWhat;
	
	/**
	 * 当前游戏状态.
	 */
	private boolean state;
	
	/**
	 * 需要传递一个棋子绘制类.
	 * @param chess
	 */
	public Game(Chess chess) {
		this.dChess = chess;
		
		// 创建对应AI.
		try {
			ai = (AI) Class.forName(ConfigurationFactory.getConfig("config").getProperty("game.ai")).getConstructor(Chess.class).newInstance(dChess);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			e.printStackTrace();
			System.err.println("创建AI出错");
		}
	}
	
	/**
	 * 对游戏进行初始化.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param gameRedBlack 当前玩家是红方开始黑方.
	 */
	public void init(boolean gameRedBlack) {
		state = true;
		
		playerWhat = gameRedBlack;
		aiWhat = !playerWhat;
		
		// 子类扩展初始化
		init();
		
		// 初始化人机
		ai.init(aiWhat);
		
		// 红棋先下
		this.gameRedBlack = true;
		
		// 人机红则让人机先下.
		if (aiWhat == true) {
			ai.playChess();
		}
	}
	
	/**
	 * 子类用于初始化的函数.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 */
	protected abstract void init();
	
	/**
	 * 当棋子/棋盘被点击时调用,玩家专用.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param chess 对应棋子/空棋
	 */
	public void onClick(JLabel chess) {
		if (gameRedBlack == playerWhat) {
			onClick(chess,playerWhat);
		}
	}
	
	/**
	 * 棋子被点击时就会调用.<br>
	 * 执行完一个有效操作需要调用 {@link #chessMoveAnim()}.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param chess 	被点击的象棋
	 * @param redBlack 	红方还是黑方
	 */
	public abstract void onClick(JLabel chess,boolean redBlack);
	
	/**
	 * 棋子移动时调用.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param targetX 目标位置的x轴
	 * @param targetY 目标位置的y轴
	 */
	public final void chessMove(int targetX,int targetY) {
		new Thread(() -> { 
			chessMoveAnim(targetX, targetY);
			if (state) {
				gameRedBlack = !gameRedBlack;
				if (gameRedBlack == aiWhat) {
					// 提醒人机进行下棋
					ai.playChess();
				}
			}
		}).start();
	}
	
	/**
	 * 棋子移动动画,从一点到另一点,提供给子类实现对应动画.<br>
	 * 此方法不应被直接调用,请使用 {@link #chessMove(int, int)}
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param targetX 目标位置的x轴
	 * @param targetY 目标位置的y轴
	 */
	protected abstract void chessMoveAnim(int targetX,int targetY);
	
	/**
	 * 游戏结束.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 */
	protected void stop() {
		state = false;
	}
	
}
