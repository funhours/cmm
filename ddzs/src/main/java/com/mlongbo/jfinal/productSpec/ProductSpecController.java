package com.mlongbo.jfinal.productSpec;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.mlongbo.jfinal.common.utils.DateUtils;
import com.mlongbo.jfinal.common.utils.RandomUtils;
import com.mlongbo.jfinal.common.utils.StringUtils;
import com.mlongbo.jfinal.controller.BaseController;
import com.mlongbo.jfinal.model.ProductSpec;
import com.mlongbo.jfinal.vo.AjaxResult;

/**
 * 
 * @ClassName ProductSpecController
 * @Description TODO(商品类别控制器)
 * @author FunHours
 * @Date 2017年5月9日 下午7:27:19
 * @version 1.0.0
 */
public class ProductSpecController extends BaseController {
	static ProductSpec psdao = new ProductSpec().dao();
	private final AjaxResult result = new AjaxResult();
	public void index() {
	    String userId = getLoginUserId();
		//分页参数
		Page<ProductSpec> page = psdao.paginate(getParaToInt("p", 1), 10,"select *","from product_spec where 1=1 and userId = '"+userId+"' order by creationDate");
		setAttr("page", page);
		render("index.html");
	}
	
	/**
	 * 
	 * @Description 添加修改商品类别
	 */
	public void edit(){
	    boolean saveOk = false;
	    String specName = getPara("specName");
	    int editType = getParaToInt("editType");
	    String userId = getLoginUserId();
	    if(editType == 0){//新增
	        saveOk = new ProductSpec().set("id", RandomUtils.randomCustomUUID())
	        .set("specName", specName)
	        .set("status", 0)
	        .set("creationDate", DateUtils.getNowTimeStamp())
            .set("userId", userId).save();
	        if(saveOk){
	            result.success("保存成功");
	        }else{
	            result.addError("保存失败，请联系管理员");
	        }
	    }else if(editType == 1){//修改
	        String pSpecId = getPara("pSpecId");
	        
	        ProductSpec pSpec = psdao.findById(pSpecId);
	        saveOk = pSpec.set("specName", specName).update();
	        
	        if(saveOk){
                result.success("保存成功");
            }else{
                result.addError("保存失败，请联系管理员");
            }
	    }
	    renderJson(result);
	}
	
	//启用
    public void review(){
        String _reviewList = getPara("reviewList");
        String reviewList[] = _reviewList.split(",");
        List<ProductSpec> recordList = new ArrayList<ProductSpec>();
        for (int i = 0; i < reviewList.length; i++) {
            ProductSpec pSpec = psdao.findById(reviewList[i]); 
            pSpec.set("status", 1);
            recordList.add(pSpec);
        }
        int[] resultCount = Db.batchUpdate(recordList, reviewList.length);
        result.setData(resultCount);
        renderJson(result);
    }
    //禁用
    public void disable(){
        String _reviewList = getPara("reviewList");
        String reviewList[] = _reviewList.split(",");
        List<ProductSpec> recordList = new ArrayList<ProductSpec>();
        for (int i = 0; i < reviewList.length; i++) {
            ProductSpec pType = psdao.findById(reviewList[i]); 
            pType.set("status", 0);
            recordList.add(pType);
        }
        int[] resultCount = Db.batchUpdate(recordList, reviewList.length);
        result.setData(resultCount);
        renderJson(result);
    }
    
    // 单个删除
    public void delById(){
        String pSpecId = getPara("pSpecId");
        boolean isDelete = psdao.deleteById(pSpecId);
        
        renderJson(isDelete);
    }
    
    /**
     * 多个删除
     */
    public void delByIds(){
        
        String _delsList = getPara("delsList");
        String delsList[] = _delsList.split(",");
        List<ProductSpec> recordList = new ArrayList<ProductSpec>();
        for (int i = 0; i < delsList.length; i++) {
            ProductSpec pType = psdao.findById(delsList[i]); 
            pType.set("status", 0);
            recordList.add(pType);
        }
        String sql = "delete from product_spec where 1=1 and id = ?";
        int[] resultCount = Db.batch(sql, "id", recordList, recordList.size());
        result.setData(resultCount);
        renderJson(result);
    }
    
    /**
     * 
     * @Description (搜索)
     */
    public void search(){
        String userId = getLoginUserId();
        String searchSpecName = getPara("searchSpecName");
        if(StringUtils.isEmpty(searchSpecName)){//查询条件为空的查询所有
            Page<ProductSpec> page = psdao.paginate(getParaToInt("p", 1), 10,"select * ","from product_spec where 1=1 and userId = '"+userId+"' order by creationDate");
            setAttr("page", page);
        }else{
            Page<ProductSpec> page = psdao.paginate(getParaToInt("p", 1), 10,"select * ","from product_spec where 1=1 and userId = '"+userId+"' and specName like '%"+searchSpecName+"%' order by creationDate");
            setAttr("page", page);
        }
        render("index.html");
        
    }
    
}
