package shendi.chinesechess.listener;

import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.EventListener;
import java.util.HashMap;

/**
 * 所有监听的工厂,一切监听从此工厂获取.<br>
 * <table border='1'>
 * 	<tr>
 * 		<th>名称</th>
 * 		<th>描述</th>
 * 	</tr>
 * 	<tr>
 * 		<td>buttonAction</td>
 * 		<td>所有按钮的活动监听</td>
 * 	</tr>
 *  <tr>
 * 		<td>labelMouse</td>
 * 		<td>所有标签的鼠标监听</td>
 * 	</tr>
 * </table>
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public class ListenerFactory {
	
	/**
	 * 包含所有的监听.
	 */
	private static final HashMap<String,EventListener> LISTENERS = new HashMap<>();
	
	static {
		LISTENERS.put("buttonAction", new ButtonActionListener());
		LISTENERS.put("labelMouse", new LabelMouseListener());
		
	}
	
	/**
	 * 获取对应名称的监听.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param name 名称
	 * @return 对应监听
	 */
	public static EventListener getListener(String name) { return LISTENERS.get(name); }
	
	/**
	 * 获取对应活动监听.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param name 名称
	 * @return 对应的活动监听
	 */
	public static ActionListener getActionListener(String name) { return (ActionListener) LISTENERS.get(name); }
	
	/**
	 * 获取对应鼠标监听.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param name 名称
	 * @return 对应的鼠标监听
	 */
	public static MouseListener getMouseListener(String name) { return (MouseListener) LISTENERS.get(name); }
	
}
