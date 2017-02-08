function AJAX_OBJ(_url, _callback) {
    this.url = _url;
    this.callback = _callback;
    this.createXMLHttp = function () {
        var xmlHttp = {};
        if (window.XMLHttpRequest) {
            xmlHttp = new XMLHttpRequest();
        }
        else if (window.ActiveXObject) {
            xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
        }
        return xmlHttp;
    };
    this.sendRequese = function () {
        var xmlHttp = this.createXMLHttp();
        xmlHttp.open("GET", this.url, true);
        xmlHttp.onreadystatechange = callbackFun;
        xmlHttp.send(null);
        var self = this;
        function callbackFun() {
            if (xmlHttp.readyState == 4) {
                if (xmlHttp.status == 200) {
                    var text = xmlHttp.responseText;
                    self.callback(text);
                } else if (xmlHttp.status != 200) {
                    self.callback(-1);
                }
            }
        }
    }
}

function AJAX_OBJ_POST(_url, postData, _callback) {
    this.url = _url;
    this.callback = _callback;
    postData = (function (obj) {//转成post需要的字符串
        var str = "";
        for (var prop in obj) {
            str += prop + "=" + obj[prop] + "&"
        }
        return str;
    })(postData);
    this.createXMLHttp = function () {
        var xmlHttp = {};
        if (window.XMLHttpRequest) {
            xmlHttp = new XMLHttpRequest();
        }
        else if (window.ActiveXObject) {
            xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
        }
        return xmlHttp;
    };
    this.sendRequese = function () {
        var xmlHttp = this.createXMLHttp();
        var self = this;
        xmlHttp.open("POST", this.url, true);
        xmlHttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        xmlHttp.onreadystatechange = callbackFun;
        xmlHttp.send(postData);
        function callbackFun() {
            if (xmlHttp.readyState == 4) {
                if (xmlHttp.status == 200) {
                    var text = xmlHttp.responseText;
                    self.callback(text);
                } else if (xmlHttp.status != 200) {
                    self.callback(-1);
                }
            }
        }
    };
}