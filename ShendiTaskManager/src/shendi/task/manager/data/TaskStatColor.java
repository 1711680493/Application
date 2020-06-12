package shendi.task.manager.data;

import java.awt.Color;
import java.util.HashMap;

import javax.swing.JLabel;

/**
 * 用于设置指定任务状态的颜色.
 * @author <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>Shendi</a>
 */
public class TaskStatColor {
	private static HashMap<String,Color> statColor = new HashMap<>();
	private static HashMap<String,Boolean> statEnabled = new HashMap<>();
	
	static {
		statColor.put("待完成", Color.BLUE);
		statEnabled.put("待完成", true);
		
		statColor.put("完成", Color.GREEN);
		statEnabled.put("完成", false);
		
		statColor.put("未完成", Color.RED);
		statEnabled.put("未完成", false);
	}
	
	/**
	 * 设置颜色,以及状态.
	 * @param stat 要被设置的控件.
	 */
	public static void stateColor(JLabel state) {
		var text = state.getText();
		Color color = statColor.get(text);
		if (color != null) state.setForeground(color);
		state.getParent().getComponents()[0].setEnabled(statEnabled.get(text));
	}

}
