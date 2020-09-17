/**
 * 整合了其他js的文件
 * @author Shendi
 */
var loading;
{
	var loadingDiv = document.createElement("div");
	loadingDiv.style.position = "absolute";
	loadingDiv.style.top = 0;
	loadingDiv.style.width = "100%";
	loadingDiv.style.height = "100%";
	loadingDiv.style.background = "rgba(0,0,0,0.6)";
	loadingDiv.style.zIndex = 9999;
	
	var loadingImg = document.createElement("img");
	loadingImg.src = "/img/loading.gif";
	loadingImg.className = "loadingImg";
	loadingDiv.appendChild(loadingImg);
	
	var style = document.createElement("style");
	style.type = "text/css";
	style.innerHTML = ".loadingImg {position: absolute; top: 50%; left: 50%; width: 20%; transform: translate(-50%, -50%);}" +
			"@media screen and (max-width: 720px) { .loadingImg {width : 50%;}}";
	
	loading = {
		// 显示遮罩,首次需要指定图片路径,使用 showAndSet(img) 方法
		show : function () {
			document.head.appendChild(style);
			document.body.appendChild(loadingDiv);			
		},
		// 隐藏遮罩
		hide : function () {
			document.body.removeChild(loadingDiv);
			document.head.removeChild(style);
		},
		// 显示遮罩并修改遮罩图片
		showAndSet : function (img) {
			loadingImg.src = img;
			show();
		},
		// 遮罩块
		getLoadingDiv : loadingDiv,
		// 遮罩图
		getLoadingImg : loadingImg
	}
}

/**
 * 封装了对 cookie 的操作
 * @author Shendi
 */
var cookie = {
		/** 添加Cookie */
		add : function (key, value, time) {
			var date = new Date();
			if (time == null) {
				document.cookie = escape(btoa(key)) + "=" + escape(btoa(value));
				return;
			}
			date.setTime(date.getTime() + time);
	        document.cookie = escape(btoa(key)) + "=" + escape(btoa(value)) + ";expires=" + date.toGMTString();
		},
		/**
		 * 获取Cookie
		 * @param isEncode 如果不为null,则返回的cookie值不会被编码
		 */
		get : function (key, isEncode) {
			var c = document.cookie;
			if (c != "") {
				var cookies = c.split(";");
				for (let i = 0; i < cookies.length; i++) {
					let map = cookies[i].split("=");
					if (key == atob(unescape(map[0]))) {
						return isEncode == null ? escape(atob(unescape(map[1]))) : atob(unescape(map[1]));
					}
				}
			}
		},
		/** 删除指定Cookie */
		del : function (key) {
			var c = document.cookie;
			if (c != "") {
				var cookies = c.split(";");
				for (let i = 0; i < cookies.length; i++) {
					let map = cookies[i].split("=");
					if (key == atob(unescape(map[0]))) {
						var date = new Date();
						date.setTime(date.getTime()-1);
				        document.cookie = escape(btoa(key)) + "=;expires=" + date.toGMTString();
					}
				}
			}
		},
		/** 判断指定 key 是否存在 */
		exists : function (key) {
			var c = document.cookie;
			if (c == "") return false;
			var cookies = c.split(";");
			for (let i = 0; i < cookies.length; i++) {
				if (key == atob(unescape(cookies[i].split("=")[0]))) {
					return true;
				}
			}
			return false;
		},
		/** Cookie是否存在 */
		cookieExists : function () {
			return document.cookie != "";
		},
		/** 清除Cookie */
		clear : function () {
			var date=new Date();
            date.setTime(date.getTime() - 1);
            var keys=document.cookie.match(/[^ =;]+(?=\=)/g);
            if (keys) {
            	for (var i =  keys.length; i--;) document.cookie=keys[i] + "=; expires=" + date.toGMTString();
            }
		}
};

/**
 * 封装了对窗口的操作.
 * @author Shendi
 */
var win = {
		/**
		 * 在新窗口中打开一个连接
		 * @param url 请求路径
		 * @param type 请求类型
		 * @param param 请求参数,格式为name=value&name=value...
		 */
		open : function (url, type, param) {
			var form = document.createElement("form");     
			form.action = url;
			form.target = "_blank";
			form.method = type;
			form.style.display = "none";
			
			var params = param.split("&");
			for (var i = 0;i < params.length; i++) {
				var input = document.createElement("input");
				var map = params[i].split("=");
				input.name = map[0];
				input.value= map[1];
				form.appendChild(input);
			}
			document.body.appendChild(form);
			form.submit();
			document.body.removeChild(form);
		}

};

