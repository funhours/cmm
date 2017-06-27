package com.demo.article;

import com.demo.common.model.Article;
import com.demo.common.model.Blog;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;

/**
 * 本 demo 仅表达最为粗浅的 jfinal 用法，更为有价值的实用的企业级用法
 * 详见 JFinal 俱乐部: http://jfinal.com/club
 * 
 * BlogController
 * 所有 sql 与业务逻辑写在 Model 或 Service 中，不要写在 Controller 中，养成好习惯，有利于大型项目的开发与维护
 */
@Before(AritcleInterceptor.class)
public class ArticleController extends Controller {
	
	static ArticleService service = new ArticleService();
	
	public void index() {
		setAttr("articlePage", service.paginate(getParaToInt(0, 1), 10));
		render("article.html");
	}
	
	public void add() {
	    
	}
	
	/**
	 * save 与 update 的业务逻辑在实际应用中也应该放在 serivce 之中，
	 * 并要对数据进正确性进行验证，在此仅为了偷懒
	 */
	@Before(ArticleValidator.class)
	public void save() {
		getModel(Article.class).save();
		redirect("/article");
	}
	
	public void edit() {
		setAttr("article", service.findById(getParaToInt()));
	}
	
	/**
	 * save 与 update 的业务逻辑在实际应用中也应该放在 serivce 之中，
	 * 并要对数据进正确性进行验证，在此仅为了偷懒
	 */
	@Before(ArticleValidator.class)
	public void update() {
	    getModel(Article.class).save();
        redirect("/article");
	}
	
	public void delete() {
	    getModel(Article.class).save();
        redirect("/article");
	}
}


