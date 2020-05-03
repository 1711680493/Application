var rooms;
//获取房间数据
var url;
if (window.XMLHttpRequest) url = new XMLHttpRequest();
else url = new ActiveXObject("Microsoft.XMLHTTP");
url.open("POST","/WhoIsTheSpy/rooms",false);
url.onreadystatechange = function () {
	if (url.readyState == 4) {
		if (url.status != 200) {
			//请求出错处理
			return;
		}
		rooms = url.responseText;
	}
}
url.setRequestHeader("Content-type","application/x-www-form-urlencoded");
url.send();