package com.mlongbo.jfinal.model;

import com.jfinal.plugin.activerecord.Model;

/**
 * @author malongbo
 * @date 2015/2/13
 */
public class OrdersJdSearch {
    
    /**
     * @Field @serialVersionUID : TODO(这里用一句话描述这个类的作用)
     */
    private static final long serialVersionUID = -2923151700613835849L;
	public static final OrdersJdSearch dao = new OrdersJdSearch();
	
    public static String orderId = "orderId";
    public static String productName = "productName";
    public static String productCount = "productCount";
    public static String productPrice = "productPrice";
    public static String recipient = "recipient";
    public static String recipientTel = "recipientTel";
    public static String recipientAddress = "recipientAddress";
    public static String shipper = "shipper";
    public static String shipperTel = "shipperTel";
    public static String orderEntry = "orderEntry";
    public static String remarks = "remarks";
    public static String orderStatus = "orderStatus";
    public static String relationUser = "relationUser";
    public static String creationDate = "creationDate";
    
    public static String getOrderId() {
        return orderId;
    }
    
    public static void setOrderId(String orderId) {
        OrdersJdSearch.orderId = orderId;
    }
    
    public static String getProductName() {
        return productName;
    }
    
    public static void setProductName(String productName) {
        OrdersJdSearch.productName = productName;
    }
    
    public static String getProductCount() {
        return productCount;
    }
    
    public static void setProductCount(String productCount) {
        OrdersJdSearch.productCount = productCount;
    }
    
    public static String getProductPrice() {
        return productPrice;
    }
    
    public static void setProductPrice(String productPrice) {
        OrdersJdSearch.productPrice = productPrice;
    }
    
    public static String getRecipient() {
        return recipient;
    }
    
    public static void setRecipient(String recipient) {
        OrdersJdSearch.recipient = recipient;
    }
    
    public static String getRecipientTel() {
        return recipientTel;
    }
    
    public static void setRecipientTel(String recipientTel) {
        OrdersJdSearch.recipientTel = recipientTel;
    }
    
    public static String getRecipientAddress() {
        return recipientAddress;
    }
    
    public static void setRecipientAddress(String recipientAddress) {
        OrdersJdSearch.recipientAddress = recipientAddress;
    }
    
    public static String getShipper() {
        return shipper;
    }
    
    public static void setShipper(String shipper) {
        OrdersJdSearch.shipper = shipper;
    }
    
    public static String getShipperTel() {
        return shipperTel;
    }
    
    public static void setShipperTel(String shipperTel) {
        OrdersJdSearch.shipperTel = shipperTel;
    }
    
    public static String getOrderEntry() {
        return orderEntry;
    }
    
    public static void setOrderEntry(String orderEntry) {
        OrdersJdSearch.orderEntry = orderEntry;
    }
    
    public static String getRemarks() {
        return remarks;
    }
    
    public static void setRemarks(String remarks) {
        OrdersJdSearch.remarks = remarks;
    }
    
    public static String getOrderStatus() {
        return orderStatus;
    }
    
    public static void setOrderStatus(String orderStatus) {
        OrdersJdSearch.orderStatus = orderStatus;
    }
    
    public static String getRelationUser() {
        return relationUser;
    }
    
    public static void setRelationUser(String relationUser) {
        OrdersJdSearch.relationUser = relationUser;
    }
    
    public static long getSerialversionuid() {
        return serialVersionUID;
    }
    
    
	
}
