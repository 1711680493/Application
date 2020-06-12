package shendi.task.manager.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

import javax.swing.JOptionPane;

import shendi.kit.log.Log;

/**
 * 用于统计任务完成度等.
 * @author <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>Shendi</a>
 */
public class Statistics {

	/**
	 * 总数
	 */
	public int num;
	
	/**
	 * 完成数
	 */
	public int finishNum;

	private Statistics() {}
	
	/**
	 * 执行处理的线程数.
	 */
	private static int taskThreadNum;
	
	/**
	 * 获取统计.
	 * @return {@link Statistics}
	 */
	public static Statistics getStatistics() {
		File taskDir = new File(TaskFile.PATH);
		
		Statistics stts = new Statistics();
		
		if (taskDir.exists()) {
			File[] taskFiles = taskDir.listFiles();
			
			taskThreadNum = taskFiles.length;
			
			for (File taskFile : taskFiles) {
				new Thread(() -> {
					int fileNum = 0;
					int fileFinishNum = 0;
					
					try (BufferedReader reader = new BufferedReader(new FileReader(taskFile))) {
						String str;
						while ((str = reader.readLine()) != null) {
							fileNum++;
							if (Objects.equals("完成", str.split("[|]")[1].substring(1))) {
								fileFinishNum++;
							}
						}
					} catch (IOException io) {
						var message = io.getMessage();
						Log.printErr("统计出错: " + message);
						JOptionPane.showMessageDialog(null, "统计出错: " + message,"错误",JOptionPane.ERROR_MESSAGE);
					}
					
					synchronized (stts) {
						stts.num += fileNum;
						stts.finishNum += fileFinishNum;
						if (--taskThreadNum < 1) {
							stts.notify();
						}
					}
				}).start();
			}
			
			synchronized (stts) {
				try { stts.wait(); } catch (InterruptedException e) {
					var message = e.getMessage();
					Log.printErr("统计出错: " + message);
					JOptionPane.showMessageDialog(null, "统计出错: " + message,"错误",JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		return stts;
	}
	
}
