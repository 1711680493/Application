package shendi.test.wlan;

import java.util.ArrayList;
import java.util.List;

public class Test {
	public static void main(String[] args) throws Exception {
		
		// 加载dll文件
		System.load("D:\\TestWlan\\TestWlan\\x64\\Debug\\WlanUtil.dll");
		
		try (WlanUtil util = new WlanUtil()) {
			int result = util.open(2, 0);
			if (result != ErrorCode.ERROR_SUCCESS) {
				System.out.println("打开句柄失败: " + result);
			}
			
			List<WlanInterface> list = new ArrayList<>();
			result = util.getWlanEnumInterfaces(list);
			
			for (WlanInterface wi : list) {
				System.out.println("-----------" + wi.index + "------------");
				System.out.println(wi.GUID);
				System.out.println(wi.desc);
				System.out.println(wi.state);
			}
			
			util.disconnect(list.get(0).index);
			
			result = util.connectByPwd(list.get(0).index, "Shendi", "12345678");
			System.out.println(result);
		}
		
	}
	
}
