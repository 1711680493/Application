# Cookie 封装类
>cookie.js是源码<br>
>cookie.min.js是压缩后的文件,实际应用使用这个<br>
>已对cookie进行加密编码处理,什么样的数据都能存入
## API
#### cookie.add(key, value, time);
>添加键值进入cookie<br>
>key为cookie的名称,value为值,time为有效时间,为毫秒值<br>
#### cookie.get(key)
>获取对应Cookie<br>
>key为cookie的键,返回键对应的值
#### cookie.del(key)<br>
>删除对应Cookie<br>
>key为cookie的键,无返回
#### cookie.exists(key)
>判断指定键是否存在<br>
>key为cookie的键,存在则返回true,否则false
#### cookie.clear()
>清除cookie,无参数.
