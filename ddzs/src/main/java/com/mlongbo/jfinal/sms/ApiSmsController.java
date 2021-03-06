package com.mlongbo.jfinal.sms;

import java.io.File;
import java.util.Hashtable;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.jfinal.plugin.activerecord.Page;
import com.mlongbo.jfinal.common.utils.RandomUtils;
import com.mlongbo.jfinal.common.weixin.QRUtil;
import com.mlongbo.jfinal.common.weixin.WeiXinPayUtil;
import com.mlongbo.jfinal.config.AppProperty;
import com.mlongbo.jfinal.config.Context;
import com.mlongbo.jfinal.controller.BaseController;
import com.mlongbo.jfinal.model.InitUseCount;
import com.mlongbo.jfinal.model.OrderBuyConfig;
import com.mlongbo.jfinal.model.OrderBuyLog;
import com.mlongbo.jfinal.model.SmsBuyConfig;
import com.mlongbo.jfinal.model.SmsBuyLog;
import com.mlongbo.jfinal.model.SmsTemplate;
import com.mlongbo.jfinal.model.TimeBuyConfig;
import com.mlongbo.jfinal.model.TimeBuyLog;
import com.mlongbo.jfinal.vo.AjaxResult;

public class ApiSmsController extends BaseController {
    private final AjaxResult result = new AjaxResult();
    private final SmsBuyConfig sbcDao = new SmsBuyConfig().dao();
    private final OrderBuyConfig obcDao = new OrderBuyConfig().dao();
    private final TimeBuyConfig tbcDao = new TimeBuyConfig().dao();
	
	/**
	 * 
	 * @Description (跳转到购买页)
	 */
	public void getBuyConfig(){
	    List<SmsBuyConfig> sbConfig = sbcDao.find("select * from sms_buy_config where 1=1");
	    List<OrderBuyConfig> obConfig = obcDao.find("select * from order_buy_config where 1=1");
	    List<TimeBuyConfig> tbConfig = tbcDao.find("select * from time_buy_config where 1=1");
	    
	    JSONObject jsobj = new JSONObject();
	    jsobj.put("sbConfig", sbConfig);
	    jsobj.put("obConfig", obConfig);
	    jsobj.put("tbConfig", tbConfig);
	    
	    renderJson(jsobj);
	    
	}
	
	/**
	 * 
	 * @Description (跳转到单独的购买页)
	 */
	public void toAloneBuyPage(){
	    List<SmsBuyConfig> sbConfig = sbcDao.find("select * from sms_buy_config where 1=1");
	    setAttr("sbConfig", sbConfig);
	    List<OrderBuyConfig> obConfig = obcDao.find("select * from order_buy_config where 1=1");
	    setAttr("obConfig", obConfig);
	    List<TimeBuyConfig> tbConfig = tbcDao.find("select * from time_buy_config where 1=1");
        setAttr("tbConfig", tbConfig);
	    render("aloneBuyPage.html");
	}
	
	public void getBuyLog(){
	    String userId = getPara("userId");
	  //短信购买记录
        Page<SmsBuyLog> sbuyLogPage = SmsBuyLog.dao.paginate(getParaToInt("p", 1), 10,"select *","from sms_buy_log where 1=1 and userId = '"+ userId+"'");
      //订单购买记录
        Page<OrderBuyLog> oBuyLogPage = OrderBuyLog.dao.paginate(getParaToInt("p", 1), 10,"select *","from order_buy_log where 1=1 and userId = '"+ userId+"'");
      //时长购买记录
        Page<TimeBuyLog> tBuyLogPage = TimeBuyLog.dao.paginate(getParaToInt("p", 1), 10,"select *","from time_buy_log where 1=1 and userId = '"+ userId+"'");
        
        JSONObject jsobj = new JSONObject();
        jsobj.put("sbuyLogPage", sbuyLogPage);
        jsobj.put("oBuyLogPage", oBuyLogPage);
        jsobj.put("tBuyLogPage", tBuyLogPage);
        
        renderJson(jsobj);
	}
	
	
	/**
	 * 
	 * @Description (编辑短信模板)
	 */
	public void edit(){
	    SmsTemplate smsTemplate = new SmsTemplate();
	    int editType = getParaToInt("editType");
	    String template = getPara("template");
	    String userId = getLoginUserId();
	    if(editType == 0){//新增
	        smsTemplate.set("id", RandomUtils.randomCustomUUID())
	                   .set("userId", userId)
	                   .set("template", template)
	                   .save();
	       result.success("保存成功");
	    }else if(editType == 1){//修改
	        String tempId = getPara("tempId");
	        smsTemplate = smsTemplate.findById(tempId);
	        smsTemplate.set("template", template).update();
	        
	    }
	    result.setData(smsTemplate);
        renderJson(result);
	}
	
