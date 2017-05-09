package com.mlongbo.jfinal.product;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.mlongbo.jfinal.common.utils.DateUtils;
import com.mlongbo.jfinal.common.utils.RandomUtils;
import com.mlongbo.jfinal.common.utils.StringUtils;
import com.mlongbo.jfinal.controller.BaseController;
import com.mlongbo.jfinal.model.Product;
import com.mlongbo.jfinal.model.ProductSpec;
import com.mlongbo.jfinal.model.ProductType;
import com.mlongbo.jfinal.vo.AjaxResult;

public class ProductController extends BaseController {
	static Product dao = new Product().dao();
	static ProductType ptdao = new ProductType().dao();
	static ProductSpec psdao = new ProductSpec().dao();
	private final AjaxResult result = new AjaxResult();
	
	public void index() {
		//分页参数
	    String userId = getLoginUserId();
		Page<Product> page = dao.paginate(getParaToInt("p", 1), 10,"select p.*,pt.typeName,ps.specName ","from product p inner join product_type pt on p.typeId = pt.id INNER JOIN product_spec ps ON p.specId = ps.id where 1=1 and p.userId = '"+userId+"' order by p.creationDate");
		setAttr("page", page);
		
		List<ProductType> pTypeList = ptdao.find("select * from product_type where 1=1 and userId='"+userId+"' and status = 1 order by creationDate");
		setAttr("pTypeList", pTypeList);
		
		List<ProductSpec> pSpecList = psdao.find("select * from product_spec where 1=1 and userId='"+userId+"' and status = 1 order by creationDate");
		setAttr("pSpecList", pSpecList);
		
		render("index.html");
	}
	
	/**
     * 方法描述：跳转编辑页面
     * @author: FunHours
     */
    public void toEdit(){
        String userId = getLoginUserId();
        int editType = getParaToInt("editType");
        List<ProductType> pTypeList = ptdao.find("select * from product_type where 1=1 and userId='"+userId+"' and status = 1 order by creationDate");
        List<ProductSpec> pSpecList = psdao.find("select * from product_spec where 1=1 and userId='"+userId+"' and status = 1 order by creationDate");
        if(editType == 1){//1：编辑 0：新增
            String proId = getPara("proId");
            Record product = Db.findFirst("select * from product where id = '" + proId+"'"  );
            setAttr("product", product);
            setAttr("pTypeList", pTypeList);
            setAttr("pSpecList", pSpecList);
            render("edit.html");
            return;
        }
        
        setAttr("pTypeList", pTypeList);
        setAttr("pSpecList", pSpecList);
        render("add.html");
    }
    
    /**
     * 方法描述：保存修改
     */
    public void saveProductEdit(){
        boolean saveOk = false;
        String userId = getLoginUserId();
        Product product = getModel(Product.class);
        //修改
        String productId = product.get("id");
        product.set("creationDate", DateUtils.getNowTimeStamp());
        product.set("userId", userId);
        if(!StringUtils.isEmpty(productId)){//修改
            if(product.update()){
                saveOk = true;
                result.success("信息修改成功");
            }else{
                result.addError("信息修改失败，请联系管理员");
            };
            
            result.setData(saveOk);
        }else{
            // 新增
            productId = RandomUtils.randomCustomUUID();
            saveOk = product
                    .set("id", productId)
                    .set("creationDate", DateUtils.getNowTimeStamp())
                    .set("status", 0)
                    .save();
            
            if(saveOk){
                result.success("信息保存成功");
            }else{
                result.addError("信息保存失败，请联系管理员");
            };
            result.setData(saveOk);
        }
        renderJson(result);
    }
    
  //启用
    public void review(){
        String _reviewList = getPara("reviewList");
        String reviewList[] = _reviewList.split(",");
        List<Product> recordList = new ArrayList<Product>();
        for (int i = 0; i < reviewList.length; i++) {
            Product product = dao.findById(reviewList[i]); 
            product.set("status", 1);
            recordList.add(product);
        }
        int[] resultCount = Db.batchUpdate(recordList, reviewList.length);
        result.setData(resultCount);
        renderJson(result);
    }
    //禁用
    public void disable(){
        String _reviewList = getPara("reviewList");
        String reviewList[] = _reviewList.split(",");
        List<Product> recordList = new ArrayList<Product>();
        for (int i = 0; i < reviewList.length; i++) {
            Product product = dao.findById(reviewList[i]); 
            product.set("status", 0);
            recordList.add(product);
        }
        int[] resultCount = Db.batchUpdate(recordList, reviewList.length);
        result.setData(resultCount);
        renderJson(result);
    }
    
    // 单个删除
    public void delById(){
        String proId = getPara("proId");
        boolean isDelete = dao.deleteById(proId);
        
        renderJson(isDelete);
    }
    
    /**
     * 多个删除
     */
    public void delByIds(){
        
        String _delsList = getPara("delsList");
        String delsList[] = _delsList.split(",");
        List<Product> recordList = new ArrayList<Product>();
        for (int i = 0; i < delsList.length; i++) {
            Product product = dao.findById(delsList[i]); 
            product.set("status", 0);
            recordList.add(product);
        }
        String sql = "delete from product where 1=1 and id = ?";
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
        String pType = getPara("pType");
        String searchName = getPara("searchName");
        if(pType.equals("0") && StringUtils.isNotEmpty(searchName)){//指定商品名所有
            Page<Product> page = dao.paginate(getParaToInt("p", 1), 10,"select p.*,pt.typeName ","from product p inner join product_type pt on p.typeId = pt.id where 1=1 and p.userId = '"+userId+"' and p.name like '%"+searchName+"%' order by 'creationDate'");
            setAttr("page", page);
        }else if(pType.equals("0") && StringUtils.isEmpty(searchName)){//商品名为空的所有
            Page<Product> page = dao.paginate(getParaToInt("p", 1), 10,"select p.*,pt.typeName ","from product p inner join product_type pt on p.typeId = pt.id where 1=1 and p.userId = '"+userId+"' order by 'creationDate'");
            setAttr("page", page);
        }else if(StringUtils.isNotEmpty(pType) && StringUtils.isEmpty(searchName)){//商品名为空的指定类型
            Page<Product> page = dao.paginate(getParaToInt("p", 1), 10,"select p.*,pt.typeName ","from product p inner join product_type pt on p.typeId = pt.id where 1=1 and p.userId = '"+userId+"' and p.typeId = '"+pType+"' order by 'creationDate'");
            setAttr("page", page);
        }else{//指定类型指定商品
            Page<Product> page = dao.paginate(getParaToInt("p", 1), 10,"select p.*,pt.typeName ","from product p inner join product_type pt on p.typeId = pt.id where 1=1 and p.userId = '"+userId+"' and p.typeId = '"+pType+"' and p.name like '%"+searchName+"%' order by 'creationDate'");
            setAttr("page", page);
        }
        
        List<ProductType> pTypeList = ptdao.find("select * from product_type where 1=1");
        setAttr("pTypeList", pTypeList);
        
        render("index.html");
        
    }
	
}
