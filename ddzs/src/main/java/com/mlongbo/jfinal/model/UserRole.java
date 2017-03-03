package com.mlongbo.jfinal.model;

import java.util.Map;

import com.jfinal.plugin.activerecord.Model;

/**
 * @author malongbo
 * @date 2015/2/13
 */
public class UserRole extends Model<UserRole> {
	public static String USER_ID = "userID";
	public static String ROLE_ID = "roleID";

	
	private static final long serialVersionUID = 1L;
	public static final UserRole userRole = new UserRole();

    
	@Override
	public Map<String, Object> getAttrs() {
		return super.getAttrs();
	}


	public static String getROLE_ID() {
		return ROLE_ID;
	}


	public static void setROLE_ID(String rOLE_ID) {
		ROLE_ID = rOLE_ID;
	}


	public static String getUSER_ID() {
		return USER_ID;
	}


	public static void setUSER_ID(String uSER_ID) {
		USER_ID = uSER_ID;
	}


}
