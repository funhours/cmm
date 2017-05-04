/*
 * 判断当前字符串是否为地址
 * */
function isAddress(temp) {
    if (-1 < temp.indexOf("省") || -1 < temp.indexOf("市")
        || -1 < temp.indexOf("区") || -1 < temp.indexOf("县")) {
        return true;
    }
    return false;
}

/*
* 获取地址详细信息
* */
function getAddress(orderTag) {
    if (-1 < orderTag.indexOf("收货人："))
        orderTag = orderTag.replace("收货人：", "");
    var adds = {};
    var city_key = getCityKey(orderTag);
    adds.province = getProvince(orderTag);
    
    if(0 != adds.province.length) {///匹配到了省份信息
        if (0 != city_key.length) {
            if (-1 < orderTag.indexOf("省")) {
                adds.city =
                    orderTag.substring((orderTag.indexOf("省") + 1), orderTag.indexOf(city_key));
            } else {
                if ("北京" == adds.province || "天津" == adds.province
                    || "上海" == adds.province || "重庆" == adds.province)
                    adds.city = adds.province;
                else
                    adds.city =
                        orderTag.substring((orderTag.indexOf(adds.province) + adds.province.length), orderTag.indexOf(city_key));
            }
            var area_info = "";
            if (-1 < orderTag.indexOf("区")) {
                area_info = "区";
                adds.area =
                    orderTag.substring((orderTag.indexOf(city_key) + 1), orderTag.indexOf(area_info));
                adds.detail = orderTag.substring((orderTag.indexOf(area_info) + 1));
            } else if (-1 < orderTag.indexOf("县")) {
                area_info = "县";
                adds.area =
                    orderTag.substring((orderTag.indexOf(city_key) + 1), orderTag.indexOf(area_info));
                adds.detail = orderTag.substring((orderTag.indexOf(area_info) + 1));
            } else {
                adds.area = "";
                adds.detail = orderTag.substring((orderTag.indexOf(city_key) + 1));
            }
        } else {    ///未找到关键字市
            if (isDirectCity(adds["province"])) {
                adds["city"] = adds["province"];
                var detailInfo = orderTag.replace(adds["city"], "");
                var areaKeyIndex = getAreaIndex(detailInfo);
                if (-1 < areaKeyIndex){
                    adds["area"] = detailInfo.substring(0, areaKeyIndex);
                    adds["detail"] = detailInfo.substring(areaKeyIndex + 1);
                }
            } else {    ///不是直辖市
                if (-1 < orderTag.indexOf("省")){
                    var detailInfo = orderTag.replace(adds["province"] + "省", "");
                    var areaKeyIndex = getAreaIndex(detailInfo);
                    adds["area"] = detailInfo.substring(0, areaKeyIndex);
                    adds["detail"] = detailInfo.substring(areaKeyIndex + 1);
                }
            }
        }
    } else { ///没有省份信息
        if (0 !== city_key.length ){
            adds["city"] = orderTag.substring(0, orderTag.indexOf(city_key));
            var detailInfo = orderTag.substring(orderTag.indexOf(city_key) + 1);
            var areaKeyIndex = getAreaIndex(detailInfo);
            adds["area"] = detailInfo.substring(0, areaKeyIndex);
            adds["detail"] = detailInfo.substring(areaKeyIndex + 1);
        }
    }
    return adds;
}

/*
* 获取区或者县所在位置
* @getAreaIndex
* */
function getAreaIndex(temp) {
    if (-1 < temp.indexOf("区"))
        return temp.indexOf("区");
    else if (-1 < temp.indexOf("县"))
        return temp.indexOf("县");
    else if (-1 < temp.indexOf("州"))
        return temp.indexOf("州");
    else
        return -1;
}

/*
* 是否是直辖市
* isDirectCity
* */
function isDirectCity(temp) {
    if (-1 < temp.indexOf("重庆")
        || -1 < temp.indexOf("北京")
        || -1 < temp.indexOf("上海")
        || -1 < temp.indexOf("天津"))
        return true;
    else
        return false;
}

/*
* 获取次级地址关键字
* @getCityKey
* */
function getCityKey(orderTag) {
    var city_key = "";
    if(-1 < orderTag.indexOf("州") && 0 > orderTag.indexOf("市"))
        city_key = "州";
    else if(-1 < orderTag.indexOf("市") && 0 > orderTag.indexOf("州"))
        city_key = "市";
    else if(0 > orderTag.indexOf("市") && 0 > orderTag.indexOf("州"))
        city_key = "";
    else
        city_key = "市";
    return city_key;
}

/*
* 获取省份信息
* */
function getProvince(orderTag){
    var province = "";
    if(-1 < orderTag.indexOf("重庆"))
        province = "重庆";
    else if(-1 < orderTag.indexOf("北京"))
        province = "北京";
    else if(-1 < orderTag.indexOf("上海"))
        province = "上海";
    else if(-1 < orderTag.indexOf("天津"))
        province = "天津";
    else if(-1 < orderTag.indexOf("香港"))
        province = "香港";
    else if(-1 < orderTag.indexOf("澳门"))
        province = "澳门";
    else if(-1 < orderTag.indexOf("台湾"))
        province = "台湾";
    else if(-1 < orderTag.indexOf("四川"))
        province = "四川";
    else if(-1 < orderTag.indexOf("河北"))
        province = "河北";
    else if(-1 < orderTag.indexOf("河南"))
        province = "河南";
    else if(-1 < orderTag.indexOf("陕西"))
        province = "陕西";
    else if(-1 < orderTag.indexOf("山西"))
        province = "山西";
    else if(-1 < orderTag.indexOf("山东"))
        province = "山东";
    else if(-1 < orderTag.indexOf("甘肃"))
        province = "甘肃";
    else if(-1 < orderTag.indexOf("辽宁"))
        province = "辽宁";
    else if(-1 < orderTag.indexOf("吉林"))
        province = "吉林";
    else if(-1 < orderTag.indexOf("黑龙江"))
        province = "黑龙江";
    else if(-1 < orderTag.indexOf("云南"))
        province = "云南";
    else if(-1 < orderTag.indexOf("贵州"))
        province = "贵州";
    else if(-1 < orderTag.indexOf("福建"))
        province = "福建";
    else if(-1 < orderTag.indexOf("广东"))
        province = "广东";
    else if(-1 < orderTag.indexOf("海南"))
        province = "海南";
    else if(-1 < orderTag.indexOf("湖北"))
        province = "湖北";
    else if(-1 < orderTag.indexOf("湖南"))
        province = "湖南";
    else if(-1 < orderTag.indexOf("江西"))
        province = "江西";
    else if(-1 < orderTag.indexOf("安徽"))
        province = "安徽";
    else if(-1 < orderTag.indexOf("江苏"))
        province = "江苏";
    else if(-1 < orderTag.indexOf("浙江"))
        province = "浙江";
    else if(-1 < orderTag.indexOf("青海"))
        province = "青海";
    else if(-1 < orderTag.indexOf("新疆"))
        province = "新疆";
    else if(-1 < orderTag.indexOf("西藏"))
        province = "西藏";
    else if(-1 < orderTag.indexOf("广西"))
        province = "广西";
    else if(-1 < orderTag.indexOf("内蒙古"))
        province = "内蒙古";
    else if(-1 < orderTag.indexOf("宁夏"))
        province = "宁夏";
    else
        province = "";
    return province;
}