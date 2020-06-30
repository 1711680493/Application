package shendi.nat.client.util;

import shendi.kit.log.Log;
import shendi.nat.client.bean.NATInfo;

/**
 * 协议工具类,通过此类轻松组装数据.
 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
 * @version 1.0
 */
public class ProtocolUtils {

	/**
	 * 服务端穿透通信.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param type 类型
	 * @param info 后续数据信息,默认会转化成此对象toString形式,字节数组会做另外处理.
	 * @return 组装成的数据的字节表示形式
	 */
	public static final byte[] serverNAT(String type,Object... info) {
		byte[] data = type.getBytes();
		for (Object obj : info) {
			byte[] temp = data;
			byte[] bytes;
			
			if (obj instanceof byte[]) {
				bytes = (byte[]) obj;
			} else {
				bytes = obj.toString().getBytes();
			}
			
			data = new byte[temp.length + bytes.length + 1];
			System.arraycopy(temp, 0, data, 0, temp.length);
			data[temp.length] = ';';
			System.arraycopy(bytes, 0, data, temp.length+1, bytes.length);
		}
		byte[] temp = data;
		data = new byte[temp.length + 2];
		System.arraycopy(temp, 0, data, 0, temp.length);
		data[data.length - 1] = -3;
		data[data.length - 2] = -2;
		return data;
	}
	
	/**
	 * 打开隧道的协议组装.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param id 隧道id
	 * @return 组装后的数据
	 */
	public static final byte[] open(String id) {
		byte[] typeData = "open;".getBytes();
		byte[] idData = id.getBytes();
		byte[] data = new byte[typeData.length + idData.length];
		System.arraycopy(typeData, 0, data, 0, typeData.length);
		System.arraycopy(idData, 0, data, typeData.length, idData.length);
		return data;
	}
	
	/**
	 * 关闭隧道的协议组装.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param id 隧道id
	 * @return 组装后的数据
	 */
	public static final byte[] close(String id) {
		byte[] typeData = "close;".getBytes();
		byte[] idData = id.getBytes();
		byte[] data = new byte[typeData.length + idData.length];
		System.arraycopy(typeData, 0, data, 0, typeData.length);
		System.arraycopy(idData, 0, data, typeData.length, idData.length);
		return data;
	}
	
	/**
	 * 解析开启隧道操作返回的数据.
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param data 数据
	 * @return 操作是否成功.
	 */
	public static final boolean analysisOpen(String data) {
		String[] datas = data.split(";");
		// 一层层获取
		if (datas.length > 0) {
			String status = datas[0];
			if ("ok".equals(status)) {
				if (datas.length > 4) {
					NATInfo.getInfo().open(datas[1], Integer.parseInt(datas[2]), Integer.parseInt(datas[3]), datas[4]);
					return true;
				} else {
					NATInfo.getInfo().getView().sendInfo("开启隧道失败,网络不佳,请重试");
					Log.printAlarm("开启隧道失败,数据不完整: " + data);
					return false;
				}
			} else if ("error".equals(status)) {
				NATInfo.getInfo().getView().sendInfo(datas[1]);
				Log.print("开启隧道失败,信息为: " + datas[1]);
			} else {
				NATInfo.getInfo().getView().sendInfo("开启隧道失败,协议错误.");
				Log.printAlarm("开启隧道失败,数据协议错误: " + data);
			}
		}
		return false;
	}
	
	/**
	 * 用于判断数据是否完整(结尾).
	 * @author Shendi <a href='tencent://AddContact/?fromId=45&fromSubId=1&subcmd=all&uin=1711680493'>QQ</a>
	 * @param data 数据
	 * @param num 数据的有效数
	 * @return 数据是否结尾.
	 */
	public static final boolean isData(byte[] data,int num) {
		// 有效数不能大于数据的长度,也不能小于 2 个.
		if (data.length < num || data.length < 2 || num < 2) return false;
		// 当前协议的数据是以 -2,-3 结尾.
		return data[num - 2] == -2 && data[num - 1] == -3;
	}
	
}
