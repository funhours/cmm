package com.mlongbo.jfinal.order;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mlongbo.jfinal.controller.BaseController;
import com.mlongbo.jfinal.model.LogisticsLog;
import com.mlongbo.jfinal.model.Orders;


public class OrderDistController extends BaseController {
    
    static Orders dao = new Orders().dao();
    static LogisticsLog lDao = new LogisticsLog().dao();
    
    public void index(){
        
        String requestPara = getPara("RequestData");
        
        JSONObject jobj = (JSONObject) JSONObject.parse(requestPara);
        
        //先保存到本地，再返回快递鸟接口数据，然后再做系统业务处理
        if(jobj!=null){
            
            // 保存物流数据到本地
            
            String eBusinessID = jobj.getString("EBusinessID");
            
            String pushTime = jobj.getString("PushTime");
            
            String count = jobj.getString("Count");
            
            String data = jobj.getString("Data");
            
            LogisticsLog logisticsLog = new LogisticsLog();
            logisticsLog.set("eBusinessID", eBusinessID)
            .set("pushTime", pushTime)
            .set("count", count)
            .set("data", data).save();
            
            //返回快递鸟接口数据
            String AcceptTime = System.currentTimeMillis()+"";
            
            String Traces = "{'EBusinessID':'1281025','UpdateTime':'"+AcceptTime+"','Success': true,'Reason': '}";
            
            renderJson(Traces);
        }
        
        
        //物流信息
        JSONArray data = (JSONArray) jobj.get("Data");
        JSONObject lastState = (JSONObject) JSONObject.parse(data.getString(data.size()-1));
        String orderCode = lastState.getString("OrderCode");
        int orderState = lastState.getInteger("State");
        Orders order = dao.findById(orderCode);
        switch (orderState) {
        case 0://无轨迹
            order.set("orderStatus", 4);//系统状态已打印
            break;
        case 1://已揽收
            order.set("orderStatus", 5);//系统状态已收件
            break;
        case 2://在途中
            order.set("orderStatus", 5);//系统状态快递中
            break;
        case 3://签收
            order.set("orderStatus", 6);//系统状态已收件
            break;
        case 4://问题件
            order.set("orderStatus", 7);//系统状态异常件
            order.set("reason", lastState.getString("Reason"));//保存异常原因
            break;

        default:
            order.update();
            break;
        }

    }

}
