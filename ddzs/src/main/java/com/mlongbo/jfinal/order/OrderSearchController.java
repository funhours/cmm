package com.mlongbo.jfinal.order;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.mlongbo.jfinal.common.utils.StringUtils;
import com.mlongbo.jfinal.controller.BaseController;
import com.mlongbo.jfinal.model.Orders;
import com.mlongbo.jfinal.model.OrdersSearch;
import com.mlongbo.jfinal.model.Product;
import com.mlongbo.jfinal.model.ProductType;
import com.mlongbo.jfinal.model.User;
import com.mlongbo.jfinal.model.Utility;

public class OrderSearchController extends BaseController {

    private OrdersSearch ordersSearch = new OrdersSearch();
    private Product pdao = new Product().dao();
    private ProductType ptdao = new ProductType().dao();
    private User udao = new User().dao();
    private int page = 1;

    public void index() {
        String userId = getLoginUserId();
        setAttr("orderSearchPage", Orders.dao.paginate(page, 15, "select *",
                "from orders where 1=1 and relationUser = '" + userId + "' order by creationDate desc"));

        // 查询所有产品
//        String psql = "select * from product where 1=1 and userId = '" + userId + "'";
//        List<Product> pList = pdao.find(psql);
//        setAttr("pList", pList);
        // 查询所有产品
        String ptSql = "select * from product_type where 1=1 and userId = '" + userId + "'";
        List<ProductType> ptList = ptdao.find(ptSql);
        setAttr("ptList", ptList);

        // 查询所有代理或员工
        String usql = "select * from t_user where 1=1 and parentUserId = '" + userId + "'";
        List<User> uList = udao.find(usql);
        setAttr("uList", uList);

        render("index.html");
    }

    /**
     * @throws ParseException
     * @Description (系统订单查询)
     */
    public void orderSeachByPara() throws ParseException {
        String sqlCondition = " 1=1";
        Map<String, String[]> paraMap;
        String[] paraStr;
        int searchType = getParaToInt("searchType");
        String userId = getLoginUserId();
        String startDate = getPara("startDate");
        String endDate = getPara("endDate");
        String startTime = "";
        if(StringUtils.isEmpty(startDate)){
            startTime = "0";
        }else{
            startTime = getTime(startDate+":23-59-59");
        }
        String endTime = "";
        if(StringUtils.isEmpty(endDate)){
            endTime = System.currentTimeMillis()+"";
        }else{
            endTime = getTime(endDate+":23-59-59");
        }

        switch (searchType) {
        case 1:// 系统订单
            paraMap = getParaMap();
            paraStr = Utility.makePara(ordersSearch, "ordersSearch", paraMap);
            sqlCondition += paraStr[0];
            setAttr("total", Orders.dao
                    .findFirst("select sum(productPrice) as totalPrice,count(*) as orderCount from orders where"
                            + sqlCondition + " and creationDate BETWEEN "+startTime+" AND "+endTime+" and relationUser = '" + userId + "' order by creationDate desc"));
            setAttr("orderSearchPage", Orders.dao.paginate(page, 15, "select *", "from orders ord where" + sqlCondition
                    + " and creationDate BETWEEN "+startTime+" AND "+endTime+" and relationUser = '" + userId + "' order by creationDate desc"));
            setAttr("searchCon", paraStr[1]);
            break;
        case 2:// 淘宝订单
            paraMap = getParaMap();
            paraStr = Utility.makePara(ordersSearch, "ordersSearch", paraMap);
            sqlCondition += paraStr[0];
            setAttr("orderSearchPage", Orders.dao.paginate(page, 15, "select *", "from orders_tb where" + sqlCondition
                    + " and creationDate BETWEEN "+startTime+" AND "+endTime+" and relationUser = '" + userId + "' order by creationDate desc"));
            setAttr("searchCon", paraStr[1]);
            break;
        case 3:// 京东订单
            paraMap = getParaMap();
            paraStr = Utility.makePara(ordersSearch, "ordersSearch", paraMap);
            sqlCondition += paraStr[0];
            setAttr("orderSearchPage", Orders.dao.paginate(page, 15, "select *", "from orders_jd where" + sqlCondition
                    + " and creationDate BETWEEN "+startTime+" AND "+endTime+" and relationUser = '" + userId + "' order by creationDate desc"));
            setAttr("searchCon", paraStr[1]);
            break;

        default:
            break;
        }
        // 查询所有产品
        String psql = "select * from product where 1=1 and userId = '" + userId + "'";
        List<Product> pList = pdao.find(psql);
        setAttr("pList", pList);

        // 查询所有代理或员工
        String usql = "select * from t_user where 1=1 and parentUserId = '" + userId + "'";
        List<User> uList = udao.find(usql);
        setAttr("uList", uList);
        render("index.html");
    }
    
    
    
    
    public void getYearOrderInfo(){
        String sqlCondition = " 1=1";
        Map<String, String[]> paraMap;
        String[] paraStr;
        String userId = getLoginUserId();
        String startDate = getPara("startDate");
        String endDate = getPara("endDate");
        paraMap = getParaMap();
        paraStr = Utility.makePara(ordersSearch, "ordersSearch", paraMap);
        sqlCondition += paraStr[0];
        String startTime = "";
        if(StringUtils.isEmpty(startDate)){
            startTime = System.currentTimeMillis()+"";
        }else{
            startTime = getTime(startDate+":24-59-59");
        }
        String endTime = "";
        if(StringUtils.isEmpty(endDate)){
            endTime = System.currentTimeMillis()+"";
        }else{
            endTime = getTime(endDate+":24-59-59");
        }
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        String priceSql ="select month,SUM(productPrice) as prices from orders where "+ sqlCondition + " and creationDate BETWEEN "+startTime+" AND "+endTime+" and year = "+year+" and relationUser = '" + userId + "' GROUP BY month"; 
        String countSql = "select month,count(*) as counts from orders where "+ sqlCondition + " and creationDate BETWEEN "+startTime+" AND "+endTime+" and year = "+year+" and relationUser = '" + userId + "' GROUP BY month";
        
        List<Record> prices = Db.find(priceSql);
        List<Record> counts = Db.find(countSql);
        String _prices[] = new String[]{"0","0","0","0","0","0","0","0","0","0","0","0"};
        String _counts[] = new String[]{"0","0","0","0","0","0","0","0","0","0","0","0"};
        
        for (int i = 0; i < prices.size(); i++) {
            JSONObject pricesObj = JSONObject.parseObject(prices.get(i).toString());
            int month = pricesObj.getIntValue("month")-1;
            _prices[month] = pricesObj.getString("prices");
        }
        
        for (int i = 0; i < counts.size(); i++) {
            JSONObject countsObj = JSONObject.parseObject(counts.get(i).toString());
            int month = countsObj.getIntValue("month")-1;
            _counts[month] = countsObj.getString("counts");
        }
        
        JSONObject resultObj = new JSONObject();
        resultObj.put("prices", _prices);
        resultObj.put("counts", _counts);
        
        renderJson(resultObj);
        
    }
    
    
    
    
    /**
     * 
     * @Description (根据类型ID查询规格及价格)
     */
    public void getProByTypeId(){
        String typeId = getPara("typeId");
        List<Product> pList = pdao.find("select * from product where 1=1 and typeId = ?", typeId);
        renderJson(pList);
    }
    

    // 将字符串转为时间戳

    public static String getTime(String user_time) {
        String re_time = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd:hh-mm-ss");
        Date d;
        try {

            d = sdf.parse(user_time);
            long l = d.getTime();
            String str = String.valueOf(l);
            re_time = str.substring(0, 13);

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return re_time;
    }
}
