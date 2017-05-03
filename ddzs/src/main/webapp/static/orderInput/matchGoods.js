///获取商品名称
function matchGoods(temp) {
    if(-1 < temp.indexOf("粑粑柑"))
        return "粑粑柑";
    else if(-1 < temp.indexOf("春见"))
        return "春见";
    else if(-1 < temp.indexOf("丑橘"))
        return "丑橘";
    else if(-1 < temp.indexOf("爱媛"))
        return "爱媛";
    else if(-1 < temp.indexOf("浦江椪柑"))
        return "浦江椪柑";
    else if(-1 < temp.indexOf("碰柑"))
        return "碰柑";
    else if(-1 < temp.indexOf("椪柑"))
        return "椪柑";
    else if(-1 < temp.indexOf("柚子"))
        return "柚子";
    else if(-1 < temp.indexOf("红心柚"))
        return "红心柚";
    else if(-1 < temp.indexOf("纽荷尔"))
        return "纽荷尔";
    else if(-1 < temp.indexOf("纽荷尔橙"))
        return "纽荷尔橙";
    else if(-1 < temp.indexOf("红心蜜柚"))
        return "红心蜜柚";
    else if(-1 < temp.indexOf("琵琶果"))
        return "枇杷果";
    else if(-1 < temp.indexOf("琵琶"))
        return "琵琶";
    else if(-1 < temp.indexOf("丑八怪"))
        return "丑八怪";
    else if(-1 < temp.indexOf("黄心猕猴桃"))
        return "黄心猕猴桃";
    else
        return "";
}
///获取商品重量
function getGoodsWeight(temp) {
    temp = numTransfer(temp);       ///将商品字段中的中文数字转换成阿拉伯数字
    var goods_num = "";
    var tempNoNum = temp.replace(/\d+/g,"");        ///删除商品中的数字
    if(-1 < temp.indexOf("斤")) {
        var index = tempNoNum.indexOf("斤");
        if(0 < index) {
            var startChar = tempNoNum.substr(index - 1, 1);        ///获取斤字前面的字符
            var index1 = temp.indexOf(startChar);
            goods_num = temp.substring(index1 + 1, temp.indexOf("斤"));
        } else {
            goods_num = temp.substring(0, temp.indexOf("斤"));
        }
    } else {
        goods_num = "1";
    }
    return goods_num;
}

///中英文数字兑换
function numTransfer(temp) {
    if(-1 < temp.indexOf("一"))
        temp = temp.replace(/一/g, "1");
    if(-1 < temp.indexOf("二"))
        temp = temp.replace(/二/g, "2");
    if(-1 < temp.indexOf("三"))
        temp = temp.replace(/三/g, "3");
    if(-1 < temp.indexOf("四"))
        temp = temp.replace(/四/g, "4");
    if(-1 < temp.indexOf("五"))
        temp = temp.replace(/五/g, "5");
    if(-1 < temp.indexOf("六"))
        temp = temp.replace(/六/g, "6");
    if(-1 < temp.indexOf("七"))
        temp = temp.replace(/七/g, "7");
    if(-1 < temp.indexOf("八"))
        temp = temp.replace(/八/g, "8");
    if(-1 < temp.indexOf("九"))
        temp = temp.replace(/九/g, "9");
    if(-1 < temp.indexOf("十")) {
        temp = temp.replace(/十/g, tenTransfer(temp));
    }
    return temp;
}

///判断十所在位置
function tenTransfer(temp) {
    var index = temp.indexOf("十");
    if(0 < index){
        var previousChar = temp.substr(index - 1, 1);
        var followingChar = temp.substr(index + 1, 1);
        if(!isNaN(previousChar) && !isNaN(followingChar)){
            return "";
        } else if (!isNaN(previousChar) && isNaN(followingChar)){
            return "0";
        } else if (isNaN(previousChar) && isNaN(followingChar)){
            return "10";
        } else {
            return "1";
        }
    } else {
        var followingChar = temp.substr(index + 1, 1);
        if(isNaN(followingChar))
            return "10";
        else
            return "1";
    }
}

///获取商品数量
function getGoodsNum(temp) {
    if(undefined == temp)
        return "1";
    temp = numTransfer(temp);       ///将商品字段中的中文数字转换成阿拉伯数字
    var goods_num = "";
    var tempNoNum = temp.replace(/\d+/g,"");        ///删除商品中的数字
    var keyWords = ["箱", "件", "份"];
    for(var i in keyWords) {
        if (-1 < temp.indexOf(keyWords[i])) {
            var index = tempNoNum.indexOf(keyWords[i]);
            if (0 < index) {
                var startChar = tempNoNum.substr(index - 1, 1);        ///获取特殊字前面的字符
                var index1 = temp.indexOf(startChar);
                goods_num = temp.substring(index1 + 1, temp.indexOf(keyWords[i]));
            } else {
                goods_num = temp.substring(0, temp.indexOf(keyWords[i]));
            }
            break;
        }
    }
    if(0 == goods_num.length)
        goods_num = "1";
    return goods_num;
}