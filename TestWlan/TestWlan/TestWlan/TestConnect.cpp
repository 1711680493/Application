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
	// 打开连接
	HANDLE handle = NULL;
	DWORD useVersion = 0;
	DWORD result = WlanOpenHandle(2, NULL, &useVersion, &handle);
	if (result != ERROR_SUCCESS) {
		printf("WlanOpenHandle失败: %d", result);
		return 0;
	}

	// 获取无线LAN接口,我这电脑只有一个无线LAN接口
	PWLAN_INTERFACE_INFO_LIST list = NULL;
	result = WlanEnumInterfaces(handle, NULL, &list);
	if (result != ERROR_SUCCESS) {
		printf("WlanEnumInterfaces失败: %d", result);
		return 0;
	}
	PWLAN_INTERFACE_INFO info = &list->InterfaceInfo[0];

	/*
		WlanConnect函数 尝试连接到特定网络
		参数
			HANDLE                            hClientHandle,
				客户端句柄,由WlanOpenHandle获取
			const GUID                        *pInterfaceGuid,
				接口GUID(之前WlanEnumInterfaces获取...)
			const PWLAN_CONNECTION_PARAMETERS pConnectionParameters,
				指向WLAN_CONNECTION_PARAMETERS结构的指针
				该结构指定连接类型,模式,网络配置文件,标识网络的SSID以及其他参数
			PVOID                             pReserved
				保留参数,NULL
		返回值
			ERROR_SUCCESS 函数执行成功
			ERROR_INVALID_PARAMETER 太多情况可触发,看文档
			ERROR_INVALID_HANDLE 在句柄表中找不到 句柄hClientHandle
			RPC_STATUS 各种错误代码。
			ERROR_ACCESS_DENIED 呼叫者没有足够的权限
	*/
	WLAN_CONNECTION_PARAMETERS connParam;
	// 连接模式
	connParam.wlanConnectionMode = wlan_connection_mode_profile;
	// 指定用于连接的配置文件
	connParam.strProfile = L"401";
	// 指示网络的BSS类型.如果提供了配置文件,则此BSS类型必须与配置文件中的类型相同
	connParam.dot11BssType = dot11_BSS_type_infrastructure;
	connParam.pDot11Ssid = NULL;
	connParam.pDesiredBssidList = NULL;
	// 用于指定连接参数的标志
	connParam.dwFlags = WLAN_CONNECTION_HIDDEN_NETWORK;

	result = WlanConnect(handle, &info->InterfaceGuid, &connParam, NULL);
	if (result != ERROR_SUCCESS) {
		printf("连接失败: %d", result);
	} else {
		printf("连接成功");
	}

	if (list != NULL) { WlanFreeMemory(list); }
	return 0;
}