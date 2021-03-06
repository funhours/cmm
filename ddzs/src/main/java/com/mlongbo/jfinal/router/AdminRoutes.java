package com.mlongbo.jfinal.router;

import com.jfinal.config.Routes;
import com.mlongbo.jfinal.accountBook.AccountBookController;
import com.mlongbo.jfinal.banner.BannerController;
import com.mlongbo.jfinal.extQuery.ExtQueryController;
import com.mlongbo.jfinal.feedback.FeedBackController;
import com.mlongbo.jfinal.index.IndexController;
import com.mlongbo.jfinal.interceptor.LoginSessionInterceptor;
import com.mlongbo.jfinal.login.LoginController;
import com.mlongbo.jfinal.logistics.LogisticsController;
import com.mlongbo.jfinal.logistics.LogisticsInquiryController;
import com.mlongbo.jfinal.order.ESheetController;
import com.mlongbo.jfinal.order.OrderAbnormalController;
import com.mlongbo.jfinal.order.OrderController;
import com.mlongbo.jfinal.order.OrderDistController;
import com.mlongbo.jfinal.order.OrderSearchController;
import com.mlongbo.jfinal.order.SmartInputController;
import com.mlongbo.jfinal.person.PersonController;
import com.mlongbo.jfinal.product.ProductController;
import com.mlongbo.jfinal.productSpec.ProductSpecController;
import com.mlongbo.jfinal.productType.ProductTypeController;
import com.mlongbo.jfinal.sms.SmsController;

public class AdminRoutes extends Routes {

	@Override
	public void config() {
		
		// 添加后台管理拦截器，将拦截在此方法中注册的所有 Controller
		addInterceptor(new LoginSessionInterceptor());
				
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
		//产品规格管理
		add("/pspec", ProductSpecController.class, "/productSpec");
		//订单管理
		add("/order", OrderController.class, "/order");
		//订单搜索
        add("/orderSearch", OrderSearchController.class, "/order/search");
        //异常订单处理
        add("/order/abnormal", OrderAbnormalController.class, "/order/abnormal");
        //电子面单
        add("/order/eSheet", ESheetController.class, "/order/eSheet");
        //智能录入
        add("/order/smart", SmartInputController.class, "/order/smart");
        //物流轨迹异步推送接口
        add("/order/dist", OrderDistController.class);
        //留言管理
        add("/feedback", FeedBackController.class, "/feedback");
        //账本管理
        add("/accountBook", AccountBookController.class, "/accountBook");
        //banner图管理
        add("/banner", BannerController.class, "/banner");
        //内部物流查询
        add("/logistics", LogisticsController.class, "/logistics");
        //外部物流查
        add("/logisticsInquiry",LogisticsInquiryController.class,"/logisticsInquiry");
        //外部查询页管理
        add("/extQuery",ExtQueryController.class,"/extQuery");
	}

}
