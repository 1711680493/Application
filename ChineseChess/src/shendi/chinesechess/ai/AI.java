package shendi.chinesechess.ai;

import shendi.chinesechess.view.Chess;

/**
 * 象棋 AI 抽象类,继承此类可以模拟一个玩家进行下棋.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public abstract class AI {
	/**
	 * 绘制象棋的类,持有游戏类引用.
	 */
	protected Chess dChess;
	
	/**
	 * 需要通过对应的绘制象棋类来创建对应AI.
	 * @param chess 绘制象棋的类.
	 */
	public AI(Chess chess) { this.dChess = chess; }
	
	/**
	 * 初始化对应AI.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param redBlack 红队还是黑队
	 */
	public abstract void init(boolean redBlack);
	
	/**
	 * 提醒 AI 该下棋了.<br>
	 * 提供给子类实现的下棋方法.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 */
	public abstract void playChess();
	
}
