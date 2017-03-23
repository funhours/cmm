package com.mlongbo.jfinal.router;

import com.jfinal.config.Routes;
import com.mlongbo.jfinal.index.IndexController;
import com.mlongbo.jfinal.interceptor.AdminAuthInterceptor;
import com.mlongbo.jfinal.login.LoginController;
import com.mlongbo.jfinal.order.OrderController;
import com.mlongbo.jfinal.person.PersonController;
import com.mlongbo.jfinal.product.ProductController;
import com.mlongbo.jfinal.productType.ProductTypeController;
import com.mlongbo.jfinal.sms.SmsController;

public class AdminRoutes extends Routes {

	@Override
	public void config() {
		
		// 添加后台管理拦截器，将拦截在此方法中注册的所有 Controller
		addInterceptor(new AdminAuthInterceptor());
				
		setBaseViewPath("/_view/_admin");
		add("/login", LoginController.class);
		//首页
		add("/admin", IndexController.class, "/index");
		//短信管理
		add("/smsManage", SmsController.class, "/sms");
		//微商人员管理
		add("/person", PersonController.class, "/person");
		//产品管理
		add("/product", ProductController.class, "/product");
		//产品类型管理
		add("/ptype", ProductTypeController.class, "/productType");
		//订单管理
		add("/order", OrderController.class, "/order");
	}

}
