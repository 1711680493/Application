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
		/** 获取Cookie */
		get : function (key) {
			var c = document.cookie;
			if (c != "") {
				var cookies = c.split(";");
				for (let i = 0; i < cookies.length; i++) {
					let map = cookies[i].split("=");
					if (key == atob(unescape(map[0]))) {
						return atob(unescape(map[1]));
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
}
