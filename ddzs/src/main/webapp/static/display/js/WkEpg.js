(function () {

    function getByteLength(str) {
        if (!!str) {
            if (str.constructor != String) {
                str = str.toString();
            }
        } else {
            return 0;
        }
        var byteLen = 0,
            len = str.length;
        if (str) {
            for (var i = 0; i < len; i++) {
                if (str.charCodeAt(i) > 255) byteLen += 2;
                else {
                    if (str.charCodeAt(i) == 119) {
                        byteLen += 2;
                    } else {
                        byteLen++;
                    }
                }

            }
            return byteLen;
        }
        return 0;
    }

    function getCutedString(sSource, iLen, dot) {
        if (!!sSource) {
            if (sSource.constructor != String) {
                sSource = sSource.toString();
            }
        } else {
            return "...";
        }
        var str = "";
        var l = 0;
        var schar;
        for (var i = 0; schar = sSource.charAt(i); i++) {
            l += (schar.match(/[^\x00-\xff]/) != null ? 2 : 1);
            if (schar == "W" || schar == "w") {
                l = l + 1;
            }
            if (l > iLen) {
                break;
            }
            str += schar;
        }
        if (dot || str == "" || l <= iLen) return str;
        else return str + "...";
    }

    /**
     *
     * @param curareaid 当前区域ID
     * @param index 区域内当前DOM的ID
     * @param areas 所有区域集合
     * @constructor
     */
    function PageObj(curareaid, index, areas) {
        this.curareaid = curareaid;
        this.areas = areas;
        this.curdirindex = undefined;
        this.backurl = undefined;
        this.goBackEvent = undefined;
        this.delEvent = undefined;
        this.mp = undefined;

        this.pageOkEvent = undefined;
        this.pageVolumeUpEvent = undefined;
        this.pageVolumeDownEvent = undefined;
        this.pageVolumeMuteEvent = undefined;

        this.t_num = undefined;
        this.channelNumCount = 0;
        this.tempChannelNum = 0;
        this.channelNum = 0;

        this.showChannelNum = function (num) {
            this.channelNumCount++;
            if (this.channelNumCount > 4) {
                return true;
            }
            this.tempChannelNum = this.tempChannelNum * 10 + num;
            this.channelNum = this.tempChannelNum;
            if ($("channelNumPanel") != null) {
                $("channelNumPanel").innerHTML = this.channelNum;
            }
        };

        this.jumpToChannelSBM = function () {
            this.goChannel();
        };
        this.goChannel = function () {
            if (this.mp != undefined) { //ztewebkit
                this.mp.leaveChannel();
            }
            if ($("channelNumPanel") != null) {
                $("channelNumPanel").style.display = "none";
            }
            if (!!this.pageOkEvent) {
                this.pageOkEvent();
            }
            var back_url = location.href;
            if (back_url.indexOf('back_flag=') >= 0) {
                back_url = back_url;
            } else if (back_url.indexOf('?') >= 0) {
                back_url = back_url + "&back_flag=1";
            } else {
                back_url = back_url + "?back_flag=1";
            }
            var channelUrl = "channelplayer/channelPlayer.jsp?channelIndex=" + this.channelNum + "&returnurl=" + escape(back_url);
            window.location.href = "transform.jsp?pageurl=" + escape(channelUrl);
        }
        this.pageGetFocus = function () {
            this.channelNum = 0;
            this.channelNumCount = 0;
            this.tempChannelNum = 0;
            $("jump_hidden_iframe").src = "";
            $("jump_hidden_iframe").style.display = "none";
            $("channelNumPanel").innerHTML = "";
            window.focus();
        }
        this.pageNumTypeEvent = undefined;
        /*		this.pageNumTypeEvent = function(tempnum) {
         if ($("channelNumPanel") == null) { //add by wuming at 2014.09.24
         var channelNumDiv = document.createElement("div");
         channelNumDiv.id = "channelNumPanel";
         channelNumDiv.style.color = "#66cc00";
         channelNumDiv.style.fontSize = "50px";
         channelNumDiv.style.left = "1120px";
         channelNumDiv.style.top = "30px";
         channelNumDiv.style.fontWeight = "bold";
         channelNumDiv.style.position = "absolute";
         channelNumDiv.style.zIndex = 20;
         if (document.body != null) {
         document.body.appendChild(channelNumDiv);
         }
         }
         $("channelNumPanel").style.display = "block";
         this.showChannelNum(tempnum);
         if (this.t_num != undefined) {
         clearTimeout(this.t_num);
         }
         this.t_num = setTimeout("pageobj.jumpToChannelSBM()", 3000); //切换频道
         };*/

        if (this.areas != undefined) {
            for (i = 0; i < this.areas.length; i++) {
                this.areas[i].id = i;
                this.areas[i].pageobj = this;
            }
        }
        this.pageVolumeUp = function () {
            if (!!this.pageVolumeUpEvent) this.pageVolumeUpEvent();

        };
        this.pageVolumeDown = function () {
            if (!!this.pageVolumeDownEvent) this.pageVolumeDownEvent();
        };
        this.pageVolumeMute = function () {
            if (!!this.pageVolumeMuteEvent) this.pageVolumeMuteEvent();
        };
        this.del = function () {
            if (!!this.delEvent) this.delEvent();
        };
        this.pageTurn = function (num, areaid) {
            if (this.areas != undefined) this.areas[areaid == undefined ? this.curareaid : areaid].pageturn(num, areaid);
        };
        this.ok = function () {
            if (!!this.pageOkEvent) this.pageOkEvent();
            if (this.areas != undefined) this.areas[this.curareaid].ok();
        };
        this.getfocusdom = function () {
            if (this.areas != undefined) return this.areas[this.curareaid].doms[this.areas[this.curareaid].curindex];
        };
        this.getfocusindex = function () {
            if (this.areas != undefined) return this.areas[this.curareaid].curindex;
        };
        this.changefocus = function (areaid, index) {
            if (this.areas != undefined) for (i = 0; i < this.areas.length; i++) {
                this.areas[i].clearfocus();
                if (i == areaid) this.areas[i].changefocus(index, true);
                this.curareaid = areaid;
            }
        };
        this.goBack = function () {
            if (!!this.goBackEvent) this.goBackEvent();
            if (this.backurl != undefined) {
                window.location.href = this.backurl;
            }
        };
        this.numType = function (num) {
            if (!!this.pageNumTypeEvent) {
                this.pageNumTypeEvent(num);
            }
        };
        this.move = function (dir) {
            var cross;
            var isemptydom = false;
            if (this.curareaid == undefined || this.areas == undefined) {
                return false;
            }
            var tempcurarea = this.areas[this.curareaid];
            var tempAreaEndTurnPageFlag = true;
            if (typeof(tempcurarea.areaEndTurnPage) !== "undefined") {
                tempAreaEndTurnPageFlag = tempcurarea.areaEndTurnPage[dir];
            }

            var num = (dir == 0 || dir == 1 ? -1 : 1);
            var dirindex = dir;
            this.curdirindex = dir;
            if (dirindex == 0 || dirindex == 2) {
                if (tempcurarea.columnsort) { //按列排序
                    var currow = tempcurarea.curindex % tempcurarea.numrow;
                    if (num > 0) cross = (currow + num) > tempcurarea.numrow - 1;
                    else cross = (currow + num) < 0;
                    if (!cross) {
                        if (tempcurarea.datanum != undefined) {
                            var tmpnextindex = tempcurarea.curindex + num;
                            if (tmpnextindex > tempcurarea.datanum - 1) {
                                cross = true;
                                isemptydom = true;
                            }
                        }
                    }
                    if (tempcurarea.circledir == 1) return tempcurarea.insidemove(true, dirindex, num, cross);
                } else { //按行排序
                    var currow = parseInt(tempcurarea.curindex / tempcurarea.numcolum);
                    if (num > 0) cross = (currow + num) > tempcurarea.numrow - 1;
                    else cross = (currow + num) < 0;
                    if (!cross) {
                        if (tempcurarea.datanum != undefined) {
                            var tmpnextindex = tempcurarea.curindex + num * tempcurarea.numcolum;
                            if (tmpnextindex > tempcurarea.datanum - 1) {
                                cross = true;
                                isemptydom = true;
                            }
                        }
                    }
                    if (tempcurarea.circledir == 0) return tempcurarea.insidemove(true, dirindex, num, cross);
                }
            } else if (dirindex == 1 || dirindex == 3) {
                if (tempcurarea.columnsort) { //按列排序
                    var curcolum = parseInt(tempcurarea.curindex / tempcurarea.numrow);
                    if (num > 0) cross = (curcolum + num) > tempcurarea.numcolum - 1;
                    else cross = (curcolum + num) < 0;
                    if (!cross) {
                        if (tempcurarea.datanum != undefined) {
                            var tmpnextindex = tempcurarea.curindex + num * tempcurarea.numrow;
                            if (tmpnextindex > tempcurarea.datanum - 1) {
                                cross = true;
                                isemptydom = true;
                            }
                        }
                    }
                    if (tempcurarea.circledir == 0) return tempcurarea.insidemove(true, dirindex, num, cross);
                } else { //按行排序
                    var curcolum = tempcurarea.curindex % tempcurarea.numcolum;
                    if (num > 0) cross = (curcolum + num) > tempcurarea.numcolum - 1;
                    else cross = (curcolum + num) < 0;
                    if (!cross) {
                        if (tempcurarea.datanum != undefined) {
                            var tmpnextindex = tempcurarea.curindex + num;
                            if (tmpnextindex > tempcurarea.datanum - 1) {
                                cross = true;
                                isemptydom = true;
                            }
                        }
                    }
                    if (tempcurarea.circledir == 1) return tempcurarea.insidemove(true, dirindex, num, cross);
                }
            }
            if (cross) {
                if (tempcurarea.endwiseCrossturnpage && tempAreaEndTurnPageFlag && (dirindex == 0 || dirindex == 2) && ((dirindex == 0 && tempcurarea.curpage > 1) || tempcurarea.cirturnpage || (dirindex == 2 && tempcurarea.curpage < tempcurarea.pagecount))) {

                    if (!!tempcurarea.areaCorssEvent) {
                        if (!tempcurarea.areaCorssEvent()) {
                            return false;
                        }
                    }

                    if (tempcurarea.pagecount == 1) return -1;
                    if (dirindex == 0) tempcurarea.pageturn(-1);
                    else if (dirindex == 2) tempcurarea.pageturn(1);
                    return -1;
                } else if (tempcurarea.broadwiseCrossturnpage && tempAreaEndTurnPageFlag && (dirindex == 1 || dirindex == 3) && ((dirindex == 1 && tempcurarea.curpage > 1) || tempcurarea.cirturnpage || (dirindex == 3 && tempcurarea.curpage < tempcurarea.pagecount))) {

                    if (!!tempcurarea.areaCorssEvent) {
                        if (!tempcurarea.areaCorssEvent()) {
                            return false;
                        }
                    }

                    if (tempcurarea.pagecount == 1) return -1;
                    if (dirindex == 1) tempcurarea.pageturn(-1);
                    else if (dirindex == 3) tempcurarea.pageturn(1);
                    return -1;
                } else {
                    if (tempcurarea.directions[dirindex] == -1 ? false : true) {
                        if (this.areas[tempcurarea.directions[dirindex]].lockin || (this.areas[tempcurarea.directions[dirindex]].datanum != undefined && this.areas[tempcurarea.directions[dirindex]].datanum <= 0)) return -1;

                        var tempresult = tempcurarea.areaout(tempcurarea.curindex, dirindex);
                        if (!tempresult) return;
                        var tmpstable = 0;
                        var tmpstablearea = parseInt(tempcurarea.directions[dirindex]);
                        if (tempcurarea.stablemoveindex != undefined && tempcurarea.stablemoveindex[dirindex] != -1) {
                            var tmpmoveindex = tempcurarea.stablemoveindex[dirindex].split(',');
                            if (isemptydom) {
                                if (dirindex == 2) {
                                    var lastcolumindex = (tempcurarea.numrow - 1) * tempcurarea.numcolum;
                                    var tmpcolum = tempcurarea.curindex % tempcurarea.numcolum;
                                    stableindex = lastcolumindex + tmpcolum;
                                } else if (dirindex == 3) {
                                    var tmprow = parseInt((tempcurarea.curindex + 1) / tempcurarea.numcolum);
                                    stableindex = (tmprow + 1) * tempcurarea.numcolum - 1;
                                }
                            } else {
                                stableindex = tempcurarea.curindex;
                            }
                            for (k = 0; k < tmpmoveindex.length; k++) {
                                var tmp = tmpmoveindex[k].split('-');
                                if (parseInt(tmp[0]) == stableindex) {
                                    if (tmp[1].indexOf('>') == -1) tmpstable = parseInt(tmp[1]);
                                    else {
                                        tmpstablearea = parseInt(tmp[1].split('>')[0]);
                                        tmpstable = parseInt(tmp[1].split('>')[1]);
                                    }
                                    break;
                                }
                            }
                        }
                        if (!!this.areas[tmpstablearea].doms[tmpstable]) {
                            this.areas[tmpstablearea].areain(tmpstable, dirindex);
                        } else {//如果不存在直接跳到下一个区域,使用当前区域的tempcurarea,add by wm at 2015.05.27
                            var nextAreaStation = tempcurarea.doms[tempcurarea.curindex].nextAreaStation;
                            if (!!nextAreaStation) {
                                this.areas[parseInt(nextAreaStation.split('>')[0])].areain(parseInt(nextAreaStation.split('>')[1]), dirindex);
                            }
                        }
                    }
                }
            } else {
                return tempcurarea.insidemove(false, dirindex, num);
            }
        };
    }

    /*Area对象*/
    /**
     *
     * @param numrow 行
     * @param numcolum 列
     * @param doms
     * @param directions
     * @constructor
     */
    function Area(numrow, numcolum, doms, directions) {
        this.datanum = undefined;//当前页面数据长度
        this.curindex = 0;
        this.doms = doms;
        this.stablemoveindex = undefined;
        this.curpage = 1;
        this.pagecount = 1;
        this.lock = false;
        this.areaOkEvent = undefined;
        this.areaInBeforeEvent = undefined;
        this.areaInAfterEvent = undefined;
        this.areaOutBeforeEvent = undefined;
        this.areaOutAfterEvent = undefined;
        this.stayindexarray = new Array(0, 0, 0, 0);
        this.staydirarray = new Array(false, false, false, false);
        this.staystylearray = new Array();
        this.id = undefined;
        this.numrow = numrow;
        this.numcolum = numcolum;
        this.circledir = undefined;
        this.cirturnpage = false;
        this.directions = directions;
        this.pageobj = undefined;
        this.areaPageTurnEvent = undefined;
        this.changefocusBeforeEvent = undefined;
        this.changefocusAfterEvent = undefined;
        this.changeunfocusBeforeEvent = undefined;
        this.changeunfocusAfterEvent = undefined;
        //默认按行排序
        this.columnsort = false;
        this.directionsallow = new Array(this.directions[0] == -1 ? false : true, this.directions[1] == -1 ? false : true, this.directions[2] == -1 ? false : true, this.directions[3] == -1 ? false : true);

        this.endwiseCrossturnpage = false;
        this.broadwiseCrossturnpage = false;

        this.areaEndTurnPage = undefined;
        this.areaCorssEvent = undefined;
        this.areaStayInitFocus = false;
        this.pageTurnFlag = 0;
        this.focusfirst = false;
        this.darkfocusstyle = undefined;
        this.darkunfocusstyle = undefined;
        this.darkPage = undefined;
        this.darkIndex = undefined;

        this.nextAreaArray = undefined;//add by wm at 2015.05.27
        for (i = 0; i < this.doms.length; i++) {
            this.doms[i].id = i;
            this.doms[i].area = this;
        }
        this.setDarkfocus = function (darkPage, darkIndex, darkfocusstyle, darkunfocusstyle) {
            this.darkPage = darkPage;
            this.darkIndex = darkIndex;

            if (darkfocusstyle != undefined && !(darkfocusstyle.constructor == Array)) darkfocusstyle = new Array(darkfocusstyle);
            if (darkunfocusstyle != undefined && !(darkunfocusstyle.constructor == Array)) darkunfocusstyle = new Array(darkunfocusstyle);

            this.darkfocusstyle = darkfocusstyle;
            this.darkunfocusstyle = darkunfocusstyle;
            this.doms[darkIndex].focusstyle = darkfocusstyle;
            this.doms[darkIndex].unfocusstyle = darkunfocusstyle;

            if (this.pageobj.curareaid == this.id && this.curindex == this.darkIndex) {
                this.doms[darkIndex].changestyle(this.darkfocusstyle);
            } else {
                this.doms[darkIndex].changestyle(this.darkunfocusstyle);
            }
        };
        this.changeDarkFocus = function () {
            for (var i = 0; i < this.doms.length; i++) { //暗焦点
                if (this.curpage == this.darkPage && i == this.darkIndex) {
                    if (i == this.curindex) {
                        this.doms[i].changestyle(this.darkfocusstyle);
                    } else {
                        this.doms[i].changestyle(this.darkunfocusstyle);
                    }
                    this.doms[i].focusstyle = this.darkfocusstyle;
                    this.doms[i].unfocusstyle = this.darkunfocusstyle;
                } else {
                    this.doms[i].changestyle(this.doms[i].tempunfocusstyle);
                    this.doms[i].focusstyle = this.doms[i].tempfocusstyle;
                    this.doms[i].unfocusstyle = this.doms[i].tempunfocusstyle;
                }
            }
        };
        this.pageTurnAndChangeFocus = function () {
            if (this.pageTurnFlag != 0) {
                var num = this.pageTurnFlag;
                if (this.endwiseCrossturnpage) { //纵向翻页
                    this.changefocus(this.curindex, false);
                    if (num > 0) {
                        if (this.datanum < this.curindex % this.numcolum + 1) { //如果取数之后的数据总数比当前的焦点位置小
                            this.changefocus(this.datanum - 1, true); //聚焦到最后一个
                        } else {
                            this.changefocus(this.curindex % this.numcolum, true); //聚焦到第一行的当前位置
                        }
                    } else {
                        if ((this.datanum - 1) % this.numcolum < this.curindex % this.numcolum) { //如果上一页最后一行的位置比当前的焦点位置小
                            this.changefocus(this.datanum - 1, true); //聚焦到最后一个
                        } else {
                            this.changefocus((Math.ceil(this.datanum / this.numcolum) - 1) * this.numcolum + this.curindex % this.numcolum, true); //聚焦到最后一行的当前位置
                        }
                    }
                } else if (this.broadwiseCrossturnpage) { //横向翻页
                    this.changefocus(this.curindex, false);
                    var curcolumn = Math.ceil((this.curindex + 1) / this.numcolum); //当前行
                    var gocolumn = Math.ceil(this.datanum / this.numcolum); //翻页后的行数
                    if (num > 0) {
                        if (gocolumn < curcolumn) {
                            this.changefocus((gocolumn - 1) * this.numcolum, true); //聚焦最后一行的第一个
                        } else {
                            this.changefocus((curcolumn - 1) * this.numcolum, true); //聚焦当前行的第一个
                        }
                    } else {
                        if (gocolumn <= curcolumn) {
                            this.changefocus(this.datanum - 1, true); //聚焦最后一行的最后一个
                        } else {
                            this.changefocus(curcolumn * this.numcolum - 1, true); //聚焦当前行的最后一个
                        }
                    }
                }
            }
        };
        this.changefocus = function (index, focusornot) {
            if (!!this.changefocusBeforeEvent) {
                if (focusornot) {
                    if (this.changefocusBeforeEvent()) return;
                }
            }
            if (!!this.changeunfocusBeforeEvent) {
                if (!focusornot) {
                    if (this.changeunfocusBeforeEvent()) return;
                }
            }
            this.curindex = index;
            if (!!this.doms[index]) {
                this.doms[index].changefocus(focusornot);
            }
            if (!!this.changefocusAfterEvent) {
                if (focusornot) {
                    if (this.changefocusAfterEvent()) return;
                }
            }
            if (!!this.changeunfocusAfterEvent) {
                if (!focusornot) {
                    if (this.changeunfocusAfterEvent()) return;
                }
            }
        };
        this.setfocuscircle = function (circledir) {
            this.circledir = circledir;
        };
        this.insidemove = function (circle, dirindex, num, cross) {
            this.changefocus(this.curindex, false);
            var nextindex;
            if (this.columnsort) { //按列排序
                if (dirindex == 1 || dirindex == 3) nextindex = !circle ? (this.curindex + num * this.numrow) : (num > 0 ? (cross ? (this.curindex - (this.numcolum - 1) * this.numrow) : (this.curindex + num * this.numrow)) : (cross ? (this.curindex + (this.numcolum - 1) * this.numrow) : (this.curindex + num * this.numrow)));
                else nextindex = !circle ? (this.curindex + num) : (num > 0 ? (cross ? (this.curindex - this.numrow + 1) : (this.curindex + num)) : (cross ? (this.curindex + this.numrow - 1) : (this.curindex + num)));
            } else { //按行排序
                if (dirindex == 0 || dirindex == 2) nextindex = !circle ? (this.curindex + num * this.numcolum) : (num > 0 ? (cross ? (this.curindex - (this.numrow - 1) * this.numcolum) : (this.curindex + num * this.numcolum)) : (cross ? (this.curindex + (this.numrow - 1) * this.numcolum) : (this.curindex + num * this.numcolum)));
                else nextindex = !circle ? (this.curindex + num) : (num > 0 ? (cross ? (this.curindex - this.numcolum + 1) : (this.curindex + num)) : (cross ? (this.curindex + this.numcolum - 1) : (this.curindex + num)));
            }
            this.changefocus(nextindex, true);
            this.curindex = nextindex;
            return -1;
        };
        this.changtop = function () {

        };
        this.getid = function () {
            return this.id;
        };
        this.clearfocus = function () {
            this.changefocus(this.curindex, false);
            this.curindex = 0;
        };
        this.setendwiseCrossturnpage = function (tempvalue) {
            this.endwiseCrossturnpage = tempvalue;
        };
        this.setbroadwiseCrossturnpage = function (tempvalue) {
            this.broadwiseCrossturnpage = tempvalue;
        };
        this.ok = function () {
            if (this.areaOkEvent != undefined) this.areaOkEvent();
            if (this.darkfocusstyle != undefined && this.darkunfocusstyle != undefined) {
                this.darkIndex = this.curindex;
                this.darkPage = this.curpage;
                this.changeDarkFocus();
            }
            this.doms[this.curindex].ok();
        };
        this.areain = function (stableindex, dir) {
            if (!!this.areaInBeforeEvent) {
                if (this.areaInBeforeEvent()) {
                    return true;
                }
            }
            this.pageobj.curareaid = this.id;
            var tmpindex = stableindex;
            if (stableindex != undefined) {
                if (this.datanum != undefined && stableindex > this.datanum - 1) {
                    tmpindex = this.datanum - 1;
                }
            }
            var tempdir = (dir + 2) > 3 ? (dir + 2) % 4 : (dir + 2);
            if (this.staydirarray[tempdir]) {
                tmpindex = this.stayindexarray[tempdir];
            }
            if (this.datanum != undefined && tmpindex > this.datanum - 1) {
                tmpindex = this.datanum - 1;
            }
            this.changefocus(tmpindex, true);
            if (!!this.areaInAfterEvent) {
                if (this.areaInAfterEvent()) {
                    return true;
                } else {
                    return false;
                }
            }
        };
        this.areaout = function (stableindex, dir) {
            if (!!this.areaOutBeforeEvent) {
                if (this.areaOutBeforeEvent()) {
                    return true;
                }
            }
            if (this.staydirarray[dir]) {
                if (this.stayindexarray[dir] != undefined) this.stayindexarray[dir] = stableindex;
                if (this.staystylearray[dir] != undefined) {
                    if (!(this.staystylearray[dir].constructor == Array)) {
                        this.staystylearray[dir] = new Array(this.staystylearray[dir]);
                    }
                    this.doms[this.stayindexarray[dir]].changestyle(this.staystylearray[dir]);
                    this.curindex = stableindex;
                    if (!!this.changeunfocusBeforeEvent) {
                        this.changeunfocusBeforeEvent();
                    } //modified by wuming on 2014.06.16
                } else {
                    this.changefocus(stableindex, false);
                }
            } else {
                this.changefocus(stableindex, false);
            }
            if (!!this.areaOutAfterEvent) {
                if (this.areaOutAfterEvent()) {
                    return true;
                }
            }
            return true;
        };
        this.pageturn = function (num, areaid) {
            if (this.lock) return;
            if (this.curpage != undefined && this.pagecount != undefined) {
                var nextpage = this.curpage + num;
                var nextindexid = 0;
                if (nextpage > this.pagecount) {
                    if (this.cirturnpage) nextpage = 1;
                    else {
                        nextpage = this.pagecount;
                        return;
                    }
                }
                if (nextpage < 1) {
                    if (this.cirturnpage) nextpage = this.pagecount;
                    else {
                        nextpage = 1;
                        return;
                    }
                }
                this.curpage = nextpage;
                if (!!this.areaPageTurnEvent) {
                    this.pageTurnFlag = num;
                    this.areaPageTurnEvent(num);
                }
                if (num > 0 || this.focusfirst) {
                    nextindexid = 0;
                } else {
                    nextindexid = (this.datanum == undefined ? this.doms.length - 1 : this.datanum - 1);
                }
                if (this.darkfocusstyle != undefined && this.darkunfocusstyle != undefined) {
                    this.changeDarkFocus();
                }
                if (this.id == this.pageobj.curareaid) {
                    if (!this.areaStayInitFocus) {
                        this.changefocus(this.curindex, false);
                        this.changefocus(nextindexid, true);
                    }
                }
            }
        };
    }

    /*DOM对象 焦点对象目前*/
    /**
     * doms[i] = new DomData(domsidstring + i, domsfocusstyle, domsunfocusstyle);
     * @constructor
     */
    function DomData(tempdomid, tempfocusstyles, tempunfocusstyles) {
        this.contentdom = undefined;
        this.id = undefined;
        this.htmldomid = tempdomid;
        this.htmldom = undefined;
        this.focusEvent = undefined;
        this.unfocusEvent = undefined;
        this.domOkEvent = undefined;
        this.mylink = "";
        this.canfocus = true;
        this.area = undefined;
        this.tempfocusstyle = tempfocusstyles;
        this.tempunfocusstyle = tempunfocusstyles;
        this.focusstyle = tempfocusstyles;
        this.unfocusstyle = tempunfocusstyles;
        this.nextAreaStation = undefined;//add by wm at 2015.05.27

        this.ok = function () {
            if (this.domOkEvent != undefined) this.domOkEvent();
            this.gotolink();
        };
        this.gotolink = function () {
            if (this.mylink != undefined && this.mylink != "") {
                window.location.href = this.mylink;
            }
        };
        this.changefocus = function (focusornot) {
            if (focusornot) {
                //聚焦事件
                if (!!this.focusEvent) this.focusEvent();
            } else {
                //失焦事件
                if (!!this.unfocusEvent) this.unfocusEvent();
            }
            if (focusornot) {
                //判断焦点是否可以移动上去
                /* var tempindex=this.id;
                 var tempnum=(this.area.datanum==undefined?this.area.doms.length:this.area.datanum);
                 var tempin=-1;
                 for(var i=tempindex;i<tempnum;i++){
                 if(this.area.doms[i].canfocus) {
                 tempin=i;
                 break;
                 }
                 }
                 if(tempin==-1){
                 tempin=0;
                 // this.area.pageobj.move();
                 }
                 if(this.id==tempin){
                 this.changestyle(this.focusstyle);
                 }else{
                 this.area.doms[tempin].changefocus(true);
                 this.area.curindex=tempin;
                 }*/
                this.changestyle(this.focusstyle);
            } else {
                this.changestyle(this.unfocusstyle);
            }
        };

        this.changestyle = function (newstyle) {
            if (newstyle == undefined) return;
            for (j = 0; j < newstyle.length; j++) {
                tmpstyle = newstyle[j];
                if (tmpstyle != undefined) {
                    var tmpproerty = tmpstyle.split(':');
                    if (this.htmldom == undefined) {
                        this.htmldom = $(this.htmldomid);
                    }
                    this.htmldom[tmpproerty[0]] = tmpproerty[1];
                }
            }
        };
    }

    /**
     *
     * @param numrow  行
     * @param numcolum  列
     * @param directions  当前区域离开后的区域
     * @param domsidstring  当前区域ID索引
     * @param domsfocusstyle  选中焦点样式
     * @param domsunfocusstyle  未选中焦点样式
     * @returns {Area}
     * @constructor
     */
    function AreaCreator(numrow, numcolum, directions, domsidstring, domsfocusstyle, domsunfocusstyle) {
        var doms = new Array();
        if (domsfocusstyle != undefined && !(domsfocusstyle.constructor == Array)) domsfocusstyle = new Array(domsfocusstyle);

        if (domsunfocusstyle != undefined && !(domsunfocusstyle.constructor == Array)) domsunfocusstyle = new Array(domsunfocusstyle);

        for (i = 0; i < numrow * numcolum; i++) {
            doms[i] = new DomData(domsidstring + i, domsfocusstyle, domsunfocusstyle);
        }
        var area = new Area(numrow, numcolum, doms, directions);
        return area;
    }

    function PageCreator(curareaid, index, areas) {
        var temppageobj = new PageObj(curareaid, index, areas);
        if (curareaid != undefined && index != undefined && areas != undefined) {
            temppageobj.areas[curareaid].changefocus(index, true);
            temppageobj.curareaid = curareaid;
        }
        window['WkEpg'].pageobj = temppageobj;
        return temppageobj;
    }

    var userNavigator = navigator.userAgent.toLowerCase();

    if (userNavigator.indexOf("ztebw") > -1 || userNavigator.indexOf("ipanel") > -1) document.onkeypress = keyDown;
    else document.onkeydown = keyDown;

    function keyDown() {
        // JScript 文件
        var KEY_TV_IPTV = 1290;
        var KEY_POWEROFF = 1291;
        var KEY_SUBTITLE = 1292;
        var KEY_BOOKMARK = 1293;
        var KEY_PIP = 1294;
        var KEY_LOCAL = 1295;
        var KEY_REFRESH = 1296;
        var KEY_SETUP = 282;
        var KEY_HOME = 292;
        var KEY_BACK = 8;
        var KEY_DEL = 8;
        var KEY_ENTER = 13;
        var KEY_OK = 13;
        var KEY_HELP = 284;
        var KEY_LEFT = 37;
        var KEY_UP = 38;
        var KEY_RIGHT = 39;
        var KEY_DOWN = 40;
        var KEY_PAGEUP = 33;
        var KEY_PAGEDOWN = 34;
        var KEY_0 = 48;
        var KEY_1 = 49;
        var KEY_2 = 50;
        var KEY_3 = 51;
        var KEY_4 = 52;
        var KEY_5 = 53;
        var KEY_6 = 54;
        var KEY_7 = 55;
        var KEY_8 = 56;
        var KEY_9 = 57;

        var KEY_CHANNELUP = 257;
        var KEY_CHANNELDOWN = 258;
        var KEY_VOLUP = 259;
        var KEY_VOLDOWN = 260;
        var KEY_MUTE = 261;
        var KEY_PLAY = 263;
        var KEY_PAUSE = 263;
        var KEY_SEEK = 271;
        var KEY_SWITCH = 280;
        var KEY_FAVORITE = 281;
        var KEY_AUDIOCHANNEL = 286;
        var KEY_IME = 283;
        var KEY_FASTFORWARD = 264;
        var KEY_FASTREWIND = 265;
        var KEY_SEEKEND = 266;
        var KEY_SEEKBEGIN = 267;
        var KEY_STOP = 270;
        var KEY_MENU = 290;
        var KEY_RED = 275;
        var KEY_GREEN = 276;
        var KEY_YELLOW = 277;
        var KEY_BLUE = 278;
        var KEY_STAR = 106;
        var KEY_SHARP = 105;
        var KEY_F1 = 291;
        var KEY_F2 = 292;
        var KEY_F3 = 293;
        var KEY_F4 = 294;
        var KEY_F5 = 295;
        var KEY_F6 = 296;

        //事件 规范是0x300
        var KEY_EVENT = 768;
        var key_code = event.which ? event.which : event.keyCode;

        if (window['WkEpg'].pageobj == undefined) return;
        switch (key_code) {
            case 48:
            case KEY_0:
                window['WkEpg'].pageobj.numType(0);
                break;
            case 49:
            case KEY_1:
                window['WkEpg'].pageobj.numType(1);
                break;
            case 50:
            case KEY_2:
                window['WkEpg'].pageobj.numType(2);
                break;
            case 51:
            case KEY_3:
                window['WkEpg'].pageobj.numType(3);
                break;
            case 52:
            case KEY_4:
                window['WkEpg'].pageobj.numType(4);
                break;
            case 53:
            case KEY_5:
                window['WkEpg'].pageobj.numType(5);
                break;
            case 54:
            case KEY_6:
                window['WkEpg'].pageobj.numType(6);
                break;
            case 55:
            case KEY_7:
                window['WkEpg'].pageobj.numType(7);
                break;
            case 56:
            case KEY_8:
                window['WkEpg'].pageobj.numType(8);
                break;
            case 57:
            case KEY_9:
                window['WkEpg'].pageobj.numType(9);
                break;
            case 87:
            //up
            case KEY_UP:
                window['WkEpg'].pageobj.move(0);
                break;
            case 65:
            //left
            case KEY_LEFT:
                window['WkEpg'].pageobj.move(1);
                break;
            case 83:
            //down
            case KEY_DOWN:
                window['WkEpg'].pageobj.move(2);
                break;
            case 68:
            //right
            case KEY_RIGHT:
                //right
                window['WkEpg'].pageobj.move(3);
                break;
            case 13:
            case KEY_OK:
                //enter
                window['WkEpg'].pageobj.ok();
                break;
            case 32:
            // 空格
            case KEY_BACK:
                window['WkEpg'].pageobj.goBack();
                break;
            case 280:
            //中兴
            case 287:
                //同洲
                window['WkEpg'].pageobj.del();
                break;
            case 188:
            case KEY_PAGEUP:
                window['WkEpg'].pageobj.pageTurn(-1);
                break;
            case 190:
            case KEY_PAGEDOWN:
                window['WkEpg'].pageobj.pageTurn(1);
                break;
            case KEY_VOLUP:
                window['WkEpg'].pageobj.pageVolumeUp();
                break;
            case KEY_VOLDOWN:
                window['WkEpg'].pageobj.pageVolumeDown();
                break;
            case KEY_MUTE:
                window['WkEpg'].pageobj.pageVolumeMute();
                break;
            default:
                break;
        }
        return 0;
    }

    window['WkEpg'] = new Object();
    window['WkEpg'].AreaCreator = AreaCreator;
    window['WkEpg'].PageCreator = PageCreator;
    window['WkEpg'].Area = Area;
    window['WkEpg'].getByteLength = getByteLength;
    window['WkEpg'].DomData = DomData;
    window['WkEpg'].getCutedString = getCutedString;
    window['WkEpg'].$ = $;
})();

function $(id) {
    return document.getElementById(id);
}

function msg(m) {
    var d = document.getElementById("msgUrl");
    if (d.childNodes.length > 18) {
        d.removeChild(d.childNodes[0]);
    }
    var c = document.createElement("div");
    c.style.cssText = "word-break:break-all;;padding:2px;width:1000px;";
    d.appendChild(c);
    var n = new Date();
    c.innerHTML += n.getHours() + ":" + n.getMinutes() + ":" + n.getSeconds() + "--->" + m;
}