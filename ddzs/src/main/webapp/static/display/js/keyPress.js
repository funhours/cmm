var _win = window;
var _numWin = window;
var mulu = "";
var channelNum = "";//输入的频道号
var inputNumTimer = -1;
var isLive=false;
keyBvod = keyRed = function () {
	var url = mulu + "channel.html";
	_win.location.href = url;
};
keyTvod = keyGreen = function () {
	var url = mulu + "tvod.jsp";
	_win.location.href = url;
};

keyVod = keyYellow = function () {
	var url = mulu + "vod_film.html";
	_win.location.href = url;
};

keyInfo = keyBlue = function () {
	_win.location.href = mulu + "info.html";
};

//创建频道号DIV
// createDiv();
function createDiv(){
	var channelNoDivId="channelNoDiv";
	var obj = document.getElementById(channelNoDivId);
	if(obj == null||obj == undefined){
		var div=document.createElement("div");
		div.id=channelNoDivId;
		document.body.appendChild(div);
		document.getElementById(channelNoDivId).innerHTML='<div id="channelNum" style="position:absolute; left:1090px; top:30px; height:40px; width:50px;text-align:center;color:#00cc00;font-size:52px; font-weight:bolder; z-index:99;"></div>';
	}
}
function inputNum(num){
	channelNum=channelNum+""+num;
	(channelNum.length == 4) && (channelNum=num+"");
	document.getElementById("channelNum").innerHTML=channelNum;
	clearTimeout(inputNumTimer);
	inputNumTimer = setTimeout(
	function(){
		checkChanNum(channelNum);
	},1000);
}
//检查频道号是否存在，存在则跳转
function checkChanNum(chanNum)
{
	channelNum = chanNum;
	var chanObj = parent.chanNumObj["N"+parent.Util.addZero(chanNum,3)];
	if(chanObj != undefined){
		//var url=mulu+"playControlChannel.html?chanNum="+channelNum+"&chanId="+chanObj.chanId;
		var url=mulu+"playControlChannel.jsp?chanNum="+channelNum+"&chanId="+chanObj.chanId;
		
		if(isLive){//当前页面为直播播放页面，按数字键不跳转直接换视频
			changeChannel(chanObj.chanId,channelNum,1);
		}else{ 
			window.location.href=url;
		}
	}
	channelNum="";
	document.getElementById("channelNum").innerHTML=channelNum;
}
//接收机顶盒发送的虚拟事件

keyIptvEvent = function()
{
	eval("eventJson="+Utility.getEvent());
	var typeStr = eventJson.type;
	switch(typeStr)
	{	
		case "EVENT_TVMS":
		case "EVENT_TVMS_ERROR":
			return 0;
		case "EVENT_MEDIA_ERROR":
			return 0;
		case "EVENT_MEDIA_END":
			run(finishedPlay);
			return 0;
		case "EVENT_PLTVMODE_CHANGE":
			run(changeShiftOrLive,eventJson);
			return 0;
		case "EVENT_PLAYMODE_CHANGE":
			run(playModeChange,eventJson);
			return 0;
		case "EVENT_MEDIA_BEGINING":
			return 0;
		case "EVENT_GO_CHANNEL":
			return 0;
		default :
			return 0;

	}
	return true;
}

document.onkeypress = grabEvent;
document.onirkeypress = grabEvent;
function grabEvent()
{
	var keycode = event.which;
	switch(keycode)
	{
		case 272:
			return run(keyPortal);				//KEY_PORTAL  首页
		case 48:
			return run(inputNum,0);	//KEY_0  数字键0
		case 49:
			return run(inputNum,1);	//KEY_1  数字键1
		case 50:
			return run(inputNum,2);	//KEY_2  数字键2
		case 51:
			return run(inputNum,3);	//KEY_3  数字键3
		case 52:
			return run(inputNum,4);	//KEY_4  数字键4
		case 53:
			return run(inputNum,5);	//KEY_5  数字键5
		case 54:
			return run(inputNum,6);	//KEY_6  数字键6
		case 55:
			return run(inputNum,7);	//KEY_7  数字键7
		case 56:
			return run(inputNum,8);	//KEY_8  数字键8
		case 57:
			return run(inputNum,9);	//KEY_9  数字键9
		case 38:
			return run(keyUp);	    //KEY_UP    向上
		case 40:
			return run(keyDown);	//KEY_DOWN  向下
		case 37:
			return run(keyLeft);	//KEY_LEFT  向左
		case 39:
			return run(keyRight);	//KEY_RIGHT 向右
		case 13:
			return run(keyEnter);	//KEY_ENTER 确定
		case 33:
			return run(keyPageUp);	//KEY_PAGEUP    上页
		case 34:
			return run(keyPageDown);//KEY_PAGEDOWN  下页
		case 8:
			return run(keyBack);	//KEY_RETURN    返回
		case 270:
			return run(keyBack);	//KEY_STOP  	停止
		case 271:
			return run(keyPos);	//KEY_POS  	定位
		case 275:
			return run(keyRed); 	//KEY_RED  		快捷键(红色)
		case 276:
			return run(keyGreen);	//KEY_GREEN 	快捷键(绿色)
		case 277:
			return run(keyYellow);	//KEY_YELLOW    快捷键(黄色)
		case 278:
			return run(keyBlue);	//KEY_BLUE  	快捷键(蓝色)
		case 281:
			return run(keyFavourite);//KEY_FAVOURITE 收藏夹
		case 1108:
			return run(keyBvod);	//KEY_BVOD  快捷键(直播)
		case 1110:
			return run(keyTvod);	//KEY_TVOD  快捷键(回看)
		case 1109:
			return run(keyVod);		//KEY_VOD   快捷键(点播)
		case 1111:
			return run(keyComm);
		case 262:
		case 280://华为2108高清机顶盒声道键键值
			return run(keyTrack);    //KEY_SWITCH 声道
		case 268:
			return run(keyInfo);	//KEY_INFO   信息
		case 768:
			return run(keyIptvEvent);//KEY_IPTV_EVENT   虚拟事件
		case 263:
			return run(keyPausePlay);//KEY_PAUSE_PLAY   播放，暂停
		case 259:
			return run(keyVolUp);	//KEY_VOL_UP       音量加
		case 260:
			return run(keyVolDown);	//KEY_VOL_DOWN     音量减
		case 257:
			return run(keyChanUp);//KEY_CHANNEL_UP   频道加
		case 258:
			return run(keyChanDown);//KEY_CHANNEL_DOWN 频道减
		case 261:
			return run(keyMute);	//KEY_MUTE         静音
		case 264:
			return run(keyFastForward);//KEY_FAST_FORWARD 快进
		case 265:
			return run(keyFastRewind);//KEY_FAST_REWIND  快退
		case 283:
			return run(keyBottomLine);//KEY_BOTTOMLINE   下划线
		case 284:
			return run(keyHelp);//KEY_BOTTOMLINE   下划线
		case 39170:
			return run(keyPreviewTimesOver);//KEY_PREVIEWTIMES 预览次数
		case 39171:
			return run(keyPreviewTimesUp);//KEY_PREVIEWTIME  预览时间
		case 39172:
			return run(keyStbNoChannel);//KEY_STBNOCHANNEL 不存在该频道
		default:
			return 0;
	}
}
function run(fun,arg)
{
	if(fun != undefined)
	{
		var ret = fun(arg);
		return ret == undefined ? 0 : ret;//ret;
	}
	else
	{
		return 0;
	}
}
this.focus();