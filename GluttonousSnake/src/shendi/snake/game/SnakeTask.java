package shendi.snake.game;

import java.util.TimerTask;

import shendi.snake.snake.Snake;
import shendi.snake.snake.SnakeAction;

public class SnakeTask extends TimerTask {
	private int[][] scene;
	private Snake snake;
	private SnakeRun run;
	
	public SnakeTask(int[][] scene, Snake snake, SnakeRun run) {
		this.scene = scene;
		this.snake = snake;
		this.run = run;
	}

	@Override
	public void run() {
		//输出数组 文字贪吃蛇
//		for (var i = 0;i < 20;i++) {
//			System.out.println(Arrays.toString(scene[i]));
//		}
//		System.out.println("----------------------------");
		//之前位置改为蛇尾
		scene[snake.getY()][snake.getX()] = -1;
		//蛇行动
		if (snake.getAction() == SnakeAction.left) {
			//判断是否死亡 撞墙 咬到尾巴
			int x = snake.getX() - 1;
			if (scene[snake.getY()][x] >= 3) {
				this.cancel();
				run.isStart = false;
				return;
			}
			//判断是否吃到食物
			if (scene[snake.getY()][x] == 1) {
				snake.setSnakeBody(snake.getSnakeBody()+1);
				//生成食物
				run.drawFood();
			}
			//设置蛇头位置
			snake.setX(x);
		} else if (snake.getAction() == SnakeAction.right) {
			//判断是否死亡 撞墙 咬到尾巴
			int x = snake.getX() + 1;
			if (scene[snake.getY()][x] >= 3) {
				this.cancel();
				run.isStart = false;
				return;
			}
			//判断是否吃到食物
			if (scene[snake.getY()][x] == 1) {
				snake.setSnakeBody(snake.getSnakeBody()+1);
				//生成食物
				run.drawFood();
			}
			//设置蛇头位置
			snake.setX(x);
		} else if (snake.getAction() == SnakeAction.up) {
			//判断是否死亡 撞墙 咬到尾巴
			int y = snake.getY() - 1;
			if (scene[y][snake.getX()] >= 3) {
				this.cancel();
				run.isStart = false;
				return;
			}
			//判断是否吃到食物
			if (scene[y][snake.getX()] == 1) {
				snake.setSnakeBody(snake.getSnakeBody()+1);
				//生成食物
				run.drawFood();
			}
			//设置蛇头位置
			snake.setY(y);
		} else {
			//判断是否死亡 撞墙 咬到尾巴
			int y = snake.getY() + 1;
			if (scene[y][snake.getX()] >= 3) {
				this.cancel();
				run.isStart = false;
				return;
			}
			//判断是否吃到食物
			if (scene[y][snake.getX()] == 1) {
				snake.setSnakeBody(snake.getSnakeBody()+1);
				//生成食物
				run.drawFood();
			}
			//设置蛇头位置
			snake.setY(y);
		}
		scene[snake.getY()][snake.getX()] = 2;
		//重新绘制
		run.drawSnake();
	}
	
}
