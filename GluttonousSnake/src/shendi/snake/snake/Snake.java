package shendi.snake.snake;

/**
 * 代表一条蛇
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public class Snake {
	//蛇头坐标
	private int x;
	private int y;
	//蛇移动方向
	private SnakeAction action = SnakeAction.left;
	//蛇身数量
	private int snakeBody = 1;
	
	public Snake(int x,int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public SnakeAction getAction() {
		return action;
	}

	public void setAction(SnakeAction action) {
		this.action = action;
	}

	public int getSnakeBody() {
		return snakeBody;
	}

	public void setSnakeBody(int snakeBody) {
		this.snakeBody = snakeBody;
	}
	
}
