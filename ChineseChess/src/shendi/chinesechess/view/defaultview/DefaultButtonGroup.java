package shendi.chinesechess.view.defaultview;

import javax.swing.JButton;

import shendi.chinesechess.listener.ListenerFactory;
import shendi.chinesechess.view.ButtonGroup;

/**
 * 默认按钮组.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public class DefaultButtonGroup extends ButtonGroup {
	private static final long serialVersionUID = -7918775327989317575L;
	
	/**
	 * 开始按钮和关闭按钮
	 */
	private static JButton start,stop;
	
	@Override
	public void init() {
		start = new JButton("开启");
		start.addActionListener(ListenerFactory.getActionListener("buttonAction"));
		add(start);
		
		stop = new JButton("停止");
		stop.setEnabled(false);
		stop.addActionListener(ListenerFactory.getActionListener("buttonAction"));
		add(stop);
	}
	
	public static JButton getStart() { return start; }
	public static JButton getStop() { return stop; }
}
