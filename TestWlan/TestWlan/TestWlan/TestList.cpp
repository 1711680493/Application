#ifndef UNICODE
#define UNICODE
#endif

#include <windows.h>
#include <wlanapi.h>
#include <objbase.h>
#include <wtypes.h>

#include <stdio.h>
#include <stdlib.h>

// Need to link with Wlanapi.lib and Ole32.lib
#pragma comment(lib, "wlanapi.lib")
#pragma comment(lib, "ole32.lib")

// 列出无线WLAN列表
int main() {
	/*
		WlanOpenHandle 函数
			该WlanOpenHandle功能打开到服务器的连接。
		四个参数 分别为
		_In_ DWORD dwClientVersion,
			客户端支持的最高版本WLAN API,1(Win xp或者Win xp sp2)/2(一般用这个Win vista或Win server 2008)
		_Reserved_ PVOID pReserved,保留参数,填NULL就行
		_Out_ PDWORD pdwNegotiatedVersion,
			在此会话中使用的WLAN API版本,通常是客户端和服务器都支持的最高版本,官方demo填的是0,此参数为指针
		_Out_ PHANDLE phClientHandle
			客户端在此会话中使用的句柄,整个会话中,其他功能都使用此句柄,此参数为指针
		返回码
			ERROR_SUCCESS 函数执行成功
			ERROR_INVALID_PARAMETER pdwNegotiatedVersion是NULL，phClientHandle为NULL，或保留不空。
			ERROR_NOT_ENOUGH_MEMORY 无法分配内存来创建客户端上下文。
			RPC_STATUS 各种错误代码。
			ERROR_REMOTE_SESSION_LIMIT_EXCEEDED 服务器已发出过多的句柄。
	*/
	// 这里两个参数是必须的,传递方式也是如此,否则会出ERROR_INVALID_PARAMETER,可能与函数重载有关
	// 打开的句柄
	HANDLE ch = NULL;
	DWORD useVersion = 0;
	DWORD dwResult = WlanOpenHandle(2, NULL, &useVersion, &ch);
	if (dwResult != ERROR_SUCCESS) {
		const char* error = NULL;
		switch (dwResult) {
		case ERROR_INVALID_PARAMETER:
			error = "ERROR_INVALID_PARAMETER";
			break;
		case ERROR_NOT_ENOUGH_MEMORY:
			error = "ERROR_NOT_ENOUGH_MEMORY";
			break;
		case ERROR_REMOTE_SESSION_LIMIT_EXCEEDED:
			error = "ERROR_REMOTE_SESSION_LIMIT_EXCEEDED";
			break;
		}
		printf("WlanOpenHandle函数执行失败,错误码为: %s", error);
		return 0;
	}

	/*
		WlanEnumInterfaces函数枚举所有的本地计算机上当前启用的无限LAN接口
		三个参数 分别为
		HANDLE                    hClientHandle,
			客户端会话句柄,由先前对WlanOpenHandle函数调用获得
		PVOID                     pReserved,
			保留参数,必须填NULL
		PWLAN_INTERFACE_INFO_LIST *ppInterfaceList
			该指针用以WLAN_INTERFACE_INFO_LIST结构接收返回的无限LAN的列表
			如果调用成功则由WLANEnumInterface函数分配返回的WLAN_INTERFACE_INFO_LIST缓冲区
		返回码
			ERROR_SUCCESS 函数执行成功
			ERROR_INVALID_PARAMETER
				如果hClientHandle或ppInterfaceList参数为NULL,
				如果pReserved不为NULL
				如果hClientHandle参数无效
			ERROR_INVALID_HANDLE 在句柄表中找不到 句柄hClientHandle。
			ERROR_NOT_ENOUGH_MEMORY 没有足够的内存来处理此请求并为查询结果分配内存
	*/
	/*
		list中有三个参数
		dwIndex 索引
		dwNumberOfItems 集合内元素数
		interfaceInfo 具体元素数组
	*/
	PWLAN_INTERFACE_INFO_LIST list = NULL;
	dwResult = WlanEnumInterfaces(ch, NULL, &list);
	if (dwResult != ERROR_SUCCESS) {
		const char* error = NULL;
		switch (dwResult) {
		case ERROR_INVALID_PARAMETER:
			error = "ERROR_INVALID_PARAMETER";
			break;
		case ERROR_INVALID_HANDLE:
			error = "ERROR_INVALID_HANDLE";
			break;
		case ERROR_NOT_ENOUGH_MEMORY:
			error = "ERROR_NOT_ENOUGH_MEMORY";
			break;
		}
		printf("WlanEnumInterfaces函数执行失败,错误码为: %s", error);
		return 0;
	}

	// WLANGetAvailabelNetworkList函数调用时候的列表,与上一个List相同,但第三个参数变成了Network
	PWLAN_AVAILABLE_NETWORK_LIST lanList = NULL;
	// 遍历网卡接口集合,我电脑只有一个无线网卡
	int len = list->dwNumberOfItems;//=1
	PWLAN_INTERFACE_INFO lanInfo = NULL;
	for (int i = 0; i < len; i++) {
		// 取出
		lanInfo = &list->InterfaceInfo[i];
		// 网卡状态获取
		const char* lanState = NULL;
		switch (lanInfo->isState) {
		case wlan_interface_state_not_ready:
			lanState = "Not ready";
			break;
		case wlan_interface_state_connected:
			lanState = "Connected";
			break;
		case wlan_interface_state_ad_hoc_network_formed:
			lanState = "First node in a ad hoc network";
			break;
		case wlan_interface_state_disconnecting:
			lanState = "Disconnecting";
			break;
		case wlan_interface_state_disconnected:
			lanState = "Not connected";
			break;
		case wlan_interface_state_associating:
			lanState = "Attempting to associate with a network";
			break;
		case wlan_interface_state_discovering:
			lanState = "Auto configuration is discovering settings for the network";
			break;
		case wlan_interface_state_authenticating:
			lanState = "In process of authenticating";
			break;
		default:
			lanState = "Unknown state " + lanInfo->isState;
			break;
		}
		
		// GUID
		WCHAR lanGUID[39];
		StringFromGUID2(lanInfo->InterfaceGuid, (LPOLESTR)&lanGUID, sizeof(lanGUID) / sizeof(*lanGUID));
		printf("网卡 %ws 信息\n状态: %s\nGUID: %ws\n------------------", lanInfo->strInterfaceDescription, lanState, lanGUID);

		/*
			WLANGetAvailabelNetworkList函数 获取无线LAN接口上的可用网络列表
			HANDLE                       hClientHandle,
				会话句柄,由先前WlanOpenHandle函数调用获得
			const GUID                   *pInterfaceGuid,
				指向要查询的无线LAN接口的GUID的指针,可以通过WLANEnumInterfaces函数确定无线LAN接口的GUID
			DWORD                        dwFlags,
				一组标志,用于控制列表中返回的网络类型,此参数可以是这些可能值的组合
				WLAN_AVAILABLE_NETWORK_INCLUDE_ALL_ADHOC_PROFILES 0x0
					在可用网络列表中包括所有临时网络配置文件,包括不可见的配置文件
				WLAN_AVAILABLE_NETWORK_INCLUDE_ALL_MANUAL_HIDDEN_PROFILES 0x2
					在可用网络列表中包括所有隐藏的网络配置文件,包括不可见的配置文件
				注:如果在带有SP3的Windows XP和带有SP2的Windows XP的无线局域网API中指定了此标志,则将其视为无效参数
			PVOID                        pReserved,
				保留参数,必须为NULL
			PWLAN_AVAILABLE_NETWORK_LIST *ppAvailableNetworkList
				指向存储的指针,用于接收WLAN_AVAILABEL_NETWORK_LIST结构中返回的可见网络列表
			返回码
				ERROR_SUCCESS 函数执行成功
				ERROR_INVALID_PARAMETER
					如果hClientHandle,pInterfaceGuid或ppAvailableNetworkList参数为NULL
					如果pReserved不为NULL
					如果dwFlags参数值设置为无效值或hClientHandle参数无效
				ERROR_INVALID_HANDLE 在句柄表中找不到 句柄hClientHandle。
				ERROR_NDIS_DOT11_POWER_STATE_INVALID
					与接口关联的无线电已关闭,无线电关闭时,没有可用的网络
				RPC_STATUS 各种错误代码
				ERROR_NOT_ENOUGH_MEMORY 没有足够的内存来处理此请求并为查询结果分配内存
		*/
		dwResult = WlanGetAvailableNetworkList(ch, &lanInfo->InterfaceGuid, 0, NULL, &lanList);
		if (dwResult != ERROR_SUCCESS) {
			const char* error = NULL;
			switch (dwResult) {
			case ERROR_INVALID_PARAMETER:
				error = "ERROR_INVALID_PARAMETER";
				break;
			case ERROR_INVALID_HANDLE:
				error = "ERROR_INVALID_HANDLE";
				break;
			case ERROR_NDIS_DOT11_POWER_STATE_INVALID:
				error = "ERROR_NDIS_DOT11_POWER_STATE_INVALID";
				break;
			case ERROR_NOT_ENOUGH_MEMORY:
				error = "ERROR_NOT_ENOUGH_MEMORY";
				break;
			}
			printf("WlanGetAvailableNetworkList函数执行失败,错误码为: %s", error);
			continue;
		}

		// 遍历无线网集合
		int len = lanList->dwNumberOfItems;
		PWLAN_AVAILABLE_NETWORK network = NULL;
		for (int j = 0; j < len; j++) {
			// 获取具体元素
			network = &lanList->Network[j];
			// WLAN名称
			int size = network->dot11Ssid.uSSIDLength;
			if (size == 0) {
				continue;
			}
			char* name = (char*) malloc((size + 1) * sizeof(char));
			// C语言字符串以\0结尾,在这个地方踩坑了...
			name[size] = '\0';
			for (int k = 0; k < size; k++) {
				name[k] = network->dot11Ssid.ucSSID[k];
			}

			printf("---------------\n");
			printf("[%d]\n关联的配置文件名称(无则空): %ws\nWLAN名称 : %s\n", j, network->strProfileName, name);

			free(name);
			name = NULL;
		}
	}

	// 释放指针指向的内存,并释放指针
	if (lanList != NULL) {
		WlanFreeMemory(lanList);
		lanList = NULL;
	}

	if (list != NULL) {
		WlanFreeMemory(list);
		list = NULL;
	}
	return 0;
}

