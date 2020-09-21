/**
 * 封装了对 ajax 的操作
 * @author Shendi
 */
ajax = {
	/** @return 一个新的原生 ajax 对象 */
	xhr : function () {
		return window.XMLHttpRequest ? new XMLHttpRequest() : new ActiveXObject("Microsoft.XMLHTTP");
	},
	/**
	 * 执行 ajax 请求.
	 * @param {String} type 请求的类型
	 * @param {String} url 请求的路径
	 * @param {function} callback 请求状态为4时回调的函数,其中会传递三个参数,分别为响应数据,响应状态,xhr.
	 * @param {boolean} sync true异步,false同步
	 * @param {String} data 请求的数据,一般用于POST请求
	 * @param {boolean} crossDomain 是否允许跨域
	 */
	req : function (type, url, callback, sync, data, crossDomain) {
		var xhr = ajax.xhr();
		xhr.withCredentials = crossDomain == true;
		if (callback != null) {
			xhr.onreadystatechange = function () {
				if (xhr.readyState == 4) {
					callback(xhr.responseText, xhr.status, xhr);
				}
			};
		}
		xhr.open(type, url, sync==true);
		xhr.send(data);
	},
	/**
	 * 执行 ajax 的 post 请求
	 * @param {Object} url 请求路径
	 * @param {Object} data 请求数据
	 * @param {Object} callback 回调方法
	 * @param {Object} sync 是否异步 true异步
	 * @param {Object} crossDomain 是否跨域
	 */
	post : function (url, data, callback, sync, crossDomain) {
		var xhr = ajax.xhr();
		xhr.withCredentials = crossDomain == true;
		if (callback != null) {
			xhr.onreadystatechange = function () {
				if (xhr.readyState == 4) {
					callback(xhr.responseText, xhr.status, xhr);
				}
			};
		}
		xhr.open("POST", url, sync==true);
		xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
		xhr.send(data);
	}
};
