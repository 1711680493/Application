package shendi.snake;

import java.awt.Graphics;

/**
 * 绘制蛇的接口
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public interface Draw {
	/**
	 * 绘制
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param g
	 * @param obj 用到的参数
	 */
	void draw(Graphics g,Object... obj);
}
