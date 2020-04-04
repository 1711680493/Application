package shendi.snake.game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Timer;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import shendi.listener.SnakeListener;
import shendi.snake.snake.Snake;
import shendi.snake.snake.SnakeAction;
import shendi.snake.snake.SnakeDrawFactory;
import shendi.snake.view.MainFrame;


/**
 * 启动蛇
 * @author Shendi
 * <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 */
public class SnakeRun {
	//游戏是否开始
	protected boolean isStart;
	//场景 数字为 -1蛇头上一次位置-蛇身 0代表空 1代表食物  2代表蛇头 3代表墙壁 4 以及后面的数字代表蛇身
	private int[][] scene;
	//蛇 蛇头红色 蛇身灰色
	private JLabel snakeLabel;
	private JLabel food; 
	//格子
	private JLabel grid;
	//游戏运行
	protected Timer t = new Timer();
	private SnakeTask task;
	//蛇的信息
	private Snake snake;
	//蛇互动监听
	private SnakeListener snakeListener;
	/**
	 * 初始化场景
	 * @param width 窗体的宽度
	 * @param height 窗体的高度
	 */
	public SnakeRun(MainFrame frame) {
		scene = new int[20][30];
		//初始化场景
		{
			//竖排
			for (var i = 0;i < 20;i++) {
				scene[i][0] = 3; scene[i][29] = 3;
			}
			//横排
			for (var i = 1;i < 29;i++) {
				scene[0][i] = 3; scene[19][i] = 3;
			}
			//蛇
			scene[10][15] = 2;
			scene[10][16] = 4;
			snake = new Snake(15,10);
		}
		//输出数组 文字贪吃蛇
//		for (var i = 0;i < 20;i++) {
//			System.out.println(Arrays.toString(scene[i]));
//		}
//		System.out.println("----------------------------");
		//绘制格子 边界
		{
			BufferedImage gridIcon = new BufferedImage(600,400,BufferedImage.TYPE_INT_RGB);
			Graphics g = gridIcon.getGraphics();
			g.setColor(Color.GREEN);
			g.fillRect(0, 0, 600, 400);
			g.setColor(Color.WHITE);
			for (var i = 0;i < 20;i++) {
				//绘制横排
				g.drawLine(0,i * 20,600,i * 20);		
				for (var j = 0;j < 30;j++) {
					//绘制竖排
					g.drawLine(j * 20,0,j * 20,400);
				}
			}
			//绘制边界
			g.setColor(Color.CYAN);
			g.fillRect(0,0,600,20);
			g.fillRect(0,380,600,20);
			g.fillRect(0,20,20,360);
			g.fillRect(580,20,20,360);
			g.dispose();
			grid = new JLabel(new ImageIcon(gridIcon));
			grid.setBounds(20,10,600,400);
		}
		//初始蛇位置
		{
			BufferedImage gridIcon = new BufferedImage(600,400,BufferedImage.TYPE_INT_ARGB);
			Graphics g = gridIcon.getGraphics();
			g.setColor(Color.RED);
			g.fillRoundRect(300,200,20, 20,90,90);
			g.setColor(Color.GRAY);
			g.fillRoundRect(320,200,20, 20,90,90);
			g.dispose();
			snakeLabel = new JLabel(new ImageIcon(gridIcon));
			snakeLabel.setBounds(20,10,600,400);
		}
		//初始化食物
		BufferedImage gridIcon = new BufferedImage(20,20,BufferedImage.TYPE_INT_ARGB);
		Graphics g = gridIcon.getGraphics();
		g.setColor(Color.BLUE);
		g.fillRoundRect(0, 0, 20, 20, 90,90);
		g.dispose();
		food = new JLabel(new ImageIcon(gridIcon));
		food.setBounds(-20,-20,20, 20);
		//添加进窗体
		{
			frame.add(snakeLabel);
			frame.add(food);
			frame.add(grid);
		}
		snakeListener = new SnakeListener(snake);
	}
	/**
	 * 开始游戏
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 */
	public void start(MainFrame frame) {
		//改变状态
		isStart = true;
		//绘制一个食物
		drawFood();
		//添加监听
		addListener(frame);
		//开始游戏
		run();
	}
	
	/**
	 * 重置游戏
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 */
	public void reset(MainFrame frame) {
		isStart = false;
		//移除监听
		frame.removeKeyListener(snakeListener);
		//停止游戏
		if (task == null) return;
		task.cancel();
		t.cancel();
		//复位
		{
			//场景
			for (var i = 1;i < 19;i++) {
				for (var j = 1;j < 29;j++) {
					scene[i][j] = 0;
				}
			}
			//蛇
			snake.setSnakeBody(1);
			snake.setAction(SnakeAction.left);
			snake.setX(15); snake.setY(10);
			scene[10][15] = 2;
			scene[10][16] = 4;
			//食物
			food.setLocation(-20, -20);
		}
	}
	
	/**
	 * 添加监听
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param frame
	 */
	private void addListener(MainFrame frame) {
		frame.addKeyListener(snakeListener);
	}
	
	/**
	 * 开始游戏
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 */
	private void run() {
		t = new Timer();
		task = new SnakeTask(scene, snake, this);
		t.schedule(task,0,200);
	}
	
	/**
	 * 绘制蛇
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 */
	protected void drawSnake() {
		BufferedImage gridIcon = new BufferedImage(600,400,BufferedImage.TYPE_INT_ARGB);
		//绘制
		SnakeDrawFactory.getSnakeDraw().draw(gridIcon.getGraphics(),snake,scene);
		snakeLabel.setIcon(new ImageIcon(gridIcon));
	}
	
	/**
	 * 绘制食物
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 */
	protected void drawFood() {
		int x = (int) (Math.random() * 100 % 30);
		int y = (int) (Math.random() * 100 % 20);
		while (scene[y][x] != 0) {
			x = (int) (Math.random() * 100 % 30);
			y = (int) (Math.random() * 100 % 20);
		}
		food.setLocation(20 + x * 20,10 + y * 20);
		scene[y][x] = 1;
	}
	
	/**
	 * 获取游戏状态
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @return
	 */
	public boolean isStart() {
		return isStart;
	}
}
