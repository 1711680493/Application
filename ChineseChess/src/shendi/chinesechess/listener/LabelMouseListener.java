package shendi.chinesechess.listener;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;

import shendi.chinesechess.disposal.DefaultGame;
import shendi.chinesechess.view.MainView;

/**
 * 所有标签的鼠标监听.<br>
 * 包括棋子等.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public class LabelMouseListener implements MouseListener {

	private DefaultGame game = (DefaultGame) MainView.getChess().getGame();
	
	@Override
	public void mouseClicked(MouseEvent e) {
		Object source = e.getSource();
		// 这里为Label的点击事件,可能为JLabel,可能为Label
		if (source instanceof JLabel) {
			JLabel label = (JLabel) source;
			if (e.getButton() == MouseEvent.BUTTON1) {
				String text = label.getText();
				
				// 不是对应label则将处理交给游戏类.
				switch (text) {
				default: game.onClick(label);
				}
			}
			
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
