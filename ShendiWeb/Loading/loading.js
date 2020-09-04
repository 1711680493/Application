/**
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
