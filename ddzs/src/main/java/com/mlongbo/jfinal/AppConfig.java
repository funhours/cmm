package com.mlongbo.jfinal;

import java.sql.Connection;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.wall.WallFilter;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.JFinal;
import com.jfinal.json.MixedJsonFactory;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.template.Engine;
import com.mlongbo.jfinal.config.Context;
import com.mlongbo.jfinal.handler.APINotFoundHandler;
import com.mlongbo.jfinal.handler.ContextHandler;
import com.mlongbo.jfinal.interceptor.ErrorInterceptor;
import com.mlongbo.jfinal.model._MappingKit;
import com.mlongbo.jfinal.router.APIRouter;
import com.mlongbo.jfinal.router.ActionRouter;
import com.mlongbo.jfinal.router.BackRoutes;

/**
 * JFinal总配置文件，挂接所有接口与插件
 * @author mlongbo
 */
public class AppConfig extends JFinalConfig {
	
	private static Prop p = loadConfig();
	private WallFilter wallFilter;
	
	public static void main(String[] args) {
		/**
		 * 特别注意：Eclipse 之下建议的启动方式
		 */
		JFinal.start("src/main/webapp", 80, "/", 5);
		
	}
	
	private static Prop loadConfig() {
		try {
			// 优先加载生产环境配置文件
			return PropKit.use("jfinal_club_config_pro.txt");
		} catch (Exception e) {
			// 找不到生产环境配置文件，再去找开发环境配置文件
			return PropKit.use("jfinal_club_config_dev.txt");
		}
	}

    /**
     * 常量配置
     */
	@Override
	public void configConstant(Constants me) {
		me.setDevMode(p.getBoolean("devMode", false));//开启开发模式
		me.setJsonFactory(MixedJsonFactory.me());
		me.setEncoding("UTF-8");
//        me.setViewType(ViewType.JSP);

	}

    /**
     * 所有接口配置
     */
	@Override
	public void configRoute(Routes me) {
		me.add(new APIRouter());//接口路由
        me.add(new ActionRouter()); //页面路由
        me.add(new BackRoutes());//后台路由
        
	}
	
    /**
     * 配置模板引擎，通常情况只需配置共享的模板函数
     */
	@Override
	public void configEngine(Engine me) {
		me.addSharedFunction("/_view/common/__layout.html");
    	me.addSharedFunction("/_view/common/_paginate.html");
	    me.addSharedFunction("/_view/_admin/common/__admin_layout.html");
	}

    /**
     * 插件配置
     */
	@Override
	public void configPlugin(Plugins me) {
		
		DruidPlugin druidPlugin = getDruidPlugin();
	    wallFilter = new WallFilter();              // 加强数据库安全
	    wallFilter.setDbType("mysql");
	    druidPlugin.addFilter(wallFilter);
	    druidPlugin.addFilter(new StatFilter());    // 添加 StatFilter 才会有统计数据
	    me.add(druidPlugin);
	    
	    ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin);
	    arp.setTransactionLevel(Connection.TRANSACTION_READ_COMMITTED);
	    _MappingKit.mapping(arp);
	    // 强制指定复合主键的次序，避免不同的开发环境生成在 _MappingKit 中的复合主键次序不相同
	    arp.setPrimaryKey("document", "mainMenu,subMenu");
	    me.add(arp);
        if (p.getBoolean("devMode", false)) {
            arp.setShowSql(true);
        }
        
	    me.add(new EhCachePlugin());
	    //me.add(new Cron4jPlugin(p));定时调度
		
	}

    /**
     * 拦截器配置
     */
	@Override
	public void configInterceptor(Interceptors me) {
		me.add(new ErrorInterceptor());
		
	}

    /**
     * handle 配置*
     */
	@Override
	public void configHandler(Handlers me) {
        me.add(new ContextHandler());
		me.add(new APINotFoundHandler());
	}

    @Override
    public void afterJFinalStart() {
        Context.me().init();
        wallFilter.getConfig().setSelectUnionCheck(false);
    }

    @Override
    public void beforeJFinalStop() {
        Context.me().destroy();
    }

	
	/**
     * 抽取成独立的方法，例于 _Generator 中重用该方法，减少代码冗余
     */
	public static DruidPlugin getDruidPlugin() {
		return new DruidPlugin(p.get("jdbcUrl"), p.get("user"), p.get("password").trim());
	}
}