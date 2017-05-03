package com.mlongbo.jfinal.model.esheet;

import java.math.BigDecimal;

public class Commodity {
    public static String GoodsName = "";
//    public static String GoodsCode = "";
    public static int Goodsquantity;
    public static BigDecimal GoodsPrice;
    public static double GoodsWeight;
//    public static String GoodsDesc = "";
    public static double GoodsVol;
    
    public static String getGoodsName() {
        return GoodsName;
    }
    
    public static void setGoodsName(String goodsName) {
        GoodsName = goodsName;
    }
    
//    public static String getGoodsCode() {
//        return GoodsCode;
//    }
//    
//    public static void setGoodsCode(String goodsCode) {
//        GoodsCode = goodsCode;
//    }
    
    public static int getGoodsquantity() {
        return Goodsquantity;
    }
    
    public static void setGoodsquantity(int goodsquantity) {
        Goodsquantity = goodsquantity;
    }
    
    
    public static BigDecimal getGoodsPrice() {
        return GoodsPrice;
    }

    
    public static void setGoodsPrice(BigDecimal goodsPrice) {
        GoodsPrice = goodsPrice;
    }

    public static double getGoodsWeight() {
        return GoodsWeight;
    }
    
    public static void setGoodsWeight(double goodsWeight) {
        GoodsWeight = goodsWeight;
    }
    
  /*  public static String getGoodsDesc() {
        return GoodsDesc;
    }
    
    public static void setGoodsDesc(String goodsDesc) {
        GoodsDesc = goodsDesc;
    }*/
    
    public static double getGoodsVol() {
        return GoodsVol;
    }
    
    public static void setGoodsVol(double goodsVol) {
        GoodsVol = goodsVol;
    }
    
    
    
}
