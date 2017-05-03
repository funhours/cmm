package com.mlongbo.jfinal.logisticsController;

import java.util.List;

import com.mlongbo.jfinal.api.KdniaoTrackQueryAPI;
import com.mlongbo.jfinal.controller.BaseController;
import com.mlongbo.jfinal.model.Shipper;
import com.mlongbo.jfinal.model.User;
import com.mlongbo.jfinal.vo.AjaxResult;

public class LogisticsController extends BaseController {
	private final AjaxResult result = new AjaxResult();
	 User udao = new User().dao();
	public void index() {
	    String userId = getLoginUserId();
	    String sql = "select * from t_user where 1=1 and userId = '" + userId + "'";
        User user = udao.findFirst(sql);
	    List<Shipper> shipper = new Shipper().dao().find("select * from shipper order by (Case when shipperCode = '"+user.getStr("shipperCode")+"' then 0 else 1 end),id asc" );
        setAttr("shipper", shipper);
	    render("index.html");
	}
	
	/**
	 * 
	 * @Description (物流查询)
	 */
	public void getOrderTraces(){
	    try {
    	    String ShipperCode = getPara("ShipperCode");
    	    String LogisticCode = getPara("LogisticCode");
    	    KdniaoTrackQueryAPI api = new KdniaoTrackQueryAPI();
    	    String traceResult = "";
            traceResult = api.getOrderTracesByJson(ShipperCode, LogisticCode);
            result.success(traceResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
	    renderJson(result);
	}
	    
}
