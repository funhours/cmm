package com.mlongbo.jfinal.router;

import com.jfinal.config.Routes;
import com.mlongbo.jfinal.api.AccountAPIController;
import com.mlongbo.jfinal.api.CommonAPIController;
import com.mlongbo.jfinal.api.FileAPIController;
import com.mlongbo.jfinal.order.ApiOrderController;
import com.mlongbo.jfinal.person.ApiPersonController;

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
    }
}
