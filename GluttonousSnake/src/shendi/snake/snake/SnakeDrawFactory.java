package shendi.snake.snake;

import shendi.snake.Draw;

/**
 * 绘制蛇的工厂类
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public class SnakeDrawFactory {
	private static final Draw draw = new SnakeDraw();
	/**
	 * 获取绘制
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @return
	 */
	public static Draw getSnakeDraw() {
		return draw;
	}
}
