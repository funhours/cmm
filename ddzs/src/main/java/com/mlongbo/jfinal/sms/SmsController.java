package com.mlongbo.jfinal.sms;

import java.io.File;
import java.util.Hashtable;
import java.util.List;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.mlongbo.jfinal.common.utils.FileUtils;
import com.mlongbo.jfinal.common.utils.RandomUtils;
import com.mlongbo.jfinal.common.weixin.QRUtil;
import com.mlongbo.jfinal.common.weixin.WeiXinPayUtil;
import com.mlongbo.jfinal.model.SmsBuyConfig;
import com.mlongbo.jfinal.model.SmsSendLog;
import com.mlongbo.jfinal.model.SmsStore;
import com.mlongbo.jfinal.model.SmsTemplate;
import com.mlongbo.jfinal.vo.AjaxResult;

public class SmsController extends Controller {
    private final AjaxResult result = new AjaxResult();

	public void template(){
	    String userId = getPara("userId");
	    
	    //查询短信模板
	    String sql = "select * from sms_template where 1=1 and userId = ?";
	    SmsTemplate smsTemplate = SmsTemplate.dao.findFirst(sql,userId);
	    setAttr("smsTemplate", smsTemplate);
	    
	    //查询短信使用量
	    String smsUseSql = "select * from sms_send_log where 1=1 and userId = ?";
	    List<SmsSendLog> smsSendLog = SmsSendLog.dao.find(smsUseSql,userId);
	    setAttr("useCount", smsSendLog.size());
	    //查询短信剩余量
	    String smsRemainingSql = "select * from sms_store where 1=1 and userId = ?";
	    SmsStore smsStore = SmsStore.dao.findFirst(smsRemainingSql,userId);
	    setAttr("smsStore", smsStore);
	    
	  //分页参数
        Page<SmsBuyConfig> buyConfigPage = SmsBuyConfig.dao.paginate(getParaToInt("p", 1), 10,"select *","from sms_buy_config");
        setAttr("buyConfigPage", buyConfigPage);
	    
	    render("index.html");
	}
	
	/**
	 * 
	 * @Description (编辑短信模板)
	 */
	public void edit(){
	    SmsTemplate smsTemplate = new SmsTemplate();
	    int editType = getParaToInt("editType");
	    String template = getPara("template");
	    String userId = getPara("userId");
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
	    }else{//新增
	        SmsBuyConfig smsBuyConfig = getModel(SmsBuyConfig.class);
	        smsBuyConfig.set("id", RandomUtils.randomCustomUUID()).save();
	        
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
	 * @Description (购买短信)
	 */
	public void buySms(){
	    WeiXinPayUtil wxpy = new WeiXinPayUtil();
	    String QrCode ="";
	    String QrCodeUrl ="";
	    try {
	        QrCode = wxpy.weixin_pay();
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
        String path = Class .class.getResource("/").toURI().getPath();
        File outputFile = new File(path+File.separator+"QR_zxh.gif");
        QRUtil.writeToFile(bitMatrix, format, outputFile); 
        QrCodeUrl = outputFile.getPath();
    } catch (Exception e) {
        e.printStackTrace();
    } 
	    
	    
	    
	    result.success(QrCodeUrl);
	    renderJson(result);
	}
}
