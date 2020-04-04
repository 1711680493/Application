package shendi.listener;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import shendi.snake.snake.Snake;
import shendi.snake.snake.SnakeAction;

public class SnakeListener implements KeyListener {
	//蛇的引用
	private Snake snake;
	//上次点击时间
	private long upClick = 0;
	
	public SnakeListener(Snake snake) {
		this.snake = snake;
	}
	
	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getWhen() - upClick < 100) {
			return;
		}
		int code = e.getKeyCode();
		switch (code) {
		case KeyEvent.VK_A:
		case KeyEvent.VK_LEFT:
			if (!(snake.getAction() == SnakeAction.right)) {
				snake.setAction(SnakeAction.left);
				upClick = e.getWhen();
			}
			break;
		case KeyEvent.VK_D:
		case KeyEvent.VK_RIGHT:
			if (!(snake.getAction() == SnakeAction.left)) {
				snake.setAction(SnakeAction.right);
				upClick = e.getWhen();
			}
			break;	
		case KeyEvent.VK_W:
		case KeyEvent.VK_UP:
			if (!(snake.getAction() == SnakeAction.down)) {
				snake.setAction(SnakeAction.up);
				upClick = e.getWhen();
			}
			break;
		case KeyEvent.VK_S:
		case KeyEvent.VK_DOWN:
			if (!(snake.getAction() == SnakeAction.up)) {
				snake.setAction(SnakeAction.down);
				upClick = e.getWhen();
			}
			break;
		}		
	}

	public void keyReleased(KeyEvent e) {}
}
