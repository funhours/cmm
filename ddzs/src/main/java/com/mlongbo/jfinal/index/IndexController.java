/**
 * 请勿将俱乐部专享资源复制给其他人，保护知识产权即是保护我们所在的行业，进而保护我们自己的利益
 * 即便是公司的同事，也请尊重 JFinal 作者的努力与付出，不要复制给同事
 * 
 * 如果你尚未加入俱乐部，请立即删除该项目，或者现在加入俱乐部：http://jfinal.com/club
 * 
 * 俱乐部将提供 jfinal-club 项目文档与设计资源、专用 QQ 群，以及作者在俱乐部定期的分享与答疑，
 * 价值远比仅仅拥有 jfinal club 项目源代码要大得多
 * 
 * JFinal 俱乐部是五年以来首次寻求外部资源的尝试，以便于有资源创建更加
 * 高品质的产品与服务，为大家带来更大的价值，所以请大家多多支持，不要将
 * 首次的尝试扼杀在了摇篮之中
 */

package com.mlongbo.jfinal.index;

import java.util.Calendar;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.mlongbo.jfinal.controller.BaseController;

/**
 * 首页控制器
 * Calendar now = Calendar.getInstance();  
    System.out.println("年: " + now.get(Calendar.YEAR));  
    System.out.println("月: " + (now.get(Calendar.MONTH) + 1) + "");  
    System.out.println("日: " + now.get(Calendar.DAY_OF_MONTH));  
    System.out.println("时: " + now.get(Calendar.HOUR_OF_DAY));  
    System.out.println("分: " + now.get(Calendar.MINUTE));  
    System.out.println("秒: " + now.get(Calendar.SECOND));  
    System.out.println("当前时间毫秒数：" + now.getTimeInMillis());  
    System.out.println(now.getTime());  
 */
public class IndexController extends BaseController {

	static IndexService srv = IndexService.me;
	public void index() {
	    //String userId = getLoginUserId();
	    String userId = getLoginUserId();
	    //获取订单数
	    Calendar now = Calendar.getInstance(); 
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH) + 1;
        int day = now.get(Calendar.DAY_OF_MONTH);
        
        String daySql = "select count(*) as dayCount from orders where 1=1 and relationUser = '"+userId+"' and year = "+year+" and month = "+month+" and day = "+day;
        Record dayCount = Db.findFirst(daySql);
        
        String monthSql = "select count(*) as monthCount from orders where 1=1 and relationUser = '"+userId+"' and year = "+year+" and month = "+month;
        Record monthCount = Db.findFirst(monthSql);
        
        String allSql = "select count(*) as allCount from orders where 1=1 and relationUser = '"+userId+"'";
        Record allCount = Db.findFirst(allSql);
        
        String daySumSql = "select SUM(ord.productPrice) as daySum from orders ord where 1=1 and relationUser = '"+userId+"' and year = "+year+" and month = "+month+" and day = "+day;
        Record daySum = Db.findFirst(daySumSql);
        
        String monthSumSql = "select SUM(ord.productPrice) as monthSum from orders ord where 1=1 and relationUser = '"+userId+"' and year = "+year+" and month = "+month;
        Record monthSum = Db.findFirst(monthSumSql);
        
        String allSumSql = "select SUM(ord.productPrice) as allSum from orders ord where 1=1 and relationUser = '"+userId+"'";
        Record allSum = Db.findFirst(allSumSql);
        
        JSONObject dayCountObj = JSONObject.parseObject(dayCount.toString());
        JSONObject monthCountObj = JSONObject.parseObject(monthCount.toString());
        JSONObject allCountObj = JSONObject.parseObject(allCount.toString());
        
        JSONObject daySumObj = JSONObject.parseObject(daySum.toString());
        JSONObject monthSumObj = JSONObject.parseObject(monthSum.toString());
        JSONObject allSumObj = JSONObject.parseObject(allSum.toString());
        
        setAttr("dayCount", dayCountObj.getString("dayCount"));
        setAttr("monthCount", monthCountObj.getString("monthCount"));
        setAttr("allCount", allCountObj.getString("allCount"));
        
        setAttr("daySum", daySumObj.getString("daySum"));
        setAttr("monthSum", monthSumObj.getString("monthSum"));
        setAttr("allSum", allSumObj.getString("allSum"));
		render("index.html");
	}
	
	public void getYearOrderInfo(){
	    /*String userId = getLoginUserId();*/
	    String userId = getLoginUserId();
	    Calendar now = Calendar.getInstance(); 
        int year = now.get(Calendar.YEAR);
	    String priceSql ="select SUM(productPrice) as prices from orders where 1=1 and relationUser = '"+userId+"' and year = "+year+" GROUP BY month"; 
	    String countSql = "select count(*) as counts from orders where 1=1 and relationUser = '"+userId+"' and year = "+year+" GROUP BY month";
	    
	    List<Record> prices = Db.find(priceSql);
	    List<Record> counts = Db.find(countSql);
	    String _prices[] = new String[prices.size()];
	    String _counts[] = new String[counts.size()];
	    
	    for (int i = 0; i < prices.size(); i++) {
	        JSONObject pricesObj = JSONObject.parseObject(prices.get(i).toString());
	        _prices[i] = pricesObj.getString("prices");
        }
	    for (int i = 0; i < _counts.length; i++) {
	        JSONObject countsObj = JSONObject.parseObject(counts.get(i).toString());
	        _counts[i] = countsObj.getString("counts");
        }
	    
	    JSONObject resultObj = new JSONObject();
	    resultObj.put("prices", _prices);
	    resultObj.put("counts", _counts);
	    
	    renderJson(resultObj);
	    
	}

}
