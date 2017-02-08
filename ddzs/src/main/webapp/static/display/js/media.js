var mp = new MediaPlayer();
function initMediaPara(_left, _top, _width, _heigth) {
    var instanceId = mp.getNativePlayerInstanceID();
    var playListFlag = 0;
    var videoDisplayMode = 1;
    var height = _heigth;
    var width = _width;
    var left = _left;
    var top = _top;
    var muteFlag = 0;
    var subtitleFlag = 0;
    var videoAlpha = 0;
    var cycleFlag = 1;
    var randomFlag = 0;
    var autoDelFlag = 0;
    var useNativeUIFlag = 1;
    //初始话mediaplayer对象
    mp.initMediaPlayer(instanceId, playListFlag, videoDisplayMode, height, width, left, top, muteFlag, useNativeUIFlag, subtitleFlag, videoAlpha, cycleFlag, randomFlag, autoDelFlag);
    mp.setChannelNoUIFlag(0);
    mp.setVideoDisplayArea(_left, _top, _width, _heigth);
    mp.setVideoDisplayMode((_left == 0 || _top == 0) ? 1 : 0);
    mp.setAllowTrickmodeFlag(0);
    mp.setNativeUIFlag(0);
    mp.setAudioTrackUIFlag(1);
    mp.setMuteUIFlag(1);
    mp.setAudioVolumeUIFlag(0);
    mp.setProgressBarUIFlag(0);
    mp.refreshVideoDisplay();
}
function playVod(url) {
    var mediaStr = '[{mediaUrl:"' + url + '",';
    mediaStr += 'mediaCode: "jsoncode1",';
    mediaStr += 'IsHD:"0",'; //add
    mediaStr += 'mediaType:2,';
    mediaStr += 'audioType:1,';
    mediaStr += 'videoType:1,';
    mediaStr += 'streamType:1,';
    mediaStr += 'drmType:1,';
    mediaStr += 'fingerPrint:0,';
    mediaStr += 'copyProtection:1,';
    mediaStr += 'allowTrickmode:1,';
    mediaStr += 'startTime:0,';
    mediaStr += 'endTime:20000,';
    mediaStr += 'entryID:"jsonentry1"}]';
    mp.setSingleMedia(mediaStr); //设置媒体播放器播放媒体内容
    mp.playFromStart();
}
function playVod(url, playType, beginTime) {
    var mediaStr = '[{mediaUrl:"' + url + '",';
    mediaStr += 'mediaCode: "jsoncode1",';
    mediaStr += 'IsHD:"0",'; //add
    mediaStr += 'mediaType:2,';
    mediaStr += 'audioType:1,';
    mediaStr += 'videoType:1,';
    mediaStr += 'streamType:1,';
    mediaStr += 'drmType:1,';
    mediaStr += 'fingerPrint:0,';
    mediaStr += 'copyProtection:1,';
    mediaStr += 'allowTrickmode:1,';
    mediaStr += 'startTime:0,';
    mediaStr += 'endTime:20000,';
    mediaStr += 'entryID:"jsonentry1"}]';
    mp.setSingleMedia(mediaStr); //设置媒体播放器播放媒体内容
    if (playType == 6) {
        var type = 1; //自然播放
        var speed = 1; //播放速度
        mp.playByTime(type, beginTime, speed);
    }
    else {
        mp.playFromStart();
    }
}
function playChannel(chanNum) {
    mp.joinChannel(chanNum);
}