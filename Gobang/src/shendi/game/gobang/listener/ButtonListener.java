package shendi.game.gobang.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import shendi.game.gobang.disposal.Game;
import shendi.game.gobang.view.SettingDialog;

/**
 * 所有按钮的监听,单例.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public class ButtonListener implements ActionListener {

	private ButtonListener() {}
	private static final ButtonListener BTN_LISTENER = new ButtonListener();
	
	@Override
	public void actionPerformed(ActionEvent e) {
		var text = e.getActionCommand();
		switch (text) {
		case "开始游戏":
			Game.getGame().start();
			break;
		case "设置":
			new SettingDialog().setVisible(true);
			break;
		}
	}
	
	/**
	 * 获取按钮监听事件.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @return {@link ButtonListener}
	 */
	public static ButtonListener getBtnlistener() { return BTN_LISTENER; }

}
