package shendi.task.manager;

import java.awt.EventQueue;

import javax.swing.UIManager;

import shendi.kit.log.Log;

/**
 * 开启整个程序.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public class Start {
	
	public static void main(String[] args) throws ClassNotFoundException {
		// 日志不在控制台显示
		new Thread(() -> Log.setIsLog(false)).start();
		
		// 设置布局管理器
		try { UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel"); } catch (Exception e) { e.printStackTrace(); }
		
		// 开启主窗体
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Application.FRAME.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
}
