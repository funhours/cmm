package com.mlongbo.jfinal.model;

import java.util.Map;

import com.jfinal.plugin.activerecord.Model;

/**
 * @author malongbo
 * @date 2015/2/13
 */
public class User extends Model<User> {
	public static String USER_ID = "userId";
	public static String LOGIN_NAME = "loginName";
	public static String NICK_NAME = "nickName";
	public static String PASSWORD = "password";
	public static String USERTYPE = "userType";
	public static String EMAIL = "email";
	public static String STATUS = "status";
	public static String CREATION_DATE = "creationDate";
	public static String AVATAR = "avatar";
	public static String PERSONAREA = "personArea";
	public static String TELEPHONE = "telephone";
	public static String QQ = "QQ";
	public static String WEIBO = "weiBo";
	public static String PERSONINFO = "personInfo";
	public static String SALT = "salt";

	
	private static final long serialVersionUID = 1L;
	public static final User user = new User();
    /**
     * 获取用户id*
     * @return 用户id
     */
    public String userId() {
        return getStr(USER_ID);
        
    }

    
	@Override
	public Map<String, Object> getAttrs() {
		return super.getAttrs();
	}
	
	public void setSalt(java.lang.String salt) {
		set(SALT, salt);
	}

	public java.lang.String getSalt() {
		return get(SALT);
	}
	
	public void setPassword(java.lang.String password) {
		set(PASSWORD, password);
	}

	public java.lang.String getPassword() {
		return get(PASSWORD);
	}

	public User removeSensitiveInfo() {
		remove(PASSWORD, "salt");
		return this;
	}
	
	public java.lang.Integer getStatus() {
		return get(STATUS);
	}
	

	
}
