/**
 * Created by luogf on 2017/2/22.
 */
function smartAnalysis(input, callback) {
    var orders = input.split("\n"); ///通过回车将各个订单元素分开
    var orderOs = new Array(); ///最终返回的订单对象集合

    orders.forEach(function(each) {
        if (undefined != each && 0 != each.length) {
            var orderTags = deleteNull(orderInfoSplit(each));
            orderTags = deepSpilt(orderTags);
            var phones_nums = new Array();
            for (var j = 0; j < orderTags.length; j++) {
                var posA = searchStrs(orderTags[j], "1");
                for (var k in posA) {
                    var phoneNum = orderTags[j].substr(posA[k], 11);
                    if (isPhoneNum(phoneNum))
                        phones_nums.push(phoneNum);
                }
            }

            var orderO = {
                toPeople : "",
                toContact : "",
                adds : {},
                goods : "",
                goodsNum : "",
                fromPeople : "",
                fromContact : ""
            };

            if (2 == phones_nums.length) {
                var x = -1, y = -1, z = -1;     ///x寄件人所在元素位置，y寄件人电话号码，z收件人电话号码
                for (var j = 0; j < orderTags.length; j++) {
                    if (-1 < orderTags[j].indexOf("发") || -1 < orderTags[j].indexOf("寄")) {
                        var shipper_info =
                            orderTags[j].substring((orderTags[j].indexOf("发") || orderTags[j].indexOf("寄")));
                        if (-1 < shipper_info.indexOf(phones_nums[0])) {
                            x = j;
                            y = 0;
                            z = 1;
                            orderO.fromPeople = getFromPeople(1, shipper_info, phones_nums[0]);
                            break;
                        } else if (-1 < shipper_info.indexOf(phones_nums[1])) {
                            x = j;
                            y = 1;
                            z = 0;
                            orderO.fromPeople = getFromPeople(1, shipper_info, phones_nums[1]);
                            break;
                        } else if (-1 < orderTags[j + 1].indexOf(phones_nums[0])) {
                            x = j + 1;
                            y = 0;
                            z = 1;
                            if(0 == orderTags[j].substring(3).length)
                                orderO.fromPeople = getFromPeople(3, orderTags[j + 1], phones_nums[0]);
                            else
                                orderO.fromPeople = getFromPeople(2, shipper_info, phones_nums[0]);
                            break;
                        } else if (-1 < orderTags[j + 1].indexOf(phones_nums[1])) {
                            x = j + 1;
                            y = 1;
                            z = 0;
                            if(0 == orderTags[j].substring(3).length)
                                orderO.fromPeople = getFromPeople(3, orderTags[j + 1], phones_nums[0]);
                            else
                                orderO.fromPeople = getFromPeople(2, shipper_info, phones_nums[0]);
                            break;
                        } else {

                        }
                    } else if (-1 < orderTags[j].indexOf("收")) {

                    } else {
                        z = 0;
                        y = 1;
                    }
                }
                if(-1 != x && -1 != y && -1 !=z) {
                    orderO.fromContact = phones_nums[y];
                    if(-1 != x)
                        orderTags[x] = orderTags[x].replace(phones_nums[y], "");
                    matchConsignee(phones_nums[z], orderTags, orderO);
                }
            } else if (1 == phones_nums.length) {
                matchConsignee(phones_nums[0], orderTags, orderO);
            } else {
            }
                arrangeInfo(orderO);
                orderOs.push(orderO);
        }
    });

    callback(null, orderOs);
}

/*
 * 订单信息分割
 * */
function orderInfoSplit(orderInfo) {
    if (-1 < orderInfo.indexOf("，") && 0 > orderInfo.indexOf(" ")) {
        var orderTags = orderInfo.split("，");
        return orderTags;
    } else if (0 > orderInfo.indexOf("，") && -1 < orderInfo.indexOf(" ")) {
        var orderTags = orderInfo.split(" ");
        return orderTags;
    } else if (0 > orderInfo.indexOf("，") && 0 > orderInfo.indexOf(" ")) {
        var orderTags =  new Array();
        orderTags.push(orderInfo);
        return orderTags;
    } else {
        var levelTwo = [];
        var levelOne = orderInfo.split("，");
        for (var i in levelOne) {
            if (0 > levelOne[i].indexOf(" ")) {
                levelTwo.push(levelOne[i]);
            } else {
                var orderTags = levelOne[i].split(" ");
                for (var j in orderTags) {
                    levelTwo.push(orderTags[j]);
                }
            }
        }

        return levelTwo;
    }
}

/*
 * 删除数组中null元素
 * */
function deleteNull(orders) {
    var newOrders = [];
    for (var i in orders) {
        if ("" === orders[i])
            continue;
        newOrders.push(orders[i]);
    }
    return newOrders;
}

/*
* 少见符号的分割例如"。"
* */
function deepSpilt(orderTags) {
    var new_tags = new Array();
    for(var i in orderTags){
        if(-1 < orderTags[i].indexOf("。")){
            var deep_one = orderTags[i].split("。");
            for(var j in deep_one){
                new_tags.push(deep_one[j]);
            }
        } else {
            new_tags.push(orderTags[i]);
        }
    }

    return new_tags;
}

/*
 * 找出当前字符在源字符串中的所有匹配位置
 * */
///找出字符串中指定字符的所有位置
function searchStrs(str, subStr) {
    var positions = new Array();
    var pos = str.indexOf(subStr);
    while (pos > -1) {
        positions.push(pos);
        pos = str.indexOf(subStr, pos + 1);
    }
    return positions;
}

