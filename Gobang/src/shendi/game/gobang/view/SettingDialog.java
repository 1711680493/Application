package shendi.game.gobang.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;

import shendi.game.gobang.disposal.Game;

/**
 * 游戏设置对话框.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public class SettingDialog extends JDialog {
	private static final long serialVersionUID = 3033192597251928717L;
	
	public SettingDialog() {
		setLayout(null);
		
		JButton submitBtn = new JButton("完成");
		submitBtn.setBounds(44, 75, 93, 23);
		add(submitBtn);
		
		JSpinner chessBoardNum = new JSpinner();
		chessBoardNum.setBounds(102, 11, 50, 22);
		chessBoardNum.setValue(Game.getGame().getChessBoardNum());
		add(chessBoardNum);
		
		JLabel chessBoardNumLabel = new JLabel("棋盘大小: ");
		chessBoardNumLabel.setBounds(21, 10, 71, 23);
		add(chessBoardNumLabel);
		
		JSpinner vectoryChessNum = new JSpinner();
		vectoryChessNum.setBounds(102, 43, 50, 22);
		vectoryChessNum.setValue(Game.getGame().getVectoryNum());
		add(vectoryChessNum);
		
		JLabel vectoryChessNumLabel = new JLabel("胜利棋数:");
		vectoryChessNumLabel.setBounds(21, 43, 71, 23);
		add(vectoryChessNumLabel);
		setTitle("设置");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 200, 150);
		
		// 直接添加监听,设置值.
		submitBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int num1 = (int) chessBoardNum.getValue();
				int num2 = (int) vectoryChessNum.getValue();
				if (num1 <= 3 || num2 <= 3) JOptionPane.showMessageDialog(null, "棋盘大小和胜利条件最小为3!");
				else {
					Game.getGame().setChessBoardNum(num1);
					Game.getGame().setVectoryNum(num2);
					dispose();
				}
			}
		});
	}
}
