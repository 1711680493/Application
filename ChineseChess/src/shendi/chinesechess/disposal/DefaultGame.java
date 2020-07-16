package shendi.chinesechess.disposal;

import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import shendi.chinesechess.view.Chess;
import shendi.chinesechess.view.defaultview.DefaultChess;

/**
 * 默认游戏逻辑处理类,人机对战.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public class DefaultGame extends Game {
	
	/**
	 * 上一次被选中的棋子.
	 */
	private JLabel upChess;
	
	/**
	 * 显示的行走路线.
	 */
	private ArrayList<JLabel> nullChesses = new ArrayList<>();
	
	/**
	 * 对应的棋子绘制类,用于获取一些组件.
	 */
	private DefaultChess dChess;
	
	/**
	 * 吃棋数量,用于获取空点
	 */
	private int eatChessNum = 0;
	
	/**
	 * 可以被吃的棋子的点.
	 */
	private int eatChess = 0;
	
	public DefaultGame(Chess chess) {
		super(chess);
		this.dChess = (DefaultChess) chess;
	}
	
	@Override public void init() {
		clearNullChess();
		upChess = null;
		eatChessNum = 0;
		eatChess = 0;
	}
	
	/**
	 * 当前执行操作的是什么队伍.
	 */
	private boolean redBlack;
	
	@Override public void onClick(JLabel chess,boolean redBlack) {
		this.redBlack = redBlack;
		String text = chess.getText();
		String[][] scene = dChess.getScene();
		
		clearNullChess();
		dChess.select.setVisible(false);
		
		// 选择己方棋子则选中/重新选中,否则为吃棋/行走
		// 提取的重复代码
		if (upChess == null) {
			if (!isShowNullChess(chess.getText())) {
				upChess = chess;
				dChess.upSelect.setVisible(true);
				dChess.upSelect.setLocation(chess.getX(), chess.getY());
			} else {
				return;
			}
		} else {
			if (!isShowNullChess(chess.getText())) {
				upChess = null;
				onClick(chess, redBlack);
			// 不为行走则为吃棋,就不需要执行后面代码了
			} else if (!text.contains(":")) {
				eat(upChess, chess, scene);
				// 将/帅被吃则失败
				if (chess.isVisible() == false) {
					if ("红帅".equals(text)) {
						if (dChess.redBlack) {
							JOptionPane.showMessageDialog(null, "你失败了");
						} else {
							JOptionPane.showMessageDialog(null, "你胜利了");
						}
						super.stop();
					} else if ("黑将".equals(text)) {
						if (dChess.redBlack) {
							JOptionPane.showMessageDialog(null, "你胜利了");
						} else {
							JOptionPane.showMessageDialog(null, "你失败了");
						}
						super.stop();
					}
					// 棋子移动
					chessMove(chess.getX(), chess.getY());
				}
				return;
			}
		}
		
		// 显示可行走路线和执行行走.
		// 人机和玩家都可以显示可走路线.
		switch (text) {
		case "红车":
		case "黑車":
			// 选择车
			if (upChess == null || upChess == chess) selectChe(chess, scene);
			break;
		case "红炮":
		case "黑炮":
			if (upChess == null || upChess == chess) selectPao(chess, scene);
			break;
		case "红帅":
		case "黑将":
			if (upChess == null || upChess == chess) selectMain(chess, scene);
			break;
		case "红马":
		case "黑馬":
			if (upChess == null || upChess == chess) selectMa(chess, scene);
			break;
		case "红兵":
		case "黑卒":
			if (upChess == null || upChess == chess) selectBing(chess, scene);
			break;
		case "红相":
		case "黑象":
			if (upChess == null || upChess == chess) selectXiang(chess, scene);
			break;
		case "红仕":
		case "黑士":
			if (upChess == null || upChess == chess) selectShi(chess, scene);
			break;
		// 判断是否为走棋
		default:
			// 如果有选中棋子,并且执行到此,就将上一次选中隐藏(上方不管是什么都会将上一次选中显示)
			if (upChess == null || upChess == chess) {
				dChess.upSelect.setVisible(false);
			} else {
				if (text.contains(":")) {
					dChess.select.setVisible(true);
					dChess.upSelect.setVisible(true);
					
					int x = chess.getX();
					int y = chess.getY();
					dChess.select.setLocation(x,y);
					chess.setLocation(upChess.getX(), upChess.getY());
					scene[dChess.getPos(upChess.getY())][dChess.getPos(upChess.getX())] = chess.getName();
					scene[dChess.getPos(y)][dChess.getPos(x)] = upChess.getName();
					
					// 棋子移动
					chessMove(x, y);
					
//					for (int i = 0;i < scene.length;i++) System.out.println(Arrays.toString(scene[i]));
				}
			}
		}
	}
	
	/**
	 * 清除行走路径.<br>
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 */
	private void clearNullChess() {
		int size = nullChesses.size();
		for (int i = 0;i < size;i++) {
			JLabel chess = nullChesses.remove(0);
			chess.setVisible(false);
		}
		eatChess = 0;
	}
	
	/**
	 * 车的可走路线.<br>
	 * 车走直线,不隔子.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param chess 车棋子
	 * @param scene 场景
	 */
	private void selectChe(JLabel chess,String[][] scene) {
		// 显示可走路线,横着竖着
		int x = dChess.getPos(chess.getX());
		int y = dChess.getPos(chess.getY());
		// 左右上下
		for (int i = x - 1;i >= 0;i--) {
			if (!scene[y][i].contains(":")) {
				if (isShowNullChess(scene[y][i])) {
					JLabel nullChess = dChess.getNullChess(String.valueOf(eatChess));
					eatChess++;
					nullChess.setLocation(dChess.getPos(i), chess.getY());
					nullChess.setVisible(true);
					nullChesses.add(nullChess);
				}
				break;
			}
			JLabel nullChess = dChess.getNullChess(scene[y][i]);
			nullChess.setVisible(true);
			nullChesses.add(nullChess);
		}
		for (int i = x + 1;i < 9;i++) {
			if (!scene[y][i].contains(":")) {
				if (isShowNullChess(scene[y][i])) {
					JLabel nullChess = dChess.getNullChess(String.valueOf(eatChess));
					eatChess++;
					nullChess.setLocation(dChess.getPos(i), chess.getY());
					nullChess.setVisible(true);
					nullChesses.add(nullChess);
				}
				break;
			}
			JLabel nullChess = dChess.getNullChess(scene[y][i]);
			nullChess.setVisible(true);
			nullChesses.add(nullChess);
		}
		for (int i = y - 1;i >= 0;i--) {
			if (!scene[i][x].contains(":")) {
				if (isShowNullChess(scene[i][x])) {
					JLabel nullChess = dChess.getNullChess(String.valueOf(eatChess));
					eatChess++;
					nullChess.setLocation(chess.getX(), dChess.getPos(i));
					nullChess.setVisible(true);
					nullChesses.add(nullChess);
				}
				break;
			}
			JLabel nullChess = dChess.getNullChess(scene[i][x]);
			nullChess.setVisible(true);
			nullChesses.add(nullChess);
		}
		for (int i = y + 1;i < 10;i++) {
			if (!scene[i][x].contains(":")) {
				if (isShowNullChess(scene[i][x])) {
					JLabel nullChess = dChess.getNullChess(String.valueOf(eatChess));
					eatChess++;
					nullChess.setLocation(chess.getX(), dChess.getPos(i));
					nullChess.setVisible(true);
					nullChesses.add(nullChess);
				}
				break;
			}
			JLabel nullChess = dChess.getNullChess(scene[i][x]);
			nullChess.setVisible(true);
			nullChesses.add(nullChess);
		}
	}
	
	/**
	 * 炮的可走路线.
	 * 跑走直线,隔子打.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param chess 炮棋子
	 * @param scene 场景
	 */
	private void selectPao(JLabel chess,String[][] scene) {
		// 显示可走路线,横着竖着
		int x = dChess.getPos(chess.getX());
		int y = dChess.getPos(chess.getY());
		// 左右上下,看对面有无棋子
		for (int i = x - 1;i >= 0;i--) {
			if (!scene[y][i].contains(":")) {
				// 跳过这颗棋子,继续向左判断.
				for (int j = i - 1;j >= 0;j--) {
					if (!scene[y][j].contains(":")) {
						if (!isShowNullChess(scene[y][j])) break;
						JLabel nullChess = dChess.getNullChess(String.valueOf(eatChess));
						eatChess++;
						nullChess.setLocation(dChess.getPos(j), chess.getY());
						nullChess.setVisible(true);
						nullChesses.add(nullChess);
						break;
					}
				}
				break;
			}
			JLabel nullChess = dChess.getNullChess(scene[y][i]);
			nullChess.setVisible(true);
			nullChesses.add(nullChess);
		}
		for (int i = x + 1;i < 9;i++) {
			if (!scene[y][i].contains(":")) {
				for (int j = i + 1;j < 9;j++) {
					if (!scene[y][j].contains(":")) {
						if (!isShowNullChess(scene[y][j])) break;
						JLabel nullChess = dChess.getNullChess(String.valueOf(eatChess));
						eatChess++;
						nullChess.setLocation(dChess.getPos(j), chess.getY());
						nullChess.setVisible(true);
						nullChesses.add(nullChess);
						break;
					}
				}
				break;
			}
			JLabel nullChess = dChess.getNullChess(scene[y][i]);
			nullChess.setVisible(true);
			nullChesses.add(nullChess);
		}
		for (int i = y - 1;i >= 0;i--) {
			if (!scene[i][x].contains(":")) {
				for (int j = i - 1;j >= 0;j--) {
					if (!scene[j][x].contains(":")) {
						if (!isShowNullChess(scene[j][x])) break;
						JLabel nullChess = dChess.getNullChess(String.valueOf(eatChess));
						eatChess++;
						nullChess.setLocation(chess.getX(), dChess.getPos(j));
						nullChess.setVisible(true);
						nullChesses.add(nullChess);
						break;
					}
				}
				break;
			}
			JLabel nullChess = dChess.getNullChess(scene[i][x]);
			nullChess.setVisible(true);
			nullChesses.add(nullChess);
		}
		for (int i = y + 1;i < 10;i++) {
			if (!scene[i][x].contains(":")) {
				for (int j = i + 1;j < 10;j++) {
					if (!scene[j][x].contains(":")) {
						if (!isShowNullChess(scene[j][x])) break;
						JLabel nullChess = dChess.getNullChess(String.valueOf(eatChess));
						eatChess++;
						nullChess.setLocation(chess.getX(), dChess.getPos(j));
						nullChess.setVisible(true);
						nullChesses.add(nullChess);
						break;
					}
				}
				break;
			}
			JLabel nullChess = dChess.getNullChess(scene[i][x]);
			nullChess.setVisible(true);
			nullChesses.add(nullChess);
		}
	}
	
	/**
	 * 将和帅的可走路线.<br>
	 * 不管将和帅都在下方.
	 * 将帅走格,不出格,谁先碰面谁被吃.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param chess 将/帅棋子
	 * @param scene 场景
	 */
	private void selectMain(JLabel chess,String[][] scene) {
		// 只能在指定范围 [3 <= x <= 5],我方[7 <= y <= 9],人机方[0 <= y <= 2]
		// 一次只走一格,可以吃任何棋,将军碰面则可以直接吃对面将军
		int x = dChess.getPos(chess.getX());
		int y = dChess.getPos(chess.getY());
		
		// 左右上下
		if (x > 3 && x <= 5) {
			var text = scene[y][x - 1];
			if (!text.contains(":")) {
				if (isShowNullChess(text)) {
					JLabel nullChess = dChess.getNullChess(String.valueOf(eatChess));
					eatChess++;
					nullChess.setLocation(dChess.getPos(x - 1), chess.getY());
					nullChess.setVisible(true);
					nullChesses.add(nullChess);
				}
			} else {
				JLabel nullChess = dChess.getNullChess(text);
				nullChess.setVisible(true);
				nullChesses.add(nullChess);
			}
		}
		if (x >= 3 && x < 5) {
			var text = scene[y][x + 1];
			if (!text.contains(":")) {
				if (isShowNullChess(text)) {
					JLabel nullChess = dChess.getNullChess(String.valueOf(eatChess));
					eatChess++;
					nullChess.setLocation(dChess.getPos(x + 1), chess.getY());
					nullChess.setVisible(true);
					nullChesses.add(nullChess);
				}
			} else {
				JLabel nullChess = dChess.getNullChess(text);
				nullChess.setVisible(true);
				nullChesses.add(nullChess);
			}
		}
		// 玩家上下[y|7 <= y <= 9]
		if (redBlack == dChess.redBlack) {
			if (y > 7) {
				var text = scene[y - 1][x];
				if (!text.contains(":")) {
					if (isShowNullChess(text)) {
						JLabel nullChess = dChess.getNullChess(String.valueOf(eatChess));
						eatChess++;
						nullChess.setLocation(chess.getX(), dChess.getPos(y - 1));
						nullChess.setVisible(true);
						nullChesses.add(nullChess);
					}
				} else {
					JLabel nullChess = dChess.getNullChess(text);
					nullChess.setVisible(true);
					nullChesses.add(nullChess);
				}
				// 先看对方将是否和己方在同一直线上,是则看中间有无别的棋子.
				F:for (int i = 0;i < 3;i++) {
					var chessName = scene[i][x];
					if ("红帅".equals(chessName) || "黑将".equals(chessName)) {
						for (int j = i + 1;j < y;j++) {
							if (!scene[j][x].contains(":")) {
								break F;
							}
						}
						JLabel eatNullChess = dChess.getNullChess(String.valueOf(eatChess));
						eatChess++;
						eatNullChess.setLocation(chess.getX(), dChess.getPos(i));
						eatNullChess.setVisible(true);
						nullChesses.add(eatNullChess);
						break;
					}
				}
			}
			if (y < 9) {
				var text = scene[y + 1][x];
				if (!text.contains(":")) {
					if (isShowNullChess(text)) {
						JLabel nullChess = dChess.getNullChess(String.valueOf(eatChess));
						eatChess++;
						nullChess.setLocation(chess.getX(), dChess.getPos(y + 1));
						nullChess.setVisible(true);
						nullChesses.add(nullChess);
					}
				} else {
					JLabel nullChess = dChess.getNullChess(text);
					nullChess.setVisible(true);
					nullChesses.add(nullChess);
				}
			}
		// 人机上下[y|0 <= y <= 2]
		} else {
			if (y < 2) {
				var text = scene[y + 1][x];
				if (!text.contains(":")) {
					if (isShowNullChess(text)) {
						JLabel nullChess = dChess.getNullChess(String.valueOf(eatChess));
						eatChess++;
						nullChess.setLocation(chess.getX(), dChess.getPos(y + 1));
						nullChess.setVisible(true);
						nullChesses.add(nullChess);
					}
				} else {
					JLabel nullChess = dChess.getNullChess(text);
					nullChess.setVisible(true);
					nullChesses.add(nullChess);
				}
				// 先看对方将是否和己方在同一直线上,是则看中间有无别的棋子.
				F:for (int i = 9;i > 6;i--) {
					var chessName = scene[i][x];
					if ("红帅".equals(chessName) || "黑将".equals(chessName)) {
						for (int j = i - 1;j > 0;j--) {
							if (!scene[j][x].contains(":")) {
								break F;
							}
						}
						JLabel eatNullChess = dChess.getNullChess(String.valueOf(eatChess));
						eatChess++;
						eatNullChess.setLocation(chess.getX(), dChess.getPos(i));
						eatNullChess.setVisible(true);
						nullChesses.add(eatNullChess);
						break;
					}
				}
			}
			if (y > 0) {
				var text = scene[y - 1][x];
				if (!text.contains(":")) {
					if (isShowNullChess(text)) {
						JLabel nullChess = dChess.getNullChess(String.valueOf(eatChess));
						eatChess++;
						nullChess.setLocation(chess.getX(), dChess.getPos(y - 1));
						nullChess.setVisible(true);
						nullChesses.add(nullChess);
					}
				} else {
					JLabel nullChess = dChess.getNullChess(text);
					nullChess.setVisible(true);
					nullChesses.add(nullChess);
				}
			}
		}
	}
	
	
	/**
	 * 马的可走路线.<br>
	 * 马走日,正日倒日全能走,路前不能有它子.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param chess 马棋子
	 * @param scene 场景
	 */
	private void selectMa(JLabel chess,String[][] scene) {
		// 马跳日,路前不能有棋子.
		int x = dChess.getPos(chess.getX());
		int y = dChess.getPos(chess.getY());
		
		// 左右上下,每一边都有两个点,左边的话,棋子左边不能有棋,右上下同样.
		if (x >= 2 && scene[y][x - 1].contains(":")) {
			// 左边的上面
			if (y > 0) {
				var text = scene[y - 1][x - 2];
				if (isShowNullChess(text)) {
					if (!text.contains(":")) {
						JLabel nullChess = dChess.getNullChess(String.valueOf(eatChess));
						eatChess++;
						nullChess.setLocation(dChess.getPos(x - 2), dChess.getPos(y - 1));
						nullChess.setVisible(true);
						nullChesses.add(nullChess);
					} else {
						JLabel nullChess = dChess.getNullChess(text);
						nullChess.setVisible(true);
						nullChesses.add(nullChess);
					}
				}
			}
			// 左边的下面,竖排有十格
			if (y < 9) {
				var text = scene[y + 1][x - 2];
				if (isShowNullChess(text)) {
					if (!text.contains(":")) {
						JLabel nullChess = dChess.getNullChess(String.valueOf(eatChess));
						eatChess++;
						nullChess.setLocation(dChess.getPos(x - 2), dChess.getPos(y + 1));
						nullChess.setVisible(true);
						nullChesses.add(nullChess);
					} else {
						JLabel nullChess = dChess.getNullChess(text);
						nullChess.setVisible(true);
						nullChesses.add(nullChess);
					}
				}
			}
		}
		// 右 0-8,九个格子,所以这里为 <= 6
		if (x <= 6 && scene[y][x + 1].contains(":")) {
			// 右边的上面
			if (y > 0) {
				var text = scene[y - 1][x + 2];
				if (isShowNullChess(text)) {
					if (!text.contains(":")) {
						JLabel nullChess = dChess.getNullChess(String.valueOf(eatChess));
						eatChess++;
						nullChess.setLocation(dChess.getPos(x + 2), dChess.getPos(y - 1));
						nullChess.setVisible(true);
						nullChesses.add(nullChess);
					} else {
						JLabel nullChess = dChess.getNullChess(text);
						nullChess.setVisible(true);
						nullChesses.add(nullChess);
					}
				}
			}
			// 右边的下面
			if (y < 9) {
				var text = scene[y + 1][x + 2];
				if (isShowNullChess(text)) {
					if (!text.contains(":")) {
						JLabel nullChess = dChess.getNullChess(String.valueOf(eatChess));
						eatChess++;
						nullChess.setLocation(dChess.getPos(x + 2), dChess.getPos(y + 1));
						nullChess.setVisible(true);
						nullChesses.add(nullChess);
					} else {
						JLabel nullChess = dChess.getNullChess(text);
						nullChess.setVisible(true);
						nullChesses.add(nullChess);
					}
				}
			}
		}
		// 上
		if (y >= 2 && scene[y - 1][x].contains(":")) {
			// 上面的左边
			if (x > 0) {
				var text = scene[y - 2][x - 1];
				if (isShowNullChess(text)) {
					if (!text.contains(":")) {
						JLabel nullChess = dChess.getNullChess(String.valueOf(eatChess));
						eatChess++;
						nullChess.setLocation(dChess.getPos(x - 1), dChess.getPos(y - 2));
						nullChess.setVisible(true);
						nullChesses.add(nullChess);
					} else {
						JLabel nullChess = dChess.getNullChess(text);
						nullChess.setVisible(true);
						nullChesses.add(nullChess);
					}
				}
			}
			// 上面的右边
			if (x < 8) {
				var text = scene[y - 2][x + 1];
				if (isShowNullChess(text)) {
					if (!text.contains(":")) {
						JLabel nullChess = dChess.getNullChess(String.valueOf(eatChess));
						eatChess++;
						nullChess.setLocation(dChess.getPos(x + 1), dChess.getPos(y - 2));
						nullChess.setVisible(true);
						nullChesses.add(nullChess);
					} else {
						JLabel nullChess = dChess.getNullChess(text);
						nullChess.setVisible(true);
						nullChesses.add(nullChess);
					}
				}
			}
		}
		// 下 竖排十个格子,0-9
		if (y <= 7 && scene[y + 1][x].contains(":")) {
			// 下面的左边
			if (x > 0) {
				var text = scene[y + 2][x - 1];
				if (isShowNullChess(text)) {
					if (!text.contains(":")) {
						JLabel nullChess = dChess.getNullChess(String.valueOf(eatChess));
						eatChess++;
						nullChess.setLocation(dChess.getPos(x - 1), dChess.getPos(y + 2));
						nullChess.setVisible(true);
						nullChesses.add(nullChess);
					} else {
						JLabel nullChess = dChess.getNullChess(text);
						nullChess.setVisible(true);
						nullChesses.add(nullChess);
					}
				}
			}
			// 下面的右边
			if (x < 8) {
				var text = scene[y + 2][x + 1];
				if (isShowNullChess(text)) {
					if (!text.contains(":")) {
						JLabel nullChess = dChess.getNullChess(String.valueOf(eatChess));
						eatChess++;
						nullChess.setLocation(dChess.getPos(x + 1), dChess.getPos(y + 2));
						nullChess.setVisible(true);
						nullChesses.add(nullChess);
					} else {
						JLabel nullChess = dChess.getNullChess(text);
						nullChess.setVisible(true);
						nullChesses.add(nullChess);
					}
				}
			}
		}
	}
	
	/**
	 * 兵的可走路线.<br>
	 * 兵只向前,过河可横走,不能后退.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param chess 兵棋子
	 * @param scene 场景
	 */
	private void selectBing(JLabel chess,String[][] scene) {
		int x = dChess.getPos(chess.getX());
		int y = dChess.getPos(chess.getY());
		
		// 玩家的y < 5为过河,人机的 y > 4为过河
		if (redBlack == dChess.redBlack) {
			if (y < 5) {
				// 显示左
				if (x > 0) {
					var text = scene[y][x - 1];
					if (isShowNullChess(text)) {
						if (!text.contains(":")) {
							JLabel nullChess = dChess.getNullChess(String.valueOf(eatChess));
							eatChess++;
							nullChess.setLocation(dChess.getPos(x - 1), dChess.getPos(y));
							nullChess.setVisible(true);
							nullChesses.add(nullChess);
						} else {
							JLabel nullChess = dChess.getNullChess(text);
							nullChess.setVisible(true);
							nullChesses.add(nullChess);
						}
					}
				}
				// 显示右
				if (x < 8) {
					var text = scene[y][x + 1];
					if (isShowNullChess(text)) {
						if (!text.contains(":")) {
							JLabel nullChess = dChess.getNullChess(String.valueOf(eatChess));
							eatChess++;
							nullChess.setLocation(dChess.getPos(x + 1), dChess.getPos(y));
							nullChess.setVisible(true);
							nullChesses.add(nullChess);
						} else {
							JLabel nullChess = dChess.getNullChess(text);
							nullChess.setVisible(true);
							nullChesses.add(nullChess);
						}
					}
				}
			}
			// 显示上方的点.
			if (y > 0) {
				var text = scene[y - 1][x];
				if (isShowNullChess(text)) {
					if (!text.contains(":")) {
						JLabel nullChess = dChess.getNullChess(String.valueOf(eatChess));
						eatChess++;
						nullChess.setLocation(dChess.getPos(x), dChess.getPos(y - 1));
						nullChess.setVisible(true);
						nullChesses.add(nullChess);
					} else {
						JLabel nullChess = dChess.getNullChess(text);
						nullChess.setVisible(true);
						nullChesses.add(nullChess);
					}
				}
			}
		// y > 4为过河
		} else {
			if (y > 4) {
				// 显示左
				if (x > 0) {
					var text = scene[y][x - 1];
					if (isShowNullChess(text)) {
						if (!text.contains(":")) {
							JLabel nullChess = dChess.getNullChess(String.valueOf(eatChess));
							eatChess++;
							nullChess.setLocation(dChess.getPos(x - 1), dChess.getPos(y));
							nullChess.setVisible(true);
							nullChesses.add(nullChess);
						} else {
							JLabel nullChess = dChess.getNullChess(text);
							nullChess.setVisible(true);
							nullChesses.add(nullChess);
						}
					}
				}
				// 显示右
				if (x < 8) {
					var text = scene[y][x + 1];
					if (isShowNullChess(text)) {
						if (!text.contains(":")) {
							JLabel nullChess = dChess.getNullChess(String.valueOf(eatChess));
							eatChess++;
							nullChess.setLocation(dChess.getPos(x + 1), dChess.getPos(y));
							nullChess.setVisible(true);
							nullChesses.add(nullChess);
						} else {
							JLabel nullChess = dChess.getNullChess(text);
							nullChess.setVisible(true);
							nullChesses.add(nullChess);
						}
					}
				}
			}
			// 显示上方的点.
			if (y < 9) {
				var text = scene[y + 1][x];
				if (isShowNullChess(text)) {
					if (!text.contains(":")) {
						JLabel nullChess = dChess.getNullChess(String.valueOf(eatChess));
						eatChess++;
						nullChess.setLocation(dChess.getPos(x), dChess.getPos(y + 1));
						nullChess.setVisible(true);
						nullChesses.add(nullChess);
					} else {
						JLabel nullChess = dChess.getNullChess(text);
						nullChess.setVisible(true);
						nullChesses.add(nullChess);
					}
				}
			}
		}
	}
	
	/**
	 * 相的可走路线.<br>
	 * 相飞田,不能过河,田中有子不能走.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param chess 相棋子
	 * @param scene 场景
	 */
	private void selectXiang(JLabel chess,String[][] scene) {
		int x = dChess.getPos(chess.getX());
		int y = dChess.getPos(chess.getY());
		
		// 只有四个点,左上,左下,右上,右下.
		// 玩家相 y >= 5,人机 y <= 4
		if (redBlack == dChess.redBlack) {
			if (x >= 2 && y >= 5) {
				// 左上
				if (y >= 7 && scene[y - 1][x - 1].contains(":")) {
					var text = scene[y - 2][x - 2];
					if (isShowNullChess(text)) {
						if (!text.contains(":")) {
							JLabel nullChess = dChess.getNullChess(String.valueOf(eatChess));
							eatChess++;
							nullChess.setLocation(dChess.getPos(x - 2), dChess.getPos(y - 2));
							nullChess.setVisible(true);
							nullChesses.add(nullChess);
						} else {
							JLabel nullChess = dChess.getNullChess(text);
							nullChess.setVisible(true);
							nullChesses.add(nullChess);
						}
					}
				}
				// 左下
				if (y <= 7 && scene[y + 1][x - 1].contains(":")) {
					var text = scene[y + 2][x - 2];
					if (isShowNullChess(text)) {
						if (!text.contains(":")) {
							JLabel nullChess = dChess.getNullChess(String.valueOf(eatChess));
							eatChess++;
							nullChess.setLocation(dChess.getPos(x - 2), dChess.getPos(y + 2));
							nullChess.setVisible(true);
							nullChesses.add(nullChess);
						} else {
							JLabel nullChess = dChess.getNullChess(text);
							nullChess.setVisible(true);
							nullChesses.add(nullChess);
						}
					}
				}
			}
			if (x <= 6 && y >= 5) {
				// 右上
				if (y >= 7 && scene[y - 1][x + 1].contains(":")) {
					var text = scene[y - 2][x + 2];
					if (isShowNullChess(text)) {
						if (!text.contains(":")) {
							JLabel nullChess = dChess.getNullChess(String.valueOf(eatChess));
							eatChess++;
							nullChess.setLocation(dChess.getPos(x + 2), dChess.getPos(y - 2));
							nullChess.setVisible(true);
							nullChesses.add(nullChess);
						} else {
							JLabel nullChess = dChess.getNullChess(text);
							nullChess.setVisible(true);
							nullChesses.add(nullChess);
						}
					}
				}
				// 右下
				if (y <= 7 && scene[y + 1][x + 1].contains(":")) {
					var text = scene[y + 2][x + 2];
					if (isShowNullChess(text)) {
						if (!text.contains(":")) {
							JLabel nullChess = dChess.getNullChess(String.valueOf(eatChess));
							eatChess++;
							nullChess.setLocation(dChess.getPos(x + 2), dChess.getPos(y + 2));
							nullChess.setVisible(true);
							nullChesses.add(nullChess);
						} else {
							JLabel nullChess = dChess.getNullChess(text);
							nullChess.setVisible(true);
							nullChesses.add(nullChess);
						}
					}
				}
			}
		} else {
			if (x >= 2 && y <= 4) {
				// 左上
				if (y <= 2 && scene[y + 1][x - 1].contains(":")) {
					var text = scene[y + 2][x - 2];
					if (isShowNullChess(text)) {
						if (!text.contains(":")) {
							JLabel nullChess = dChess.getNullChess(String.valueOf(eatChess));
							eatChess++;
							nullChess.setLocation(dChess.getPos(x - 2), dChess.getPos(y + 2));
							nullChess.setVisible(true);
							nullChesses.add(nullChess);
						} else {
							JLabel nullChess = dChess.getNullChess(text);
							nullChess.setVisible(true);
							nullChesses.add(nullChess);
						}
					}
				}
				// 左下
				if (y >= 2 && scene[y - 1][x - 1].contains(":")) {
					var text = scene[y - 2][x - 2];
					if (isShowNullChess(text)) {
						if (!text.contains(":")) {
							JLabel nullChess = dChess.getNullChess(String.valueOf(eatChess));
							eatChess++;
							nullChess.setLocation(dChess.getPos(x - 2), dChess.getPos(y - 2));
							nullChess.setVisible(true);
							nullChesses.add(nullChess);
						} else {
							JLabel nullChess = dChess.getNullChess(text);
							nullChess.setVisible(true);
							nullChesses.add(nullChess);
						}
					}
				}
			}
			if (x <= 6 && y <= 4) {
				// 右上
				if (y <= 2 && scene[y + 1][x + 1].contains(":")) {
					var text = scene[y + 2][x + 2];
					if (isShowNullChess(text)) {
						if (!text.contains(":")) {
							JLabel nullChess = dChess.getNullChess(String.valueOf(eatChess));
							eatChess++;
							nullChess.setLocation(dChess.getPos(x + 2), dChess.getPos(y + 2));
							nullChess.setVisible(true);
							nullChesses.add(nullChess);
						} else {
							JLabel nullChess = dChess.getNullChess(text);
							nullChess.setVisible(true);
							nullChesses.add(nullChess);
						}
					}
				}
				// 右下
				if (y >= 2 && scene[y - 1][x + 1].contains(":")) {
					var text = scene[y - 2][x + 2];
					if (isShowNullChess(text)) {
						if (!text.contains(":")) {
							JLabel nullChess = dChess.getNullChess(String.valueOf(eatChess));
							eatChess++;
							nullChess.setLocation(dChess.getPos(x + 2), dChess.getPos(y - 2));
							nullChess.setVisible(true);
							nullChesses.add(nullChess);
						} else {
							JLabel nullChess = dChess.getNullChess(text);
							nullChess.setVisible(true);
							nullChesses.add(nullChess);
						}
					}
				}
			}
		}
	}
	
	/**
	 * 仕的可走路线.<br>
	 * 仕走斜线一格走,只在将军格内.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param chess 仕棋子
	 * @param scene 场景
	 */
	private void selectShi(JLabel chess,String[][] scene) {
		int x = dChess.getPos(chess.getX());
		int y = dChess.getPos(chess.getY());
		
		// 左上,左下,右上,右下
		// 玩家只能在 [y|7 <= y <= 9],人机[y|0 <= y <= 2]
		if (redBlack == dChess.redBlack) {
			if (x > 3 && x <= 5) {
				// 左上
				if (y >= 8) {
					var text = scene[y - 1][x - 1];
					if (!text.contains(":")) {
						if (isShowNullChess(text)) {
							JLabel nullChess = dChess.getNullChess(String.valueOf(eatChess));
							eatChess++;
							nullChess.setLocation(dChess.getPos(x - 1), dChess.getPos(y - 1));
							nullChess.setVisible(true);
							nullChesses.add(nullChess);
						}
					} else {
						JLabel nullChess = dChess.getNullChess(text);
						nullChess.setVisible(true);
						nullChesses.add(nullChess);
					}
				}
				// 左下
				if (y <= 8) {
					var text = scene[y + 1][x - 1];
					if (!text.contains(":")) {
						if (isShowNullChess(text)) {
							JLabel nullChess = dChess.getNullChess(String.valueOf(eatChess));
							eatChess++;
							nullChess.setLocation(dChess.getPos(x - 1), dChess.getPos(y + 1));
							nullChess.setVisible(true);
							nullChesses.add(nullChess);
						}
					} else {
						JLabel nullChess = dChess.getNullChess(text);
						nullChess.setVisible(true);
						nullChesses.add(nullChess);
					}
				}
			}
			if (x >= 3 && x < 5) {
				// 右上
				if (y >= 8) {
					var text = scene[y - 1][x + 1];
					if (!text.contains(":")) {
						if (isShowNullChess(text)) {
							JLabel nullChess = dChess.getNullChess(String.valueOf(eatChess));
							eatChess++;
							nullChess.setLocation(dChess.getPos(x + 1), dChess.getPos(y - 1));
							nullChess.setVisible(true);
							nullChesses.add(nullChess);
						}
					} else {
						JLabel nullChess = dChess.getNullChess(text);
						nullChess.setVisible(true);
						nullChesses.add(nullChess);
					}
				}
				// 右下
				if (y <= 8) {
					var text = scene[y + 1][x + 1];
					if (!text.contains(":")) {
						if (isShowNullChess(text)) {
							JLabel nullChess = dChess.getNullChess(String.valueOf(eatChess));
							eatChess++;
							nullChess.setLocation(dChess.getPos(x + 1), dChess.getPos(y + 1));
							nullChess.setVisible(true);
							nullChesses.add(nullChess);
						}
					} else {
						JLabel nullChess = dChess.getNullChess(text);
						nullChess.setVisible(true);
						nullChesses.add(nullChess);
					}
				}
			}
		} else {
			if (x > 3 && x <= 5) {
				// 左上
				if (y <= 2) {
					var text = scene[y + 1][x - 1];
					if (!text.contains(":")) {
						if (isShowNullChess(text)) {
							JLabel nullChess = dChess.getNullChess(String.valueOf(eatChess));
							eatChess++;
							nullChess.setLocation(dChess.getPos(x - 1), dChess.getPos(y + 1));
							nullChess.setVisible(true);
							nullChesses.add(nullChess);
						}
					} else {
						JLabel nullChess = dChess.getNullChess(text);
						nullChess.setVisible(true);
						nullChesses.add(nullChess);
					}
				}
				// 左下
				if (y > 0) {
					var text = scene[y - 1][x - 1];
					if (!text.contains(":")) {
						if (isShowNullChess(text)) {
							JLabel nullChess = dChess.getNullChess(String.valueOf(eatChess));
							eatChess++;
							nullChess.setLocation(dChess.getPos(x - 1), dChess.getPos(y - 1));
							nullChess.setVisible(true);
							nullChesses.add(nullChess);
						}
					} else {
						JLabel nullChess = dChess.getNullChess(text);
						nullChess.setVisible(true);
						nullChesses.add(nullChess);
					}
				}
			}
			if (x >= 3 && x < 5) {
				// 右上
				if (y <= 2) {
					var text = scene[y + 1][x + 1];
					if (!text.contains(":")) {
						if (isShowNullChess(text)) {
							JLabel nullChess = dChess.getNullChess(String.valueOf(eatChess));
							eatChess++;
							nullChess.setLocation(dChess.getPos(x + 1), dChess.getPos(y + 1));
							nullChess.setVisible(true);
							nullChesses.add(nullChess);
						}
					} else {
						JLabel nullChess = dChess.getNullChess(text);
						nullChess.setVisible(true);
						nullChesses.add(nullChess);
					}
				}
				// 右下
				if (y > 0) {
					var text = scene[y - 1][x + 1];
					if (!text.contains(":")) {
						if (isShowNullChess(text)) {
							JLabel nullChess = dChess.getNullChess(String.valueOf(eatChess));
							eatChess++;
							nullChess.setLocation(dChess.getPos(x + 1), dChess.getPos(y - 1));
							nullChess.setVisible(true);
							nullChesses.add(nullChess);
						}
					} else {
						JLabel nullChess = dChess.getNullChess(text);
						nullChess.setVisible(true);
						nullChesses.add(nullChess);
					}
				}
			}
		}
	}
	
	
	/**
	 * 执行吃棋操作.<br>
	 * 只管吃棋,不分红黑,区分操作在onClick.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param upChess 上一次选择的棋子.
	 * @param chess 被吃棋子.
	 * @param scene 场景.
	 * @see #onClick(JLabel, DefaultChess, boolean)
	 */
	private void eat(JLabel upChess,JLabel chess,String[][] scene) {
		String upText = upChess.getText();
		
		// 当前棋子的位置和与目标位置的偏移.
		int upX = dChess.getPos(upChess.getX());
		int upY = dChess.getPos(upChess.getY());
		int x = dChess.getPos(chess.getX());
		int y = dChess.getPos(chess.getY());
		int xOffset = x - upX;
		int yOffset = y - upY;
		
		switch (upText) {
		// 车走直线,并且不能跨棋.
		case "红车":
		case "黑車":
			// 同一线上,xOffset = 0代表x是一样的,y不一样,反之.
			// 中间隔了棋子则不能吃,反之能吃
			if (xOffset == 0) {
				if (yOffset < 0) {
					for (int i = upY - 1;i > y;i--)
						if (!scene[i][x].contains(":")) return;
				} else {
					for (int i = upY + 1;i < y;i++)
						if (!scene[i][x].contains(":")) return;
				}
			} else if (yOffset == 0) {
				if (xOffset < 0) {
					for (int i = upX - 1;i > x;i--)
						if (!scene[y][i].contains(":")) return;
				} else {
					for (int i = upX + 1;i < x;i++)
						if (!scene[y][i].contains(":")) return;
				}
			} else return;
			break;
		// 只能隔子打棋
		case "红炮":
		case "黑炮":
		{
			// xOffset==0 则代表吃的是上/下的,反之
			int num = 0;
			if (xOffset == 0) {
				// 指定位置必须有一棋子,不会走空.
				if (yOffset < 0) {
					for (int i = y + 1;i < upY;i++) {
						if (!scene[i][x].contains(":")) num++;
					}
				} else {
					for (int i = upY + 1;i < y;i++) {
						if (!scene[i][x].contains(":")) num++;
					}
				}
			} else if (yOffset == 0) {
				if (xOffset < 0) {
					for (int i = x + 1;i < upX;i++) {
						if (!scene[y][i].contains(":")) num++;
					}
				} else {
					for (int i = upX + 1;i < x;i++) {
						if (!scene[y][i].contains(":")) num++;
					}
				}
			}
			if (num != 1) return;
		}
			break;
		// 将/帅 只走一格,碰面可直接吃
		case "红帅":
		case "黑将":
			// 左右吃,要在指定格子内只能在指定范围 [x|3 <= x <= 5]
			if (xOffset != 0 && yOffset != 0) return;
			if (yOffset == 0) {
				if (xOffset != -1 && xOffset != 1 || (x < 3 || x > 5)) return;
			} else {
				// 判断有无碰面
				boolean isEat = true;
				if (redBlack == dChess.redBlack) {
					if (yOffset < 0) {
						for (int i = upY - 1;i >= y;i--) {
							if (!scene[i][x].contains(":")) {
								var chessName = scene[i][x];
								if ("红帅".equals(chessName) || "黑将".equals(chessName)) {
									isEat = false;
								}
								break;
							}
						}
					}
					// 上下吃,在指定范围内[y|7 <= y <= 9]
					if (isEat)
						if (yOffset != -1 || yOffset != 1 && (y < 7 || y > 9)) return;
				} else {
					if (yOffset > 0) {
						for (int i = upY + 1;i <= y;i++) {
							if (!scene[i][x].contains(":")) {
								var chessName = scene[i][x];
								if ("红帅".equals(chessName) || "黑将".equals(chessName)) {
									isEat = false;
								}
								break;
							}
						}
					}
					// 上下吃,在指定范围内[y|0 <= y <= 2]
					if (isEat)
						if (yOffset != -1 || yOffset != 1 && (y < 0 || y > 2)) return;
				}
			}
			break;
		// 马只能跳日,被拦住不能吃.
		case "红马":
		case "黑馬":
			if (!((xOffset == 1 && yOffset == 2) || (xOffset == 1 && yOffset == -2)
					|| (xOffset == -1 && yOffset == 2) || (xOffset == -1 && yOffset == -2)
					|| (xOffset == 2  && yOffset == 1) || (xOffset == 2  && yOffset == -1)
					|| (xOffset == -2 && yOffset == 1) || (xOffset == -2 && yOffset == -1)
					|| (yOffset == 1  && xOffset == 2) || (yOffset == 1  && xOffset == -2)
					|| (yOffset == -1 && xOffset == 2) || (yOffset == -1 && xOffset == -2)
					|| (yOffset == 2  && xOffset == 1) || (yOffset == 2  && xOffset == -1)
					|| (yOffset == -2 && xOffset == 1) || (yOffset == -2 && xOffset == -1))) {
				return;
			}
			// 根据当前棋子位置计算,左边一格为-1,右边一格1,上边一格为-1,下边一格1.
			// x=-2	y=-1(左上)y=1(左下)
			if (xOffset == -2 && yOffset == -1 || (xOffset == -2 && yOffset == 1)) {
				if (!(upX >= 2 && scene[upY][upX - 1].contains(":"))) { return; }
			}
			// x=2 y=-1(右上)y=1(右下)
			if (xOffset == 2 && yOffset == -1 || (xOffset == 2 && yOffset == 1)) {
				if (!(upX <= 6 && scene[upY][upX + 1].contains(":"))) { return; }
			}
			// y=-2 x=-1(上左) x=1(上右)
			if (xOffset == -1 && yOffset == -2 || (xOffset == 1 && yOffset == -2)) {
				if (!(upY >= 2 && scene[upY - 1][upX].contains(":"))) { return; }
			}
			// y=2 x=-1(下左)x=1(下右)
			if (xOffset == -1 && yOffset == 2 || (xOffset == 1 && yOffset == 2)) {
				if (!(upY <= 7 && scene[upY + 1][upX].contains(":"))) { return; }
			}
			break;
		// 只能吃左右上一格的子,并且过河才可以吃左右的子
		case "红兵":
		case "黑卒":
			// 玩家方永远在下方
			if (redBlack == dChess.redBlack) {
				if (y > 4) { if (xOffset != 0) return; }
				
				if (xOffset != 0 && yOffset != 0) return;
				if (xOffset != 0) {
					if (xOffset != -1 && xOffset != 1) return;
				} else if (yOffset != 0) {
					if (yOffset != -1) return;
				}
			} else {
				if (y < 5) { if (xOffset != 0) return; }
				
				if (xOffset != 0 && yOffset != 0) return;
				if (xOffset != 0) {
					if (xOffset != -1 && xOffset != 1) return;
				} else if (yOffset != 0) {
					if (yOffset != 1) return;
				}
			}
			break;
		// 相只能吃河内的,并且田字格并且田字中间不能有棋
		case "红相":
		case "黑象":
			// 玩家方永远在下方
			if (redBlack == dChess.redBlack) {
				if (y < 5) return;
			} else {
				if (y > 4) return;
			}
			// 田字中间是否有棋
			// 左上左下右上右下
			if (xOffset == -2 && yOffset == -2) {
				if (!scene[upY - 1][upX - 1].contains(":")) return;
			} else if (xOffset == -2 && yOffset == 2) {
				if (!scene[upY + 1][upX - 1].contains(":")) return;
			} else if (xOffset == 2 && yOffset == -2) {
				if (!scene[upY - 1][upX + 1].contains(":")) return;
			} else if (xOffset == 2 && yOffset == 2) {
				if (!scene[upY + 1][upX + 1].contains(":")) return;
			} else {
				return;
			}
			break;
		// 仕吃斜线,不能出格.
		case "红仕":
		case "黑士":
			if (xOffset != -1 && xOffset != 1 || (yOffset != -1) && yOffset != 1) return;
			if (x < 3 || x > 5) return;
			if (redBlack == dChess.redBlack) {
				if (y < 7) return;
			} else {
				if (y > 2) return;
			}
			break;
		default: return;
		}
		
		chess.setVisible(false);
		JLabel nullChess = dChess.getNullChess(eatChessNum++);
		nullChess.setLocation(upChess.getX(), upChess.getY());
		
		dChess.select.setLocation(chess.getX(), chess.getY());
		dChess.select.setVisible(true);
		
		scene[upY][upX] = nullChess.getName();
		scene[y][x] = upChess.getName();
		
//		for (int i = 0;i < scene.length;i++) System.out.println(Arrays.toString(scene[i]));
	}
	
	@Override protected void chessMoveAnim(int targetX,int targetY) {
		int xOffset = targetX - upChess.getX() >> 2;
		int yOffset = targetY - upChess.getY() >> 2;
		for (int i = 0;i < 4;i++) {
			upChess.setLocation(upChess.getX() + xOffset, upChess.getY() + yOffset);
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		upChess.setLocation(targetX, targetY);
		upChess = null;
	}
	
	/**
	 * 是否需要显示可走路线.<br>
	 * 是自己队伍的则不显示 false,不是自己队伍的则显示 true.<br>
	 * 也用于判断是否是自己的队友,不是则true,是则false.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param chessText 象棋唯一名称.
	 * @return 需要显示返回 true,不需要则false.是自己队友则false,不是则true
	 */
	private boolean isShowNullChess(String chessText) {
		if (chessText.contains("红") && redBlack) return false;
		else if (chessText.contains("黑") && !redBlack) return false;
		else return true;
	}
	
}
