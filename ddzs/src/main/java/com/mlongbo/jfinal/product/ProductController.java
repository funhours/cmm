package com.mlongbo.jfinal.product;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.mlongbo.jfinal.model.Product;

public class ProductController extends Controller {
	static Product dao = new Product().dao();
	//static ProductType ptdao = new ProductType().dao();
	
	public void index() {
		//分页参数
		Page<Product> page = dao.paginate(getParaToInt("p", 1), 10,"select p.*,pt.typeName ","from product p inner join product_type pt on p.typeId = pt.id where 1=1 order by creationDate");
		setAttr("page", page);
		render("index.html");
	}
	
}
