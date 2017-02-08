package com.mlongbo.jfinal.router;

import com.jfinal.config.Routes;
import com.mlongbo.jfinal.index.IndexController;

public class BackRoutes extends Routes {

	@Override
	public void config() {
		setBaseViewPath("/_view");
		add("/admin", IndexController.class, "/index");
	}

}
