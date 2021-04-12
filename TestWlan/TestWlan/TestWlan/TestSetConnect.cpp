#include <windows.h>
#include <wlanapi.h>
#include <objbase.h>
#include <wtypes.h>

#include <stdio.h>
#include <stdlib.h>

// Need to link with Wlanapi.lib and Ole32.lib
#pragma comment(lib, "wlanapi.lib")
#pragma comment(lib, "ole32.lib")

// 设置并连接WLAN
int main() {
	// 打开连接
	DWORD useVersion = 0;
	HANDLE handle = NULL;
	PWLAN_INTERFACE_INFO_LIST list = NULL;
	PWLAN_INTERFACE_INFO info;

	// 配置文件内容
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
		WlanSetProfile 设置特定的配置文件的内容
		参数
			HANDLE     hClientHandle,				句柄
			const GUID *pInterfaceGuid,				接口GUID
			DWORD      dwFlags, 标志
				0 所有用户的配置文件
				WLAN_PROFILE_GROUP_POLICY(0x1) 组策略配置
				WLAN_PROFILE_USER(0x2) 每个用户的配置文件
			LPCWSTR    strProfileXml,				包含配置文件的XML表示形式
			LPCWSTR    strAllUserProfileSecurity,	在所有用户配置文件上设置安全描述符字符串,如果dwFlags设置为WLAN_PROFILE_USER,则忽略此参数
			BOOL       bOverwrite,					指定此配置文件是否覆盖现有的配置文件
			PVOID      pReserved,					NULL
			DWORD      *pdwReasonCode				一个WLAN_REASON_CODE值,指示配置文件为什么是无效的
		返回值
			ERROR_SUCCESS 函数执行成功
			ERROR_ACCESS_DENIED 没有权限
			ERROR_ALREADY_EXISTS 配置已经存在,当bOverwrite为FALSE时使用此返回值
			ERROR_BAD_PROFILE 指定配置无效
			ERROR_INVALID_PARAMETER 参数有问题
			ERROR_NO_MATCH 不支持配置文件中的一项或多项
			RPC_STATUS 各种错误代码。
	*/
	DWORD reasonCode;
	result = WlanSetProfile(handle, &info->InterfaceGuid, 0, xmlContent, NULL, true, NULL, &reasonCode);
	if (result != ERROR_SUCCESS) {
		printf("WlanSetProfile失败: %d", result);
		goto exit;
	}

	// 连接网络
	WLAN_CONNECTION_PARAMETERS connParam;
	// 连接模式
	connParam.wlanConnectionMode = wlan_connection_mode_profile;
	// 指定用于连接的配置文件
	connParam.strProfile = L"Shendi";
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
		// 这里的连接成功为函数执行成功,如果没有对应WLAN则无法连接,需要使用其他函数进行检测
		printf("连接成功");
	}
exit:
	// 关闭句柄,释放资源
	if (list != NULL) WlanFreeMemory(list);
	if (handle != NULL) WlanCloseHandle(handle, NULL);
	return 0;
}