	//----------------------------------短信购买----------------------------------------------------------
	
	/**
	 * 
	 * @Description (编辑购买模板)
	 */
	public void editBuyConfig(){
	    int sbcEditType = getParaToInt("sbcEditType");
	    if(sbcEditType == 1){//修改
	        String sbcId = getPara("sbcId");
	        String sbcPrice = getPara("sbcPrice");
	        
	        SmsBuyConfig smsBuyConfig = SmsBuyConfig.dao.findById(sbcId);
	        smsBuyConfig.set("price", sbcPrice).update();
	        
	        result.success("保存成功");
	    }else if(sbcEditType == 0){//新增
	        SmsBuyConfig smsBuyConfig = getModel(SmsBuyConfig.class);
	        smsBuyConfig.set("id", RandomUtils.randomCustomUUID()).save();
	        
	        result.success("保存成功");
	    }
	  
        renderJson(result);
	}
	
	/**
	 * 
	 * @Description (编辑初始化使用量)
	 */
	public void editInitUseCountConfig(){
	    int sbcEditType = getParaToInt("sbcEditType");
        if(sbcEditType == 1){//修改短信数量
            String smsCount = getPara("smsCount");
            
            InitUseCount smsBuyConfig = InitUseCount.dao.findById(1);
            smsBuyConfig.set("smsCount", smsCount).update();
            
            result.success("保存成功");
        }else if(sbcEditType == 2){//修改订单数量
            String orderCount = getPara("orderCount");
            InitUseCount smsBuyConfig = InitUseCount.dao.findById(1);
            smsBuyConfig.set("orderCount", orderCount).update();
            result.success("保存成功");
        }else if(sbcEditType == 3){//修改使用时长
            String useDay = getPara("useDay");
            InitUseCount smsBuyConfig = InitUseCount.dao.findById(1);
            smsBuyConfig.set("useDay", useDay).update();
            result.success("保存成功");
        }else if(sbcEditType == 0){//新增
            InitUseCount initUseCount = getModel(InitUseCount.class);
            initUseCount.save();
            
            result.success("保存成功");
        }
      
        renderJson(result);
	}
	
	/**
	 * 
	 * @Description (删除)
	 */
	public void delSbcById(){
	    String sbcId = getPara("sbcId");
	    boolean isDel = SmsBuyConfig.dao.findById(sbcId).delete();
	    if(isDel){
	        result.success("删除成功");
	    }else{
	        result.addError("删除失败");
	    }
	    renderJson(result);
	}
	
	
	
	/**
     * 
     * @Description (编辑时长购买模板)
     */
    public void timeEditBuyConfig(){
        int tbcEditType = getParaToInt("tbcEditType");
        if(tbcEditType == 1){//修改
            String tbcId = getPara("tbcId");
            String tbcPrice = getPara("tbcPrice");
            
            TimeBuyConfig timeBuyConfig = TimeBuyConfig.dao.findById(tbcId);
            timeBuyConfig.set("price", tbcPrice).update();
            
            result.success("保存成功");
        }else if(tbcEditType == 0){//新增
            TimeBuyConfig timeBuyConfig = getModel(TimeBuyConfig.class);
            timeBuyConfig.set("id", RandomUtils.randomCustomUUID()).save();
            
            result.success("保存成功");
        }
        
        renderJson(result);
    }

