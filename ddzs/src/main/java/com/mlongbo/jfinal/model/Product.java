package com.mlongbo.jfinal.model;

import com.jfinal.plugin.activerecord.Model;

/**
 * @author malongbo
 * @date 2015/2/13
 */
public class Product extends Model<Product> {
	public static String ID = "id";
	public static String NAME = "name";
	public static String SPEC = "spec";
	public static String PRICE = "price";
	public static String STOCK = "stock";
	public static String DESC = "desc";
	public static String STATUS = "status";
	public static String TYPE_ID = "typeId";
	public static String CREATION_DATE = "creationDate";
	public static ProductType productType;

	
	private static final long serialVersionUID = 1L;
	public static final Product dao = new Product();
	
	
	public ProductType getPruductType (){
		return ProductType.dao.findById(get("typeId"));
	}
	
	
	
}
