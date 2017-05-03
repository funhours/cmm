package com.mlongbo.jfinal.order;

import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.mlongbo.jfinal.common.utils.DateUtils;
import com.mlongbo.jfinal.common.utils.RandomUtils;
import com.mlongbo.jfinal.model.Orders;
import com.mlongbo.jfinal.model.OrdersSearch;
import com.mlongbo.jfinal.model.User;
import com.mlongbo.jfinal.model.Utility;
import com.mlongbo.jfinal.vo.AjaxResult;


public class ApiOrderController extends Controller {
    private OrdersSearch ordersSearch = new OrdersSearch();
    private final AjaxResult result = new AjaxResult();
    private int page = 1;
    static User userDao = new User().dao();
    static Orders dao = new Orders().dao();
    
    
    /**
     * 
     * @Description (所有系统订单)
     */
    public void allOrder(){
        String userId = getPara("userId");
        
        User user = userDao.findById(userId);
        int userType = user.getInt("userType");
        Page<Orders> allOrdersPage = new Page<Orders>();
        if(userType == 1 ){
            allOrdersPage = dao.paginate(getParaToInt("p", 1), 10,"select * ","from orders where 1=1  order by creationDate");
            setAttr("allOrdersPage", allOrdersPage);
        }else if(userType == 2){
            allOrdersPage = dao.paginate(getParaToInt("p", 1), 10,"select * ","from orders where 1=1 and relationUser = '"+userId+"' order by creationDate");
            setAttr("allOrdersPage", allOrdersPage);
        }else if(userType == 3 || userType == 4){
            allOrdersPage = dao.paginate(getParaToInt("p", 1), 10,"select * ","from orders where 1=1 and orderEntry = '"+user.getStr("nickName").trim()+"' order by creationDate");
            setAttr("allOrdersPage", allOrdersPage);
            
        }
        
        
        renderJson(allOrdersPage);
    }
    
    
    /**
     * 
     * @Description (系统订单查询)
     */
   public void orderSearchByPara(){
       String sqlCondition = " 1=1";
       Map<String,String[]> paraMap ;
       String userId = getPara("userId");
       User user = new User().dao().findById(userId);
       String userName = user.getStr("nickName");
       String[] paraStr;
       paraMap = getParaMap();
       paraStr = Utility.makePara(ordersSearch, "ordersSearch", paraMap);
       sqlCondition += paraStr[0];
       Orders orders = Orders.dao.findFirst("select sum(productPrice) as totalPrice,count(*) as orderCount from orders ord where" + sqlCondition + " and orderEntry = '"+userName+"' order by creationDate desc");
       Page<Orders> orderSearchPage = Orders.dao.paginate(page, 15, "select *", "from orders ord where" + sqlCondition + " and orderEntry = '"+userName+"' order by creationDate desc");
       setAttr("searchCon", paraStr[1]);
        
       result.setData(orders); 
       result.setData(orderSearchPage); 
       renderJson(result);
   }
   
   /**
    * 
    * @Description (智能录入订单)
    */
   public void addOrder(){
       String params = getPara("params");
       String userId = getPara("userId");
       User user = new User().dao().findFirst("select * from t_user where userId = '"+ userId+"'" );
       User relationUser = new User().dao().findFirst(" select * from t_user where userId = '" + user.getStr("userId")+"'");
       JSONObject obj = JSONObject.parseObject(params);
       JSONArray jobj = JSONObject.parseArray(obj.getString("reserveOrderNoList"));
       int successCount = 0;
       int failCount = 0;
       int orderStatus = 9;//订单状态：供应商自己和员工录入不用审核，代理商录入订单需要审核
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
               orders.set("orderId", RandomUtils.randomCustomUUID())
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