    /**
     * 
     * @Description (删除使用时长购买方式)
     */
    public void delTbcById(){
        String tbcId = getPara("tbcId");
        boolean isDel = TimeBuyConfig.dao.findById(tbcId).delete();
        if(isDel){
            result.success("删除成功");
        }else{
            result.addError("删除失败");
        }
        renderJson(result);
    }
    
	
	
	/**
	 * 1.获取信息组装
	 * 2.提交到支付数据
	 * 3.TODO--记录日志----回调函数写
	 * 4.TODO--更改用户短信条数----回调函数
	 * @Description (购买短信)
	 */
	public void buySms(){
	    String userId = getPara("userId");
	    String smsBuyId = getPara("smsBuyId");
	    
	    SmsBuyConfig sbConfig = sbcDao.findById(smsBuyId);
	    
	    String order_price = sbConfig.getBigDecimal("price").toString().replace(".",""); 
	    String body = "蜂鸟订单系统短信条数购买："+sbConfig.getInt("count"); 
	    String out_trade_no = RandomUtils.randomCustomUUID();
	    
	    
	    WeiXinPayUtil wxpy = new WeiXinPayUtil();
	    String QrCode = "";
	    String QrCodeUrl = "";
	    String outUrl = "";
	    try {
	        QrCode = wxpy.weixin_pay(order_price,body,out_trade_no);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
	    //二维码宽高设置
	    int width = 300; 
	    int height = 300; 
	    //二维码的图片格式
	    String format = "gif"; 
	    Hashtable hints = new Hashtable(); 
	    //内容所使用编码 
	    hints.put(EncodeHintType.CHARACTER_SET, "utf-8"); 
	    BitMatrix bitMatrix;
	    try {
	        bitMatrix = new MultiFormatWriter().encode(QrCode,BarcodeFormat.QR_CODE, width, height, hints);
	        //生成二维码 
//        String path = Class .class.getResource("/").toURI().getPath();
	        String path = "";
	        //表示存放在tomcat应用目录中
	        if (AppProperty.me().appPath() == 1) {
	            path = Context.me().getRequest().getSession().getServletContext().getRealPath("/");
	        }
	        
	        path += AppProperty.me().uploadRooPath();
	        
	        File outputFile = new File(path+File.separator+"QR_zxh_"+userId+".gif");
	        QRUtil.writeToFile(bitMatrix, format, outputFile); 
	        QrCodeUrl = outputFile.getPath();
	        int index = QrCodeUrl.lastIndexOf("\\");
	        outUrl = QrCodeUrl.substring(index+1, QrCodeUrl.length());
	    } catch (Exception e) {
	        e.printStackTrace();
	    } 
	    
	    result.success(outUrl);
	    renderJson(result);
	}
	
	
	/**
	 * 1.获取信息组装
	 * 2.提交到支付数据
	 * 3.TODO--记录日志----回调函数写
	 * 4.TODO--更改用户短信条数----回调函数
	 * @Description (购买短信)
	 */
	public void buyTime(){
	    String userId = getPara("userId");
	    String timeBuyId = getPara("TimeBuyId");
	    
	    TimeBuyConfig tbConfig = tbcDao.findById(timeBuyId);
	    
	    String time_price = tbConfig.getBigDecimal("price").toString().replace(".",""); 
	    String body = "蜂鸟订单系统使用时长购买："+tbConfig.getInt("count"); 
	    String out_trade_no = RandomUtils.randomCustomUUID();
	    
	    
	    WeiXinPayUtil wxpy = new WeiXinPayUtil();
	    String QrCode = "";
	    String QrCodeUrl = "";
	    String outUrl = "";
	    try {
	        QrCode = wxpy.weixin_pay(time_price,body,out_trade_no);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
	    //二维码宽高设置
	    int width = 300; 
	    int height = 300; 
	    //二维码的图片格式
	    String format = "gif"; 
	    Hashtable hints = new Hashtable(); 
	    //内容所使用编码 
	    hints.put(EncodeHintType.CHARACTER_SET, "utf-8"); 
	    BitMatrix bitMatrix;
	    try {
	        bitMatrix = new MultiFormatWriter().encode(QrCode,BarcodeFormat.QR_CODE, width, height, hints);
	        //生成二维码 
//        String path = Class .class.getResource("/").toURI().getPath();
	        String path = "";
	        //表示存放在tomcat应用目录中
	        if (AppProperty.me().appPath() == 1) {
	            path = Context.me().getRequest().getSession().getServletContext().getRealPath("/");
	        }
	        
	        path += AppProperty.me().uploadRooPath();
	        
	        File outputFile = new File(path+File.separator+"QR_zxh_"+userId+".gif");
	        QRUtil.writeToFile(bitMatrix, format, outputFile); 
	        QrCodeUrl = outputFile.getPath();
	        int index = QrCodeUrl.lastIndexOf("\\");
	        outUrl = QrCodeUrl.substring(index+1, QrCodeUrl.length());
	    } catch (Exception e) {
	        e.printStackTrace();
	    } 
	    
	    result.success(outUrl);
	    renderJson(result);
	}
	
	
	/**
	    * 1.获取信息组装
	    * 2.提交到支付数据
	    * 3.TODO--记录日志----回调函数写
	    * 4.TODO--更改用户短信条数----回调函数
	    * @Description (购买订单)
	    */
	   public void buyOrder(){
	       String userId = "";
	       try {
	           userId = getLoginUserId();
	        } catch (Exception e) {
	            userId = getPara("userId");
	        }
	       String orderBuyId = getPara("orderBuyId");
	       
	       OrderBuyConfig obConfig = obcDao.findById(orderBuyId);
	       
	       String order_price = obConfig.getBigDecimal("price").toString().replace(".",""); 
	       String body = "蜂鸟订单系统订单条数购买："+obConfig.getInt("count"); 
	       String out_trade_no = RandomUtils.randomCustomUUID();
	       
	       
	       WeiXinPayUtil wxpy = new WeiXinPayUtil();
	       String QrCode = "";
	       String QrCodeUrl = "";
	       String outUrl = "";
	       try {
	           QrCode = wxpy.weixin_pay(order_price,body,out_trade_no);
	       } catch (Exception e) {
	           e.printStackTrace();
	       }
	       
	     //二维码宽高设置
	   int width = 300; 
	   int height = 300; 
	   //二维码的图片格式
	   String format = "gif"; 
	   Hashtable hints = new Hashtable(); 
	   //内容所使用编码 
	   hints.put(EncodeHintType.CHARACTER_SET, "utf-8"); 
	   BitMatrix bitMatrix;
	   try {
	       bitMatrix = new MultiFormatWriter().encode(QrCode,BarcodeFormat.QR_CODE, width, height, hints);
	       //生成二维码 
//	       String path = Class .class.getResource("/").toURI().getPath();
	       String path = "";
	       //表示存放在tomcat应用目录中
	       if (AppProperty.me().appPath() == 1) {
	           path = Context.me().getRequest().getSession().getServletContext().getRealPath("/");
	       }
	       
	       path += AppProperty.me().uploadRooPath();
	       
	       File outputFile = new File(path+File.separator+"QR_zxh_"+userId+".gif");
	       QRUtil.writeToFile(bitMatrix, format, outputFile); 
	       QrCodeUrl = outputFile.getPath();
	       int index = QrCodeUrl.lastIndexOf("\\");
	       outUrl = QrCodeUrl.substring(index+1, QrCodeUrl.length());
	   } catch (Exception e) {
	       e.printStackTrace();
	   } 

	       result.success(outUrl);
	       renderJson(result);
	   }
}
