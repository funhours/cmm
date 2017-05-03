package com.mlongbo.jfinal.model;

import com.jfinal.plugin.activerecord.Model;


public class OrderBuyLog extends Model<OrderBuyLog> {

    /**
     * @Field @serialVersionUID : TODO(这里用一句话描述这个类的作用)
     */
    private static final long serialVersionUID = 614969014212012927L;
    public static final OrderBuyLog dao = new OrderBuyLog();
}
