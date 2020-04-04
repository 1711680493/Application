package shendi.snake.view;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

import shendi.snake.game.SnakeRun;

/**
 * 主窗体
 * @author Shendi
 * <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 */
public final class MainFrame extends JFrame {
	private final int WIDTH = 660;
	private final int HEIGHT = 460;
	private final  SnakeRun run;
	
	private static final long serialVersionUID = 1L;
	public MainFrame() {
		//初始化窗体属性
		setTitle("贪吃蛇 -Shendi");
		setBounds(10,10,WIDTH,HEIGHT);
		setLayout(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		//初始化场景
		run = new SnakeRun(this);
		//按下回车就运行
		addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
			}
			@Override
			public void keyReleased(KeyEvent e) {
				//回车开始游戏 r重置游戏
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					if (!run.isStart()) {
						run.start(MainFrame.this);
					}
				}
				if (e.getKeyCode() == KeyEvent.VK_R) {
					run.reset(MainFrame.this);
				}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
	}
	
	
}
