package shendi.web.server;

import java.util.HashMap;

import shendi.kit.config.ConfigurationFactory;
import shendi.web.server.util.InputStreamUtils;

/**
 * 用于处理映射类,继承自 ShendiWebServer 的类
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public final class SwsClassLoader extends ClassLoader {
	/**
	 * 此类只提供一个功能,就是加载映射类并调用.
	 */
	private SwsClassLoader() {}
	
	public static SwsClassLoader sws = new SwsClassLoader();
	
	/**
	 * 享元模式,类池.
	 */
	private HashMap<String,Class<?>> swsClass = new HashMap<>();
	
	/**
	 * 查找指定类并加载成class.
	 * @param name 类的全路径名
	 * @param path 文件路径
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 */
	public Class<?> loadSwsClass(String name,String path) {
		//判断是否热更新
		if ("true".equals(ConfigurationFactory.getConfig("config").getProperty("update"))) {
			//ClassLoader要重新加载class则需要一个新的ClassLoader对象...
			sws = new SwsClassLoader();
			//获取指定class文件字节流形式
			byte[] classData = InputStreamUtils.getFile(path);
			//将字节转class.
			//这里第一个参数为 name,设置为null代表是未知的(因为可能包名不同等导致加载失败)
			return defineClass(null,classData,0,classData.length);
		} else {
			//判断池子里有无指定class
			if (swsClass.containsKey(name)) {
				return swsClass.get(name);
			} else {
				//获取指定class文件字节流形式
				byte[] classData = InputStreamUtils.getFile(path);
				//将字节转class.
				//这里第一个参数为 name,设置为null代表是未知的(因为可能包名不同等导致加载失败)
				Class<?> clazz = defineClass(null,classData,0,classData.length);
				//存入池子
				swsClass.put(name, clazz);
				return clazz;
			}
		}
	}
	
}
