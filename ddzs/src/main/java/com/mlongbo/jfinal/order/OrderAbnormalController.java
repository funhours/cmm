package com.mlongbo.jfinal.order;

import com.mlongbo.jfinal.controller.BaseController;
import com.mlongbo.jfinal.model.Orders;
import com.mlongbo.jfinal.vo.AjaxResult;


public class OrderAbnormalController extends BaseController {
    private final AjaxResult result = new AjaxResult();
    private int page = 1;
    static Orders dao = new Orders().dao();
    
    public void index(){
        String userId = getLoginUserId();
        setAttr("orderAbnormalPage", Orders.dao.paginate(page, 15, "select *", "from orders where 1=1 and relationUser = '"+userId+"' and orderStatus = 7 order by creationDate desc"));
        render("index.html");
    }
    
    
    
       
}
