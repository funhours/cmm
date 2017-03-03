package com.mlongbo.jfinal.model;

import java.util.Map;

import com.jfinal.plugin.activerecord.Model;

/**
 * @author malongbo
 * @date 2015/2/13
 */
public class Permission extends Model<Permission> {
	public static String PERMISSION_ID = "permissionID";
	public static String ESCRIPTION = "escription";

	
	private static final long serialVersionUID = 1L;
	public static final Permission permission = new Permission();

    
	@Override
	public Map<String, Object> getAttrs() {
		return super.getAttrs();
	}


	public static String getPERMISSION_ID() {
		return PERMISSION_ID;
	}


	public static void setPERMISSION_ID(String pERMISSION_ID) {
		PERMISSION_ID = pERMISSION_ID;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}




}
