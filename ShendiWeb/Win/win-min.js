/**
 * 封装了对窗口的操作.
 * @author Shendi
 */
var win={open:function(url,type,param){var form=document.createElement("form");form.action=url;form.target="_blank";form.method=type;form.style.display="none";var params=param.split("&");for(var i=0;i<params.length;i++){var input=document.createElement("input");var map=params[i].split("=");input.name=map[0];input.value=map[1];form.appendChild(input)}document.body.appendChild(form);form.submit();document.body.removeChild(form)}};