int wmain()
{
	// Declare and initialize variables.

	HANDLE hClient = NULL;
	DWORD dwMaxClient = 2;      //    
	DWORD dwCurVersion = 0;
	DWORD dwResult = 0;
	DWORD dwRetVal = 0;
	int iRet = 0;

	WCHAR GuidString[39] = { 0 };

	unsigned int i, j, k;

	/* variables used for WlanEnumInterfaces  */

	PWLAN_INTERFACE_INFO_LIST pIfList = NULL;
	PWLAN_INTERFACE_INFO pIfInfo = NULL;

	PWLAN_AVAILABLE_NETWORK_LIST pBssList = NULL;
	PWLAN_AVAILABLE_NETWORK pBssEntry = NULL;

	int iRSSI = 0;

	dwResult = WlanOpenHandle(dwMaxClient, NULL, &dwCurVersion, &hClient);
	if (dwResult != ERROR_SUCCESS) {
		wprintf(L"WlanOpenHandle failed with error: %u\n", dwResult);
		return 1;
		// You can use FormatMessage here to find out why the function failed
	}

	dwResult = WlanEnumInterfaces(hClient, NULL, &pIfList);
	if (dwResult != ERROR_SUCCESS) {
		wprintf(L"WlanEnumInterfaces failed with error: %u\n", dwResult);
		return 1;
		// You can use FormatMessage here to find out why the function failed
	}
	else {
		wprintf(L"Num Entries: %lu\n", pIfList->dwNumberOfItems);
		wprintf(L"Current Index: %lu\n", pIfList->dwIndex);
		for (i = 0; i < (int)pIfList->dwNumberOfItems; i++) {
			pIfInfo = (WLAN_INTERFACE_INFO *)&pIfList->InterfaceInfo[i];
			wprintf(L"  Interface Index[%u]:\t %lu\n", i, i);
			iRet = StringFromGUID2(pIfInfo->InterfaceGuid, (LPOLESTR)&GuidString,
				sizeof(GuidString) / sizeof(*GuidString));
			// For c rather than C++ source code, the above line needs to be
			// iRet = StringFromGUID2(&pIfInfo->InterfaceGuid, (LPOLESTR) &GuidString, 
			//     sizeof(GuidString)/sizeof(*GuidString)); 
			if (iRet == 0)
				wprintf(L"StringFromGUID2 failed\n");
			else {
				wprintf(L"  InterfaceGUID[%d]: %ws\n", i, GuidString);
			}
			wprintf(L"  Interface Description[%d]: %ws", i,
				pIfInfo->strInterfaceDescription);
			wprintf(L"\n");
			wprintf(L"  Interface State[%d]:\t ", i);
			switch (pIfInfo->isState) {
			case wlan_interface_state_not_ready:
				wprintf(L"Not ready\n");
				break;
			case wlan_interface_state_connected:
				wprintf(L"Connected\n");
				break;
			case wlan_interface_state_ad_hoc_network_formed:
				wprintf(L"First node in a ad hoc network\n");
				break;
			case wlan_interface_state_disconnecting:
				wprintf(L"Disconnecting\n");
				break;
			case wlan_interface_state_disconnected:
				wprintf(L"Not connected\n");
				break;
			case wlan_interface_state_associating:
				wprintf(L"Attempting to associate with a network\n");
				break;
			case wlan_interface_state_discovering:
				wprintf(L"Auto configuration is discovering settings for the network\n");
				break;
			case wlan_interface_state_authenticating:
				wprintf(L"In process of authenticating\n");
				break;
			default:
				wprintf(L"Unknown state %ld\n", pIfInfo->isState);
				break;
			}
			wprintf(L"\n");

			dwResult = WlanGetAvailableNetworkList(hClient,
				&pIfInfo->InterfaceGuid,
				0,
				NULL,
				&pBssList);

			if (dwResult != ERROR_SUCCESS) {
				wprintf(L"WlanGetAvailableNetworkList failed with error: %u\n",
					dwResult);
				dwRetVal = 1;
				// You can use FormatMessage to find out why the function failed
			}
			else {
				wprintf(L"WLAN_AVAILABLE_NETWORK_LIST for this interface\n");

				wprintf(L"  Num Entries: %lu\n\n", pBssList->dwNumberOfItems);

				for (j = 0; j < pBssList->dwNumberOfItems; j++) {
					pBssEntry =
						(WLAN_AVAILABLE_NETWORK *)& pBssList->Network[j];

					wprintf(L"  Profile Name[%u]:  %ws\n", j, pBssEntry->strProfileName);

					wprintf(L"  SSID[%u]:\t\t ", j);
					if (pBssEntry->dot11Ssid.uSSIDLength == 0)
						wprintf(L"\n");
					else {
						for (k = 0; k < pBssEntry->dot11Ssid.uSSIDLength; k++) {
							wprintf(L"%c", (int)pBssEntry->dot11Ssid.ucSSID[k]);
						}
						wprintf(L"\n");
					}

					wprintf(L"  BSS Network type[%u]:\t ", j);
					switch (pBssEntry->dot11BssType) {
					case dot11_BSS_type_infrastructure:
						wprintf(L"Infrastructure (%u)\n", pBssEntry->dot11BssType);
						break;
					case dot11_BSS_type_independent:
						wprintf(L"Infrastructure (%u)\n", pBssEntry->dot11BssType);
						break;
					default:
						wprintf(L"Other (%lu)\n", pBssEntry->dot11BssType);
						break;
					}

					wprintf(L"  Number of BSSIDs[%u]:\t %u\n", j, pBssEntry->uNumberOfBssids);

					wprintf(L"  Connectable[%u]:\t ", j);
					if (pBssEntry->bNetworkConnectable)
						wprintf(L"Yes\n");
					else {
						wprintf(L"No\n");
						wprintf(L"  Not connectable WLAN_REASON_CODE value[%u]:\t %u\n", j,
							pBssEntry->wlanNotConnectableReason);
					}

					wprintf(L"  Number of PHY types supported[%u]:\t %u\n", j, pBssEntry->uNumberOfPhyTypes);

					if (pBssEntry->wlanSignalQuality == 0)
						iRSSI = -100;
					else if (pBssEntry->wlanSignalQuality == 100)
						iRSSI = -50;
					else
						iRSSI = -100 + (pBssEntry->wlanSignalQuality / 2);

					wprintf(L"  Signal Quality[%u]:\t %u (RSSI: %i dBm)\n", j,
						pBssEntry->wlanSignalQuality, iRSSI);

					wprintf(L"  Security Enabled[%u]:\t ", j);
					if (pBssEntry->bSecurityEnabled)
						wprintf(L"Yes\n");
					else
						wprintf(L"No\n");

					wprintf(L"  Default AuthAlgorithm[%u]: ", j);
					switch (pBssEntry->dot11DefaultAuthAlgorithm) {
					case DOT11_AUTH_ALGO_80211_OPEN:
						wprintf(L"802.11 Open (%u)\n", pBssEntry->dot11DefaultAuthAlgorithm);
						break;
					case DOT11_AUTH_ALGO_80211_SHARED_KEY:
						wprintf(L"802.11 Shared (%u)\n", pBssEntry->dot11DefaultAuthAlgorithm);
						break;
					case DOT11_AUTH_ALGO_WPA:
						wprintf(L"WPA (%u)\n", pBssEntry->dot11DefaultAuthAlgorithm);
						break;
					case DOT11_AUTH_ALGO_WPA_PSK:
						wprintf(L"WPA-PSK (%u)\n", pBssEntry->dot11DefaultAuthAlgorithm);
						break;
					case DOT11_AUTH_ALGO_WPA_NONE:
						wprintf(L"WPA-None (%u)\n", pBssEntry->dot11DefaultAuthAlgorithm);
						break;
					case DOT11_AUTH_ALGO_RSNA:
						wprintf(L"RSNA (%u)\n", pBssEntry->dot11DefaultAuthAlgorithm);
						break;
					case DOT11_AUTH_ALGO_RSNA_PSK:
						wprintf(L"RSNA with PSK(%u)\n", pBssEntry->dot11DefaultAuthAlgorithm);
						break;
					default:
						wprintf(L"Other (%lu)\n", pBssEntry->dot11DefaultAuthAlgorithm);
						break;
					}

					wprintf(L"  Default CipherAlgorithm[%u]: ", j);
					switch (pBssEntry->dot11DefaultCipherAlgorithm) {
					case DOT11_CIPHER_ALGO_NONE:
						wprintf(L"None (0x%x)\n", pBssEntry->dot11DefaultCipherAlgorithm);
						break;
					case DOT11_CIPHER_ALGO_WEP40:
						wprintf(L"WEP-40 (0x%x)\n", pBssEntry->dot11DefaultCipherAlgorithm);
						break;
					case DOT11_CIPHER_ALGO_TKIP:
						wprintf(L"TKIP (0x%x)\n", pBssEntry->dot11DefaultCipherAlgorithm);
						break;
					case DOT11_CIPHER_ALGO_CCMP:
						wprintf(L"CCMP (0x%x)\n", pBssEntry->dot11DefaultCipherAlgorithm);
						break;
					case DOT11_CIPHER_ALGO_WEP104:
						wprintf(L"WEP-104 (0x%x)\n", pBssEntry->dot11DefaultCipherAlgorithm);
						break;
					case DOT11_CIPHER_ALGO_WEP:
						wprintf(L"WEP (0x%x)\n", pBssEntry->dot11DefaultCipherAlgorithm);
						break;
					default:
						wprintf(L"Other (0x%x)\n", pBssEntry->dot11DefaultCipherAlgorithm);
						break;
					}

					wprintf(L"  Flags[%u]:\t 0x%x", j, pBssEntry->dwFlags);
					if (pBssEntry->dwFlags) {
						if (pBssEntry->dwFlags & WLAN_AVAILABLE_NETWORK_CONNECTED)
							wprintf(L" - Currently connected");
						if (pBssEntry->dwFlags & WLAN_AVAILABLE_NETWORK_HAS_PROFILE)
							wprintf(L" - Has profile");
					}
					wprintf(L"\n");

					wprintf(L"\n");
				}
			}
		}

	}
	if (pBssList != NULL) {
		WlanFreeMemory(pBssList);
		pBssList = NULL;
	}

	if (pIfList != NULL) {
		WlanFreeMemory(pIfList);
		pIfList = NULL;
	}

	return dwRetVal;
}