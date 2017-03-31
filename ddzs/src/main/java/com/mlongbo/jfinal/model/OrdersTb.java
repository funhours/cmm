package com.mlongbo.jfinal.model;

import com.jfinal.plugin.activerecord.Model;

/**
 * @author malongbo
 * @date 2015/2/13
 */
public class OrdersTb extends Model<OrdersTb> {
    
    public static String ORDER_ID = "orderId";
    public static String PRODUCT_NAME = "productName";
    public static String PRODUCT_COUNT = "productCount";
    public static String PRODUCT_PRICE = "productPrice";
    public static String RECIPIENT = "recipient";
    public static String RECIPIENT_TEL = "recipientTel";
    public static String RECIPIENT_ADDRESS = "recipientAddress";
    public static String SHIPPER = "shipper";
    public static String SHIPPER_TEL = "shipperTel";
    public static String ORDER_ENTRY = "orderEntry";
    public static String REMARKS = "remarks";
    public static String ORDER_STATUS = "orderStatus";
    public static String RELATION_USER = "relationUser";
    public static String CREATION_DATE = "creationDate";
	
	private static final long serialVersionUID = 1L;
	public static final OrdersTb dao = new OrdersTb();
    
    public static String getORDER_ID() {
        return ORDER_ID;
    }
    
    public static void setORDER_ID(String oRDER_ID) {
        ORDER_ID = oRDER_ID;
    }
    
    public static String getPRODUCT_NAME() {
        return PRODUCT_NAME;
    }
    
    public static void setPRODUCT_NAME(String pRODUCT_NAME) {
        PRODUCT_NAME = pRODUCT_NAME;
    }
    
    public static String getPRODUCT_COUNT() {
        return PRODUCT_COUNT;
    }
    
    public static void setPRODUCT_COUNT(String pRODUCT_COUNT) {
        PRODUCT_COUNT = pRODUCT_COUNT;
    }
    
    public static String getPRODUCT_PRICE() {
        return PRODUCT_PRICE;
    }
    
    public static void setPRODUCT_PRICE(String pRODUCT_PRICE) {
        PRODUCT_PRICE = pRODUCT_PRICE;
    }
    
    public static String getRECIPIENT() {
        return RECIPIENT;
    }
    
    public static void setRECIPIENT(String rECIPIENT) {
        RECIPIENT = rECIPIENT;
    }
    
    public static String getRECIPIENT_TEL() {
        return RECIPIENT_TEL;
    }
    
    public static void setRECIPIENT_TEL(String rECIPIENT_TEL) {
        RECIPIENT_TEL = rECIPIENT_TEL;
    }
    
    public static String getRECIPIENT_ADDRESS() {
        return RECIPIENT_ADDRESS;
    }
    
    public static void setRECIPIENT_ADDRESS(String rECIPIENT_ADDRESS) {
        RECIPIENT_ADDRESS = rECIPIENT_ADDRESS;
    }
    
    public static String getSHIPPER() {
        return SHIPPER;
    }
    
    public static void setSHIPPER(String sHIPPER) {
        SHIPPER = sHIPPER;
    }
    
    public static String getSHIPPER_TEL() {
        return SHIPPER_TEL;
    }
    
    public static void setSHIPPER_TEL(String sHIPPER_TEL) {
        SHIPPER_TEL = sHIPPER_TEL;
    }
    
    public static String getORDER_ENTRY() {
        return ORDER_ENTRY;
    }
    
    public static void setORDER_ENTRY(String oRDER_ENTRY) {
        ORDER_ENTRY = oRDER_ENTRY;
    }
    
    public static String getREMARKS() {
        return REMARKS;
    }
    
    public static void setREMARKS(String rEMARKS) {
        REMARKS = rEMARKS;
    }
    
    public static String getORDER_STATUS() {
        return ORDER_STATUS;
    }
    
    public static void setORDER_STATUS(String oRDER_STATUS) {
        ORDER_STATUS = oRDER_STATUS;
    }
    
    public static String getRELATION_USER() {
        return RELATION_USER;
    }
    
    public static void setRELATION_USER(String rELATION_USER) {
        RELATION_USER = rELATION_USER;
    }
    
    public static String getCREATION_DATE() {
        return CREATION_DATE;
    }
    
    public static void setCREATION_DATE(String cREATION_DATE) {
        CREATION_DATE = cREATION_DATE;
    }
	
	
	
}
