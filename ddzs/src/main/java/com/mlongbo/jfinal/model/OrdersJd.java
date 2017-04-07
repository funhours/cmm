package com.mlongbo.jfinal.model;

import com.jfinal.plugin.activerecord.Model;

/**
 * @author malongbo
 * @date 2015/2/13
 */
public class OrdersJd extends Model<OrdersJd> {
    
    /**
     * @Field @serialVersionUID : TODO(这里用一句话描述这个类的作用)
     */
    private static final long serialVersionUID = -2923151700613835849L;
	
	public static final OrdersJd dao = new OrdersJd();
	
}
