package shendi.chinesechess.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * 象棋工厂,用于方便的生成指定棋子.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public class ChessFactory {
	
	/**
	 * 绘制默认的棋子.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param redOrBlack true为红棋,false为黑棋.
	 * @param name 什么棋子.
	 * @param listenerName 事件内的名称.
	 * @param chessName 棋子的唯一名称.
	 * @param width 棋子宽
	 * @param height 棋子高
	 * @return 绘制好的棋子.
	 */
	public static JLabel createDefaultChess(boolean redOrBlack,String name,String listenerName,String chessName,int width,int height) {
		JLabel chess = new JLabel(listenerName);
		chess.setName(chessName);
		
		BufferedImage img = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
		
		Graphics g = img.getGraphics();
		// 绘制棋子,通过某种计算自适应
		g.setColor(Color.BLACK);
		g.fillRoundRect(0, 0, width, height, 180, 180);
		g.setColor(Color.YELLOW);
		int backgroundX = (int)(width - width * 0.9) / 2;
		int backgroundWidth = (int)(width * 0.9);
		int backgroundHeight = (int)(height * 0.8);
		g.fillRoundRect(backgroundX, 0, backgroundWidth, backgroundHeight, 180, 180);
		if (redOrBlack) {
			g.setColor(Color.RED);
		} else {
			g.setColor(Color.BLACK);
		}
		g.drawRoundRect(backgroundX << 1, backgroundX, backgroundHeight, backgroundHeight - (backgroundX << 1), 180, 180);
		g.setFont(new Font("黑体", Font.BOLD, (width + height) >> 2));
		g.drawString(name, width >> 2, (int)(height * 0.6));
		g.dispose();
		
		chess.setIcon(new ImageIcon(img));
		chess.setSize(width, height);
		return chess;
	}
	
	/**
	 * 测试 棋子.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param args null
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame("Test");
		JLabel chess = new JLabel();
		chess.setBounds(0, 0, 200, 200);
		int width = 100;
		int height = 100;
		BufferedImage img = new BufferedImage(200,200,BufferedImage.TYPE_INT_ARGB);
		Graphics g = img.getGraphics();
		g.setColor(Color.BLACK);
		g.fillRoundRect(0, 0, width, height, 180, 180);
		g.setColor(Color.YELLOW);
		int backgroundX = (int)(width - width * 0.9) / 2;
		int backgroundWidth = (int)(width * 0.9);
		int backgroundHeight = (int)(height * 0.8);
		g.fillRoundRect(backgroundX, 0, backgroundWidth, backgroundHeight, 180, 180);
		g.setColor(Color.RED);
		g.drawRoundRect(backgroundX << 1, backgroundX, backgroundHeight, backgroundHeight - (backgroundX << 1), 180, 180);
		g.setFont(new Font("仿宋", Font.BOLD, (width + height) >> 2));
		g.drawString("车", width >> 2, (int)(height * 0.6));
		g.dispose();
		chess.setIcon(new ImageIcon(img));
		
		BufferedImage img2 = new BufferedImage(200,200,BufferedImage.TYPE_INT_ARGB);
		Graphics g2 = img2.getGraphics();
		g2.setColor(Color.RED);
		g2.fillRoundRect(0, 0, 200, 200, 180, 180);
		g2.drawString("車", 0, 0);
		
		chess.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				chess.setIcon(new ImageIcon(img2));
			}
			
		});
		
		frame.setLayout(null);
		frame.add(chess);
		frame.setVisible(true);
		frame.setSize(300, 300);
		frame.setDefaultCloseOperation(3);
	}
	
}
