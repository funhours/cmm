package com.mlongbo.jfinal.model;

import com.jfinal.plugin.activerecord.Model;

/**
 * @author malongbo
 * @date 2015/2/13
 */
public class OrderStore extends Model<OrderStore> {
	
	private static final long serialVersionUID = 1L;
	public static final OrderStore dao = new OrderStore();
	
}
