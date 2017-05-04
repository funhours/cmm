package com.mlongbo.jfinal.login;

import com.jfinal.kit.HashKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;
import com.mlongbo.jfinal.model.OrderStore;
import com.mlongbo.jfinal.model.Session;
import com.mlongbo.jfinal.model.User;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoginService
{
  public static final LoginService me = new LoginService();
  private final User UserDao = (User)new User().dao();
  public static final String loginUserCacheName = "loginUser";
  public static final String sessionIdName = "jfinalId";

  public Ret login(String loginName, String password, boolean keepLogin, String loginIp)
  {
    loginName = loginName.toLowerCase().trim();
    password = password.trim();
    User loginUser = (User)this.UserDao.findFirst("select * from t_user where loginName=? limit 1", new Object[] { loginName });
    if (loginUser == null) {
      return Ret.fail("msg", "用户名或密码不正确");
    }

    String hashedPass = HashKit.sha256("kyddzs" + password);

    if (!loginUser.getPassword().equals(hashedPass)) {
      return Ret.fail("msg", "用户名或密码不正确");
    }

    long liveSeconds = keepLogin ? 94608000 : 7200;

    int maxAgeInSeconds = (int)(keepLogin ? liveSeconds : -1L);

    long expireAt = System.currentTimeMillis() + liveSeconds * 1000L;

    Session session = new Session();
    String sessionId = StrKit.getRandomUUID();
    session.setId(sessionId);
    session.setUserId(loginUser.getUserId());
    session.setExpireAt(Long.valueOf(expireAt));
    if (!session.save()) {
      return Ret.fail("msg", "保存 session 到数据库失败，请联系管理员");
    }

    loginUser.put("sessionId", sessionId);
    CacheKit.put("loginUser", sessionId, loginUser);

    createLoginLog(loginUser.getUserId(), loginIp);

    String orderRemainingSql = "select * from order_store where 1=1 and userId = ?";
    OrderStore orderStore = (OrderStore)OrderStore.dao.findFirst(orderRemainingSql, new Object[] { loginUser.getUserId() });

    boolean isExpired = false;
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String expiryDate = loginUser.get("expiryDate").toString();
    try {
      Date date = df.parse(expiryDate);
      long diff = date.getTime() - System.currentTimeMillis();
      if (diff <= 60000L)
        isExpired = true;
    }
    catch (ParseException e) {
      e.printStackTrace();
    }

    return Ret.ok("jfinalId", sessionId)
      .set("loginUser", loginUser)
      .set("orderStore", orderStore)
      .set("isExpired", Boolean.valueOf(isExpired))
      .set("maxAgeInSeconds", Integer.valueOf(maxAgeInSeconds));
  }

  public User getLoginUserWithSessionId(String sessionId) {
    return (User)CacheKit.get("loginUser", sessionId);
  }

  public User loginWithSessionId(String sessionId, String loginIp)
  {
    Session session = (Session)Session.dao.findById(sessionId);
    if (session == null) {
      return null;
    }
    if (session.isExpired()) {
      session.delete();
      return null;
    }

    User loginUser = (User)this.UserDao.findById(session.getUserId());

    if ((loginUser != null) && (loginUser.getStatus().intValue() == 1)) {
      loginUser.removeSensitiveInfo();
      loginUser.put("sessionId", sessionId);
      CacheKit.put("loginUser", sessionId, loginUser);

      createLoginLog(loginUser.getUserId(), loginIp);
      return loginUser;
    }
    return null;
  }

  private void createLoginLog(String UserId, String loginIp)
  {
    Record loginLog = new Record().set("userId", UserId).set("ip", loginIp).set("loginAt", new Date());
    Db.save("login_log", loginLog);
  }

  public void logout(String sessionId)
  {
    if (sessionId != null) {
      CacheKit.remove("loginUser", sessionId);
      Session.dao.deleteById(sessionId);
    }
  }

  public void reloadLoginUser(User loginUserOld)
  {
    String sessionId = (String)loginUserOld.get("sessionId");
    User loginUser = (User)this.UserDao.findFirst("select * from t_user where userId=? limit 1", new Object[] { loginUserOld.getUserId() });
    loginUser.removeSensitiveInfo();
    loginUser.put("sessionId", sessionId);

    CacheKit.put("loginUser", sessionId, loginUser);
  }
}