package com.mlongbo.jfinal.order;

import java.util.Map;

import com.mlongbo.jfinal.controller.BaseController;
import com.mlongbo.jfinal.model.Orders;
import com.mlongbo.jfinal.model.OrdersSearch;
import com.mlongbo.jfinal.model.Utility;


public class OrderSearchController extends BaseController {
    private OrdersSearch ordersSearch = new OrdersSearch();
    private int page = 1;
    
    public void index(){
        String userId = getLoginUserId();
        setAttr("orderSearchPage", Orders.dao.paginate(page, 15, "select *", "from orders where 1=1 and relationUser = '"+userId+"' order by creationDate desc"));
        render("index.html");
    }
    
    /**
     * 
     * @Description (系统订单查询)
     */
   public void orderSeachByPara(){
       String sqlCondition = " 1=1";
       Map<String,String[]> paraMap ;
       String[] paraStr;
       int searchType = getParaToInt("searchType");
       String userId = getLoginUserId();
       switch (searchType) {
    case 1://系统订单
        paraMap = getParaMap();
        paraStr = Utility.makePara(ordersSearch, "ordersSearch", paraMap);
        sqlCondition += paraStr[0];
        setAttr("total",Orders.dao.findFirst("select sum(productPrice) as totalPrice,count(*) as orderCount from orders ord where" + sqlCondition + " and relationUser = '"+userId+"' order by creationDate desc"));
        setAttr("orderSearchPage", Orders.dao.paginate(page, 15, "select *", "from orders ord where" + sqlCondition + " and relationUser = '"+userId+"' order by creationDate desc"));
        setAttr("searchCon", paraStr[1]);
        break;
    case 2://淘宝订单
        paraMap = getParaMap();
        paraStr = Utility.makePara(ordersSearch, "ordersSearch", paraMap);
        sqlCondition += paraStr[0];
        setAttr("orderSearchPage", Orders.dao.paginate(page, 15, "select *", "from orders_tb where" + sqlCondition + " and relationUser = '"+userId+"' order by creationDate desc"));
        setAttr("searchCon", paraStr[1]);
        break;
    case 3://京东订单
        paraMap = getParaMap();
        paraStr = Utility.makePara(ordersSearch, "ordersSearch", paraMap);
        sqlCondition += paraStr[0];
        setAttr("orderSearchPage", Orders.dao.paginate(page, 15, "select *", "from orders_jd where" + sqlCondition + " and relationUser = '"+userId+"' order by creationDate desc"));
        setAttr("searchCon", paraStr[1]);
        break;

    default:
        break;
    }
       
       render("index.html");
   }
}
