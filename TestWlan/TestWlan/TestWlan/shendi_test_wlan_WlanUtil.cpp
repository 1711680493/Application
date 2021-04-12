#include <map>
#include <windows.h>
#include <wlanapi.h>
#include <objbase.h>
#include <wtypes.h>

#include <stdio.h>
#include <stdlib.h>
// WCHARתchar
#include <comdef.h>
// Need to link with Wlanapi.lib and Ole32.lib
#pragma comment(lib, "wlanapi.lib")
#pragma comment(lib, "ole32.lib")

#include "shendi_test_wlan_WlanUtil.h"

// ӳ���
std::map<int, HANDLE> handles;

// ��������ӿ��б�,һ�㲻�����ı�,���Թ��ô˱���
PWLAN_INTERFACE_INFO_LIST list = NULL;

/* ��ȡ�����hash��,Ψһ��ʶ */
int getObjHashCode(JNIEnv * env, jobject obj) {
	jmethodID hashCode = env->GetMethodID(env->FindClass("java/lang/Object"), "hashCode", "()I");
	return env->CallIntMethod(obj, hashCode);
}

/* ��Wlan���Ӿ��,�������ӳ�����. */
JNIEXPORT jint JNICALL Java_shendi_test_wlan_WlanUtil_openHandle(JNIEnv * env, jobject obj, jint maxVersion, jint useVersion) {
	DWORD uv = useVersion;
	HANDLE handle = NULL;
	
	DWORD result = WlanOpenHandle(maxVersion, NULL, &uv, &handle);

	if (result == ERROR_SUCCESS) {
		handles.insert(std::map<int, HANDLE>::value_type(getObjHashCode(env, obj), handle));
	}

	return result;

}

/* �ر�Wlan���Ӿ��,�������ӳ�����ɾ��,����Դ�ͷ�. */
JNIEXPORT jint JNICALL Java_shendi_test_wlan_WlanUtil_closeHandle(JNIEnv * env, jobject obj) {
	if (list != NULL) WlanFreeMemory(list);
	// ��ȡ���
	std::map<int, HANDLE>::iterator it = handles.find(getObjHashCode(env, obj));
	if (it != handles.end()) {
		DWORD result = WlanCloseHandle(it->second, NULL);
		handles.erase(getObjHashCode(env, obj));
		return result;
	}
	// �Զ���״̬��,404Ϊӳ�����û�ж�Ӧ���
	return 404;
}

/* ��ȡ��������ӿ��б� */
JNIEXPORT jint JNICALL Java_shendi_test_wlan_WlanUtil_getWlanEnumInterfaces(JNIEnv * env, jobject obj, jobject interfaces) {
	// ��ȡ���
	std::map<int, HANDLE>::iterator it = handles.find(getObjHashCode(env, obj));
	if (it != handles.end()) {
		DWORD result = WlanEnumInterfaces(it->second, NULL, &list);
		if (result == ERROR_SUCCESS) {
			// ArrayList��add����
			jclass ArrayList = env->FindClass("java/util/ArrayList");
			jmethodID ArrayListAdd = env->GetMethodID(ArrayList, "add", "(Ljava/lang/Object;)Z");

			// WlanInterface�͹���
			jclass WlanInterface = env->FindClass("shendi/test/wlan/WlanInterface");
			jmethodID WlanInterfaceConstruct = env->GetMethodID(WlanInterface, "<init>", "(JLjava/lang/String;II)V");

			// ����������뵽interfaces������
			for (int i = 0; i < list->dwNumberOfItems; i++) {
				PWLAN_INTERFACE_INFO info = &list->InterfaceInfo[i];
				
				jobject interfaceObj = env->NewObject(WlanInterface, WlanInterfaceConstruct, info->InterfaceGuid,
					env->NewStringUTF(_bstr_t(info->strInterfaceDescription)), info->isState, i);
				
				env->CallBooleanMethod(interfaces, ArrayListAdd, interfaceObj);
			}
		}
		return result;
	}
	return 404;
}

/* �Ͽ� WLAN ���� */
JNIEXPORT jint JNICALL Java_shendi_test_wlan_WlanUtil_disconnect(JNIEnv * env, jobject obj, jint wlanInterfaceIndex) {
	std::map<int, HANDLE>::iterator it = handles.find(getObjHashCode(env, obj));
	if (it != handles.end()) {
		DWORD result = WlanDisconnect(it->second, &list->InterfaceInfo[wlanInterfaceIndex].InterfaceGuid, NULL);
		return result;
	}
	return 404;
}

/* ����Wlan */
JNIEXPORT jint JNICALL Java_shendi_test_wlan_WlanUtil_connect(JNIEnv * env, jobject obj, jint wlanInterfaceIndex, jstring wlanName, jstring profile) {
	std::map<int, HANDLE>::iterator it = handles.find(getObjHashCode(env, obj));
	if (it != handles.end()) {
		PWLAN_INTERFACE_INFO info = &list->InterfaceInfo[wlanInterfaceIndex];
		
		const char * xmlContentChar = env->GetStringUTFChars(profile, NULL);
		int xmlContentLen = MultiByteToWideChar(CP_ACP, 0, xmlContentChar, -1, NULL, 0);
		WCHAR * xmlContent = (WCHAR*)malloc(sizeof(WCHAR)*xmlContentLen);
		memset(xmlContent, 0, sizeof(xmlContent));
		MultiByteToWideChar(CP_ACP, 0, xmlContentChar, -1, xmlContent, xmlContentLen);

		// ����profile
		DWORD reasonCode = NULL;
		DWORD result = WlanSetProfile(it->second, &info->InterfaceGuid, 0, xmlContent, NULL, true, NULL, &reasonCode);

		if (result == ERROR_SUCCESS) {
			WLAN_CONNECTION_PARAMETERS connParam;
			// ����ģʽ
			connParam.wlanConnectionMode = wlan_connection_mode_profile;

			// ָ���������ӵ������ļ�
			int wnameLen = MultiByteToWideChar(CP_ACP, 0, env->GetStringUTFChars(wlanName, NULL), -1, NULL, 0);
			WCHAR * wname = (WCHAR*)malloc(sizeof(WCHAR)*wnameLen);
			memset(wname, 0, sizeof(wname));
			MultiByteToWideChar(CP_ACP, 0, env->GetStringUTFChars(wlanName, NULL), -1, wname, wnameLen);
			connParam.strProfile = wname;

			// ָʾ�����BSS����.����ṩ�������ļ�,���BSS���ͱ����������ļ��е�������ͬ
			connParam.dot11BssType = dot11_BSS_type_infrastructure;
			connParam.pDot11Ssid = NULL;
			connParam.pDesiredBssidList = NULL;
			// ����ָ�����Ӳ����ı�־
			connParam.dwFlags = WLAN_CONNECTION_HIDDEN_NETWORK;
			result = WlanConnect(it->second, &info->InterfaceGuid, &connParam, NULL);
			return result;
		}
		return result;
	}
	return 404;
}
