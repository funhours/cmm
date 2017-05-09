package com.mlongbo.jfinal.router;

import com.jfinal.config.Routes;
import com.mlongbo.jfinal.accountBook.ApiAccountBookController;
import com.mlongbo.jfinal.api.AccountAPIController;
import com.mlongbo.jfinal.api.CommonAPIController;
import com.mlongbo.jfinal.api.FileAPIController;
import com.mlongbo.jfinal.index.ApiIndexController;
import com.mlongbo.jfinal.order.ApiOrderController;
import com.mlongbo.jfinal.person.ApiPersonController;
import com.mlongbo.jfinal.product.ApiProductController;
import com.mlongbo.jfinal.sms.ApiSmsController;

/**
 * @author malongbo
 */
public class APIRouter extends Routes {
    @Override
    public void config() {
        //公共api
        add("/api", CommonAPIController.class);
        //用户相关
        add("/api/account", AccountAPIController.class);
        //文件相关
        add("/api/fs",FileAPIController.class);
        //订单api
        add("/api/order",ApiOrderController.class);
        //代理商及员工API
        add("/api/person",ApiPersonController.class);
        //购买配置
        add("/api/buyConfig",ApiSmsController.class);
        //商品
        add("/api/product",ApiProductController.class);
        //账单
        add("/api/accountBook",ApiAccountBookController.class);
        //首页
        add("/api/admin",ApiIndexController.class);
    }
}
