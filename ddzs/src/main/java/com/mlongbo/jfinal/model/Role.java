package com.mlongbo.jfinal.model;

import java.util.Map;

import com.jfinal.plugin.activerecord.Model;

/**
 * @author malongbo
 * @date 2015/2/13
 */
public class Role extends Model<Role> {
	public static String ROLE_ID = "roleID";
	public static String DESCRIPTION = "description";

	
	private static final long serialVersionUID = 1L;
	public static final Role role = new Role();
    /**
     * 获取用户id*
     * @return 用户id
     */
    public String roleId() {
        return getStr(ROLE_ID);
        
    }

    
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


	public static String getDESCRIPTION() {
		return DESCRIPTION;
	}


	public static void setDESCRIPTION(String dESCRIPTION) {
		DESCRIPTION = dESCRIPTION;
	}
	
	

	
}
