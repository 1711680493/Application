//投票方法
function selection(sessionId) {
	vote(sessionId, "selection");
}
//警察投票
function getIdentity(sessionId) {
	vote(sessionId, "getIdentity");
}
//杀手投票
function kill(sessionId) {
	vote(sessionId, "kill");
}

//操作
function vote(sessionId,operation) {
	var url;
	if (window.XMLHttpRequest) url = new XMLHttpRequest();
	else url = new ActiveXObject("Microsoft.XMLHTTP");
	url.open("POST","/WhoIsTheSpy/room/" + operation,true);
	url.setRequestHeader("Content-type","application/x-www-form-urlencoded");
	url.send("roomId=" + roomId + "&selection=" + sessionId);
}