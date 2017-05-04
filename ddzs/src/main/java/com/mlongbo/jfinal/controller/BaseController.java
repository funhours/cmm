package com.mlongbo.jfinal.controller;

import com.jfinal.core.Controller;
import com.mlongbo.jfinal.model.User;

public class BaseController extends Controller
{
  private User loginAccount = null;

  public User getLoginAccount() {
    if (this.loginAccount == null) {
      this.loginAccount = ((User)getAttr("loginUser"));
      if ((this.loginAccount != null) && (this.loginAccount.getStatus().intValue() == 0)) {
        throw new IllegalStateException("当前用户状态不允许登录，status = " + this.loginAccount.getStatus());
      }
    }
    return this.loginAccount;
  }

  public boolean isLogin() {
    return getLoginAccount() != null;
  }

  public boolean notLogin() {
    return !isLogin();
  }

  public String getLoginUserId()
  {
    return getLoginAccount().getUserId();
  }
}