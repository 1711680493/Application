#include <windows.h>
#include <wlanapi.h>
#include <objbase.h>
#include <wtypes.h>

#include <stdio.h>
#include <stdlib.h>

// Need to link with Wlanapi.lib and Ole32.lib
#pragma comment(lib, "wlanapi.lib")
#pragma comment(lib, "ole32.lib")

int main() {
	// ������
	HANDLE handle = NULL;
	DWORD useVersion = 0;
	DWORD result = WlanOpenHandle(2, NULL, &useVersion, &handle);
	if (result != ERROR_SUCCESS) {
		printf("WlanOpenHandleʧ��: %d", result);
		return 0;
	}

	// ��ȡ����LAN�ӿ�,�������ֻ��һ������LAN�ӿ�
	PWLAN_INTERFACE_INFO_LIST list = NULL;
	result = WlanEnumInterfaces(handle, NULL, &list);
	if (result != ERROR_SUCCESS) {
		printf("WlanEnumInterfacesʧ��: %d", result);
		return 0;
	}
	PWLAN_INTERFACE_INFO info = &list->InterfaceInfo[0];

	/*
		WlanConnect���� �������ӵ��ض�����
		����
			HANDLE                            hClientHandle,
				�ͻ��˾��,��WlanOpenHandle��ȡ
			const GUID                        *pInterfaceGuid,
				�ӿ�GUID(֮ǰWlanEnumInterfaces��ȡ...)
			const PWLAN_CONNECTION_PARAMETERS pConnectionParameters,
				ָ��WLAN_CONNECTION_PARAMETERS�ṹ��ָ��
				�ýṹָ����������,ģʽ,���������ļ�,��ʶ�����SSID�Լ���������
			PVOID                             pReserved
				��������,NULL
		����ֵ
			ERROR_SUCCESS ����ִ�гɹ�
			ERROR_INVALID_PARAMETER ̫������ɴ���,���ĵ�
			ERROR_INVALID_HANDLE �ھ�������Ҳ��� ���hClientHandle
			RPC_STATUS ���ִ�����롣
			ERROR_ACCESS_DENIED ������û���㹻��Ȩ��
	*/
	WLAN_CONNECTION_PARAMETERS connParam;
	// ����ģʽ
	connParam.wlanConnectionMode = wlan_connection_mode_profile;
	// ָ���������ӵ������ļ�
	connParam.strProfile = L"401";
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
		printf("���ӳɹ�");
	}

	if (list != NULL) { WlanFreeMemory(list); }
	return 0;
}