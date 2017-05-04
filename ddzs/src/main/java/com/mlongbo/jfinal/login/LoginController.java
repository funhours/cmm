package com.mlongbo.jfinal.login;

import com.jfinal.aop.Clear;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;
import com.mlongbo.jfinal.kit.IpKit;

public class LoginController extends Controller
{
  static final LoginService srv = LoginService.me;

  @Clear
  public void doLogin()
  {
    boolean keepLogin = getParaToBoolean("keepLogin", Boolean.valueOf(false)).booleanValue();
    String loginIp = IpKit.getRealIp(getRequest());
    Ret ret = srv.login(getPara("loginName"), getPara("password"), keepLogin, loginIp);
    if (ret.isOk()) {
      String sessionId = ret.getStr("jfinalId");
      int maxAgeInSeconds = ((Integer)ret.getAs("maxAgeInSeconds")).intValue();
      setCookie("jfinalId", sessionId, maxAgeInSeconds, true);
      setAttr("loginUser", ret.get("loginUser"));
      setAttr("orderStore", ret.get("orderStore"));

      ret.set("returnUrl", getPara("returnUrl", "/"));
    }
    renderJson(ret);
  }

  @Clear
  @ActionKey("/logout")
  public void logout()
  {
    srv.logout(getCookie("jfinalId"));
    removeCookie("jfinalId");
    redirect("/login");
  }

  public void forgetPassword()
  {
    render("forget_password.html");
  }

  public void retrievePassword()
  {
    keepPara(new String[] { "authCode" });
    render("retrieve_password.html");
  }

  public void captcha()
  {
    renderCaptcha();
  }
}