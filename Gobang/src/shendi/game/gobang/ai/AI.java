package shendi.game.gobang.ai;

import java.util.Random;

import shendi.game.gobang.disposal.Game;

/**
 * 人机类,单例模式,随机下棋.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public class AI {
	
	private AI() {}
	private static final AI AI = new AI();
	
	/**
	 * 队伍
	 */
	private boolean whiteBlack;
	
	/**
	 * 随机器
	 */
	private Random random = new Random();
	
	/**
	 * 用于初始化,比如分配队伍.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 */
	public void init(boolean whiteBlack) {
		this.whiteBlack = whiteBlack;
	}
	
	/**
	 * AI下棋.
	 * @author Shined <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 */
	public void play() {
		int[][] scene = Game.getGame().getScene();
		// 判断数组还有无空间
		for (int i = 0;i < scene.length;i++) {
			int[] xScene = scene[i];
			for (int j = 0;j < xScene.length;j++) {
				if (scene[i][j] == 0) {
					while (true) {
						int x = random.nextInt(scene.length);
						int y = random.nextInt(scene.length);
						int num = scene[y][x];
						if (num == 0) {
							Game.getGame().exec(Game.getGame().getChesses().get(y * scene.length + x), whiteBlack);
							return;
						}
					}
				}
			}
		}
		Game.getGame().stop(whiteBlack);
	}

	/**
	 * 获取AI对象.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @return {@link AI}
	 */
	public static AI getAI() { return AI; }
	
}
