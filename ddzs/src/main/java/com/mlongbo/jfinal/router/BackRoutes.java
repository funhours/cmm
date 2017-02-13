package com.mlongbo.jfinal.router;

import com.jfinal.config.Routes;
import com.mlongbo.jfinal.index.IndexController;
import com.mlongbo.jfinal.person.PersonController;
import com.mlongbo.jfinal.sms.SmsController;

public class BackRoutes extends Routes {

	@Override
	public void config() {
		setBaseViewPath("/_view");
		add("/admin", IndexController.class, "/index");
		//短信管理
		add("/smsManage", SmsController.class, "/sms");
		//微商人员管理
		add("/person", PersonController.class, "/person");
	}

}
