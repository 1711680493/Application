package shendi.chinesechess.view;

import shendi.chinesechess.disposal.Game;

/**
 * 在棋盘上绘制棋子,也是游戏的管理类.<br>
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public interface Chess {
	
	/**
	 * 给棋子初始化等.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param chessBoard 棋盘.
	 */
	void init(ChessBoard chessBoard);
	
	/**
	 * 开始游戏时绘制的棋子.<br>
	 * 绘制完需要放入棋盘.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 */
	void start();
	
	/**
	 * 结束游戏时的对棋盘的清理等.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 */
	void stop();

	/**
	 * 获取对应游戏逻辑类.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @return {@link Game}
	 */
	Game getGame();
}
