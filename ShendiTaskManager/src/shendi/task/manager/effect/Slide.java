package shendi.task.manager.effect;

import javax.swing.JComponent;

import shendi.kit.log.Log;

/**
 * 滑动效果,让一个组件从某个方向到某个方向收缩起来.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public class Slide {
	
	/**
	 * 默认速度 32 毫秒
	 */
	public static final int DEFAULT = 32;
	
	/**
	 * 使指定组件向上滑动 展开/收缩.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param component 要操作的组件
	 * @param isSlideUp 是否已经向上滑动,true展开,false收缩
	 */
	public static void slideUp(JComponent component,boolean isSlideUp) { slideUp(component,isSlideUp,DEFAULT); }
	
	/**
	 * 使指定组件向上滑动收缩  展开/收缩.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param component 要操作的组件
	 * @param isSlideUp 是否已经向上滑动,true展开,false收缩
	 * @param speed 进行操作的速度
	 */
	public static void slideUp(JComponent component,boolean isSlideUp,int speed) {
		new Thread(() -> {
			// 同步,一次只有一个线程进行操作
			synchronized (component) {
				// 记录当前窗体的高度,事后要恢复原样.
				int height = component.getHeight();
				// 获取偏移,展开/收缩
				int offset = 0;
				if (isSlideUp) {
					component.setSize(component.getWidth(), 0);
					component.setVisible(true);
					offset = -height >> 3;
				} else {
					offset = height >> 3;
				}
				
				while (true) {
					// 展开则到指定位置跳出,隐藏则低于0退出
					if (!isSlideUp) {
						if (component.getHeight() < 0) break;
					} else {
						if (component.getHeight() > height) break;
					}
					
					try { Thread.sleep(speed); } catch (InterruptedException e) {
						Log.printErr("对组件执行上滑效果时出错:" + e.getMessage());
					}
					
					component.setBounds(component.getX(), component.getY(), component.getWidth(), component.getHeight() - offset);
				}
				
				// 隐藏组件,将宽度恢复
				component.setVisible(isSlideUp);
				component.setSize(component.getWidth(), height);
			}
		}).start();
	}
	
	/**
	 * 使指定组件向下滑动 展开/收缩.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param component 要操作的组件
	 * @param isSlideUp 是否已经向上滑动,true展开,false收缩
	 */
	public static void slideDown(JComponent component,boolean isSlideUp) { slideDown(component,isSlideUp,DEFAULT); }
	
	/**
	 * 使指定组件向下滑动收缩  展开/收缩.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param component 要操作的组件
	 * @param isSlideUp 是否已经向上滑动,true展开,false收缩
	 * @param speed 进行操作的速度
	 */
	public static void slideDown(JComponent component,boolean isSlideUp,int speed) {
		new Thread(() -> {
			// 同步,一次只有一个线程进行操作
			synchronized (component) {
				// 记录当前窗体的高度,事后要恢复原样.
				int y = component.getY();
				int height = component.getHeight();
				// 获取偏移,展开/收缩
				int offset = 0;
				if (isSlideUp) {
					component.setBounds(component.getX(), y + height,component.getWidth(), 0);
					component.setVisible(true);
					offset = -height >> 3;
				} else {
					offset = height >> 3;
				}
				
				while (true) {
					// 展开则到指定位置跳出,隐藏则低于0退出
					if (!isSlideUp) {
						if (component.getHeight() < 0) break;
					} else {
						if (component.getHeight() > height) break;
					}
					
					try { Thread.sleep(speed); } catch (InterruptedException e) {
						Log.printErr("对组件执行下滑效果时出错:" + e.getMessage());
					}
					
					component.setBounds(component.getX(), component.getY() + offset, component.getWidth(), component.getHeight() - offset);
				}
				
				// 隐藏组件,将宽度恢复
				component.setVisible(isSlideUp);
				component.setBounds(component.getX(),y,component.getWidth(), height);
			}
		}).start();
	}
	
	/**
	 * 使指定组件向左滑动 展开/收缩.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param component 要操作的组件
	 * @param isSlideUp 是否已经向上滑动,true展开,false收缩
	 */
	public static void slideLeft(JComponent component,boolean isSlideUp) { slideLeft(component,isSlideUp,DEFAULT); }
	
	/**
	 * 使指定组件向左滑动收缩  展开/收缩.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param component 要操作的组件
	 * @param isSlideUp 是否已经向上滑动,true展开,false收缩
	 * @param speed 进行操作的速度
	 */
	public static void slideLeft(JComponent component,boolean isSlideUp,int speed) {
		new Thread(() -> {
			// 同步,一次只有一个线程进行操作
			synchronized (component) {
				// 记录当前窗体的高度,事后要恢复原样.
				int width = component.getWidth();
				// 获取偏移,展开/收缩
				int offset = 0;
				if (isSlideUp) {
					component.setSize(0,component.getHeight());
					component.setVisible(true);
					offset = -width >> 3;
				} else {
					offset = width >> 3;
				}
				
				while (true) {
					// 展开则到指定位置跳出,隐藏则低于0退出
					if (!isSlideUp) {
						if (component.getWidth() < 0) break;
					} else {
						if (component.getWidth() > width) break;
					}
					
					try { Thread.sleep(speed); } catch (InterruptedException e) {
						Log.printErr("对组件执行左滑效果时出错:" + e.getMessage());
					}
					
					component.setBounds(component.getX(), component.getY(), component.getWidth() - offset, component.getHeight());
				}
				
				// 隐藏组件,将宽度恢复
				component.setVisible(isSlideUp);
				component.setBounds(component.getX(),component.getY(),width,component.getHeight());
			}
		}).start();
	}
	
	/**
	 * 使指定组件向右滑动 展开/收缩.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param component 要操作的组件
	 * @param isSlideUp 是否已经向上滑动,true展开,false收缩
	 */
	public static void slideRight(JComponent component,boolean isSlideUp) { slideRight(component,isSlideUp,DEFAULT); }
	
	/**
	 * 使指定组件向右滑动收缩  展开/收缩.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param component 要操作的组件
	 * @param isSlideUp 是否已经向上滑动,true展开,false收缩
	 * @param speed 进行操作的速度
	 */
	public static void slideRight(JComponent component,boolean isSlideUp,int speed) {
		new Thread(() -> {
			// 同步,一次只有一个线程进行操作
			synchronized (component) {
				// 记录当前窗体的高度,事后要恢复原样.
				int x = component.getX();
				int width = component.getWidth();
				// 获取偏移,展开/收缩
				int offset = 0;
				if (isSlideUp) {
					component.setBounds(x + width, component.getY(),0, component.getHeight());
					component.setVisible(true);
					offset = -width >> 3;
				} else {
					offset = width >> 3;
				}
				
				while (true) {
					// 展开则到指定位置跳出,隐藏则低于0退出
					if (!isSlideUp) {
						if (component.getWidth() < 0) break;
					} else {
						if (component.getWidth() > width) break;
					}
					
					try { Thread.sleep(speed); } catch (InterruptedException e) {
						Log.printErr("对组件执行右滑效果时出错:" + e.getMessage());
					}
					
					component.setBounds(component.getX() + offset, component.getY(), component.getWidth() - offset, component.getHeight());
				}
				
				// 隐藏组件,将宽度恢复
				component.setVisible(isSlideUp);
				component.setBounds(x, component.getY(), width, component.getHeight());
			}
		}).start();
	}
	
}
