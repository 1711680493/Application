# Web 加载效果遮罩
>loaidng.js是源码<br>
>loading.min.js是压缩后的文件,实际应用使用这个

## API
>使用 loading.showAndSet("加载图片路径"); 来设置和加载效果<br>
>在设置后,后续可以直接使用 loading.show(); 来显示加载效果<br>
>也可以直接修改源代码,设置默认图片,省略第一步<br>
>>找到 loadingImg.src="/img/loading.gif"; 将中间的字符串改成自己的图片路径<br>
>使用 loading.hide(); 来隐藏加载效果<br>

>使用 loading.getLoadingDiv 来获取遮罩块<br>
>使用 loading.getLoadingImg 来获取遮罩图

### 可通过修改源码中style部分来自定义遮罩图效果
