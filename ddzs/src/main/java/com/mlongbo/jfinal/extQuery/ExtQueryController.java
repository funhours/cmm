package com.mlongbo.jfinal.extQuery;

import com.mlongbo.jfinal.controller.BaseController;
import com.mlongbo.jfinal.model.ExtQuery;
import com.mlongbo.jfinal.vo.AjaxResult;

public class ExtQueryController extends BaseController {
	private final AjaxResult result = new AjaxResult();
	static ExtQuery dao = new ExtQuery().dao();
	public void index() {
	    ExtQuery eQuery = getExtQuery();
		setAttr("eQuery", eQuery);
		render("index.html");
	}
	
	/**
	 * 
	 * @Description (跳转到配置页面)
	 */
	public void toConfig(){
	    ExtQuery eQuery = getExtQuery();
        setAttr("eQuery", eQuery);
        render("config.html");
	}


	/**
	 * 
	 * @Description (修改图片地址)
	 */
	public void saveImgUrl(){
	    String extLogo = getPara("extLogo");
	    ExtQuery eQuery = getExtQuery();
	    boolean saveOk = eQuery.set("imgUrl", extLogo).update();
	    if(saveOk){
	        result.success(eQuery);
	    }else{
	        result.addError("保存失败");
	    }
	    renderJson(result);
	}
	
	/**
	 * 
	 * @Description (修改页面标题)
	 */
	public void saveTitle(){
	    String title = getPara("title");
	    ExtQuery eQuery = getExtQuery();
	    boolean saveOk = eQuery.set("title", title).update();
	    if(saveOk){
	        result.success(eQuery);
	    }else{
	        result.addError("保存失败");
	    }
	    renderJson(result);
	}
	
	/**
	 * 
	 * @Description (修改公告)
	 */
	public void saveNotice(){
	    String notice = getPara("notice");
	    ExtQuery eQuery = getExtQuery();
	    boolean saveOk = eQuery.set("notice", notice).update();
	    if(saveOk){
	        result.success(eQuery);
	    }else{
	        result.addError("保存失败");
	    }
	    renderJson(result);
	}
	
	/**
	 * 
	 * @Description (修改页广告词)
	 */
	public void saveAdWords(){
	    String adWords = getPara("adWords");
	    ExtQuery eQuery = getExtQuery();
	    boolean saveOk = eQuery.set("adWords", adWords).update();
	    if(saveOk){
	        result.success(eQuery);
	    }else{
	        result.addError("保存失败");
	    }
	    renderJson(result);
	}
	
	/**
	 * 
	 * @Description (修改页广告词2)
	 */
	public void saveAdWords2(){
	    String adWords2 = getPara("adWords2");
	    ExtQuery eQuery = getExtQuery();
	    boolean saveOk = eQuery.set("adWords2", adWords2).update();
	    if(saveOk){
	        result.success(eQuery);
	    }else{
	        result.addError("保存失败");
	    }
	    renderJson(result);
	}
	
	/**
	 * 
	 * @Description (全部保存修改)
	 */
	public void saveEQueryEdit(){
	    ExtQuery eQuery = getModel(ExtQuery.class);
	    if(eQuery.update()){
            result.success("信息修改成功");
        }else{
            result.addError("信息修改失败，请联系管理员");
        };
        renderJson(result);
	}
	
	
	
	private ExtQuery getExtQuery() {
	    String userId = getLoginUserId();
	    ExtQuery eQuery = dao.findFirst("select * from ext_query where 1=1 and userId = '"+userId+"'");
	    return eQuery;
	}
}
