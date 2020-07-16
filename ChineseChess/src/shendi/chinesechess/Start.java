package shendi.chinesechess;

import java.lang.reflect.InvocationTargetException;

import shendi.chinesechess.view.MainView;

/**
 * 启动中国象棋.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public class Start {
	public static void main(String[] args) {
		MainView view = new MainView();
		try {
			view.onCreate();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		view.setVisible(true);
	}
	
}
