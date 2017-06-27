package com.mlongbo.jfinal.logistics;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Order;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Clear;
import com.mlongbo.jfinal.api.KdniaoTrackQueryAPI;
import com.mlongbo.jfinal.common.utils.StringUtils;
import com.mlongbo.jfinal.controller.BaseController;
import com.mlongbo.jfinal.model.ExtQuery;
import com.mlongbo.jfinal.model.Orders;
import com.mlongbo.jfinal.model.User;
import com.mlongbo.jfinal.vo.AjaxResult;

@Clear
public class LogisticsInquiryController extends BaseController {
	private final AjaxResult result = new AjaxResult();
	User udao = new User().dao();
	Orders odao = new Orders().dao();
	ExtQuery edao = new ExtQuery().dao();
	public void index() {
	    String linkKey = getPara("linkKey");
        //获取外部查询页配置
        ExtQuery eQuery = edao.findFirst("select * from ext_query where 1=1 and linkKey = '"+linkKey+"'");
        setAttr("eQuery", eQuery);
        setAttr("linkKey", linkKey);
        
	    render("LogisticsInquiry.html");
	}
	
	public void search(){
	    String no = getPara("no");
	    if(StringUtils.isNotEmpty(no)){
	        Orders order = odao.findById(no);
	        List<JSONObject> tList = new ArrayList<JSONObject>();
	        List<Orders> orders = new ArrayList<Orders>();
	        String traceResult = getOrderTraces(order.getStr("shipperCode"),order.getStr("courierNumber"));
            JSONObject jobj = JSONObject.parseObject(traceResult);
            tList.add(jobj);
            orders.add(order);
            setAttr("searchKey", order.getStr("recipient"));
            setAttr("count", 1);
            setAttr("orders", orders);
            setAttr("tList", tList);
            render("LogisticsDetail.html");
            return;
	    }
	    String searchKey = getPara("searchKey");
	    String searchDate = getPara("searchDate");
	    String linkKey = getPara("linkKey");
	    
	    if(StringUtils.isEmpty(searchKey) || StringUtils.isEmpty(searchDate) || StringUtils.isEmpty(linkKey)){
	        renderNull();
	    }else{
	      //获取外部查询页配置
	        ExtQuery eQuery = edao.findFirst("select * from ext_query where 1=1 and linkKey = '"+linkKey+"'");
	        String userId = eQuery.get("userId");
	        
	        String[] date = searchDate.split("-");
	        
	        String sql = "select * from orders where (recipient = '"+searchKey+"' or recipientTel = '"+searchKey+"' or courierNumber = '"+searchKey+"') and relationUser = '"+userId+"' and year = '"+date[0]+"' and month = '"+date[1]+"' and day = '"+date[2]+"' order by creationDate";
	        List<Orders> orders = odao.find(sql);
	        
	        List<JSONObject> jArray = new ArrayList<JSONObject>();
	        
	        //查询物流
	        for (Orders ord : orders) {
	            JSONObject jobj = new JSONObject();
	            jobj.put("shipperCode", ord.get("shipperCode"));
	            jobj.put("courierNumber", ord.get("courierNumber"));
	            jArray.add(jobj);
	        }
	        
	        List<JSONObject> tList = new ArrayList<JSONObject>();
	        for (int i = 0; i < jArray.size(); i++) {
	           String shipperCode = jArray.get(i).getString("shipperCode");
	           String courierNumber = jArray.get(i).getString("courierNumber");
	           if(!"".equals(shipperCode)&& shipperCode != null && !"".equals(courierNumber) && courierNumber != null){
	               String traceResult = getOrderTraces(shipperCode,courierNumber);
	               JSONObject jobj = JSONObject.parseObject(traceResult);
	               tList.add(jobj);
	           }
	        }
	        
	        //封装返回消息
	        
	        setAttr("searchKey", searchKey);
	        setAttr("count", orders.size());
	        setAttr("orders", orders);
	        setAttr("tList", tList);
	        render("LogisticsDetail.html");
	    }
	    
	    
        
	}
	
	/**
	 * 
	 * @Description (物流查询)
	 */
	public String getOrderTraces(String shipperCode,String logisticCode){
	    String traceResult = "";
	    try {
    	    KdniaoTrackQueryAPI api = new KdniaoTrackQueryAPI();
            traceResult = api.getOrderTracesByJson(shipperCode, logisticCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
	    return(traceResult);
	}
	
	/**
	 * 
	 * @Description (根据订单号物流查询)
	 */
	public void getOrderTracesByOrderId(){
	    String orderId = getPara("orderId");
	    Orders order = odao.findById(orderId);
	    try {
	        
	        String shipperCode = order.get("shipperCode");
	        String courierNumber = order.get("courierNumber");
	        KdniaoTrackQueryAPI api = new KdniaoTrackQueryAPI();
	        String traceResult = "";
	        traceResult = api.getOrderTracesByJson(shipperCode, courierNumber);
	        result.success(traceResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
        renderJson(result);
	}
	    
}
