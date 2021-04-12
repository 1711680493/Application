#include <windows.h>
#include <wlanapi.h>
#include <objbase.h>
#include <wtypes.h>

#include <stdio.h>
#include <stdlib.h>

// Need to link with Wlanapi.lib and Ole32.lib
#pragma comment(lib, "wlanapi.lib")
#pragma comment(lib, "ole32.lib")

// 关闭WLAN连接
int main() {
	// 打开连接
	DWORD useVersion = 0;
	HANDLE handle = NULL;
	PWLAN_INTERFACE_INFO_LIST list = NULL;
	PWLAN_INTERFACE_INFO info;

	DWORD result = WlanOpenHandle(2, NULL, &useVersion, &handle);
	if (result != ERROR_SUCCESS) {
		printf("WlanOpenHandle执行失败: %d", result);
		goto exit;
	}

	// 获取无线LAN接口,我这电脑只有一个无线LAN接口
	result = WlanEnumInterfaces(handle, NULL, &list);
	if (result != ERROR_SUCCESS) {
		printf("WlanEnumInterfaces失败: %d", result);
		goto exit;
	}
	info = &list->InterfaceInfo[0];

	/*
		WlanDisconnect 断开从其当前网络的接口
		参数
			HANDLE     hClientHandle,	句柄
			const GUID *pInterfaceGuid, 接口
			PVOID      pReserved		NULL
		返回值
			ERROR_INVALID_PARAMETER hClientHandle为NULL或无效,pInterfaceGuid为NULL,或pReserved不为NULL。
			ERROR_INVALID_HANDLE 在句柄表中找不到 句柄hClientHandle
			RPC_STATUS 各种错误代码
			ERROR_NOT_ENOUGH_MEMORY 无法为查询结果分配内存
			ERROR_ACCESS_DENIED 呼叫者没有足够的权限
	*/
	result = WlanDisconnect(handle, &info->InterfaceGuid, NULL);
	if (result != ERROR_SUCCESS) {
		printf("断开失败: %d", result);
	} else {
		printf("断开成功");
	}
exit:
	// 关闭句柄,释放资源
	if (list != NULL) WlanFreeMemory(list);
	if (handle != NULL) WlanCloseHandle(handle, NULL);
	return 0;
}