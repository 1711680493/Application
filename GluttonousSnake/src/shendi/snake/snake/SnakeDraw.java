package shendi.snake.snake;

import java.awt.Color;
import java.awt.Graphics;

import shendi.snake.Draw;

/**
 * 绘制蛇的身体
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public class SnakeDraw implements Draw {
	public void draw(Graphics g,Object... obj) {
		Snake snake = (Snake) obj[0];
		int[][] scene = (int[][]) obj[1];
		//绘制头
		g.setColor(Color.RED);
		g.fillRoundRect(snake.getX() * 20,snake.getY() * 20,20,20,90,90);
		g.setColor(Color.GRAY);
		//从数组中绘制身体
		int size = snake.getSnakeBody();
		//蛇头的位置
		int x = snake.getX();
		int y = snake.getY();
		//蛇身的标志数 第一个身体为 蛇头移动之前的位置 -1 第二个为 4 第三个5...
		int bodyNum = -1;
		//执行第一次 因为第一次的数字为-1
		{
			if (scene[y][x - 1] == bodyNum) {
				x--;
			} else if (scene[y][x + 1] == bodyNum) {
				x++;
			} else if (scene[y - 1][x] == bodyNum) {
				y--;
			} else if (scene[y + 1][x] == bodyNum) {
				y++;
			}
			bodyNum = 4;
			scene[y][x] = bodyNum;
			g.fillRoundRect(x * 20,y * 20,20,20,90,90);
		}
		for (var i = 1;i < size;i++) {
			//身体在头后面 有可能在 上/下/左/右
			if (scene[y][x - 1] == bodyNum) {
				x--;
			} else if (scene[y][x + 1] == bodyNum) {
				x++;
			} else if (scene[y - 1][x] == bodyNum) {
				y--;
			} else if (scene[y + 1][x] == bodyNum) {
				y++;
			}
			bodyNum++;
			scene[y][x] = bodyNum;
			g.fillRoundRect(x * 20,y * 20,20,20,90,90);
		}
		//清除最后一个尾巴(实现移动效果) 如果吃到食物则不需要清除 根据最后一个元素的其他三个方位来判断
		if (scene[y][x - 1] == bodyNum) scene[y][x - 1] = 0;
		else if (scene[y][x + 1] == bodyNum) scene[y][x + 1] = 0;
		else if (scene[y - 1][x] == bodyNum) scene[y - 1][x] = 0;
		else if (scene[y + 1][x] == bodyNum) scene[y + 1][x] = 0;
		g.dispose();
	}
}
