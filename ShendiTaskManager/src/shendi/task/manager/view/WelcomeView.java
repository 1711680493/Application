package shendi.task.manager.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;

import shendi.kit.config.ConfigurationFactory;
import shendi.kit.path.ProjectPath;
import shendi.task.manager.Application;

/**
 * 欢迎界面,对话框.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public class WelcomeView extends JDialog {
	private static final long serialVersionUID = 999L;

	/**
	 * Create the dialog.
	 */
	public WelcomeView() {
		setTitle("欢迎");
		setIconImage(Application.APP.logo);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setModal(true);
		setAlwaysOnTop(true);
		getContentPane().setLayout(new BorderLayout());
		
		JCheckBox nextWelcome = new JCheckBox("下次不再显示");
		nextWelcome.setForeground(Color.WHITE);
		nextWelcome.setBackground(new Color(0, 0, 0));
		nextWelcome.setSelected("1".equals(ConfigurationFactory.getConfig("config").getProperty("next_welcome")) ? true : false);
		nextWelcome.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (nextWelcome.isSelected())
					ConfigurationFactory.getConfig("config").setProperty("next_welcome", "1");
				else
					ConfigurationFactory.getConfig("config").setProperty("next_welcome", "0");
				
				try {
					ConfigurationFactory.getConfig("config").store(new FileWriter(new ProjectPath().getPath(ConfigurationFactory.getConfig("main").getProperty("config"))), "set welcome");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		getContentPane().add(nextWelcome, BorderLayout.SOUTH);
		
		JPanel topPanel = new JPanel();
		topPanel.setBackground(new Color(0, 0, 0));
		getContentPane().add(topPanel, BorderLayout.NORTH);
		
		JButton addQQBtn = new JButton("QQ 联系");
		topPanel.add(addQQBtn);
		
		JButton website = new JButton("网站");
		website.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().browse(new URI("www.shendi.xyz"));
				} catch (IOException | URISyntaxException e1) {
					e1.printStackTrace();
				}
			}
		});
		topPanel.add(website);
		
		JTextArea info = new JTextArea();
		info.setLineWrap(true);
		info.setBorder(new LineBorder(Color.WHITE));
		info.setForeground(Color.WHITE);
		info.setBackground(Color.DARK_GRAY);
		info.setEditable(false);
		StringBuilder infoTxt = new StringBuilder("本软件创建初衷是为了更方便的管理自己的任务,更直观的看到自己的进度.本人之前一直用文本写下每天任务,然后发现如果长久下去,不能直观的看到统计数,完成率,等,所以此软件就这样诞生了.\r\n版本:");
		infoTxt.append(Application.APP.VERSION);
		infoTxt.append("\r\n可以增加,删除,修改,查询任务,并且统计总任务数与完成率和天数,允许创建每周,每月,每年的长期任务.以及自己设置时间的任务.\r\n后面的版本会允许进行云端保存");
		info.setText(infoTxt.toString());
		getContentPane().add(info, BorderLayout.CENTER);
		addQQBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().browse(new URI("tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493"));
				} catch (IOException | URISyntaxException e1) {
					e1.printStackTrace();
				}
			}
		});
	}

}
