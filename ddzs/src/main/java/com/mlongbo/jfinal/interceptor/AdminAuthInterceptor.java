/**
 * 请勿将俱乐部专享资源复制给其他人，保护知识产权即是保护我们所在的行业，进而保护我们自己的利益
 * 即便是公司的同事，也请尊重 JFinal 作者的努力与付出，不要复制给同事
 * 
 * 如果你尚未加入俱乐部，请立即删除该项目，或者现在加入俱乐部：http://jfinal.com/club
 * 
 * 俱乐部将提供 jfinal-club 项目文档与设计资源、专用 QQ 群，以及作者在俱乐部定期的分享与答疑，
 * 价值远比仅仅拥有 jfinal club 项目源代码要大得多
 * 
 * JFinal 俱乐部是五年以来首次寻求外部资源的尝试，以便于有资源创建更加
 * 高品质的产品与服务，为大家带来更大的价值，所以请大家多多支持，不要将
 * 首次的尝试扼杀在了摇篮之中
 */

package com.mlongbo.jfinal.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.mlongbo.jfinal.login.LoginService;

/**
 * 后台权限管理拦截器
 * 
 * 暂时做成最简单的判断当前用户是否是管理员账号，后续改成完善的 基于用户、角色、权限的权限管理系统，并且实现角色、权限完全动态化配置
 */
public class AdminAuthInterceptor implements Interceptor {

	public void intercept(Invocation inv) {
		try {
			Controller ctl = inv.getController();
			String cookieId = ctl.getCookie(LoginService.sessionIdName);
			Record session = Db.findFirst("select * from session where id = ?", cookieId);
			String userId = session.getStr("userId");
			Record loginUser = Db.findFirst("select * from t_user where userId = ?", userId);
			ctl.setAttr("loginUser", loginUser);
			if (loginUser != null) {
				inv.invoke();
			} else {
				inv.getController().renderError(404);
			}
		} catch (Exception e) {
			inv.getController().renderError(500);
		}
	}
}
