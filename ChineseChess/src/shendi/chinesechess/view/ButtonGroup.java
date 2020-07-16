package shendi.chinesechess.view;

import javax.swing.JPanel;

/**
 * 按钮组,是一个JPanel.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public abstract class ButtonGroup extends JPanel {
	private static final long serialVersionUID = 3694424045428720391L;
	
	/**
	 * 初始化按钮组,包括按钮监听等.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 */
	public abstract void init();
	
}
