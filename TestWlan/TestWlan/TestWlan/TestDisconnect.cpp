#include <windows.h>
#include <wlanapi.h>
#include <objbase.h>
#include <wtypes.h>

#include <stdio.h>
#include <stdlib.h>

// Need to link with Wlanapi.lib and Ole32.lib
#pragma comment(lib, "wlanapi.lib")
#pragma comment(lib, "ole32.lib")

// �ر�WLAN����
int main() {
	// ������
	DWORD useVersion = 0;
	HANDLE handle = NULL;
	PWLAN_INTERFACE_INFO_LIST list = NULL;
	PWLAN_INTERFACE_INFO info;

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
		WlanDisconnect �Ͽ����䵱ǰ����Ľӿ�
		����
			HANDLE     hClientHandle,	���
			const GUID *pInterfaceGuid, �ӿ�
			PVOID      pReserved		NULL
		����ֵ
			ERROR_INVALID_PARAMETER hClientHandleΪNULL����Ч,pInterfaceGuidΪNULL,��pReserved��ΪNULL��
			ERROR_INVALID_HANDLE �ھ�������Ҳ��� ���hClientHandle
			RPC_STATUS ���ִ������
			ERROR_NOT_ENOUGH_MEMORY �޷�Ϊ��ѯ��������ڴ�
			ERROR_ACCESS_DENIED ������û���㹻��Ȩ��
	*/
	result = WlanDisconnect(handle, &info->InterfaceGuid, NULL);
	if (result != ERROR_SUCCESS) {
		printf("�Ͽ�ʧ��: %d", result);
	} else {
		printf("�Ͽ��ɹ�");
	}
exit:
	// �رվ��,�ͷ���Դ
	if (list != NULL) WlanFreeMemory(list);
	if (handle != NULL) WlanCloseHandle(handle, NULL);
	return 0;
}