package com.mlongbo.jfinal.order;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Clear;
import com.mlongbo.jfinal.common.utils.StringUtils;
import com.mlongbo.jfinal.controller.BaseController;
import com.mlongbo.jfinal.model.LogisticsLog;
import com.mlongbo.jfinal.model.Orders;

@Clear
public class OrderDistController extends BaseController {

    static Orders dao = new Orders().dao();
    static LogisticsLog lDao = new LogisticsLog().dao();

    @Clear
    public void index() {

        String requestPara = getPara("RequestData");

        if (!StringUtils.isEmpty(requestPara)) {
            JSONObject jobj = (JSONObject) JSONObject.parse(requestPara);
            System.out.println("jobj==>" + jobj.toString());

            // 先保存到本地，再返回快递鸟接口数据，然后再做系统业务处理

            // 保存物流数据到本地

            String eBusinessID = jobj.getString("EBusinessID");

            String pushTime = jobj.getString("PushTime");

            String count = jobj.getString("Count");

            JSONArray data = (JSONArray) jobj.getJSONArray("Data");

            // 物流信息
            JSONObject data0 = (JSONObject) JSONObject.parse(data.get(0).toString());
            String orderCode = data0.getString("CallBack");

            LogisticsLog logisticsLog = new LogisticsLog();
            logisticsLog.set("eBusinessID", eBusinessID)
            .set("orderId", orderCode)
            .set("pushTime", pushTime)
            .set("count", count)
            .set("data", data.toString())
            .save();
            if(StringUtils.isNotEmpty(orderCode)){
                int orderState = Integer.parseInt(data0.get("State").toString());
                Orders order = dao.findById(orderCode);
                switch (orderState) {
                case 0:// 无轨迹
                    order.set("orderStatus", 4);// 系统状态已打印
                    order.update();
                    break;
                case 1:// 已揽收
                    order.set("orderStatus", 5);// 系统状态已收件
                    order.update();
                    break;
                case 2:// 在途中
                    order.set("orderStatus", 5);// 系统状态快递中
                    order.update();
                    break;
                case 3:// 签收
                    order.set("orderStatus", 6);// 系统状态已收件
                    order.update();
                    break;
                case 4:// 问题件
                    order.set("orderStatus", 7);// 系统状态异常件
                    JSONArray arrTraces = data0.getJSONArray("Traces");
                    String errAcceptStation = arrTraces.getJSONObject(arrTraces.size() - 1).getString("AcceptStation");
                    order.set("reason", errAcceptStation);// 保存异常原因
                    order.update();
                    break;
                }
            }
            // 返回快递鸟接口数据
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            String AcceptTime = df.format(new Date());
            String Traces = "{\"EBusinessID\":\"1281025\",\"UpdateTime\":\"" + AcceptTime + "\",\"Success\": true,\"Reason\": \"\"}"; 
            renderJson(Traces);
        }
    }
}
