# Ajax 封装类
>ajax.js是源码<br>
>ajax.min.js是压缩后的文件,实际应用使用这个<br>
>提供了跨域的处理
## API
#### ajax.xhr();
>获取 ajax 对象(通常用于自己手写ajax)<br>
#### ajax.req(type, url, callback, sync, data, crossDomain)
>执行 ajax 请求<br>
>type为请求类型,url为请求路径,callback为响应的回调接口<br>
>>其中,callback(txt, status, xhr);第一个参数为响应数据,第二个参数为响应状态,第三个参数为ajax对象<br>
>sync可选,true为异步 ajax<br>
>data为可选,如果是POST请求则参数放入data中,使用与传统ajax一致<br>
>crossDomain可选,true为允许执行跨域ajax<br>
#### ajax.post(url, data, callback, sync, crossDomain)
>执行 post 请求<br>
>参数同 ajax.req()<br>
