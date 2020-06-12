package shendi.task.manager;

import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;

import shendi.kit.log.Log;
import shendi.task.manager.view.MainView;

/**
 * 整个程序的信息类.<br>
 * 包含关闭等方法.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public class Application {
	
	private Application() {
		try { logo = ImageIO.read(getClass().getResourceAsStream("/shendi_app_logo.png")); } catch (IOException e) { Log.printErr("读取logo图片错误,请检查 :" + e.getMessage()); }
		
	}
	
	/**
	 * 本类中的唯一对象.
	 */
	public static final Application APP = new Application();
	
	/**
	 * 上一次鼠标的坐标轴偏移.
	 */
	public int xOffset,yOffset;
	
	/**
	 * logo 图片.
	 */
	public Image logo;
	
	/**
	 * 当前软件的版本.
	 */
	public final int VERSION = 1;
	
	/**
	 * 当前软件的标题.
	 */
	public final String TITLE = "任务管理 -version: 1.0"; 
	
	/**
	 * 当前软件的托盘图标.
	 */
	public static final TaskTray TRAY = new TaskTray();
	
	/**
	 * 当前软件的主窗体.
	 */
	public static final MainView FRAME = new MainView();
	
	/**
	 * 关闭应用程序.<br>
	 * 关闭程序中所有正执行的,并保存对应信息.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 */
	public void close() {
		Log.close();
	}
	
}
