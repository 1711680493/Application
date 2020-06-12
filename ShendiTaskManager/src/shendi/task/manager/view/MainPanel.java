package shendi.task.manager.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import shendi.kit.log.Log;
import shendi.kit.time.TimeUtils;
import shendi.kit.time.TimeUtils.TimeDisposal;
import shendi.task.manager.data.Statistics;
import shendi.task.manager.data.TaskFile;

public class MainPanel extends JPanel {
	private static final long serialVersionUID = 1002L;

	/**
	 * Create the panel.
	 */
	public MainPanel() {
		setLayout(new BorderLayout(0, 0));
		
		JButton upDate = new JButton("←");
		upDate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TaskFile.queryTaskUpDay();
			}
		});
		add(upDate, BorderLayout.WEST);
		
		JButton downDate = new JButton("→");
		downDate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TaskFile.queryTaskDownDay();
			}
		});
		add(downDate, BorderLayout.EAST);
		
		JPanel center = new JPanel();
		add(center, BorderLayout.CENTER);
		center.setLayout(new BorderLayout(0, 0));
		
		JLabel time = new JLabel("今天还剩下 0 秒");
		center.add(time, BorderLayout.NORTH);
		time.setHorizontalAlignment(SwingConstants.CENTER);
		
		JScrollPane content = new JScrollPane();
		center.add(content, BorderLayout.CENTER);
		
		JPanel tasks = new JPanel();
		content.setViewportView(tasks);
		tasks.setLayout(new BorderLayout(0, 0));
		
		JLabel taskDate = new JLabel(TimeUtils.getTime().getFormatTime(TimeUtils.getTime().DATE).getString(System.currentTimeMillis()) + " 任务：");
		content.setColumnHeaderView(taskDate);
		
		JPanel taskContent = new JPanel();
		tasks.add(taskContent);
		taskContent.setLayout(new BoxLayout(taskContent, BoxLayout.Y_AXIS));
		// 初始化当前的任务
		TaskFile.init(taskContent,taskDate);
		
		JPanel taskBtnGroup = new JPanel();
		tasks.add(taskBtnGroup, BorderLayout.NORTH);
		taskBtnGroup.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JButton addTaskBtn = new JButton("添加");
		// 添加任务.
		addTaskBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String input = JOptionPane.showInputDialog("任务描述：");
				if (input != null && !input.isEmpty()) TaskFile.addTask(input);
			}
		});
		
		JButton today = new JButton("今天");
		today.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TaskFile.queryTaskToday();
			}
		});
		taskBtnGroup.add(today);
		
		taskBtnGroup.add(addTaskBtn);
		
		JButton delTaskBtn = new JButton("删除");
		delTaskBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TaskFile.delTask();
			}
		});
		taskBtnGroup.add(delTaskBtn);
		
		JButton finishTaskBtn = new JButton("完成所选任务");
		finishTaskBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TaskFile.finishTask();
			}
		});
		taskBtnGroup.add(finishTaskBtn);
		
		JScrollPane statistics = new JScrollPane();
		add(statistics, BorderLayout.NORTH);
		statistics.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		
		JPanel statisticsContent = new JPanel();
		statistics.setViewportView(statisticsContent);
		
		
		JLabel all = new JLabel();
		statisticsContent.add(all);
		
		new Thread(() -> {
			// 获取统计数.
			Statistics stts = Statistics.getStatistics();
			double finishNum = stts.finishNum;
			all.setText("总完成率：" + (int)(finishNum / stts.num * 100) + "%");
		}).start();
		
		JLabel year = new JLabel();
		statisticsContent.add(year);
		
		JLabel month = new JLabel();
		statisticsContent.add(month);
		
		JLabel week = new JLabel();
		statisticsContent.add(week);
		
		JButton more = new JButton("获取更多");
		statisticsContent.add(more);
		
		// 倒计时
		new Thread(() -> {
			while (true) {
				time.setText("今天还剩下 " + TimeDisposal.nowToTomorrow() / 1000 + " 秒 ");
				try { Thread.sleep(1000); } catch (InterruptedException e) { Log.printErr("主界面倒计时睡眠失败: " + e.getMessage()); }
				if (getParent() == null) break;
			}
		}).start();
		
	}
}
