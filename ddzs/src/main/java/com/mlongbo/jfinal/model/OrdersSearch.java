package com.mlongbo.jfinal.model;

/**
 * @author malongbo
 * @date 2015/2/13
 */
public class OrdersSearch {
    
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
    public static String productSpec = "productSpec";
    public static String relationUser = "relationUser";
    public static String creationDate = "creationDate";
    public static String year = "year";
    public static String month = "month";
    public static String day = "day";
    
	
	private static final long serialVersionUID = 1L;
	public static final OrdersSearch dao = new OrdersSearch();
    
    public static String getOrderId() {
        return orderId;
    }
    
    public static void setOrderId(String orderId) {
        OrdersSearch.orderId = orderId;
    }
    
    public static String getProductName() {
        return productName;
    }
    
    public static void setProductName(String productName) {
        OrdersSearch.productName = productName;
    }
    
    public static String getProductCount() {
        return productCount;
    }
    
    public static void setProductCount(String productCount) {
        OrdersSearch.productCount = productCount;
    }
    
    public static String getProductPrice() {
        return productPrice;
    }
    
    public static void setProductPrice(String productPrice) {
        OrdersSearch.productPrice = productPrice;
    }
    
    public static String getRecipient() {
        return recipient;
    }
    
    public static void setRecipient(String recipient) {
        OrdersSearch.recipient = recipient;
    }
    
    public static String getRecipientTel() {
        return recipientTel;
    }
    
    public static void setRecipientTel(String recipientTel) {
        OrdersSearch.recipientTel = recipientTel;
    }
    
    public static String getRecipientAddress() {
        return recipientAddress;
    }
    
    public static void setRecipientAddress(String recipientAddress) {
        OrdersSearch.recipientAddress = recipientAddress;
    }
    
    public static String getShipper() {
        return shipper;
    }
    
    public static void setShipper(String shipper) {
        OrdersSearch.shipper = shipper;
    }
    
    public static String getShipperTel() {
        return shipperTel;
    }
    
    public static void setShipperTel(String shipperTel) {
        OrdersSearch.shipperTel = shipperTel;
    }
    
    public static String getOrderEntry() {
        return orderEntry;
    }
    
    public static void setOrderEntry(String orderEntry) {
        OrdersSearch.orderEntry = orderEntry;
    }
    
    public static String getRemarks() {
        return remarks;
    }
    
    public static void setRemarks(String remarks) {
        OrdersSearch.remarks = remarks;
    }
    
    public static String getOrderStatus() {
        return orderStatus;
    }
    
    public static void setOrderStatus(String orderStatus) {
        OrdersSearch.orderStatus = orderStatus;
    }
    
    public static String getRelationUser() {
        return relationUser;
    }
    
    public static void setRelationUser(String relationUser) {
        OrdersSearch.relationUser = relationUser;
    }
    
    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    
    public static String getYear() {
        return year;
    }

    
    public static void setYear(String year) {
        OrdersSearch.year = year;
    }

    
    public static String getMonth() {
        return month;
    }

    
    public static void setMonth(String month) {
        OrdersSearch.month = month;
    }

    
    public static String getDay() {
        return day;
    }

    
    public static void setDay(String day) {
        OrdersSearch.day = day;
    }

    
    public static String getProductSpec() {
        return productSpec;
    }

    
    public static void setProductSpec(String productSpec) {
        OrdersSearch.productSpec = productSpec;
    }
    
}
