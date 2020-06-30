package shendi.nat.server.util;

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
	
}
