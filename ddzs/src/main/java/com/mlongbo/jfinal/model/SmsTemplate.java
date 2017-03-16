package com.mlongbo.jfinal.model;

import com.jfinal.plugin.activerecord.Model;

/**
 * @author malongbo
 * @date 2015/2/13
 */
public class SmsTemplate extends Model<SmsTemplate> {
	public static String ID = "id";
	public static String USERID = "userId";
	public static String TEMPLATE = "template";

	
	private static final long serialVersionUID = 1L;
	public static final SmsTemplate dao = new SmsTemplate();
	
}
