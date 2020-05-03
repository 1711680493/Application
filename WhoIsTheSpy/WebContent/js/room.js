//获取当前房间id
var roomId = window.location.search.split("=")[1];
//获取聊天框
var infoContent = document.getElementById("info_content");
//当前的频道
var infoType = "all";
//房间消息的线程 默认等于全部
{
	var infoUrl;
	if (window.XMLHttpRequest) infoUrl = new XMLHttpRequest();
	else infoUrl = new ActiveXObject("Microsoft.XMLHTTP");
	infoUrl.onreadystatechange = function () {
		if (infoUrl.readyState == 4) {
			//获取到消息
			let infos = infoUrl.responseText;
			if (infoUrl.status != 200) {
				//请求出错处理
				infoContent.innerHTML = infos;
				clearInterval(getInfo);
				return;
			}
			//替换内容
			let infoArray = eval(infos);
			infoContent.innerHTML = "";
			for (let i = 0;i < infoArray.length;i++) {
				let obj = infoArray[i];
				infoContent.innerHTML += obj.info + "<br />";
			}
		}
	}
	var getInfo = setInterval(function () {
		infoUrl.open("POST","/WhoIsTheSpy/info",true);
		infoUrl.setRequestHeader("Content-type","application/x-www-form-urlencoded");
		infoUrl.send("roomId=" + roomId + "&infoType=" + infoType);
	},800);
}
//退出房间按钮
var exitRoom = document.getElementById("exit_room");
exitRoom.href = "../exitRoom?roomId=" + roomId;
//发送聊天信息的按钮
let infoSendEdit = document.getElementById("info_send_edit");
let infoSendButton = document.getElementById("info_send_button");
infoSendButton.onclick = function () {
	//发送文本内容
	var url;
	if (window.XMLHttpRequest) url = new XMLHttpRequest();
	else url = new ActiveXObject("Microsoft.XMLHTTP");
	url.open("POST","/WhoIsTheSpy/send",true);
	url.setRequestHeader("Content-type","application/x-www-form-urlencoded");
	url.send("roomId=" + roomId + "&infoType=" + infoType + "&info=" + infoSendEdit.value);
	infoSendEdit.value = "";
}
//加入房间按钮
let joinRoom = document.getElementById("join_room");
joinRoom.onclick = function () {
	var url;
	if (window.XMLHttpRequest) url = new XMLHttpRequest();
	else url = new ActiveXObject("Microsoft.XMLHTTP");
	url.open("POST","/WhoIsTheSpy/joinRoom",true);
	url.setRequestHeader("Content-type","application/x-www-form-urlencoded");
	url.send("roomId=" + roomId);
}
//专属于我的消息
let myInfo = document.getElementById("info_my");
let myInfoIsMax = false;
myInfo.onclick = function () {
	if (myInfoIsMax) {
		//变小
		myInfo.className = "";
		myInfoIsMax = false;
	} else {
		//变大
		myInfo.className = "info_my";
		myInfoIsMax = true;
	}
}
//获取专属于我的消息
var myInfoUrl;
if (window.XMLHttpRequest) myInfoUrl = new XMLHttpRequest();
else myInfoUrl = new ActiveXObject("Microsoft.XMLHTTP");
let resultInfo = document.getElementById("result_info");
myInfoUrl.onreadystatechange = function () {
	if (myInfoUrl.readyState == 4) {
		//获取到消息
		let infos = myInfoUrl.responseText;
		if (myInfoUrl.status != 200) {
			return;
		}
		//内容为空则不执行操作
		let infoArray = eval(infos);
		if ("" == infoArray || infoArray == null) {
			return;
		}
		//替换内容
		myInfo.innerHTML = "";
		for (let i = 0;i < infoArray.length;i++) {
			let obj = infoArray[i];
			//判断是否为结束消息
			let isStop = obj.info.split("\|")[1];
			if (isStop == "stop") {
				//显示结束界面
				resultInfo.innerHTML = obj.info.split("|")[2];
				continueButton.parentNode.style.display = "block";
			} else {
				myInfo.innerHTML += obj.info + "<br />";
			}
		}
	}
}
var getInfo = setInterval(function () {
	myInfoUrl.open("POST","/WhoIsTheSpy/roomEvent",true);
	myInfoUrl.setRequestHeader("Content-type","application/x-www-form-urlencoded");
	myInfoUrl.send("roomId=" + roomId + "&isLast=" + (!myInfoIsMax));
},800);

//切换频道按钮
let publicInfo = document.getElementById("info_public");
let systemInfo = document.getElementById("info_system");
let togetherInfo = document.getElementById("info_together");
publicInfo.onclick = function () {
	infoType = publicInfo.value;
	infoSendButton.disabled = null;
	infoSendEdit.disabled = null;
	infoSendEdit.value = "";
}
systemInfo.onclick = function () {
	infoType = systemInfo.value;
	infoSendButton.disabled = "disabled";
	infoSendEdit.disabled = "disabled";
	infoSendEdit.value = "系统频道不允许输入";
}
togetherInfo.onclick = function () {
	infoType = togetherInfo.value;
	infoSendEdit.value = "";
}

//获取房间用户状态的线程
var userInfo = "<table border='1'>" +
"<tr>" +
	"<th>编号</th>" +
	"<th>当前玩家</th>" +
	"<th>身份</th>" +
	"<th>操作</th>" +
	"<th>操作数量</th>" +
	"<th>游戏状态</th>" +
"</tr>";
var players = document.querySelector(".players");
var userUrl;
if (window.XMLHttpRequest) userUrl = new XMLHttpRequest();
else userUrl = new ActiveXObject("Microsoft.XMLHTTP");
userUrl.onreadystatechange = function () {
	if (userUrl.readyState == 4) {
		//获取到消息
		let infos = userUrl.responseText;
		if (userUrl.status != 200) {
			//请求出错处理
			clearInterval(getUser);
			return;
		}
		//替换内容
		let infoArray = eval(infos);
		let text = userInfo;
		for (let i = 0;i < infoArray.length;i++) {
			let obj = infoArray[i];
			text += "<tr><td>";
			text += obj.id + "</td>";
			text += "<td>" + obj.user + "</td>";
			//标识
			let identity = obj.identity;
			if (identity == undefined) {
				text += "<td>无</td>";
			} else {
				text += "<td>" + identity + "</td>";
			}
			//操作
			let operation = obj.operation;
			if (operation == undefined) {
				text += "<td>无</td>";
			} else {
				text += "<td>" + operation + "</td>";
			}
			//操作数量
			let operationNum = obj.operationNum;
			if (operation == undefined) {
				text += "<td>无</td>";
			} else {
				text += "<td>" + operationNum + "</td>";
			}
			//游戏状态
			let gameState = obj.gameState;
			if (gameState == undefined) {
				text += "<td>无</td>";
			} else {
				text += "<td>" + gameState + "</td>";
			}
			text += "</tr>";
		}
		text += "</table>";
		players.innerHTML = text;
	}
}
//获取房间里的用户
var getUser = setInterval(function () {
	userUrl.open("POST","/WhoIsTheSpy/roomInfo",true);
	userUrl.setRequestHeader("Content-type","application/x-www-form-urlencoded");
	userUrl.send("roomId=" + roomId);
},800);

//结束面板继续按钮
let continueButton = document.getElementById("continue");
continueButton.onclick = function () { continueButton.parentNode.style.display = "none"; }