package com.mlongbo.jfinal.order;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.mlongbo.jfinal.api.KdniaoEOrderApi;
import com.mlongbo.jfinal.common.utils.RandomUtils;
import com.mlongbo.jfinal.common.utils.SMSUtils;
import com.mlongbo.jfinal.controller.BaseController;
import com.mlongbo.jfinal.model.Orders;
import com.mlongbo.jfinal.model.RelationUserDotPara;
import com.mlongbo.jfinal.model.Shipper;
import com.mlongbo.jfinal.model.SmsSendLog;
import com.mlongbo.jfinal.model.SmsStore;
import com.mlongbo.jfinal.model.User;
import com.mlongbo.jfinal.model.esheet.OrderEsheetSenderTemplate;
import com.mlongbo.jfinal.vo.AjaxResult;


public class ESheetController extends BaseController {
    private final AjaxResult result = new AjaxResult();
    static Orders dao = new Orders().dao();
    User udao = new User().dao();
    RelationUserDotPara rudpDao = new RelationUserDotPara().dao();
    OrderEsheetSenderTemplate tempDao = new OrderEsheetSenderTemplate().dao();
    KdniaoEOrderApi eOrderApi = new KdniaoEOrderApi();
    
    public void index(){
        String userId = getLoginUserId();
        Page<Orders> eSheetOrdersPage = dao.paginate(getParaToInt("p", 1), 10,"select * ","from orders where 1=1 and orderStatus = 1 and relationUser = '"+userId+"' order by creationDate");
        setAttr("eSheetOrdersPage", eSheetOrdersPage);
        Page<Orders> eSheetNoPrintOrdersPage = dao.paginate(getParaToInt("p", 1), 10,"select o.*,s.shipperName ","from orders o LEFT JOIN shipper s on o.shipperCode = s.shipperCode where 1=1 and orderStatus = 3 and relationUser = '"+userId+"' order by creationDate");
        setAttr("eSheetNoPrintOrdersPage", eSheetNoPrintOrdersPage);
        User user = udao.findById(userId);
        List<RelationUserDotPara> rudps = rudpDao.find("select * from relation_user_dot_para WHERE userId = '"+userId+"' order by (Case when shipperCode = '"+user.getStr("shipperCode")+"' then 0 else 1 end),id asc" );
        List<Shipper> shipper = new ArrayList<Shipper>();
        for(RelationUserDotPara rudp : rudps){
            Shipper shi = Shipper.dao.findFirst("select * from shipper where 1=1 and shipperCode = '" + rudp.getStr("shipperCode") + "'");
            shipper.add(shi);
        }
        
        setAttr("shipper", shipper);
        
        render("index.html");
    }
    
    /**
     * 
     * @Description (配置页面)
     */
    public void configPage(){
        String userId = getLoginUserId();
        //网点配置
        List<RelationUserDotPara> rudpList = rudpDao.find("select r.*,s.shipperName from relation_user_dot_para r left join shipper s on r.shipperCode = s.shipperCode  where 1=1 and userId = '" + userId + "'");
        setAttr("rudpList", rudpList);
        //默认快递
        String shipperCode = udao.findById(userId).get("shipperCode");
        Shipper shipper = Shipper.dao.findFirst("select * from shipper where 1=1 and shipperCode = '"+shipperCode+"'");
        setAttr("shipper", shipper);
        //发件信息
        String sql = "select * from order_esheet_sender_template where 1=1 and userId = '" + userId + "'";
        OrderEsheetSenderTemplate template = tempDao.findFirst(sql);
        setAttr("template", template);
        
        render("config.html");
    }
    
    /**
     * 
     * @Description (默认快递设置)
     */
    public void toDefaultShipper(){
        String userId = getLoginUserId();
        String sql = "select * from t_user where 1=1 and userId = '" + userId + "'";
        User user = udao.findFirst(sql);
        List<RelationUserDotPara> rudps = rudpDao.find("select * from relation_user_dot_para WHERE userId = '"+userId+"' order by (Case when shipperCode = '"+user.getStr("shipperCode")+"' then 0 else 1 end),id asc" );
        List<Shipper> shipper = new ArrayList<Shipper>();
        for(RelationUserDotPara rudp : rudps){
            Shipper shi = Shipper.dao.findFirst("select * from shipper where 1=1 and shipperCode = '" + rudp.getStr("shipperCode") + "'");
            shipper.add(shi);
        }
        
        setAttr("shipper", shipper);
        render("defaultShipper.html");
    }
    
