package shendi.game.gobang.disposal;

import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import shendi.game.gobang.ai.AI;
import shendi.game.gobang.listener.LabelListener;
import shendi.game.gobang.view.ChessBoard;

/**
 * 游戏逻辑执行类,游戏只需要一个处理,所以单例.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public class Game {
	
	private Game() {}
	private static final Game GAME = new Game();
	
	/**
	 * 棋盘大小,正方形,默认 9
	 */
	private int chessBoardNum = 9;
	
	/**
	 * 胜利需要的棋子数,默认5
	 */
	private int vectoryNum = 5;
	
	/**
	 * 场景,1代表白子,2黑子
	 */
	private int[][] scene;
	
	/**
	 * 当前玩家的阵营,true白子,false黑子.
	 */
	private boolean whiteBlack;
	
	/**
	 * 所有的棋子预制体.
	 */
	private ArrayList<JLabel> chesses = new ArrayList<>();
	
	/**
	 * 棋盘.
	 */
	private ChessBoard chessBoard;
	
	/**
	 * 当前下棋的队伍,true玩家,false人机.
	 */
	private boolean currentTeam;
	
	/**
	 * 开始游戏时调用.<br>
	 * 用来重置所有状态并开始游戏.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 */
	public void start() {
		if (chessBoard == null) { chessBoard = ChessBoard.getChessBoard(); }
		
		// 重置场景
		scene = new int[chessBoardNum][chessBoardNum];
		
		// 预制体长度不够则补充
		int num = chesses.size() - chessBoardNum * chessBoardNum;
		while (num < 0) {
			chesses.add(chessBoard.createChess());
			num++;
		}
		
		int x,y;
		// 重置预制体.
		for (int i = 0;i < scene.length;i++) {
			int[] xScene = scene[i];
			for (int j = 0;j < xScene.length;j++) {
				var chess = chesses.get(num);
				num++;
				chess.setIcon(null);
				x = chessBoard.getGamePos(j);
				y = chessBoard.getGamePos(i);
				chess.setLocation(x, y);
				chess.addMouseListener(LabelListener.getLabelListener());
			}
		}
		
		// 分配队伍
		whiteBlack = System.currentTimeMillis() % 2 == 0;
		AI.getAI().init(!whiteBlack);
		// 白子先走,如果是人机则人机先走.
		if (whiteBlack) {
			currentTeam = true;
		} else {
			currentTeam = false;
			AI.getAI().play();
		}
		
	}
	
	/**
	 * 停止游戏时调用.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param whiteBlack 调用此函数的是什么队伍
	 */
	public void stop(boolean whiteBlack) {
		if (this.whiteBlack == whiteBlack)
			JOptionPane.showMessageDialog(null, "你胜利了");
		else JOptionPane.showMessageDialog(null, "你失败了");
		for (var chess : chesses) chess.removeMouseListener(LabelListener.getLabelListener());
	}
	
	/**
	 * 玩家执行操作的函数.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param chess 棋子
	 */
	public void exec(JLabel chess) { if (currentTeam) exec(chess,whiteBlack); }
	
	/**
	 * 执行下棋操作.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param chess 棋子
	 * @param whiteBlack 白棋还是黑棋.
	 */
	public void exec(JLabel chess,boolean whiteBlack) {
		// 指定位置是否有棋子,有不做操作
		int x = chessBoard.getScenePos(chess.getX());
		int y = chessBoard.getScenePos(chess.getY());
		if (scene[y][x] != 0) { return; }
		// 下棋
		int team = whiteBlack ? 1 : 2;
		scene[y][x] = team;
		
//		for (int[] xScene : scene) System.out.println(Arrays.toString(xScene));
		
		if (whiteBlack) chess.setIcon(chessBoard.whiteChessIcon);
		else chess.setIcon(chessBoard.blackChessIcon);
		// 判断胜负,横竖斜有指定数量棋子则胜利,对方失败
		int h = 1,v = 1,ld = 1,rd = 1;
		
		// 横,左右
		int posX,posY;
		for (int i = 1;i < chessBoardNum;i++) {
			posX = x - i;
			if (posX < 0) break;
			if (scene[y][posX] == team) h++; else break;
		}
		for (int i = 1;i < chessBoardNum;i++) {
			posX = x + i;
			if (posX >= chessBoardNum) break;
			if (scene[y][posX] == team) h++; else break;
		}
		
		// 竖,上下
		for (int i = 1;i < chessBoardNum;i++) {
			posY = y - i;
			if (posY < 0) break;
			if (scene[posY][x] == team) v++; else break;
		}
		for (int i = 1;i < chessBoardNum;i++) {
			posY = y + i;
			if (posY >= chessBoardNum) break;
			if (scene[posY][x] == team) v++; else break;
		}
		
		// 左斜,上左下右 \
		for (int i = 1;i < chessBoardNum;i++) {
			posX = x - i;
			posY = y - i;
			if (posX < 0 || posY < 0) break;
			if (scene[posY][posX] == team) ld++; else break;
		}
		for (int i = 1;i < chessBoardNum;i++) {
			posX = x + i;
			posY = y + i;
			if (posX >=  chessBoardNum || posY >= chessBoardNum) break;
			if (scene[posY][posX] == team) ld++; else break;
		}
		
		// 右斜,上右下左 /
		for (int i = 1;i < chessBoardNum;i++) {
			posX = x + i;
			posY = y - i;
			if (posX >= chessBoardNum || posY < 0) break;
			if (scene[posY][posX] == team) rd++; else break;
		}
		for (int i = 1;i < chessBoardNum;i++) {
			posX = x - i;
			posY = y + i;
			if (posX < 0 || posY >= chessBoardNum) break;
			if (scene[posY][posX] == team) rd++; else break;
		}
		
		if (h >= vectoryNum || v >= vectoryNum || ld >= vectoryNum || rd >= vectoryNum) {
			stop(whiteBlack);
			return;
		}
		
		// 当前角色下棋完成,换一边
		currentTeam = !currentTeam;
		if (!currentTeam) AI.getAI().play();
	}
	
	/**
	 * 获取棋盘大小.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @return 棋盘大小
	 */
	public int getChessBoardNum() { return chessBoardNum; }
	/**
	 * 设置棋盘大小.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param chessBoardNum 要设置的棋盘大小
	 */
	public void setChessBoardNum(int chessBoardNum) {
		this.chessBoardNum = chessBoardNum;
		ChessBoard.getChessBoard().repaint(chessBoardNum);
	}
	
	/**
	 * 获取连珠棋胜利条件(五子?)
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @return 对应胜利所需条件
	 */
	public int getVectoryNum() { return vectoryNum; }
	/**
	 * 设置连珠棋胜利条件
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param vectoryNum 要设置的几子棋.
	 */
	public void setVectoryNum(int vectoryNum) { this.vectoryNum = vectoryNum; }
	
	/**
	 * 获取游戏对象
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @return {@link Game}
	 */
	public static Game getGame() { return GAME; }

	/**
	 * 获取当前游戏场景.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @return 游戏场景
	 */
	public int[][] getScene() { return scene; }
	
	/**
	 * 获取棋子集合.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @return 棋子集合
	 */
	public ArrayList<JLabel> getChesses() { return chesses; }
	
}
