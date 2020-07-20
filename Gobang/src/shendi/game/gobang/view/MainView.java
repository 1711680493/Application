package shendi.game.gobang.view;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import shendi.game.gobang.listener.ButtonListener;

/**
 * 主视图,用于启动游戏和初始化.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public class MainView extends JFrame {
	private static final long serialVersionUID = 268974747417192511L;
	
	public MainView() {
		setTitle("五子棋 -Shendi");
		setBounds(100, 100, 500, 500);
		setLayout(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		
		// 菜单栏和按钮
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu option = new JMenu("选项");
		menuBar.add(option);
		
		JMenuItem startGameBtn = new JMenuItem("开始游戏");
		option.add(startGameBtn);
		
		JMenuItem settingBtn = new JMenuItem("设置");
		option.add(settingBtn);
		
		// 添加背景
		JLabel background = new JLabel();
		background.setBackground(new Color(194,250,65));
		background.setBounds(0, 0, getWidth(), getHeight());
		background.setOpaque(true);
		add(background);
		
		// 添加棋盘
		add(ChessBoard.getChessBoard(),0);
		
		// 添加监听
		startGameBtn.addActionListener(ButtonListener.getBtnlistener());
		settingBtn.addActionListener(ButtonListener.getBtnlistener());
	}
	
}
