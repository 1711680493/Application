package shendi.chinesechess.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import shendi.chinesechess.view.MainView;
import shendi.chinesechess.view.defaultview.DefaultButtonGroup;

/**
 * 按钮的活动监听.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public class ButtonActionListener implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		String name = e.getActionCommand();
//		JButton button = (JButton) e.getSource();
		switch (name) {
		case "开启":
			MainView.getChess().start();
			DefaultButtonGroup.getStart().setEnabled(false);
			DefaultButtonGroup.getStop().setEnabled(true);
			// 重新绘制
			MainView.getChessBoard().invalidate();
			MainView.getChessBoard().repaint();
			break;
		case "停止":
			MainView.getChess().stop();
			DefaultButtonGroup.getStart().setEnabled(true);
			DefaultButtonGroup.getStop().setEnabled(false);
			break;
		}
	}

}
