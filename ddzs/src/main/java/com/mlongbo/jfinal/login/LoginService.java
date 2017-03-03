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

package com.mlongbo.jfinal.login;

import java.util.Date;

import com.jfinal.kit.HashKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;
import com.mlongbo.jfinal.model.Session;
import com.mlongbo.jfinal.model.User;

/**
 * 登录业务
 */
public class LoginService {

	public static final LoginService me = new LoginService();
	private final User UserDao = new User().dao();

	// 存放登录用户的 cacheName
	public static final String loginUserCacheName = "loginUser";

	// "jfinalId" 仅用于 cookie 名称，其它地方如 cache 中全部用的 "sessionId" 来做 key
	public static final String sessionIdName = "jfinalId";

	/**
	 * 登录成功返回 sessionId 与 loginUser，否则返回一个 msg
	 */
	public Ret login(String loginName, String password, boolean keepLogin, String loginIp) {
		loginName = loginName.toLowerCase().trim();
		password = password.trim();
		User loginUser = UserDao.findFirst("select * from t_user where loginName=? limit 1", loginName);
		if (loginUser == null) {
			return Ret.fail("msg", "用户名或密码不正确");
		}

		String salt = loginUser.getSalt();
		String hashedPass = HashKit.sha256(salt + password);
		// 未通过密码验证
		if (loginUser.getPassword().equals(hashedPass) == false) {
			return Ret.fail("msg", "用户名或密码不正确");
		}

		// 如果用户勾选保持登录，暂定过期时间为 3 年，否则为 120 分钟，单位为秒
		long liveSeconds =  keepLogin ? 3 * 365 * 24 * 60 * 60 : 120 * 60;
		// 传递给控制层的 cookie
		int maxAgeInSeconds = (int)(keepLogin ? liveSeconds : -1);
		// expireAt 用于设置 session 的过期时间点，需要转换成毫秒
		long expireAt = System.currentTimeMillis() + (liveSeconds * 1000);
		// 保存登录 session 到数据库
		Session session = new Session();
		String sessionId = StrKit.getRandomUUID();
		session.setId(sessionId);
		session.setUserId(loginUser.userId());
		session.setExpireAt(expireAt);
		if ( ! session.save()) {
			return Ret.fail("msg", "保存 session 到数据库失败，请联系管理员");
		}
        
		loginUser.put("sessionId", sessionId);                          // 保存一份 sessionId 到 loginUser 备用
		CacheKit.put(loginUserCacheName, sessionId, loginUser);

		createLoginLog(loginUser.userId(), loginIp);

		return Ret.ok(sessionIdName, sessionId)
						.set(loginUserCacheName, loginUser)
						.set("maxAgeInSeconds", maxAgeInSeconds);   // 用于设置 cookie 的最大存活时间
	}

	public User getLoginUserWithSessionId(String sessionId) {
		return CacheKit.get(loginUserCacheName, sessionId);
	}

	/**
	 * 通过 sessionId 获取登录用户信息
	 * sessoin表结构：session(id, UserId, expireAt)
	 *
	 * 1：先从缓存里面取，如果取到则返回该值，如果没取到则从数据库里面取
	 * 2：在数据库里面取，如果取到了，则检测是否已过期，如果过期则清除记录，
	 *     如果没过期则先放缓存一份，然后再返回
	 */
	public User loginWithSessionId(String sessionId, String loginIp) {
		Session session = Session.dao.findById(sessionId);
		if (session == null) {      // session 不存在
			return null;
		}
		if (session.isExpired()) {  // session 已过期
			session.delete();		// 被动式删除过期数据，此外还需要定时线程来主动清除过期数据
			return null;
		}

		User loginUser = UserDao.findById(session.getUserId());
		// 找到 loginUser 并且 是正常状态 才允许登录
		if (loginUser != null && loginUser.getStatus() == 1) {
			loginUser.removeSensitiveInfo();                                 // 移除 password 与 salt 属性值
			loginUser.put("sessionId", sessionId);                          // 保存一份 sessionId 到 loginUser 备用
			CacheKit.put(loginUserCacheName, sessionId, loginUser);

			createLoginLog(loginUser.userId(), loginIp);
			return loginUser;
		}
		return null;
	}

	/**
	 * 创建登录日志
	 */
	private void createLoginLog(String UserId, String loginIp) {
		Record loginLog = new Record().set("userId", UserId).set("ip", loginIp).set("loginAt", new Date());
		Db.save("login_log", loginLog);
	}


	/**
	 * 退出登录
	 */
	public void logout(String sessionId) {
		if (sessionId != null) {
			CacheKit.remove(loginUserCacheName, sessionId);
			Session.dao.deleteById(sessionId);
		}
	}

	/**
	 * 从数据库重新加载登录账户信息
	 */
	public void reloadLoginUser(User loginUserOld) {
		String sessionId = loginUserOld.get("sessionId");
		User loginUser = UserDao.findFirst("select * from t_user where userId=? limit 1", loginUserOld.userId());
		loginUser.removeSensitiveInfo();               // 移除 password 与 salt 属性值
		loginUser.put("sessionId", sessionId);        // 保存一份 sessionId 到 loginUser 备用

		// 集群方式下，要做一通知其它节点的机制，让其它节点使用缓存更新后的数据，
		// 将来可能把 User 用 id : obj 的形式放缓存，更新缓存只需要 CacheKit.remove("User", id) 就可以了，
		// 其它节点发现数据不存在会自动去数据库读取，所以未来可能就是在 UserService.getById(int id)的方法引入缓存就好
		// 所有用到 User 对象的地方都从这里去取
		CacheKit.put(loginUserCacheName, sessionId, loginUser);
	}
}
