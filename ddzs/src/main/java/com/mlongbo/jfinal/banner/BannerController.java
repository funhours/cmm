package com.mlongbo.jfinal.banner;

import java.util.List;

import com.jfinal.core.Controller;
import com.mlongbo.jfinal.model.Banner;
import com.mlongbo.jfinal.vo.AjaxResult;


public class BannerController extends Controller {
    private final AjaxResult result = new AjaxResult();
    Banner dao = new Banner().dao();
    
    public void index(){
        List<Banner> banners = dao.find("select * from banner where 1=1");
        setAttr("banner1", banners.get(0));
        setAttr("banner2", banners.get(1));
        setAttr("banner3", banners.get(2));
        setAttr("banner4", banners.get(3));
        render("index.html");
    }
    
    public void saveBanner(){
        boolean isOk = false;
        Banner banner = getModel(Banner.class);
        isOk = banner.update();
        result.success(isOk);
        renderJson(result);
    }
}
