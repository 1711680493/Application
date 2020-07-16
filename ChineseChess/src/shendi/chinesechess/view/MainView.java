package shendi.chinesechess.view;

import java.awt.Color;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;

import shendi.kit.config.ConfigurationFactory;

/**
 * 整个棋盘.<br>
 * 为了扩展,我们将绘制棋盘,绘制棋子,绘制按钮组的功能写到配置文件内.<br>
 * 启动时,会运行 onCreate 方法.<br>
 * 整个界面宽高为 600x440(窗体装饰占40高),所以棋盘为350x380,按钮组为80x380
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 * @see Chess 绘制棋子和游戏管理
 * @see ChessBoard 棋盘绘制
 * @see ButtonGroup 右侧按钮组
 */
public class MainView extends JFrame {
	private static final long serialVersionUID = -4360412531242537363L;
	
	public MainView() {
		setTitle("中国象棋-Shendi");
		setBounds(100, 100, 600, 440);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(null);
		setResizable(false);
	}
	/**
	 * 棋盘
	 */
	private static ChessBoard chessBoard;
	
	/**
	 * 棋盘上的棋子
	 */
	private static Chess chess;
	
	/**
	 * 执行按钮组
	 */
	private static ButtonGroup buttonGroup;
	
	/**
	 * 主要执行绘制棋盘和绘制棋子(对应棋盘),和绘制按钮组
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @throws ClassNotFoundException 类找不到
	 * @throws SecurityException 权限
	 * @throws NoSuchMethodException 没有方法
	 * @throws InvocationTargetException 调用
	 * @throws IllegalArgumentException 非法
	 * @throws IllegalAccessException 非法
	 * @throws InstantiationException 实例
	 */
	public void onCreate() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {
		// 绘制棋盘
		chessBoard = (ChessBoard) Class.forName(ConfigurationFactory.getConfig("config").getProperty("view.chessboard")).getConstructor().newInstance();
		chessBoard.setBounds(10, 10, 350, 380);
		chessBoard.setBackground(Color.getHSBColor(100, 90, 100));
		chessBoard.start();
		getContentPane().add(chessBoard);
		
		// 创建棋子
		chess = (Chess) Class.forName(ConfigurationFactory.getConfig("config").getProperty("view.chess")).getConstructor().newInstance();
		chess.init(chessBoard);
		
		// 初始化按钮组
		buttonGroup = (ButtonGroup) Class.forName(ConfigurationFactory.getConfig("config").getProperty("view.buttongroup")).getConstructor().newInstance();
		buttonGroup.init();
		buttonGroup.setBounds(360, 10, 220, 380);
		buttonGroup.setBackground(Color.black);
		getContentPane().add(buttonGroup);
	}
	
	/**
	 * 获取对应绘制的棋子绘制.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @return Chess
	 */
	public static Chess getChess() { return chess; }
	
	/**
	 * 获取对应棋盘.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @return ChessBoard
	 */
	public static ChessBoard getChessBoard() { return chessBoard; }
	
	/**
	 * 获取对应按钮组.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @return ButtonGroup
	 */
	public static ButtonGroup getButtonGroup() { return buttonGroup; }
	
}
