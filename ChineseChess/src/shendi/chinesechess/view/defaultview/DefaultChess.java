package shendi.chinesechess.view.defaultview;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import shendi.chinesechess.disposal.DefaultGame;
import shendi.chinesechess.disposal.Game;
import shendi.chinesechess.listener.ListenerFactory;
import shendi.chinesechess.view.Chess;
import shendi.chinesechess.view.ChessBoard;
import shendi.chinesechess.view.ChessFactory;

/**
 * 默认的象棋绘制类.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public class DefaultChess implements Chess {

	/**
	 * 对应默认游戏逻辑处理.
	 */
	private Game game = new DefaultGame(this);
	
	/**
	 * 象棋集合.
	 */
	private HashMap<String,JLabel> chesses = new HashMap<>();
	
	/**
	 * 我方为红方的象棋默认位置,如果为黑方则可通过某种算法直接对称过去.
	 */
	private HashMap<String,Point> defaultLocation = new HashMap<>();

	/**
	 * 整个地图场景,竖排(y)有十个,横排(x)九个
	 */
	private String[][] scene = new String[10][9];
	
	/**
	 * true为红队,false为黑队.
	 */
	public boolean redBlack;
	
	/**
	 * 棋子大小
	 */
	private int size = 35;
	
	/**
	 * 第一个棋子的位置,因为设置位置的是指定位置往右下,不是直接放中间,所以需要 / 2
	 */
	private int first = size / 2;
	
	/**
	 * 选中框和上一次位置的框.
	 */
	public final JLabel select,upSelect;
	
	/**
	 * 空白地方的JLabel,用于点击事件和显示棋子可走路线.<br>
	 * 这里的key应为 x:y 的字符串形式.
	 */
	private HashMap<String,JLabel> nullChesses = new HashMap<>();
	
	/**
	 * 被吃掉部分所需要使用的可走路线事件点.<br>
	 * 名称为数, 1: 2: 3:,为了能够在场景中区分获取
	 */
	private HashMap<String,JLabel> eatNullChesses = new HashMap<>();
	
	/**
	 * 空白地方的JLabel的图片,显示棋子可走路线.
	 */
	private Icon nullChessIcon;
	
	/**
	 * 场景数组和棋盘上对应的位置.
	 */
	private HashMap<Integer,Integer> positions = new HashMap<>();
	
	/**
	 * 初始化所有棋子,默认位置是从指定位置往下绘制,所以需要去掉一半.
	 */
	public DefaultChess() {
		// 将位置都初始化 就 10 格,0-9.
		for (int i = 0;i < 10;i++) {
			int v = first + i * size;
			positions.put(i, v);
			positions.put(v, i);
		}
		
		// 初始化选择框
		select = new JLabel();
		select.setBackground(Color.RED);
		select.setOpaque(true);
		select.setSize(size, size);
		select.setVisible(false);
		
		upSelect = new JLabel();
		upSelect.setBackground(Color.RED);
		upSelect.setOpaque(true);
		upSelect.setSize(size, size);
		upSelect.setVisible(false);
	
		// 可走路线图片
		BufferedImage nullChessImg = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics g = nullChessImg.getGraphics();
		g.setColor(Color.RED);
		int nullChessWidth = nullChessImg.getWidth() >> 2;
		int nullChessHeight = nullChessImg.getHeight() >> 2;
		g.fillRoundRect((int)(nullChessImg.getWidth() - nullChessWidth >> 1), nullChessImg.getHeight() - nullChessHeight >> 1, nullChessWidth, nullChessHeight, 180, 180);
		nullChessIcon = new ImageIcon(nullChessImg);
	}
	
	@Override public void init(ChessBoard chessBoard) {
		// 红方棋
		int redY = getScenePos(9);// 红方最后一排的位置
		JLabel redChe1 = ChessFactory.createDefaultChess(true,"车","红车","红车1",size,size);
		JLabel redChe2 = ChessFactory.createDefaultChess(true,"车","红车","红车2",size,size);
		chesses.put("红车1", redChe1); chesses.put("红车2", redChe2);
		defaultLocation.put("红车1", new Point(getScenePos(0),redY));
		defaultLocation.put("红车2", new Point(getScenePos(8),redY));
		JLabel redMa1 = ChessFactory.createDefaultChess(true,"马","红马","红马1",size,size);
		JLabel redMa2 = ChessFactory.createDefaultChess(true,"马","红马","红马2",size,size);
		chesses.put("红马1", redMa1); chesses.put("红马2", redMa2);
		defaultLocation.put("红马1", new Point(getScenePos(1),redY));
		defaultLocation.put("红马2", new Point(getScenePos(7),redY));
		JLabel redXiang1 = ChessFactory.createDefaultChess(true,"相","红相","红相1",size,size);
		JLabel redXinag2 = ChessFactory.createDefaultChess(true,"相","红相","红相2",size,size);
		chesses.put("红相1", redXiang1); chesses.put("红相2", redXinag2);
		defaultLocation.put("红相1", new Point(getScenePos(2),redY));
		defaultLocation.put("红相2", new Point(getScenePos(6),redY));
		JLabel redShi1 = ChessFactory.createDefaultChess(true,"仕","红仕","红仕1",size,size);
		JLabel redShi2 = ChessFactory.createDefaultChess(true,"仕","红仕","红仕2",size,size);
		chesses.put("红仕1", redShi1); chesses.put("红仕2", redShi2);
		defaultLocation.put("红仕1", new Point(getScenePos(3),redY));
		defaultLocation.put("红仕2", new Point(getScenePos(5),redY));
		JLabel redPao1 = ChessFactory.createDefaultChess(true,"炮","红炮","红炮1",size,size);
		JLabel redPao2 = ChessFactory.createDefaultChess(true,"炮","红炮","红炮2",size,size);
		int redPaoY = getScenePos(7);
		chesses.put("红炮1", redPao1); chesses.put("红炮2", redPao2);
		defaultLocation.put("红炮1", new Point(getScenePos(1),redPaoY));
		defaultLocation.put("红炮2", new Point(getScenePos(7),redPaoY));
		int redBingY = getScenePos(6);
		for (int i = 0;i < 5;i++) {
			String name = "红兵" + (i+1);
			JLabel redBing = ChessFactory.createDefaultChess(true,"兵","红兵",name,size,size);
			chesses.put(name, redBing);
			defaultLocation.put(name, new Point(getScenePos(i<<1),redBingY));
		}
		JLabel redShuai = ChessFactory.createDefaultChess(true,"帅","红帅","红帅",size,size);
		chesses.put("红帅", redShuai);
		defaultLocation.put("红帅", new Point(getScenePos(4),redY));
		
		// 黑方棋
		JLabel blackChe1 = ChessFactory.createDefaultChess(false,"車","黑車","黑車1",size,size);
		JLabel blackChe2 = ChessFactory.createDefaultChess(false,"車","黑車","黑車2",size,size);
		chesses.put("黑車1", blackChe1); chesses.put("黑車2", blackChe2);
		defaultLocation.put("黑車1", new Point(getScenePos(0),first));
		defaultLocation.put("黑車2", new Point(getScenePos(8),first));
		JLabel blackMa1 = ChessFactory.createDefaultChess(false,"馬","黑馬","黑馬1",size,size);
		JLabel blackMa2 = ChessFactory.createDefaultChess(false,"馬","黑馬","黑馬2",size,size);
		chesses.put("黑馬1", blackMa1); chesses.put("黑馬2", blackMa2);
		defaultLocation.put("黑馬1", new Point(getScenePos(1),first));
		defaultLocation.put("黑馬2", new Point(getScenePos(7),first));
		JLabel blackXiang1 = ChessFactory.createDefaultChess(false,"象","黑象","黑象1",size,size);
		JLabel blackXiang2 = ChessFactory.createDefaultChess(false,"象","黑象","黑象2",size,size);
		chesses.put("黑象1", blackXiang1); chesses.put("黑象2", blackXiang2);
		defaultLocation.put("黑象1", new Point(getScenePos(2),first));
		defaultLocation.put("黑象2", new Point(getScenePos(6),first));
		JLabel blackShi1 = ChessFactory.createDefaultChess(false,"士","黑士","黑士1",size,size);
		JLabel blackShi2 = ChessFactory.createDefaultChess(false,"士","黑士","黑士2",size,size);
		chesses.put("黑士1", blackShi1); chesses.put("黑士2", blackShi2);
		defaultLocation.put("黑士1", new Point(getScenePos(3),first));
		defaultLocation.put("黑士2", new Point(getScenePos(5),first));
		JLabel blackPao1 = ChessFactory.createDefaultChess(false,"炮","黑炮","黑炮1",size,size);
		JLabel blackPao2 = ChessFactory.createDefaultChess(false,"炮","黑炮","黑炮2",size,size);
		chesses.put("黑炮1", blackPao1); chesses.put("黑炮2", blackPao2);
		defaultLocation.put("黑炮1", new Point(getScenePos(1),getScenePos(2)));
		defaultLocation.put("黑炮2", new Point(getScenePos(7),getScenePos(2)));
		for (int i = 0;i < 5;i++) {
			String name = "黑卒" + (i+1);
			JLabel blackZu = ChessFactory.createDefaultChess(false,"卒","黑卒",name,size,size);
			chesses.put(name, blackZu);
			defaultLocation.put(name, new Point(first + i * size * 2,first + size * 3));
		}
		JLabel blackJiang = ChessFactory.createDefaultChess(false,"将","黑将","黑将",size,size);
		chesses.put("黑将", blackJiang);
		defaultLocation.put("黑将", new Point(first + size * 4,first));
		
		// 将选择框添加进棋盘
		chessBoard.add(select);
		chessBoard.add(upSelect);
		
		// 象棋数量,用于创建象棋对应空点.
		int length = chesses.size();
		
		// 循环将棋子添加进棋盘,刚开始不能让棋子出现在棋盘中,不隐藏.
		chesses.forEach((k,v) -> { v.setLocation(-100, -100); chessBoard.add(v,0); });
		
		// 创建四个 JLabel 用于代表可以吃的点
		for (int i = 0;i < 4;i++) {
			JLabel label = new JLabel();
			label.setIcon(nullChessIcon);
			label.setSize(size, size);
			label.setVisible(false);
			nullChesses.put(String.valueOf(i), label);
			chessBoard.add(label,0);
		}
				
		// 创建点击处理事件并添加进面板
		for (int i = 0;i < scene.length;i++) {
			String[] xScene = scene[i];
			for (int j = 0;j < xScene.length;j++){
				// 创建象棋对应空白点,放这里面是为了节省性能.
				if (length > 0) {
					length--;
					var text = length + ":";
					JLabel nullChess = new JLabel(text);
					nullChess.addMouseListener(ListenerFactory.getMouseListener("labelMouse"));
					nullChess.setIcon(nullChessIcon);
					nullChess.setVisible(false);
					nullChess.setName(text);
					nullChess.setSize(size, size);
					eatNullChesses.put(text,nullChess);
					chessBoard.add(nullChess,0);
				}
				if (xScene[j] == null) {
					int x = first + size * j;
					int y = first + size * i;
					
					String key = x + ":" + y;
					JLabel nullChess = new JLabel(key);
					nullChess.setBounds(x, y, size, size);
					nullChess.addMouseListener(ListenerFactory.getMouseListener("labelMouse"));
					nullChess.setIcon(nullChessIcon);
					nullChess.setVisible(false);
					nullChess.setName(key);
					scene[i][j] = key;
					nullChesses.put(key, nullChess);
					chessBoard.add(nullChess,0);
				}
			}
		}
		
	}
	
	@Override public void start() {
		// 分配队伍
		redBlack = System.currentTimeMillis() % 2 == 0 ? true : false;
		
		// 根据队伍进行初始化位置
		if (redBlack) {
			defaultLocation.forEach((k,v) -> {
				JLabel chess = chesses.get(k);
				chess.setLocation(v);
				scene[getScenePos(v.y)][getScenePos(v.x)] = k;
				// 给棋子加监听
				chess.addMouseListener(ListenerFactory.getMouseListener("labelMouse"));
			});
		} else {
			int num = size * 9;
			defaultLocation.forEach((k,v) -> {
				int y = num - v.y + size;
				JLabel chess = chesses.get(k);
				chess.setLocation(v.x, y);
				// 翻转算法 (size * 9 - y + size) - first / size;
				scene[getScenePos(y)][getScenePos(v.x)] = k;
				// 给棋子加监听
				chess.addMouseListener(ListenerFactory.getMouseListener("labelMouse"));
			});
		}
		
		game.init(redBlack);
		
		// 输出初始化后的场景.
//		for (int i = 0;i < scene.length;i++) System.out.println(Arrays.toString(scene[i]));
	}

	@Override
	public void stop() {
		chesses.forEach((k,v) -> { factoryReset(v); });
		factoryResetNullChess();
		select.setVisible(false);
		upSelect.setVisible(false);
	}
	
	/**
	 * 将棋子恢复出厂化.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param chess 棋子
	 */
	private void factoryReset(JLabel chess) {
		chess.setVisible(true);
		
		MouseListener[] mouses = chess.getMouseListeners();
		for (MouseListener m : mouses) chess.removeMouseListener(m);
		
	}
	
	/**
	 * 将空白处位置恢复出厂化,并在场景内位置也恢复默认.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param nullChess
	 */
	private void factoryResetNullChess() {
		nullChesses.forEach((k,v) -> {
			String[] xy = k.split(":");
			if (xy.length > 1) {
				int x = Integer.parseInt(xy[0]);
				int y = Integer.parseInt(xy[1]);
				v.setLocation(x,y);
				scene[getPos(y)][getPos(x)] = k;
			}
			v.setVisible(false);
		});
	}
	
	/**
	 * 将棋盘上组件的位置转换为数组中对应的位置
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param pos 棋盘上对应的位置
	 * @return 数组上对应的位置.
	 */
	public int getPos(int pos) { return getPosition(pos); }
	
	/**
	 * 将场景数组上的位置转换为棋盘上组件位置.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param scenePos 场景上数组位置.
	 * @return 棋盘上对应位置
	 */
	public int getScenePos(int scenePos) { return getPosition(scenePos); }
	
	/**
	 * 获取位置,因为计算的时候有些会四舍五入,则需要取近似值.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param position 集合内位置
	 * @return 对应数.
	 */
	private int getPosition(int position) {
		if (!positions.containsKey(position)) {
			if (positions.containsKey(position + 1))
				return positions.get(position + 1);
			else
				return positions.get(position - 1);
		}
		return positions.get(position);
	}
	
	/**
	 * 获取当前游戏场景.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @return 字符串的二维数组.
	 */
	public String[][] getScene() { return scene; }
	
	/**
	 * 获取对应象棋.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @return 如果有则返回对应象棋,否则null.
	 */
	public JLabel getChess(String name) { return chesses.get(name); }
	
	/**
	 * 获取指定可走路线点.<br>
	 * 先从 {@link #nullChesses} 中获取,如果没有则从 {@link #eatNullChesses} 中获取.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param name 可走路线点的名.
	 * @return 可走路线点.
	 */
	public JLabel getNullChess(String name) {
		return nullChesses.containsKey(name) ? nullChesses.get(name) : eatNullChesses.get(name);
	}
	
	/**
	 * 当棋子被吃掉时获取对应可走路线点.<br>
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param num 第几个被吃掉的棋子.
	 * @return 可走路线点.
	 */
	public JLabel getNullChess(int num) { return eatNullChesses.get(num + ":"); }
	
	/**
	 * 获取对应游戏逻辑处理.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @return {@link DefaultGame}
	 */
	public Game getGame() { return game; }
	
}
