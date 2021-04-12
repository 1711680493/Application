#include <windows.h>
#include <wlanapi.h>
#include <objbase.h>
#include <wtypes.h>

#include <stdio.h>
#include <stdlib.h>

// Need to link with Wlanapi.lib and Ole32.lib
#pragma comment(lib, "wlanapi.lib")
#pragma comment(lib, "ole32.lib")

// ���ò�����WLAN
int main() {
	// ������
	DWORD useVersion = 0;
	HANDLE handle = NULL;
	PWLAN_INTERFACE_INFO_LIST list = NULL;
	PWLAN_INTERFACE_INFO info;

	// �����ļ�����
	LPCWSTR xmlContent = L"<?xml version=\"1.0\"?><WLANProfile xmlns = \"http://www.microsoft.com/networking/WLAN/profile/v1\">"\
		"<name>Shendi</name>"
		"<SSIDConfig>"
			"<SSID>"
				"<name>Shendi</name>"
			"</SSID>"
			"<nonBroadcast>false</nonBroadcast>"
		"</SSIDConfig>"
		"<connectionType>ESS</connectionType>"
		"<connectionMode>manual</connectionMode>"
		"<autoSwitch>false</autoSwitch>"
		"<MSM>"
			"<security>"
				"<authEncryption>"
					"<authentication>WPA2PSK</authentication>"
					"<encryption>AES</encryption>"
					"<useOneX>false</useOneX>"
				"</authEncryption>"
				"<sharedKey>"
					"<keyType>passPhrase</keyType>"
					"<protected>false</protected>"
					"<keyMaterial>12345678</keyMaterial>"
				"</sharedKey>"
			"</security>"
		"</MSM>"
		"</WLANProfile>";

	DWORD result = WlanOpenHandle(2, NULL, &useVersion, &handle);
	if (result != ERROR_SUCCESS) {
		printf("WlanOpenHandleִ��ʧ��: %d", result);
		goto exit;
	}

	// ��ȡ����LAN�ӿ�,�������ֻ��һ������LAN�ӿ�
	result = WlanEnumInterfaces(handle, NULL, &list);
	if (result != ERROR_SUCCESS) {
		printf("WlanEnumInterfacesʧ��: %d", result);
		goto exit;
	}
	info = &list->InterfaceInfo[0];

	/*
		WlanSetProfile �����ض��������ļ�������
		����
			HANDLE     hClientHandle,				���
			const GUID *pInterfaceGuid,				�ӿ�GUID
			DWORD      dwFlags, ��־
				0 �����û��������ļ�
				WLAN_PROFILE_GROUP_POLICY(0x1) ���������
				WLAN_PROFILE_USER(0x2) ÿ���û��������ļ�
			LPCWSTR    strProfileXml,				���������ļ���XML��ʾ��ʽ
			LPCWSTR    strAllUserProfileSecurity,	�������û������ļ������ð�ȫ�������ַ���,���dwFlags����ΪWLAN_PROFILE_USER,����Դ˲���
			BOOL       bOverwrite,					ָ���������ļ��Ƿ񸲸����е������ļ�
			PVOID      pReserved,					NULL
			DWORD      *pdwReasonCode				һ��WLAN_REASON_CODEֵ,ָʾ�����ļ�Ϊʲô����Ч��
		����ֵ
			ERROR_SUCCESS ����ִ�гɹ�
			ERROR_ACCESS_DENIED û��Ȩ��
			ERROR_ALREADY_EXISTS �����Ѿ�����,��bOverwriteΪFALSEʱʹ�ô˷���ֵ
			ERROR_BAD_PROFILE ָ��������Ч
			ERROR_INVALID_PARAMETER ����������
			ERROR_NO_MATCH ��֧�������ļ��е�һ������
			RPC_STATUS ���ִ�����롣
	*/
	DWORD reasonCode;
	result = WlanSetProfile(handle, &info->InterfaceGuid, 0, xmlContent, NULL, true, NULL, &reasonCode);
	if (result != ERROR_SUCCESS) {
		printf("WlanSetProfileʧ��: %d", result);
		goto exit;
	}

	// ��������
	WLAN_CONNECTION_PARAMETERS connParam;
	// ����ģʽ
	connParam.wlanConnectionMode = wlan_connection_mode_profile;
	// ָ���������ӵ������ļ�
	connParam.strProfile = L"Shendi";
	// ָʾ�����BSS����.����ṩ�������ļ�,���BSS���ͱ����������ļ��е�������ͬ
	connParam.dot11BssType = dot11_BSS_type_infrastructure;
	connParam.pDot11Ssid = NULL;
	connParam.pDesiredBssidList = NULL;
	// ����ָ�����Ӳ����ı�־
	connParam.dwFlags = WLAN_CONNECTION_HIDDEN_NETWORK;

	result = WlanConnect(handle, &info->InterfaceGuid, &connParam, NULL);
	if (result != ERROR_SUCCESS) {
		printf("����ʧ��: %d", result);
	} else {
		// ��������ӳɹ�Ϊ����ִ�гɹ�,���û�ж�ӦWLAN���޷�����,��Ҫʹ�������������м��
		printf("���ӳɹ�");
	}
exit:
	// �رվ��,�ͷ���Դ
	if (list != NULL) WlanFreeMemory(list);
	if (handle != NULL) WlanCloseHandle(handle, NULL);
	return 0;
}