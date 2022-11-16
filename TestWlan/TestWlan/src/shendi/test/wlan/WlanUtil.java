package shendi.test.wlan;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

public class WlanUtil implements Closeable {
	/** �����Ƿ��(���) */
	private boolean isOpen;
	
	/**
	 * ��ȡ�������������ӿ���Ϣ
	 * @param interfaces ��ȡ��Ϣ�Ĵ洢����,Ϊһ���յ�list(��Ϊnull)
	 * @return ִ�д�����
	 */
	native int getWlanEnumInterfaces(List<WlanInterface> interfaces);
	
	/**
	 * �򿪾��.
	 * @param maxVersion �ͻ���֧�ֵ����WLAN�汾
	 * @param useVersion ʹ�õ�WLAN�汾
	 * @return ������
	 */
	native int openHandle(int maxVersion, int useVersion);
	
	/**
	 * �رվ��
	 * @return ������
	 */
	native int closeHandle();
	
	/**
	 * �ر�ָ���ӿڵ� WLAN ����
	 * @param �ӿڵ��±�
	 * @return ������
	 */
	native int disconnect(int wlanIIndex);
	
	/**
	 * ����ָ��Wlan,ʹ��Wlan���������ļ�����
	 * @param wlanName	����
	 * @param profile	�����ļ�����
	 * @return ������
	 */
	native int connect(int wlanInterfaceIndex, String wlanName, String profile);
	
	/**
	 * ��ȡ�������������ӿ���Ϣ
	 * @param interfaces ��ȡ��Ϣ�Ĵ洢����,Ϊһ���յ�list(��Ϊnull)
	 * @return ִ�д�����
	 */
	public int wlanEnumInterfaces(List<WlanInterface> interfaces) {
		if (!isOpen)
			throw new RuntimeException("���δ��,����ִ��open����!");
		return getWlanEnumInterfaces(interfaces);
	}
	
	/**
	 * �򿪾��,ֻ�д򿪾�������ִ����������
	 * @return ������
	 */
	public int open(int maxVersion, int useVersion) {
		isOpen = true;
		return openHandle(maxVersion, useVersion);
	}

	@Override
	public void close() throws IOException {
		if (isOpen) {
			int result = closeHandle();
			if (result != ErrorCode.ERROR_SUCCESS)
				throw new IOException("�ر�ʧ��,������Ϊ: " + result);
		}
		isOpen = false;
	}

	/**
	 * �ر�ָ���ӿڵ� WLAN ����.
	 * @param wlanI wlan�ӿ���Ϣ
	 * @return ������
	 */
	public int closeConnect(WlanInterface wlanI) {
		return closeConnect(wlanI.index);
	}
	/**
	 * �ر�ָ���ӿڵ� WLAN ����.
	 * @param wlanIIndex wlan�ӿ��±�
	 * @return ������
	 */
	public int closeConnect(int wlanIIndex) {
		if (!isOpen)
			throw new RuntimeException("���δ��,����ִ��open����!");
		return disconnect(wlanIIndex);
	}
	
	/**
	 * ʹ��һ�׹̶���ģ������WLAN
	 * @param wlanInterface ���߽ӿ���Ϣ
	 * @param wlanName ����
	 * @param pwd ����
	 * @return ������
	 */
	public int connectByPwd(WlanInterface wlanInterface, String wlanName, String pwd) {
		return connectByPwd(wlanInterface.index, wlanName, pwd);
	}
	/**
	 * ʹ��һ�׹̶���ģ������WLAN
	 * @param wlanInterfaceIndex ���߽ӿ��±�
	 * @param wlanName ����
	 * @param pwd ����
	 * @return ������
	 */
	public int connectByPwd(int wlanInterfaceIndex, String wlanName, String pwd) {
		String xmlContent = "<?xml version=\"1.0\"?><WLANProfile xmlns = \"http://www.microsoft.com/networking/WLAN/profile/v1\">"
		+ "<name>"+ wlanName +"</name>"
		+ "<SSIDConfig>"
		+	"<SSID>"
		+		"<name>" + wlanName + "</name>"
		+	"</SSID>"
		+	"<nonBroadcast>false</nonBroadcast>"
		+ "</SSIDConfig>"
		+ "<connectionType>ESS</connectionType>"
		+ "<connectionMode>manual</connectionMode>"
		+ "<autoSwitch>false</autoSwitch>"
		+ "<MSM>"
		+ "<security>"
		+		"<authEncryption>"
		+			"<authentication>WPA2PSK</authentication>"
		+			"<encryption>AES</encryption>"
		+			"<useOneX>false</useOneX>"
		+		"</authEncryption>"
		+		"<sharedKey>"
		+			"<keyType>passPhrase</keyType>"
		+			"<protected>false</protected>"
		+			"<keyMaterial>" + pwd + "</keyMaterial>"
		+		"</sharedKey></security></MSM></WLANProfile>";
		return connectByProfile(wlanInterfaceIndex, wlanName, xmlContent);
	}
	/**
	 * ����ָ��Wlan,ʹ��Wlan���������ļ�����
	 * @param wlanName	����
	 * @param profile	�����ļ�����
	 * @return ������
	 */
	public int connectByProfile(int wlanInterfaceIndex, String wlanName, String profile) {
		if (!isOpen)
			throw new RuntimeException("���δ��,����ִ��open����!");
		return connect(wlanInterfaceIndex, wlanName, profile);
	}
	
}