package com.mlongbo.jfinal.order;

import java.util.Calendar;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mlongbo.jfinal.common.utils.DateUtils;
import com.mlongbo.jfinal.common.utils.RandomUtils;
import com.mlongbo.jfinal.controller.BaseController;
import com.mlongbo.jfinal.model.Orders;
import com.mlongbo.jfinal.model.OrdersJd;
import com.mlongbo.jfinal.model.OrdersTb;
import com.mlongbo.jfinal.model.User;
import com.mlongbo.jfinal.vo.AjaxResult;

public class SmartInputController extends BaseController {
    private final AjaxResult result = new AjaxResult();
    static Orders dao = new Orders().dao();
    static OrdersTb daoTb = new OrdersTb().dao();
    static OrdersJd daoJd = new OrdersJd().dao();
    public void index() {
        render("index.html");
    }
    
    public void addOrder(){
        Calendar now = Calendar.getInstance();
        String params = getPara("params");
        String userId = getLoginUserId();
        User user = new User().dao().findFirst("select * from t_user where userId = '"+ userId+"'" );
        User relationUser = new User().dao().findFirst(" select * from t_user where userId = '" + user.getStr("userId")+"'");
        JSONObject obj = JSONObject.parseObject(params);
        JSONArray jobj = JSONObject.parseArray(obj.getString("reserveOrderNoList"));
        int successCount = 0;
        int failCount = 0;
        int orderStatus = 1;//不需要审核阶段，直接进入未分配阶段
        if(user.getInt("userType") == 4 ){
            orderStatus = 1;
        }
        for (int i = 0; i < jobj.size(); i++) {
            try {
                JSONObject orderObj = JSONObject.parseObject(jobj.get(i).toString());
                String productName = orderObj.get("productName").toString();
                String productCount = orderObj.get("productCount").toString();
                String productPrice = orderObj.get("productPrice").toString();
                String productWeight = orderObj.get("productWeight").toString();
                String recipient = orderObj.get("recipient").toString();
                String recipientTel = orderObj.get("recipientTel").toString();
                String provinceName = orderObj.get("provinceName").toString();
                String cityName = orderObj.get("cityName").toString();
                String expAreaName = orderObj.get("expAreaName").toString();
                String recipientAddress = orderObj.get("recipientAddress").toString();
                String shipper = orderObj.get("shipper").toString();
                String shipperTel = orderObj.get("shipperTel").toString();
                String remarks = orderObj.get("remarks").toString();
            
                Orders orders = new Orders();
                orders.set("orderId", now.get(Calendar.YEAR)+""+(now.get(Calendar.MONTH) + 1)+""+now.get(Calendar.DAY_OF_MONTH)+"" + RandomUtils.randomString(5)+"")
                .set("productName",productName)
                .set("productCount",productCount)
                .set("productPrice",productPrice)
                .set("productWeight",productWeight)
                .set("recipient",recipient)
                .set("recipientTel",recipientTel)
                .set("provinceName",provinceName)
                .set("cityName",cityName)
                .set("expAreaName",expAreaName)
                .set("recipientAddress",recipientAddress)
                .set("shipper",shipper)
                .set("shipperTel",shipperTel)
                .set("orderEntry",user.getStr("nickName"))
                .set("remarks",remarks)
                .set("orderStatus",orderStatus)
                .set("relationUser",relationUser.getStr("userId"))
                .set("creationDate",DateUtils.getNowTimeStamp())
                .set("year", now.get(Calendar.YEAR))
                .set("month", (now.get(Calendar.MONTH) + 1))
                .set("day", now.get(Calendar.DAY_OF_MONTH))
                .save();
                successCount++;
            } catch (Exception e) {
                e.printStackTrace();
                failCount++;
            }
         // 返回结果
            JSONObject resultJson = new JSONObject();
            resultJson.put("successCount", successCount);
            resultJson.put("failCount", failCount);
            result.success(resultJson);
            renderJson(result);
        }
    }
}