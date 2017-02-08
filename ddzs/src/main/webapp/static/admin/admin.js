$(function(){
	$("form").submit(function(){
		var $form = $(this);
		$.post($form.attr("action"), $form.serialize(), function(data){
			if (data.code === 0) {
				layer.msg("登录成功", {shift: 1});
				setTimeout(function(){window.location.href = "${ctxPath}${from}";}, 600);
			} else {
				layer.msg(data.message, {shift: 6});
			}
		}, "json");
		return false;
	});
});



function showtime()   
{   
var today;  
var hour;  
var second;  
var minute;  
var year;  
var month;  
var date;  
var strDate;   
today=new Date();   
var n_day = today.getDay();   
switch (n_day)   
{   
case 0:{   
strDate = "星期日"   
}break;   
case 1:{   
strDate = "星期一"   
}break;   
case 2:{   
strDate ="星期二"   
}break;   
case 3:{   
strDate = "星期三"   
}break;   
case 4:{   
strDate = "星期四"   
}break;   
case 5:{   
strDate = "星期五"   
}break;   
case 6:{   
strDate = "星期六"   
}break;   
case 7:{   
strDate = "星期日"   
}break;   
}   
year = today.getYear() + 1900;
month = today.getMonth()+1;   
date = today.getDate();   
hour = today.getHours();   
minute =today.getMinutes();   
second = today.getSeconds();   
if(month<10) month="0"+month;   
if(date<10) date="0"+date;   
if(hour<10) hour="0"+hour;   
if(minute<10) minute="0"+minute;   
if(second<10) second="0"+second;   
document.getElementById('clock').innerHTML = year + "年" + month + "月" + date + "日 " + strDate +" " + hour + ":" + minute + ":" + second;   
setTimeout("showtime();", 1000);   
}  