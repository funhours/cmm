package com.mlongbo.jfinal.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;

/**
 * @author malongbo
 * @date 2015/2/13
 */
public class ProductType extends Model<ProductType> {
	public static String ID = "id";
	public static String TYPE_NAME = "typeName";

	
	private static final long serialVersionUID = 1L;
	public static final ProductType dao = new ProductType();
	
	public List<Product> getProducts(){
		return Product.dao.find("select * from product where 1=1 and typeId = ?",get("id"));
	}
	
	public void delProducts(){
	    List<Product> pList = getProducts();
	    
	    for (Product product : pList) {
	        product.delete();
        }
	    
	}
	
	
}
