package com.mlongbo.jfinal.model;

import java.util.Date;
import java.util.Map;
import java.util.Set;

public class Utility {
    public static String[] makePara(Object obj, String objSearch, Map<String,String[]> paraMap) {
        String[] returnStr = new String[2];
        StringBuffer sqlStr = new StringBuffer(128);
        StringBuffer paraStr = new StringBuffer(128);
        Set<String> nameSet = paraMap.keySet();
        try {
            for(String name:nameSet){
                String[] props = name.split("\\.");
                if(props[0].equals(objSearch)){
                    Class<?> type = obj.getClass().getDeclaredField(props[1]).getType();
                    if (paraMap.get(name) != null && !paraMap.get(name)[0].equals("")) {
                        if(type == String.class){
                            sqlStr.append(" and ").append(props[1]).append(" like '%").append(paraMap.get(name)[0].trim()).append("%'");
                        }else if(type == Integer.class){
                            sqlStr.append(" and ").append(props[1]).append("=").append(paraMap.get(name)[0].trim());
                        }else if(type == Double.class){
                            sqlStr.append(" and ").append(props[1]).append("=").append(paraMap.get(name)[0].trim());
                        }else if(type == Boolean.class){
                            sqlStr.append(" and ").append(props[1]).append("=").append(paraMap.get(name)[0].trim());
                        }else if(type == Date.class){
                            sqlStr.append(" and ").append(props[1]).append(" ='").append(paraMap.get(name)[0].trim()).append("'");
                        }
                        paraStr.append("&").append(name).append("=").append(paraMap.get(name)[0].trim());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        returnStr[0] = sqlStr.toString();
        returnStr[1] = paraStr.toString();
        return returnStr;
    }
}
