package shendi.game.gobang.listener;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;

import shendi.game.gobang.disposal.Game;

/**
 * 所有棋子的监听,单例.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public class LabelListener implements MouseListener {

	private LabelListener() {}
	private static final LabelListener LABEL_LISTENER = new LabelListener();
	
	/**
	 * 获取按钮监听事件.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @return {@link LabelListener}
	 */
	public static LabelListener getLabelListener() { return LABEL_LISTENER; }

	@Override
	public void mouseClicked(MouseEvent e) {
		var chess = (JLabel)e.getSource();
		var text = chess.getText();
		
		switch (text) {
		default: Game.getGame().exec(chess);
		}
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
