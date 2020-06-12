package shendi.task.manager.view;

import java.awt.Color;
import java.awt.FlowLayout;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;

import shendi.kit.log.Log;
import shendi.task.manager.effect.Slide;

/**
 * 关于我们的窗口.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public class AboutUSView extends JPanel {
	private static final long serialVersionUID = 1001;
	
	public static AboutUSView aboutUs = new AboutUSView();
	
	/**
	 * Create the panel.
	 */
	private AboutUSView() {
		setLayout(null);
		
		JTextPane info = new JTextPane();
		info.setEditable(false);
		info.setBackground(Color.LIGHT_GRAY);
		info.setText("关于我...没啥好说的,我是个人开发者.上个联系方式吧\r\nnickname: Shendi\r\n百度搜索HackShendi\r\nQQ: 1711680493\r\n上方按钮展示我写的滑动效果.");
		
		info.setBounds(10, 206, 597, 153);
		add(info);
		
		JPanel effectPanel = new JPanel();
		effectPanel.setBounds(10, 175, 597, 33);
		add(effectPanel);
		
		JToggleButton infoBtnUp = new JToggleButton("隐藏(向上)");
		infoBtnUp.addActionListener((e) -> {
			if (infoBtnUp.isSelected()) {
				infoBtnUp.setText("隐藏(向上)");
				Slide.slideUp(info,true);
			} else {
				infoBtnUp.setText("显示(向上)");
				Slide.slideUp(info,false);
			}
		});
		infoBtnUp.setSelected(true);
		
		JToggleButton infoBtnDown = new JToggleButton("隐藏(向下)");
		infoBtnDown.addActionListener((e) -> {
			if (infoBtnDown.isSelected()) {
				infoBtnDown.setText("隐藏(向下)");
				Slide.slideDown(info,true);
			} else {
				infoBtnDown.setText("显示(向下)");
				Slide.slideDown(info,false);
			}
		});
		infoBtnDown.setSelected(true);
		
		JToggleButton infoBtnLeft = new JToggleButton("隐藏(向左)");
		infoBtnLeft.addActionListener((e) -> {
			if (infoBtnLeft.isSelected()) {
				infoBtnLeft.setText("隐藏(向左)");
				Slide.slideLeft(info,true);
			} else {
				infoBtnLeft.setText("显示(向左)");
				Slide.slideLeft(info,false);
			}
		});
		infoBtnLeft.setSelected(true);
		
		JToggleButton infoBtnRight = new JToggleButton("隐藏(向右)");
		infoBtnRight.addActionListener((e) -> {
			if (infoBtnRight.isSelected()) {
				infoBtnRight.setText("隐藏(向右)");
				Slide.slideRight(info,true);
			} else {
				infoBtnRight.setText("显示(向右)");
				Slide.slideRight(info,false);
			}
		});
		infoBtnRight.setSelected(true);
		effectPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		effectPanel.add(infoBtnUp);
		effectPanel.add(infoBtnDown);
		effectPanel.add(infoBtnLeft);
		effectPanel.add(infoBtnRight);
		
		JLabel logo = null;
		try {
			logo = new JLabel(new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/shendi_logo.png"))));
		} catch (IOException e) {
			Log.printErr("关于我们界面Logo初始化失败: " + e.getMessage());
		}
		logo.setOpaque(true);
		logo.setBackground(Color.BLACK);
		logo.setBounds(10, 0, 597, 167);
		add(logo);
	}
}
