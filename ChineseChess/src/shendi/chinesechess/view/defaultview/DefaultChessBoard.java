package shendi.chinesechess.view.defaultview;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import shendi.chinesechess.view.ChessBoard;

/**
 * 默认棋盘.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public class DefaultChessBoard extends ChessBoard {
	private static final long serialVersionUID = -2603365394736874995L;

	@Override
	protected void onCreate() {
		setLayout(null);
		// 宽度为350,高度为380,竖排9,横排10.
		BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics g = img.getGraphics();
		g.setColor(Color.BLACK);
		// 绘制横线,十条,
		for (int i = 1;i <= 10;i++) {
			g.drawLine(35, 35*i, 315, 35*i);
		}
		// 绘制竖线,九条,中间一个为字 "楚河汉界"
		for (int i = 1;i <= 9;i++) {
			g.drawLine(35*i, 35, 35*i, 175);
			g.drawLine(35*i, 210, 35*i, 350);
		}
		g.drawLine(35, 175, 35, 210);
		g.setFont(new Font("仿宋", Font.BOLD, 24));
		g.drawString("楚 河", 75, 205);
		g.drawString("汉 界", 215, 205);
		g.drawLine(315, 175, 315, 210);
		// 绘制交叉线
		g.drawLine(140, 35, 210, 105); g.drawLine(210, 35, 140, 105);
		g.drawLine(140, 280, 210, 350); g.drawLine(140, 350, 210, 280);
		
		JLabel background = new JLabel();
		background.setIcon(new ImageIcon(img));
		background.setBounds(0, 0, getWidth(), getHeight());
		add(background,-1);
	}

}
