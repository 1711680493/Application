//获取房间列表div 填充数据
var roomList = document.getElementById("rooms");
var list = eval(rooms);
for (var i = 0;i < list.length;i++) {
	var json = list[i];
	//输出一个房间的内容
	roomList.innerHTML += "<a class='room' href='room.html?id="+ json.id +"'>" +
					"<span>房间号:" + json.id + "</span>" +
					"<span>用户数量:" + json.playerNumber + "</span>" +
					"<span>房间模式:" + json.type + "</span>" +
					"</a><br />";
}

//给开始游戏按钮添加监听
var startGame = document.getElementById("start_game");
startGame.onclick = function() {
	//加入房间
	var url;
	if (window.XMLHttpRequest) url = new XMLHttpRequest();
	else url = new ActiveXObject("Microsoft.XMLHTTP");
	url.open("POST","/WhoIsTheSpy/join",true);
	url.onreadystatechange = function () {
		if (url.readyState == 4) {
			if (url.status != 200) {
				//请求出错处理
				return;
			}
			//获取到房间号
			var roomId = url.responseText;
			//进入房间
			window.location.href = "room.html?id=" + roomId;
		}
	}
	url.setRequestHeader("Content-type","application/x-www-form-urlencoded");
	url.send();
}
