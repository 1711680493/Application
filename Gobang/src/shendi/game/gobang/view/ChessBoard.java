package shendi.game.gobang.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import shendi.game.gobang.disposal.Game;

/**
 * 棋盘,单例,只有当棋盘大小被改变的时候需要重新绘制.<br>
 * 棋子也从此创建.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public class ChessBoard extends JPanel {
	private static final long serialVersionUID = 7425319034903666059L;
	
	private ChessBoard() {
		setLayout(null);
		setBounds(40, 20, 400, 400);
		setBackground(Color.ORANGE);
		setOpaque(true);
		background.setBounds(0, 0, getWidth(), getHeight());
		add(background,-1);
		repaint(Game.getGame().getChessBoardNum());
	}
	private static final ChessBoard CHESS_BOARD = new ChessBoard();
	
	/**
	 * 白色黑色棋子图.
	 */
	public Icon whiteChessIcon,blackChessIcon;
	
	/**
	 * 棋盘格.
	 */
	private JLabel background = new JLabel();
	
	/**
	 * 场景与棋盘上的位置对应.
	 */
	private HashMap<Integer,Integer> positions = new HashMap<>();
	
	/**
	 * 第一个棋盘格的大小.
	 */
	private int size;
	
	/**
	 * 第一个棋子的位置
	 */
	private int first;
	
	/**
	 * 重新绘制棋盘.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param chessBoardNum 棋盘大小
	 */
	public void repaint(int chessBoardNum) {
		BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics g = img.getGraphics();
		
		size = 380 / chessBoardNum;
		first = size >> 1;
		
		// 棋子大小初始化
		Game.getGame().getChesses().forEach((chess) -> chess.setSize(size,size));
		
		// 将棋盘上所有位置初始化
		positions.clear();
		for (int i = 0;i < chessBoardNum;i++) {
			positions.put(i, first + size * i);
		}
		
		// 绘制棋盘
		for (int i = 1;i < chessBoardNum;i++) {
			// 横线
			g.drawLine(size, size * i, 380, size * i);
			// 竖线
			g.drawLine(size * i, size, size * i, 380);
		}
		g.drawLine(size, 380 , 380, 380);
		g.drawLine(380, size, 380, 380);
		g.dispose();
		background.setIcon(new ImageIcon(img));
		invalidate();
		repaint();
		
		// 白色黑色棋子图
		int imgPos = (size - size / 2) >> 1;
		int imgSize = size >> 1;
		
		BufferedImage whiteChessImg = new BufferedImage(size,size,BufferedImage.TYPE_INT_ARGB);
		g = whiteChessImg.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRoundRect(imgPos, imgPos, imgSize, imgSize, 180, 180);
		g.dispose();
		
		BufferedImage blackChessImg = new BufferedImage(size,size,BufferedImage.TYPE_INT_ARGB);
		g = blackChessImg.getGraphics();
		g.setColor(Color.BLACK);
		g.fillRoundRect(imgPos, imgPos, imgSize, imgSize, 180, 180);
		g.dispose();
		
		whiteChessIcon = new ImageIcon(whiteChessImg);
		blackChessIcon = new ImageIcon(blackChessImg);
	}
	
	/**
	 * 创建棋子.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @return 默认的棋子.
	 */
	public JLabel createChess() {
		JLabel chess = new JLabel();
		chess.setSize(size,size);
		add(chess,0);
		return chess;
	}
	
	/**
	 * 获取棋盘.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @return {@link ChessBoard}
	 */
	public static ChessBoard getChessBoard() { return CHESS_BOARD; }
	
	/**
	 * 通过场景数组的对应位置获取游戏内位置.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param scenePos 场景位置
	 * @return 对应游戏内的位置.
	 */
	public int getGamePos(int scenePos) { return positions.get(scenePos); }
	
	/**
	 * 通过游戏内对应场景的位置.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param gamePos 游戏位置
	 * @return 对应的场景位置.
	 */
	public int getScenePos(int gamePos) {
		var entrySet = positions.entrySet();
		for (var entry : entrySet) {
			if (entry.getValue() == gamePos) {
				return entry.getKey();
			}
		}
		return -1;
	}
	
}