/*
 * 判断字符串是否为电话号码
 * */
function isPhoneNum(temp) {
    if (11 == temp.length && !isNaN(temp)) {
        return true;
    }
    return false;
}

/*
 * 匹配收货人信息
 * */
function matchConsignee(phone_num, orderTags, orderO) {
    orderO.toContact = phone_num;
    for (var j = 0; j < orderTags.length; j++) {
        if (phone_num == orderTags[j]) { ///电话号码为单独元素
            if (0 < j) {
                if (!isAddress(orderTags[(j - 1)])) {
                    orderO.toPeople = getToPeople(orderTags[(j - 1)]);
                } else {
                    ///当地址和姓名在一起仍需分析
                }
            }
        }

        if (-1 < orderTags[j].indexOf(phone_num) && phone_num != orderTags[j]) { ///电话号码不为单独元素
            if ("电" == orderTags[j][0] || "手" == orderTags[j][0] ||
                isPhoneNum(orderTags[j].substr(0, 11))) { ///无其他信息在本字段
                if (isPhoneNum(orderTags[j].substr(0, 11)) && 0 != orderTags[j].substring(11)) {
                    var temp = orderTags[j].substring(11);
                    if ("备注" == temp)
                        temp = "";
                    orderO.toPeople = getToPeople(temp);
                }
                else {
                    if (0 < j) {
                        if (!isAddress(orderTags[(j - 1)])) {
                            orderO.toPeople = getToPeople((orderTags[(j - 1)]));
                        } else {

                        }
                    }
                }
            } else {
                ///姓名和电话在同一元素里
                var index = orderTags[j].indexOf(phone_num);
                var temp = orderTags[j].substr(0, index);
                orderO.toPeople = getToPeople(temp);
            }
        }
        ///获取商品信息
        var goods = matchGoods(orderTags[j]);
        if ("" != goods) {
            orderO.goods = goods;
            if(-1 < orderTags[j].indexOf("件") || -1 < orderTags[j].indexOf("箱") || -1 < orderTags[j].indexOf("份"))
                orderO.goodsNum = getGoodsNum(orderTags[j]);
            else if(0 > orderTags[j].indexOf("件") && 0 > orderTags[j].indexOf("箱") && 0 > orderTags[j].indexOf("份"))
                orderO.goodsNum = getGoodsNum(orderTags[j + 1]);
            else
                orderO.goodsNum = "1";
            orderO.goodsWeight = getGoodsWeight(orderTags[j]);
        }
        if (isAddress(orderTags[j])) {
            orderO.adds = getAddress(orderTags[j]);
        }
    }
}

/*
 * 获取发件人信息
 * */
function getFromPeople(type, temp, phone_num) {
    if(1 == type){
        if(-1 < temp.lastIndexOf("：")){
            return temp.substring((temp.lastIndexOf("：") + 1), temp.indexOf(phone_num));
        } else {
            if(-1 < temp.indexOf("人")) {
                return temp.substring((temp.indexOf("人") + 1), temp.indexOf(phone_num));
            } else
                return temp;
        }
    } else if (2 == type){
        if (-1 < temp.lastIndexOf("：")) {
            return temp.substring((temp.lastIndexOf("：") + 1));
        } else {
            if (-1 < temp.indexOf("人"))
                return temp.substring((temp.indexOf("人") + 1));
            else
                return temp;
        }
    } else if (3 == type){
        var toPeople = temp.substring(0, temp.indexOf(phone_num));
        return toPeople;
    } else {

    }
}

/*
 * 获取收件人信息
 * */
function getToPeople(temp) {
    var people_info = "";
    if (-1 < temp.lastIndexOf("：")) {
        people_info = temp.substring((temp.lastIndexOf("：") + 1));
    } else {
        if (-1 < temp.indexOf("人"))
            people_info = temp.substring((temp.indexOf("人") + 1));
        else
            people_info = temp;
    }
    return people_info.replace("收", "");
}

/*
* 对某些关键字进行过滤
* */
function arrangeInfo(orderO) {
    if(undefined == orderO.goods)
        orderO.goods = "";
    if(undefined == orderO.goodsWeight)
        orderO.goodsWeight = "1";
    if(undefined != orderO.adds) {
        if (undefined == orderO.adds.province)
            orderO.adds.province = "";
        if (undefined == orderO.adds.city)
            orderO.adds.city = "";
        if (undefined == orderO.adds.area)
            orderO.adds.area = "";
        if (undefined == orderO.adds.detail)
            orderO.adds.detail = "";
    }
    if(undefined == orderO.goodsNum || 0 == orderO.goodsNum.length)
        orderO.goodsNum = "1";
    if(-1 < orderO.fromPeople.indexOf(":"))
        orderO.fromPeople = orderO.fromPeople.replace(":", "");
    if(-1 < orderO.toPeople.indexOf(":"))
        orderO.toPeople = orderO.toPeople.replace(":", "");
    if(undefined != orderO.adds.detail) {
        if (-1 < orderO.adds.detail.indexOf("。")) {
            orderO.adds.detail = orderO.adds.detail.substring(0, orderO.adds.indexOf("。"));
        }
        if(-1 < orderO.adds.detail.indexOf(orderO.toContact))
            orderO.adds.detail = orderO.adds.detail.replace(orderO.toContact, "");
    }
    if(8 < orderO.toPeople.length)
        orderO.toPeople = "";
    if(-1 < orderO.toPeople.indexOf("电话"))
        orderO.toPeople = orderO.toPeople.replace("电话", "");
}
