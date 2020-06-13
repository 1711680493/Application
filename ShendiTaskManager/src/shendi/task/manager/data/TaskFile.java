package shendi.task.manager.data;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import shendi.kit.log.Log;
import shendi.kit.path.ProjectPath;
import shendi.kit.time.TimeUtils;

/**
 * 持久化任务,直接操作任务数据.<br>
 * 提供对任务路径定义和操作任务文件.
 * @author <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>Shendi</a>
 */
public class TaskFile {
	
	private TaskFile() {}
	
	static {
		// 初始化目录
		String path= new ProjectPath().getPath("").concat("/task/");
		File file = new File(path);
		if (!file.exists())	file.mkdir();
		PATH = path;
	}
	
	/**
	 * 任务信息的面板
	 */
	private static JPanel taskContent;
	
	/**
	 * 显示日期
	 */
	private static JLabel timeLabel;
	
	/**
	 * 任务文件保存路径.
	 */
	public static final String PATH;
	
	/**
	 * 当前查询的天数.
	 */
	private static String time;
	
	/**
	 * 添加任务,数据持久化到硬盘.
	 * @param task 任务
	 */
	public static void addTask(String task) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(PATH + TimeUtils.getTime().getFormatTime(TimeUtils.getTime().DATE).getString(System.currentTimeMillis()),true))) {
			writer.append(task);
			writer.append("[|]待完成\r\n");
			// 刷新内容
			new Thread(() -> {
				queryTaskToday();
			}).start();
		} catch (IOException io) {
			String message = io.getMessage();
			Log.printErr("添加任务失败: " + message);
			JOptionPane.showMessageDialog(null, "添加任务失败,保存出错: " + message,"错误",JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * 初始化面板,在使用其余方法之前必须已经初始化了.
	 * @param taskContent 包含任务信息的面板.
	 */
	public static void init(JPanel taskContent, JLabel timeLabel) {
		TaskFile.taskContent = taskContent;
		TaskFile.timeLabel = timeLabel;
		queryTaskToday();
	}
	
	/**
	 * 查询今天的任务.
	 */
	public static void queryTaskToday() {
		time = TimeUtils.getTime().getFormatTime(TimeUtils.getTime().DATE).getString(System.currentTimeMillis());
		queryTaskByDay(time);
	}
	
	/**
	 * 查询上一天的任务,根据当前查询的天数.
	 */
	@SuppressWarnings("deprecation") public static void queryTaskUpDay() {
		Date date = TimeUtils.getTime().getFormatTime(TimeUtils.getTime().DATE).getDate(time);
		date.setDate(date.getDate() - 1);
		time = TimeUtils.getTime().getFormatTime(TimeUtils.getTime().DATE).getString(date);
		queryTaskByDay(time);
	}
	
	/**
	 * 查询下一天的任务,根据当前查询的天数.
	 */
	@SuppressWarnings("deprecation") public static void queryTaskDownDay() {
		Date date = TimeUtils.getTime().getFormatTime(TimeUtils.getTime().DATE).getDate(time);
		date.setDate(date.getDate() + 1);
		time = TimeUtils.getTime().getFormatTime(TimeUtils.getTime().DATE).getString(date);
		queryTaskByDay(time);
	}
	
	/**
	 * 查询指定天数的任务.
	 * @param date 哪一天
	 */
	private static void queryTaskByDay(String date) {
		File file = new File(PATH + date);
		
		// 设置显示的日期
		timeLabel.setText(date + " 的任务：");
		
		// 清空之前的任务容器
		taskContent.removeAll();
		
		if (file.exists()) {
			// 如果不是今天,则把待完成改成未完成
			boolean isNoToday = !TimeUtils.getTime().getFormatTime(TimeUtils.getTime().DATE).getString(System.currentTimeMillis()).equals(date);
			StringBuilder tempTasks = null;
			
			// 获取指定天数所有任务,放入容器内.
			try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
				
				String str;
				
				if (isNoToday) {
					tempTasks = new StringBuilder();
				}
				StringBuilder tasks = tempTasks;
				
				while ((str = reader.readLine()) != null) {
					var task = str.split("[|]");
					
					JPanel panel = new JPanel();
					panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
					
					JCheckBox checkBox = new JCheckBox(task[0].substring(0,task[0].length() - 1));
					panel.add(checkBox);
					
					String state = task[1].substring(1);
					if (isNoToday) {
						if (state.equals("待完成")) state = "未完成";
						tasks.append(task[0]);
						tasks.append("|]");
						tasks.append(state);
						tasks.append("\r\n");
					}
					JLabel label = new JLabel(state);
					panel.add(label);
					TaskStatColor.stateColor(label);
					
					taskContent.add(panel);
				}
				
				// 不是今天,将文件内的数据,待完成改为未完成.
				if (isNoToday) {
					new Thread(() -> {
						if (tasks != null) {
							try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
								writer.write(tasks.toString());
							} catch (IOException io) {
								Log.printErr("查询任务失败: " + io.getMessage());
								JOptionPane.showMessageDialog(null, "查询任务失败: " + io.getMessage());
							}
						}
					}).start();
				}
			} catch (IOException io) {
				Log.printErr("查询任务失败: " + io.getMessage());
				JOptionPane.showMessageDialog(null, "查询任务失败: " + io.getMessage());
			}
			
		}
		
		// 刷新任务容器
		taskContent.validate();
		taskContent.repaint();
	}
	
	/**
	 * 删除选中的任务.
	 */
	public static void delTask() {
		Component[] compt = taskContent.getComponents();
		ArrayList<Integer> list = new ArrayList<>(compt.length);
		// 获取选中的复选框,下标存入list,然后将文件里对应的数据删除.
		for (var i = 0;i < compt.length;i++) {
			JPanel p = (JPanel) compt[i];
			JCheckBox check = (JCheckBox) p.getComponents()[0];
			if (check.isSelected()) list.add(i);
		}
		
		File file = new File(PATH + time);
		StringBuilder tasks = new StringBuilder();
		var i = 0;
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String str;
			while ((str = reader.readLine()) != null) {
				if (!list.contains(i)) {
					tasks.append(str);
					tasks.append("\r\n");
				}
				i++;
			}
		} catch (IOException io) {
			Log.printErr("删除任务失败: " + io.getMessage());
			JOptionPane.showMessageDialog(null, "删除任务失败: " + io.getMessage());
			i = -1;
		}
		
		if (i != -1) {
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
				writer.write(tasks.toString());
			} catch (IOException io) {
				Log.printErr("删除任务失败: " + io.getMessage());
				JOptionPane.showMessageDialog(null, "删除任务失败: " + io.getMessage());
			}
		}
		
		// 查询任务.
		queryTaskByDay(time);
	}
	
	/**
	 * 完成选中的任务.
	 */
	public static void finishTask() {
		Component[] compt = taskContent.getComponents();
		ArrayList<Integer> list = new ArrayList<>(compt.length);
		// 获取选中的复选框,下标存入list,然后将文件里对应的数据修改.
		for (var i = 0;i < compt.length;i++) {
			JPanel p = (JPanel) compt[i];
			JCheckBox check = (JCheckBox) p.getComponents()[0];
			if (check.isSelected()) list.add(i);
		}
		
		File file = new File(PATH + time);
		StringBuilder tasks = new StringBuilder();
		var i = 0;
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String str;
			while ((str = reader.readLine()) != null) {
				if (list.contains(i)) {
					tasks.append(str.split("[|]")[0]);
					tasks.append("|]完成");
				} else {
					tasks.append(str);
				}
				tasks.append("\r\n");
				i++;
			}
		} catch (IOException io) {
			Log.printErr("完成任务失败: " + io.getMessage());
			JOptionPane.showMessageDialog(null, "完成任务失败: " + io.getMessage());
			i = -1;
		}
		
		if (i != -1) {
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
				writer.write(tasks.toString());
			} catch (IOException io) {
				Log.printErr("完成任务失败: " + io.getMessage());
				JOptionPane.showMessageDialog(null, "完成任务失败: " + io.getMessage());
			}
		}
		
		// 查询任务.
		queryTaskByDay(time);
	}
	
}
