package com.mlongbo.jfinal.model.esheet;


public class ESheetRequestInfo {
    
    public static String MemberID = "";
    public static String CustomerName ;
    public static String CustomerPwd ;
//    public static String SendSite ;
    public static String ShipperCode ;
//    public static String LogisticCode ;
    public static String ThrOrderCode ;
    public static String OrderCode ;
//    public static String MonthCode ;
    public static int PayType = 1;
    public static int ExpType = 1;//快递类型：1-标准快件
    public static int IsNotice = 0;//是否通知快递员上门揽件：0-通知；1-不通知；不填则默认为0
    public static double Cost = 0;
    public static double OtherCost = 0;
    public static Receiver receiver ;
    public static Sender sender ;
    public static String StartDate = "";//上门取货时间段:"yyyy-MM-dd HH:mm:ss"格式化，本文中所有时间格式相同
    public static double Weight ;
    public static int Quantity ;
    public static double Volume ;
    public static String Remark;
    public static Commodity commodity ;
    public static int IsReturnPrintTemplate = 1;
    
    public static String getMemberID() {
        return MemberID;
    }
    
    public static void setMemberID(String memberID) {
        MemberID = memberID;
    }
    
    public static String getCustomerName() {
        return CustomerName;
    }
    
    public static void setCustomerName(String customerName) {
        CustomerName = customerName;
    }
    
    public static String getCustomerPwd() {
        return CustomerPwd;
    }
    
    public static void setCustomerPwd(String customerPwd) {
        CustomerPwd = customerPwd;
    }
    
  /*  public static String getSendSite() {
        return SendSite;
    }
    
    public static void setSendSite(String sendSite) {
        SendSite = sendSite;
    }*/
    
    public static String getShipperCode() {
        return ShipperCode;
    }
    
    public static void setShipperCode(String shipperCode) {
        ShipperCode = shipperCode;
    }
    
/*    public static String getLogisticCode() {
        return LogisticCode;
    }
    
    public static void setLogisticCode(String logisticCode) {
        LogisticCode = logisticCode;
    }*/
    
    public static String getThrOrderCode() {
        return ThrOrderCode;
    }
    
    public static void setThrOrderCode(String thrOrderCode) {
        ThrOrderCode = thrOrderCode;
    }
    
    public static String getOrderCode() {
        return OrderCode;
    }
    
    public static void setOrderCode(String orderCode) {
        OrderCode = orderCode;
    }
    
   /* public static String getMonthCode() {
        return MonthCode;
    }
    
    public static void setMonthCode(String monthCode) {
        MonthCode = monthCode;
    }*/
    
    public static int getPayType() {
        return PayType;
    }
    
    public static void setPayType(int payType) {
        PayType = payType;
    }
    
    
    public static int getExpType() {
        return ExpType;
    }

    
    public static void setExpType(int expType) {
        ExpType = expType;
    }

    public static int getIsNotice() {
        return IsNotice;
    }
    
    public static void setIsNotice(int isNotice) {
        IsNotice = isNotice;
    }
    
    public static double getCost() {
        return Cost;
    }
    
    public static void setCost(double cost) {
        Cost = cost;
    }
    
    public static double getOtherCost() {
        return OtherCost;
    }
    
    public static void setOtherCost(double otherCost) {
        OtherCost = otherCost;
    }
    
    public static Receiver getReceiver() {
        return receiver;
    }
    
    public static void setReceiver(Receiver receiver) {
        ESheetRequestInfo.receiver = receiver;
    }
    
    public static Sender getSender() {
        return sender;
    }
    
    public static void setSender(Sender sender) {
        ESheetRequestInfo.sender = sender;
    }

    public static String getStartDate() {
        return StartDate;
    }
    
    public static void setStartDate(String startDate) {
        StartDate = startDate;
    }
    
    public static double getWeight() {
        return Weight;
    }
    
    public static void setWeight(double weight) {
        Weight = weight;
    }
    
    public static int getQuantity() {
        return Quantity;
    }
    
    public static void setQuantity(int quantity) {
        Quantity = quantity;
    }
    
    public static double getVolume() {
        return Volume;
    }
    
    public static void setVolume(double volume) {
        Volume = volume;
    }
    
    public static Commodity getCommodity() {
        return commodity;
    }
    
    public static void setCommodity(Commodity commodity) {
        ESheetRequestInfo.commodity = commodity;
    }
    
    public static int getIsReturnPrintTemplate() {
        return IsReturnPrintTemplate;
    }

    
    public static void setIsReturnPrintTemplate(int isReturnPrintTemplate) {
        IsReturnPrintTemplate = isReturnPrintTemplate;
    }

    public static String getRemark() {
        return Remark;
    }

    
    public static void setRemark(String remark) {
        Remark = remark;
    }
    
    
    

}
