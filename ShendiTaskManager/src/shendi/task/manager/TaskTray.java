package shendi.task.manager;

import java.awt.PopupMenu;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import shendi.kit.time.TimeUtils.TimeDisposal;

/**
 * 应用程序的托盘图标, 参考 {@link Application#logo}.<br>
 * 会初始化相关事件等.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public class TaskTray extends TrayIcon {

	public TaskTray() {
		super(Application.APP.logo);
		setImageAutoSize(true);
		
		PopupMenu p = new PopupMenu();
		// 在 Eclipse 中运行会乱码,因为操作系统使用的GBK编码.
		// 可通过设置运行参数解决 -Dfile.encoding=GBK
		// 但是设置运行参数后 Eclipse 控制台乱码,因为是UTF-8编码.
		p.add("退出");
		
		p.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				String name = e.getActionCommand();
				switch (name) {
				case "退出": Application.FRAME.dispose(); break;
				
				}
			}
		});
		
		setPopupMenu(p);
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (1 == e.getButton()) { Application.FRAME.setVisible(true); }
			}
		});
		addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Application.FRAME.setVisible(true);
			}
		});
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				setToolTip(Application.APP.TITLE + "\r\n今天倒计时: " + TimeDisposal.nowToTomorrow() / 1000);
			}
		});
	}
	
}
