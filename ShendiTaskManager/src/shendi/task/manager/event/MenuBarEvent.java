package shendi.task.manager.event;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;

import shendi.task.manager.Application;

/**
 * 菜单栏事件,包含点击,拖动等.<br>
 * 窗口拖动效果,鼠标点击记录位置,拖动将位置设置<br>
 * <b>拖动的窗体位置 = 窗体当前位置 + 现在的鼠标位置 - 鼠标上次保存的位置</b>
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public class MenuBarEvent extends MouseAdapter implements MouseMotionListener {
	/**
	 * 需要被控制的窗体.
	 */
	private JFrame frame;
	
	/**
	 * 创建一个控制指定窗体的菜单栏事件.
	 * @param frame 窗体.
	 */
	public MenuBarEvent(JFrame frame) { this.frame = frame; }
	
	/**
	 * 鼠标拖动后移动窗体.
	 */
	@Override public void mouseDragged(MouseEvent mouse) {
		frame.setLocation(frame.getX() + mouse.getX() - Application.APP.xOffset, frame.getY() + mouse.getY() - Application.APP.yOffset);
	}
	
	/**
	 * 鼠标按下后记录当前鼠标位置
	 */
	@Override public void mousePressed(MouseEvent mouse) {
		Application.APP.xOffset = mouse.getX();
		Application.APP.yOffset = mouse.getY();
	}
	
	@Override public void mouseMoved(MouseEvent e) {}
}
