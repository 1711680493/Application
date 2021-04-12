#include <map>
#include <windows.h>
#include <wlanapi.h>
#include <objbase.h>
#include <wtypes.h>

#include <stdio.h>
#include <stdlib.h>
// WCHAR转char
#include <comdef.h>
// Need to link with Wlanapi.lib and Ole32.lib
#pragma comment(lib, "wlanapi.lib")
#pragma comment(lib, "ole32.lib")

#include "shendi_test_wlan_WlanUtil.h"

// 映射表
std::map<int, HANDLE> handles;

// 无线网络接口列表,一般不会随便改变,所以共用此变量
PWLAN_INTERFACE_INFO_LIST list = NULL;

/* 获取对象的hash码,唯一标识 */
int getObjHashCode(JNIEnv * env, jobject obj) {
	jmethodID hashCode = env->GetMethodID(env->FindClass("java/lang/Object"), "hashCode", "()I");
	return env->CallIntMethod(obj, hashCode);
}

/* 打开Wlan连接句柄,放入对象映射表内. */
JNIEXPORT jint JNICALL Java_shendi_test_wlan_WlanUtil_openHandle(JNIEnv * env, jobject obj, jint maxVersion, jint useVersion) {
	DWORD uv = useVersion;
	HANDLE handle = NULL;
	
	DWORD result = WlanOpenHandle(maxVersion, NULL, &uv, &handle);

	if (result == ERROR_SUCCESS) {
		handles.insert(std::map<int, HANDLE>::value_type(getObjHashCode(env, obj), handle));
	}

	return result;

}

/* 关闭Wlan连接句柄,将句柄从映射表中删除,将资源释放. */
JNIEXPORT jint JNICALL Java_shendi_test_wlan_WlanUtil_closeHandle(JNIEnv * env, jobject obj) {
	if (list != NULL) WlanFreeMemory(list);
	// 获取句柄
	std::map<int, HANDLE>::iterator it = handles.find(getObjHashCode(env, obj));
	if (it != handles.end()) {
		DWORD result = WlanCloseHandle(it->second, NULL);
		handles.erase(getObjHashCode(env, obj));
		return result;
	}
	// 自定义状态码,404为映射表中没有对应句柄
	return 404;
}

/* 获取无线网络接口列表 */
JNIEXPORT jint JNICALL Java_shendi_test_wlan_WlanUtil_getWlanEnumInterfaces(JNIEnv * env, jobject obj, jobject interfaces) {
	// 获取句柄
	std::map<int, HANDLE>::iterator it = handles.find(getObjHashCode(env, obj));
	if (it != handles.end()) {
		DWORD result = WlanEnumInterfaces(it->second, NULL, &list);
		if (result == ERROR_SUCCESS) {
			// ArrayList和add函数
			jclass ArrayList = env->FindClass("java/util/ArrayList");
			jmethodID ArrayListAdd = env->GetMethodID(ArrayList, "add", "(Ljava/lang/Object;)Z");

			// WlanInterface和构造
			jclass WlanInterface = env->FindClass("shendi/test/wlan/WlanInterface");
			jmethodID WlanInterfaceConstruct = env->GetMethodID(WlanInterface, "<init>", "(JLjava/lang/String;II)V");

			// 将结果集输入到interfaces参数中
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

/* 断开 WLAN 连接 */
JNIEXPORT jint JNICALL Java_shendi_test_wlan_WlanUtil_disconnect(JNIEnv * env, jobject obj, jint wlanInterfaceIndex) {
	std::map<int, HANDLE>::iterator it = handles.find(getObjHashCode(env, obj));
	if (it != handles.end()) {
		DWORD result = WlanDisconnect(it->second, &list->InterfaceInfo[wlanInterfaceIndex].InterfaceGuid, NULL);
		return result;
	}
	return 404;
}

/* 连接Wlan */
JNIEXPORT jint JNICALL Java_shendi_test_wlan_WlanUtil_connect(JNIEnv * env, jobject obj, jint wlanInterfaceIndex, jstring wlanName, jstring profile) {
	std::map<int, HANDLE>::iterator it = handles.find(getObjHashCode(env, obj));
	if (it != handles.end()) {
		PWLAN_INTERFACE_INFO info = &list->InterfaceInfo[wlanInterfaceIndex];
		
		const char * xmlContentChar = env->GetStringUTFChars(profile, NULL);
		int xmlContentLen = MultiByteToWideChar(CP_ACP, 0, xmlContentChar, -1, NULL, 0);
		WCHAR * xmlContent = (WCHAR*)malloc(sizeof(WCHAR)*xmlContentLen);
		memset(xmlContent, 0, sizeof(xmlContent));
		MultiByteToWideChar(CP_ACP, 0, xmlContentChar, -1, xmlContent, xmlContentLen);

		// 设置profile
		DWORD reasonCode = NULL;
		DWORD result = WlanSetProfile(it->second, &info->InterfaceGuid, 0, xmlContent, NULL, true, NULL, &reasonCode);

		if (result == ERROR_SUCCESS) {
			WLAN_CONNECTION_PARAMETERS connParam;
			// 连接模式
			connParam.wlanConnectionMode = wlan_connection_mode_profile;

			// 指定用于连接的配置文件
			int wnameLen = MultiByteToWideChar(CP_ACP, 0, env->GetStringUTFChars(wlanName, NULL), -1, NULL, 0);
			WCHAR * wname = (WCHAR*)malloc(sizeof(WCHAR)*wnameLen);
			memset(wname, 0, sizeof(wname));
			MultiByteToWideChar(CP_ACP, 0, env->GetStringUTFChars(wlanName, NULL), -1, wname, wnameLen);
			connParam.strProfile = wname;

			// 指示网络的BSS类型.如果提供了配置文件,则此BSS类型必须与配置文件中的类型相同
			connParam.dot11BssType = dot11_BSS_type_infrastructure;
			connParam.pDot11Ssid = NULL;
			connParam.pDesiredBssidList = NULL;
			// 用于指定连接参数的标志
			connParam.dwFlags = WLAN_CONNECTION_HIDDEN_NETWORK;
			result = WlanConnect(it->second, &info->InterfaceGuid, &connParam, NULL);
			return result;
		}
		return result;
	}
	return 404;
}
