package shendi.task.manager.view;

import java.awt.AWTException;
import java.awt.SystemTray;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import shendi.kit.config.ConfigurationFactory;
import shendi.kit.log.Log;
import shendi.kit.path.ProjectPath;
import shendi.task.manager.Application;
import shendi.task.manager.event.MenuBarEvent;

/**
 * 第一个窗体,主窗体.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public class MainView extends JFrame {
	private static final long serialVersionUID = 1000L;

	/**
	 * 中间的面板.
	 */
	private final JPanel centerPanel;
	
	public MainView() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				Application.APP.close();
				System.exit(0);
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				if ("1".equals(ConfigurationFactory.getConfig("config").getProperty("app.close.showTray"))) {
					dispose();
				} else {
					setVisible(false);
					Application.TRAY.displayMessage("加油！", "多看书,多学习,少吹水\r\n有志者,事竟成.", MessageType.INFO);
				}
			}
			
		});
		setResizable(false);
		setBounds(100, 100, 650, 450);
		setTitle(Application.APP.TITLE);
		setIconImage(Application.APP.logo);
		
		// 欢迎界面
		showWelcome();
		
		// 拖动菜单栏事件,鼠标点击记录位置
		MenuBarEvent menuBarEvent = new MenuBarEvent(this);
		JMenuBar menuBar = new JMenuBar();
		menuBar.addMouseListener(menuBarEvent);
		menuBar.addMouseMotionListener(menuBarEvent);
		
		JMenu windowMenu = new JMenu("窗口(W)");
		windowMenu.setMnemonic(KeyEvent.VK_W);
		windowMenu.setDisplayedMnemonicIndex(3);
		menuBar.add(windowMenu);
		
		JMenuItem showMain = new JMenuItem("主界面");
		showMain.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switchPage(new MainPanel());
			}
		});
		windowMenu.add(showMain);
		
		JMenu settingMenu = new JMenu("设置(S)");
		settingMenu.setMnemonic(KeyEvent.VK_S);
		settingMenu.setDisplayedMnemonicIndex(3);
		menuBar.add(settingMenu);
		
		JMenuItem exit = new JMenuItem("退出程序");
		exit.addActionListener((e) -> dispose());
		
		JCheckBoxMenuItem trayNonsupportShow = new JCheckBoxMenuItem("提示不支持系统托盘");
		trayNonsupportShow.setSelected("0".equals(ConfigurationFactory.getConfig("config").getProperty("app.tray.nonsupportShow")));

		trayNonsupportShow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (trayNonsupportShow.isSelected()) {
					ConfigurationFactory.getConfig("config").setProperty("app.tray.nonsupportShow","0");
				} else {
					ConfigurationFactory.getConfig("config").setProperty("app.tray.nonsupportShow","1");
				}
				try {
					ConfigurationFactory.getConfig("config").store(new FileWriter(new ProjectPath().getPath(ConfigurationFactory.getConfig("main").getProperty("config"))), "Update nonsupportShow");
				} catch (IOException e1) {
					Log.printErr("修改不支持系统托盘弹出提示失败 :" + e1.getMessage());
				}
			}
		});

		settingMenu.add(trayNonsupportShow);
		
		JCheckBoxMenuItem closeShowTray = new JCheckBoxMenuItem("窗口关闭最小化到系统托盘");
		closeShowTray.setSelected("0".equals(ConfigurationFactory.getConfig("config").getProperty("app.close.showTray")));
		closeShowTray.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (closeShowTray.isSelected()) {
					ConfigurationFactory.getConfig("config").setProperty("app.close.showTray","0");
				} else {
					ConfigurationFactory.getConfig("config").setProperty("app.close.showTray","1");
				}
				try {
					ConfigurationFactory.getConfig("config").store(new FileWriter(new ProjectPath().getPath(ConfigurationFactory.getConfig("main").getProperty("config"))), "Update showTray");
				} catch (IOException e1) {
					Log.printErr("修改最小化到系统托盘按钮失败 :" + e1.getMessage());
				}
			}
		});
		
		settingMenu.add(closeShowTray);
		
		JCheckBoxMenuItem netSave = new JCheckBoxMenuItem("云端保存(待开发)");
		settingMenu.add(netSave);
		settingMenu.add(exit);
		
		JMenu helpMenu = new JMenu("帮助(H)");
		helpMenu.setMnemonic(KeyEvent.VK_H);
		helpMenu.setDisplayedMnemonicIndex(3);
		menuBar.add(helpMenu);
		
		JMenuItem welcome = new JMenuItem("欢迎");
		welcome.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new WelcomeView().setVisible(true);
			}
		});
		helpMenu.add(welcome);
		
		JMenuItem aboutUS = new JMenuItem("关于我们");
		aboutUS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switchPage(AboutUSView.aboutUs);
			}
		});
		helpMenu.add(aboutUS);
		
		setJMenuBar(menuBar);
		
		centerPanel = new JPanel();
		centerPanel.setLayout(null);
		getContentPane().add(centerPanel);
		
		// 实例化组件大小,避免中心面板大小为 0.
		setVisible(true);
		
		MainPanel main = new MainPanel();
		main.setSize(centerPanel.getWidth(), centerPanel.getHeight());
		centerPanel.add(main);
		
		// 显示托盘
		try {
			if (SystemTray.isSupported()) {
				SystemTray.getSystemTray().add(Application.TRAY);
			} else {
				Log.printErr("操作系统不支持托盘,创建托盘失败.");
				// 提示对话框
				if (!"1".equals(ConfigurationFactory.getConfig("config").getProperty("app.tray.nonsupportShow")))
					JOptionPane.showMessageDialog(null, "您的操作系统不支持托盘,可在设置内设置后续不在出现此类对话框","托盘不受系统支持",JOptionPane.WARNING_MESSAGE);
			}
		} catch (AWTException e) { Log.printErr("添加托盘到系统托盘失败: " + e.getMessage()); }
	}
	
	/**
	 * 显示欢迎界面,开始时执行.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 */
	private void showWelcome() {
		String nextWelcome = ConfigurationFactory.getConfig("config").getProperty("next_welcome");
		if (!"1".equals(nextWelcome)) {
			new WelcomeView().setVisible(true);
		}
	}
	
	/**
	 * 切换中心视图.<br>
	 * 所有视图都得为绝对布局.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param panel 视图
	 */
	private void switchPage(JPanel panel) {
		centerPanel.removeAll();
		panel.setSize(centerPanel.getWidth(), centerPanel.getHeight());
		centerPanel.add(panel);
		centerPanel.validate();
		centerPanel.repaint();
	}
}
