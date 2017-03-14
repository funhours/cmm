package com.mlongbo.jfinal.productType;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.mlongbo.jfinal.common.utils.DateUtils;
import com.mlongbo.jfinal.common.utils.RandomUtils;
import com.mlongbo.jfinal.common.utils.StringUtils;
import com.mlongbo.jfinal.model.ProductType;
import com.mlongbo.jfinal.model.User;
import com.mlongbo.jfinal.vo.AjaxResult;

public class ProductTypeController extends Controller {
	static ProductType ptdao = new ProductType().dao();
	private final AjaxResult result = new AjaxResult();
	public void index() {
		//分页参数
		Page<ProductType> page = ptdao.paginate(getParaToInt("p", 1), 10,"select *","from product_type where 1=1 order by creationDate");
		setAttr("page", page);
		render("index.html");
	}
	
	/**
	 * 
	 * @Description 添加修改商品类别
	 */
	public void edit(){
	    boolean saveOk = false;
	    String typeName = getPara("typeName");
	    int editType = getParaToInt("editType");
	    
	    if(editType == 0){//新增
	        saveOk = new ProductType().set("id", RandomUtils.randomCustomUUID())
	        .set("typeName", typeName)
	        .set("status", 0)
	        .set("creationDate", DateUtils.getNowTimeStamp()).save();
	        if(saveOk){
	            result.success("保存成功");
	        }else{
	            result.addError("保存失败，请联系管理员");
	        }
	    }else if(editType == 1){//修改
	        String pTypeId = getPara("pTypeId");
	        
	        ProductType pType = ptdao.findById(pTypeId);
	        saveOk = pType.set("typeName", typeName).update();
	        
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
        List<ProductType> recordList = new ArrayList<ProductType>();
        for (int i = 0; i < reviewList.length; i++) {
            ProductType pType = ptdao.findById(reviewList[i]); 
            pType.set("status", 1);
            recordList.add(pType);
        }
        int[] resultCount = Db.batchUpdate(recordList, reviewList.length);
        result.setData(resultCount);
        renderJson(result);
    }
    //禁用
    public void disable(){
        String _reviewList = getPara("reviewList");
        String reviewList[] = _reviewList.split(",");
        List<ProductType> recordList = new ArrayList<ProductType>();
        for (int i = 0; i < reviewList.length; i++) {
            ProductType pType = ptdao.findById(reviewList[i]); 
            pType.set("status", 0);
            recordList.add(pType);
        }
        int[] resultCount = Db.batchUpdate(recordList, reviewList.length);
        result.setData(resultCount);
        renderJson(result);
    }
    
    // 单个删除
    public void delById(){
        String pTypeId = getPara("pTypeId");
        boolean isDelete = ptdao.deleteById(pTypeId);
        
        renderJson(isDelete);
    }
    
    /**
     * 多个删除
     */
    public void delByIds(){
        
        String _delsList = getPara("delsList");
        String delsList[] = _delsList.split(",");
        List<ProductType> recordList = new ArrayList<ProductType>();
        for (int i = 0; i < delsList.length; i++) {
            ProductType pType = ptdao.findById(delsList[i]); 
            pType.set("status", 0);
            recordList.add(pType);
        }
        String sql = "delete from product_type where 1=1 and id = ?";
        int[] resultCount = Db.batch(sql, "id", recordList, recordList.size());
        result.setData(resultCount);
        renderJson(result);
    }
    
    /**
     * 
     * @Description (搜索)
     */
    public void search(){
        
        String searchTypeName = getPara("searchTypeName");
        if(StringUtils.isEmpty(searchTypeName)){//查询条件为空的查询所有
            Page<ProductType> page = ptdao.paginate(getParaToInt("p", 1), 10,"select * ","from product_type where 1=1 order by creationDate");
            setAttr("page", page);
        }else{
            Page<ProductType> page = ptdao.paginate(getParaToInt("p", 1), 10,"select * ","from product_type where 1=1 and typeName like '%"+searchTypeName+"%' order by creationDate");
            setAttr("page", page);
        }
        render("index.html");
        
    }
    
}