    /**
     * 
     * @Description (保存默认快递)
     */
    public void saveDShipperPara(){
        boolean saveOk = false;
        
        String userId = getLoginUserId();
        String shipperCode = getPara("shipperCode");
        try {
            String sql = "select * from t_user where 1=1 and userId = '" + userId + "'";
            User user = udao.findFirst(sql);
            saveOk = user.set("shipperCode", shipperCode).update();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(saveOk){
            result.success("保存成功");
        }else{
            result.addError("保存失败");
        }
        renderJson(result);
        
    }
    
    
    /**
     * 
     * @Description (进入网点参数设置页)
     */
    public void dotPara(){
        String userId = getLoginUserId();
        String shipperCode = getPara("shipperCode");
        String sql = "select * from relation_user_dot_para where 1=1 and userId = '" + userId + "' and shipperCode = '" + shipperCode+"'";
        RelationUserDotPara rudp = rudpDao.findFirst(sql);
        setAttr("rudp", rudp);
        render("dotParameter.html");
    }
    
    /**
     * 
     * @Description (保存网点参数)
     */
    public void saveDotPara(){
        boolean saveOk = false;
        
        String customerName = getPara("customerName");
        String customerPwd = getPara("customerPwd");
        String monthCode = getPara("monthCode");
        String userId = getLoginUserId();
        String shipperCode = getPara("shipperCode");
        try {
            String sql = "select * from relation_user_dot_para where 1=1 and userId = '" + userId + "' and shipperCode = '" + shipperCode+"'";
            RelationUserDotPara rudp = rudpDao.findFirst(sql);
            if(rudp != null){
                saveOk = rudp.set("customerName", customerName)
                .set("customerPwd", customerPwd)
                .set("monthCode", monthCode)
                .update();
            }else{
                saveOk = new RelationUserDotPara()
                .set("id", RandomUtils.randomCustomUUID())
                .set("userId", userId)
                .set("shipperCode", shipperCode)
                .set("customerName", customerName)
                .set("customerPwd", customerPwd)
                .set("monthCode", monthCode)
                .save();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(saveOk){
            result.success("保存成功");
        }else{
            result.addError("保存失败");
        }
        renderJson(result);
        
    }
    
    /**
     * 
     * @Description (删除网点配置)
     */
    public void delRudpConfig(){
        String rudpId = getPara("rudpId");
        renderJson(rudpDao.deleteById(rudpId));
    }
    
    
    /**
     * 
     * @Description (进入发货人信息设置页)
     */
    public void toTemplate(){
        String userId = getLoginUserId();
        String sql = "select * from order_esheet_sender_template where 1=1 and userId = '" + userId + "'";
        OrderEsheetSenderTemplate template = tempDao.findFirst(sql);
        setAttr("template", template);
        render("senderTemplate.html");
    }
    
    /**
     * 
     * @Description (保存发货人信息参数)
     */
    public void saveTemplate(){
        boolean saveOk = false;
        String userId = getLoginUserId();
        
        String company = getPara("company");
        String name = getPara("name");
        String mobile = getPara("mobile");
        String postCode = getPara("postCode");
        String provinceName = getPara("provinceName");
        String cityName = getPara("cityName");
        String expAreaName = getPara("expAreaName");
        String address = getPara("address");
        try {
            String sql = "select * from order_esheet_sender_template where 1=1 and userId = '" + userId +"'";
            OrderEsheetSenderTemplate template = tempDao.findFirst(sql);
            if(template != null){
                saveOk = template.set("company", company)
                        .set("name", name)
                        .set("mobile", mobile)
                        .set("postCode", postCode)
                        .set("provinceName", provinceName)
                        .set("cityName", cityName)
                        .set("expAreaName", expAreaName)
                        .set("address", address)
                        .update();
            }else{
                saveOk = new OrderEsheetSenderTemplate().set("userId", userId)
                        .set("company", company)
                        .set("name", name)
                        .set("mobile", mobile)
                        .set("postCode", postCode)
                        .set("provinceName", provinceName)
                        .set("cityName", cityName)
                        .set("expAreaName", expAreaName)
                        .set("address", address)
                        .save();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(saveOk){
            result.success("保存成功");
        }else{
            result.addError("保存失败");
        }
        renderJson(result);
        
    }
    
    
    
    
    /**
     * 
     * @Description (单个分配面单)
     * 1.查询当前选择的快递是否有配置CustomerName和CustomerPwd。没有则返回
     * 2.查询发件信息配置。没有则返回
     * 3.查询订单相关信息，组装数据
     * 4.发送请求
     * 5.接受请求，打印模板入库，订单状态变更
     * 6.返回数据
     */
    @Before(Tx.class)
    @SuppressWarnings("static-access")
    public void distributionNumber(){
        try {
            String userId = getLoginUserId();
            String orderId = getPara("orderId");
            String shipperCode = getPara("shipperCode");
            int eSheetCount = getParaToInt("eSheetCount");
            //double cost = Double.parseDouble(getPara("cost"));
//            double otherCost = Double.parseDouble(getPara("otherCost"));
            
            //1
            String sql = "select * from relation_user_dot_para where 1=1 and userId = '" + userId + "' and shipperCode = '" + shipperCode+"'";
            RelationUserDotPara rudp = rudpDao.findFirst(sql);
            if(rudp == null){
                result.addError("选择的快递未配置相应参数，请点击右上角配置按钮配置相关信息");
                renderJson(result);
            }
            
            //2
            String tempSql = "select * from order_esheet_sender_template where 1=1 and userId = '" + userId +"'";
            OrderEsheetSenderTemplate template = tempDao.findFirst(tempSql);
            if(template == null){
                result.addError("未配置发货人信息，请点击右上角配置按钮配置相关信息");
                renderJson(result);
            }
            
            Orders order = dao.findById(orderId);
            String originCode = "";
            String printTemplate = "";
            String eBusinessID = "";
            boolean success = false;
            for (int i = 0; i < eSheetCount; i++) {
                String esriJson =  "{'OrderCode': '"+orderId+"'," +
                    "'ShipperCode':'"+shipperCode+"'," +
                    "'CustomerName':'"+rudp.getStr("customerName")+"'," +
                    "'CustomerPwd':'"+rudp.getStr("customerPwd")+"'," +
                    "'PayType':1," +
                    "'ExpType':1," +
                   // "'Cost':"+cost+"," +
                   // "'OtherCost':"+otherCost+"," +
                    "'Sender':" +
                    "{" +
                    "'Company':'"+template.getStr("company")+"','Name':'"+template.getStr("name")+"','Mobile':'"+template.get("mobile").toString()+"','ProvinceName':'"+template.getStr("provinceName")+"','CityName':'"+template.getStr("cityName")+"','ExpAreaName':'"+template.getStr("expAreaName")+"','Address':'"+template.getStr("address")+"'}," +
                    "'Receiver':" +
                    "{" +
                    "'Name':'"+order.getStr("recipient")+"','Mobile':'"+order.get("recipientTel").toString()+"','ProvinceName':'"+order.getStr("provinceName")+"','CityName':'"+order.getStr("cityName")+"','ExpAreaName':'"+order.getStr("expAreaName")+"','Address':'"+order.getStr("recipientAddress")+"'}," +
                    "'Commodity':" +
                    "[{" +
                    "'GoodsName':'"+order.getStr("productName")+"','Goodsquantity':"+order.getInt("productCount")+"}]," +
                    "'Weight':"+order.getBigDecimal("productWeight")+"," +
                    "'Quantity':1," +
                    "'Volume':0.0," +
                    "'Remark':'"+order.getStr("remarks")+"'," +
                    "'IsReturnPrintTemplate':1}";
                System.out.println(esriJson);
                String eSheetResult = eOrderApi.getOrderOnlineByJson(esriJson);
                System.out.println("电子面单申请返回===>"+eSheetResult);
                
                JSONObject obj = JSONObject.parseObject(eSheetResult);
                eBusinessID = obj.get("EBusinessID").toString();
                success = (Boolean) obj.get("Success");
                if(i == (eSheetCount-1)){
                    printTemplate += obj.get("PrintTemplate").toString();
                }else{
                    printTemplate += obj.get("PrintTemplate").toString()+",";
                    
                }
                
                String eOrderStr = obj.getString("Order");
                JSONObject eOrderObj = JSONObject.parseObject(eOrderStr);
                if(i == (eSheetCount-1)){
                    originCode += eOrderObj.getString("OriginCode");
                }else{
                    originCode += eOrderObj.getString("OriginCode")+",";
                }
                
            }
            if(success){
                order.set("printTemplate", printTemplate).set("eBusinessID", eBusinessID).set("shipperCode",shipperCode).set("originCode", originCode).set("orderStatus", 3).update();
                System.out.println("电子面单分配成功，状态已修改");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.addError("保存失败，请检查相关信息");
        }
        result.success(1);
        renderJson(result);
    }
    
    /**
     * 
     * @Description (批量分配面单)
     * 1.查询当前选择的快递是否有配置CustomerName和CustomerPwd。没有则返回
     * 2.查询发件信息配置。没有则返回
     * 3.查询订单相关信息，组装数据
     * 4.发送请求
     * 5.接受请求，打印模板入库，订单状态变更
     * 6.返回数据
     */
    @SuppressWarnings("static-access")
    @Before(Tx.class)
    public void batchDistributionNumber(){
        try {
            String userId = getLoginUserId();
            String orderIdArr = getPara("orderIdArr");
            String orderIds[] = orderIdArr.split(",");
            String shipperCode = getPara("shipperCode");
           // double cost = Double.parseDouble(getPara("cost"));
//            double otherCost = Double.parseDouble(getPara("otherCost"));
            
            //1
            String sql = "select * from relation_user_dot_para where 1=1 and userId = '" + userId + "' and shipperCode = '" + shipperCode+"'";
            RelationUserDotPara rudp = rudpDao.findFirst(sql);
            if(rudp == null){
                result.addError("选择的快递未配置相应参数，请点击右上角配置按钮配置相关信息");
                renderJson(result);
            }
            
            //2
            String tempSql = "select * from order_esheet_sender_template where 1=1 and userId = '" + userId +"'";
            OrderEsheetSenderTemplate template = tempDao.findFirst(tempSql);
            if(template == null){
                result.addError("未配置发货人信息，请点击右上角配置按钮配置相关信息");
                renderJson(result);
            }
            for (int i = 0; i < orderIds.length; i++) {
                Orders order = dao.findById(orderIds[i]);
                String esriJson =  "{'OrderCode': '"+orderIds[i]+"'," +
                        "'ShipperCode':'"+shipperCode+"'," +
                        "'CustomerName':'"+rudp.getStr("customerName")+"'," +
                        "'CustomerPwd':'"+rudp.getStr("customerPwd")+"'," +
                        "'PayType':1," +
                        "'ExpType':1," +
                        //"'Cost':"+cost+"," +
                        // "'OtherCost':"+otherCost+"," +
                        "'Sender':" +
                        "{" +
                        "'Company':'"+template.getStr("company")+"','Name':'"+template.getStr("name")+"','Mobile':'"+template.get("mobile").toString()+"','ProvinceName':'"+template.getStr("provinceName")+"','CityName':'"+template.getStr("cityName")+"','ExpAreaName':'"+template.getStr("expAreaName")+"','Address':'"+template.getStr("address")+"'}," +
                        "'Receiver':" +
                        "{" +
                        "'Name':'"+order.getStr("recipient")+"','Mobile':'"+order.get("recipientTel").toString()+"','ProvinceName':'"+order.getStr("provinceName")+"','CityName':'"+order.getStr("cityName")+"','ExpAreaName':'"+order.getStr("expAreaName")+"','Address':'"+order.getStr("recipientAddress")+"'}," +
                        "'Commodity':" +
                        "[{" +
                        "'GoodsName':'"+order.getStr("productName")+"','Goodsquantity':"+order.getInt("productCount")+"}]," +
                        "'Weight':"+order.getBigDecimal("productWeight")+"," +
                        "'Quantity':1," +
                        "'Volume':0.0," +
                        "'Remark':'"+order.getStr("remarks")+"'," +
                        "'IsReturnPrintTemplate':1}";
                System.out.println(esriJson);
                String eSheetResult = eOrderApi.getOrderOnlineByJson(esriJson);
                System.out.println("电子面单申请返回===>"+eSheetResult);
                
                JSONObject obj = JSONObject.parseObject(eSheetResult);
                String eBusinessID = obj.get("EBusinessID").toString();
                boolean success = (Boolean) obj.get("Success");
                String printTemplate = obj.get("PrintTemplate").toString();
                
                String eOrderStr = obj.getString("Order");
                JSONObject eOrderObj = JSONObject.parseObject(eOrderStr);
                String originCode = eOrderObj.getString("OriginCode");
                
                if(success){//分配成功
                    order.set("printTemplate", printTemplate).set("shipperCode",shipperCode).set("eBusinessID", eBusinessID).set("originCode", originCode).set("orderStatus", 3).update();
                    System.out.println("電子麵單分配成功，狀態已修改");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.addError("保存失败，请检查相关信息");
        }
        result.success(1);
        renderJson(result);
    }
    
    //单个打印
    public void toPrint(){
        String orderId= getPara("orderId");
        Orders order = dao.findById(orderId);
        String tempLate = order.get("printTemplate");
        setAttr("tempLate", tempLate);
        setAttr("orderId", orderId);
        render("tempLate.html");
    }
    
    //批量打印
    public void toBatchPrint(){
        String orderIdArr = getPara("orderIdArr");
        String orders[] = orderIdArr.split(",");
        String tempLates[] = new String[orders.length];
        for (int i = 0; i < orders.length; i++) {
            Orders order = dao.findById(orders[i]);
            String tempLate = order.get("printTemplate");
            tempLates[i] = tempLate;
        }
        setAttr("tempLates", tempLates);
        setAttr("orderIdArr", orderIdArr);
        render("batchTempLate.html");
    }
    
    /**
     * 
     * @Description (更改订单状态为-->已打印)
     */
    public void setStatus24(){
        String orderId = getPara("orderId");
        Orders order = dao.findById(orderId);
        order.set("orderStatus", 4).update();
    }
    
    /**
     * 
     * @Description (更改订单状态为-->已打印)
     */
    public void batchSetStatus24(){
        String orderIdArr = getPara("orderIdArr");
        String orders[] = orderIdArr.split(",");
        for (int i = 0; i < orders.length; i++) {
            Orders order = dao.findById(orders[i]);
            order.set("orderStatus", 4).update();
        }
    }
    
    /**
     * 
     * @Description (发送订单短信)
     * 0.获取当短信剩余量是否够本次发送
     * 1.获取订单信息
     * 2.组装发送信息
     * 3.发送
     * 4.发送记录入库
     * 5.短信量更改
     */
    public void sendSms(){
        try {
            String orderId= getPara("orderId");
            String orderIds[] = orderId.split(",");
            String userId = getLoginUserId();
            //0
          //查询短信剩余量
            String smsRemainingSql = "select * from sms_store where 1=1 and userId = ?";
            SmsStore smsStore = SmsStore.dao.findFirst(smsRemainingSql,userId);
            int remaining = smsStore.getInt("remaining");
            if(remaining < orderIds.length){
                result.addError("短信剩余量不足,请及时充值以免影响正常业务");
                renderJson(result);
            }
            //1
            List<String> smsContents = new ArrayList<String>();
            for (int i = 0; i < orderIds.length; i++) {
                Orders order = dao.findById(orderIds[i]);
                String shipperCode = order.getStr("shipperCode");
                Shipper shipper = Shipper.dao.findFirst("select * from shipper where shipperCode = '" + shipperCode+"'");
                //2
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                String content = "亲，您购买的"+order.getStr("productName")+"已委托"+shipper.getStr("shipperName")+"镖局为您发货。镖号："+order.getStr("originCode")+".若想及时了解快递信息，请点击：http://fengniao.com/logistics/getOrderTracesByOrderId?no="+orderIds[i]+" 时间:"+df.format(new Date());
                String mobile[] = new String[]{order.get("recipientTel")+""} ;
                //3
                SMSUtils.sendOrderSms(mobile, content);
                //4
                boolean smsSendLogSave= new SmsSendLog()
                        .set("userId", userId)
                        .set("mobiles", mobile[0])
                        .set("smsContent", content)
                        .set("sendTime", df.format(new Date())).save();
            }
           
            //5
            smsStore.set("remaining", remaining-orderIds.length).update();
        } catch (Exception e) {
            e.printStackTrace();
        }
        result.success("发送成功");
        renderJson(result);
    }
    
    public void noSendSms(){
        String orderId= getPara("orderId");
        Orders order = dao.findById(orderId);
        order.set("orderStatus", 4).update();
    }

}
