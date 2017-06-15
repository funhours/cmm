package com.mlongbo.jfinal.action;

import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.mlongbo.jfinal.common.utils.StringUtils;
import com.mlongbo.jfinal.model.RegisterCode;

/**
 * @author malongbo
 * @date 2015/2/13
 * @package com.snailbaba.action
 */
@Clear
public class IndexAction extends Controller {
    public void index () {
        render("html/SingnIn.html");
    }
    
    public void doc() {
        render("doc/index.html");   
    }
    
    public void login(){
    	render("html/SingnIn.html");
    }
    
    public void CreateAccount(){
    	render("html/CreateAccount.html");
    }
    
    public void ForgetPassword(){
        render("html/forget_password.html");
    }
    
    /**
     * 
     * @Description (物流查询)
     */
    public void express(){
        render("express/PlatformQuery.html");
    }
    
    /**
     * 
     * @Description (消费者物流查询)
     */
    public void express2(){
        String orderId = getPara("no");
        setAttr("orderId", orderId);
        render("express/PlatformQuery2.html");
    }

    /**
     * 查询手机验证码 *
     * 测试使用*
     */
    public void findCode () {
        String mobile = getPara("mobile");
        if (StringUtils.isNotEmpty(mobile)) {
            String codeStr = "没查到该手机对应的验证码";
            RegisterCode code = RegisterCode.dao.findById(mobile);
            if (code != null && StringUtils.isNotEmpty(code.getStr(RegisterCode.CODE))) {
                codeStr = code.getStr(RegisterCode.CODE);
            }
            renderHtml(codeStr);
        }

    }
}
