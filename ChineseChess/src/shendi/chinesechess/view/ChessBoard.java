package shendi.chinesechess.view;

import javax.swing.JPanel;

/**
 * 绘制棋盘,这是一个JPanel.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public abstract class ChessBoard extends JPanel {
	private static final long serialVersionUID = 6446197672847216700L;

	/**
	 * 开始绘制时被调用.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 */
	public final void start() { onCreate(); }
	
	/**
	 * 具体绘制实现.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 */
	protected abstract void onCreate();
	
}
