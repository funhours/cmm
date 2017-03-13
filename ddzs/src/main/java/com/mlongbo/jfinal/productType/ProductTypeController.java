package com.mlongbo.jfinal.productType;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.mlongbo.jfinal.model.ProductType;

public class ProductTypeController extends Controller {
	static ProductType ptdao = new ProductType().dao();
	
	public void index() {
		//分页参数
		Page<ProductType> page = ptdao.paginate(getParaToInt("p", 1), 10,"select *","from product_type where 1=1 order by creationDate");
		setAttr("page", page);
		render("index.html");
	}
}
