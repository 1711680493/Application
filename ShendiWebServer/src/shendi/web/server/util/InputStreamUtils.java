package shendi.web.server.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 包含处理输入流的功能.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public class InputStreamUtils {
	/**
	 * 不需要对象
	 */
	private InputStreamUtils() {}
	
	/**
	 * 读取一行.
	 * @return 如果没有数据 则返回null
	 * @throws IOException 
	 */
	public static String readLine(BufferedInputStream input) throws IOException {
		int value = -1;
		//有效数据长度
		int len = 0;
		byte[] data = new byte[1024];
		while ((value = input.read()) != -1) {
			//如果数组长度不够则增长
			if (len >= data.length) {
				byte[] temp = data;
				data = new byte[len+1024];
				System.arraycopy(temp,0,data,0,temp.length);
			}
			data[len] = (byte) value;
			len++;
			//如果读到\n则读取完这一行
			if ((char)value == '\n') {
				break;
			}
		}
		//如果数组长度为0 则返回null
		if (len == 0) {
			return null;
		}
		return new String(data,0,len);
	}

	/**
	 * 将一个文件读取为字节流,读取的文件必须是确定存在的.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param file 文件
	 * @return 文件的字节流.
	 */
	public static byte[] getFile(File file) {
		byte[] data = new byte[0];
		try (BufferedInputStream input = new BufferedInputStream(new FileInputStream(file))) {
			int len = -1;
			byte[] bytes = new byte[1024];
			while ((len = input.read(bytes)) != -1) {
				byte[] temp = data;
				data = new byte[temp.length + len];
				System.arraycopy(temp,0,data,0,temp.length);
				System.arraycopy(bytes,0,data,temp.length,len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	/**
	 * 将一个文件读取为字节流,读取的文件必须是确定存在的.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param file 文件路径
	 * @return 文件的字节流.
	 */
	public static byte[] getFile(String file) {
		byte[] data = new byte[0];
		try (BufferedInputStream input = new BufferedInputStream(new FileInputStream(file))) {
			int len = -1;
			byte[] bytes = new byte[1024];
			while ((len = input.read(bytes)) != -1) {
				byte[] temp = data;
				data = new byte[temp.length + len];
				System.arraycopy(temp,0,data,0,temp.length);
				System.arraycopy(bytes,0,data,temp.length,len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}
}
