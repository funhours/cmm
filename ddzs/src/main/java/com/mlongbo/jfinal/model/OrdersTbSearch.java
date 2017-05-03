package com.mlongbo.jfinal.model;

import com.jfinal.plugin.activerecord.Model;

/**
 * @author malongbo
 * @date 2015/2/13
 */
public class OrdersTbSearch {
    
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
	
	private static final long serialVersionUID = 1L;
	public static final OrdersTbSearch dao = new OrdersTbSearch();
    
    public static String getOrderId() {
        return orderId;
    }
    
    public static void setOrderId(String orderId) {
        OrdersTbSearch.orderId = orderId;
    }
    
    public static String getProductName() {
        return productName;
    }
    
    public static void setProductName(String productName) {
        OrdersTbSearch.productName = productName;
    }
    
    public static String getProductCount() {
        return productCount;
    }
    
    public static void setProductCount(String productCount) {
        OrdersTbSearch.productCount = productCount;
    }
    
    public static String getProductPrice() {
        return productPrice;
    }
    
    public static void setProductPrice(String productPrice) {
        OrdersTbSearch.productPrice = productPrice;
    }
    
    public static String getRecipient() {
        return recipient;
    }
    
    public static void setRecipient(String recipient) {
        OrdersTbSearch.recipient = recipient;
    }
    
    public static String getRecipientTel() {
        return recipientTel;
    }
    
    public static void setRecipientTel(String recipientTel) {
        OrdersTbSearch.recipientTel = recipientTel;
    }
    
    public static String getRecipientAddress() {
        return recipientAddress;
    }
    
    public static void setRecipientAddress(String recipientAddress) {
        OrdersTbSearch.recipientAddress = recipientAddress;
    }
    
    public static String getShipper() {
        return shipper;
    }
    
    public static void setShipper(String shipper) {
        OrdersTbSearch.shipper = shipper;
    }
    
    public static String getShipperTel() {
        return shipperTel;
    }
    
    public static void setShipperTel(String shipperTel) {
        OrdersTbSearch.shipperTel = shipperTel;
    }
    
    public static String getOrderEntry() {
        return orderEntry;
    }
    
    public static void setOrderEntry(String orderEntry) {
        OrdersTbSearch.orderEntry = orderEntry;
    }
    
    public static String getRemarks() {
        return remarks;
    }
    
    public static void setRemarks(String remarks) {
        OrdersTbSearch.remarks = remarks;
    }
    
    public static String getOrderStatus() {
        return orderStatus;
    }
    
    public static void setOrderStatus(String orderStatus) {
        OrdersTbSearch.orderStatus = orderStatus;
    }
    
    public static String getRelationUser() {
        return relationUser;
    }
    
    public static void setRelationUser(String relationUser) {
        OrdersTbSearch.relationUser = relationUser;
    }
    
    public static long getSerialversionuid() {
        return serialVersionUID;
    }
    
	
}
