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

// �г�����WLAN�б�
int main() {
	/*
		WlanOpenHandle ����
			��WlanOpenHandle���ܴ򿪵������������ӡ�
		�ĸ����� �ֱ�Ϊ
		_In_ DWORD dwClientVersion,
			�ͻ���֧�ֵ���߰汾WLAN API,1(Win xp����Win xp sp2)/2(һ�������Win vista��Win server 2008)
		_Reserved_ PVOID pReserved,��������,��NULL����
		_Out_ PDWORD pdwNegotiatedVersion,
			�ڴ˻Ự��ʹ�õ�WLAN API�汾,ͨ���ǿͻ��˺ͷ�������֧�ֵ���߰汾,�ٷ�demo�����0,�˲���Ϊָ��
		_Out_ PHANDLE phClientHandle
			�ͻ����ڴ˻Ự��ʹ�õľ��,�����Ự��,�������ܶ�ʹ�ô˾��,�˲���Ϊָ��
		������
			ERROR_SUCCESS ����ִ�гɹ�
			ERROR_INVALID_PARAMETER pdwNegotiatedVersion��NULL��phClientHandleΪNULL���������ա�
			ERROR_NOT_ENOUGH_MEMORY �޷������ڴ��������ͻ��������ġ�
			RPC_STATUS ���ִ�����롣
			ERROR_REMOTE_SESSION_LIMIT_EXCEEDED �������ѷ�������ľ����
	*/
	// �������������Ǳ����,���ݷ�ʽҲ�����,������ERROR_INVALID_PARAMETER,�����뺯�������й�
	// �򿪵ľ��
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
		printf("WlanOpenHandle����ִ��ʧ��,������Ϊ: %s", error);
		return 0;
	}

	/*
		WlanEnumInterfaces����ö�����еı��ؼ�����ϵ�ǰ���õ�����LAN�ӿ�
		�������� �ֱ�Ϊ
		HANDLE                    hClientHandle,
			�ͻ��˻Ự���,����ǰ��WlanOpenHandle�������û��
		PVOID                     pReserved,
			��������,������NULL
		PWLAN_INTERFACE_INFO_LIST *ppInterfaceList
			��ָ������WLAN_INTERFACE_INFO_LIST�ṹ���շ��ص�����LAN���б�
			������óɹ�����WLANEnumInterface�������䷵�ص�WLAN_INTERFACE_INFO_LIST������
		������
			ERROR_SUCCESS ����ִ�гɹ�
			ERROR_INVALID_PARAMETER
				���hClientHandle��ppInterfaceList����ΪNULL,
				���pReserved��ΪNULL
				���hClientHandle������Ч
			ERROR_INVALID_HANDLE �ھ�������Ҳ��� ���hClientHandle��
			ERROR_NOT_ENOUGH_MEMORY û���㹻���ڴ������������Ϊ��ѯ��������ڴ�
	*/
	/*
		list������������
		dwIndex ����
		dwNumberOfItems ������Ԫ����
		interfaceInfo ����Ԫ������
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
		printf("WlanEnumInterfaces����ִ��ʧ��,������Ϊ: %s", error);
		return 0;
	}

	// WLANGetAvailabelNetworkList��������ʱ����б�,����һ��List��ͬ,�����������������Network
	PWLAN_AVAILABLE_NETWORK_LIST lanList = NULL;
	// ���������ӿڼ���,�ҵ���ֻ��һ����������
	int len = list->dwNumberOfItems;//=1
	PWLAN_INTERFACE_INFO lanInfo = NULL;
	for (int i = 0; i < len; i++) {
		// ȡ��
		lanInfo = &list->InterfaceInfo[i];
		// ����״̬��ȡ
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
		printf("���� %ws ��Ϣ\n״̬: %s\nGUID: %ws\n------------------", lanInfo->strInterfaceDescription, lanState, lanGUID);

		/*
			WLANGetAvailabelNetworkList���� ��ȡ����LAN�ӿ��ϵĿ��������б�
			HANDLE                       hClientHandle,
				�Ự���,����ǰWlanOpenHandle�������û��
			const GUID                   *pInterfaceGuid,
				ָ��Ҫ��ѯ������LAN�ӿڵ�GUID��ָ��,����ͨ��WLANEnumInterfaces����ȷ������LAN�ӿڵ�GUID
			DWORD                        dwFlags,
				һ���־,���ڿ����б��з��ص���������,�˲�����������Щ����ֵ�����
				WLAN_AVAILABLE_NETWORK_INCLUDE_ALL_ADHOC_PROFILES 0x0
					�ڿ��������б��а���������ʱ���������ļ�,�������ɼ��������ļ�
				WLAN_AVAILABLE_NETWORK_INCLUDE_ALL_MANUAL_HIDDEN_PROFILES 0x2
					�ڿ��������б��а����������ص����������ļ�,�������ɼ��������ļ�
				ע:����ڴ���SP3��Windows XP�ʹ���SP2��Windows XP�����߾�����API��ָ���˴˱�־,������Ϊ��Ч����
			PVOID                        pReserved,
				��������,����ΪNULL
			PWLAN_AVAILABLE_NETWORK_LIST *ppAvailableNetworkList
				ָ��洢��ָ��,���ڽ���WLAN_AVAILABEL_NETWORK_LIST�ṹ�з��صĿɼ������б�
			������
				ERROR_SUCCESS ����ִ�гɹ�
				ERROR_INVALID_PARAMETER
					���hClientHandle,pInterfaceGuid��ppAvailableNetworkList����ΪNULL
					���pReserved��ΪNULL
					���dwFlags����ֵ����Ϊ��Чֵ��hClientHandle������Ч
				ERROR_INVALID_HANDLE �ھ�������Ҳ��� ���hClientHandle��
				ERROR_NDIS_DOT11_POWER_STATE_INVALID
					��ӿڹ��������ߵ��ѹر�,���ߵ�ر�ʱ,û�п��õ�����
				RPC_STATUS ���ִ������
				ERROR_NOT_ENOUGH_MEMORY û���㹻���ڴ������������Ϊ��ѯ��������ڴ�
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
			printf("WlanGetAvailableNetworkList����ִ��ʧ��,������Ϊ: %s", error);
			continue;
		}

		// ��������������
		int len = lanList->dwNumberOfItems;
		PWLAN_AVAILABLE_NETWORK network = NULL;
		for (int j = 0; j < len; j++) {
			// ��ȡ����Ԫ��
			network = &lanList->Network[j];
			// WLAN����
			int size = network->dot11Ssid.uSSIDLength;
			if (size == 0) {
				continue;
			}
			char* name = (char*) malloc((size + 1) * sizeof(char));
			// C�����ַ�����\0��β,������ط��ȿ���...
			name[size] = '\0';
			for (int k = 0; k < size; k++) {
				name[k] = network->dot11Ssid.ucSSID[k];
			}

			printf("---------------\n");
			printf("[%d]\n�����������ļ�����(�����): %ws\nWLAN���� : %s\n", j, network->strProfileName, name);

			free(name);
			name = NULL;
		}
	}

	// �ͷ�ָ��ָ����ڴ�,���ͷ�ָ��
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