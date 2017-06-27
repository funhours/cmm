package ddzs;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class test2 {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        
       /* String requestPara = "{'Count':1,'Data':[{'CallBack':'','EBusinessID':'1281025','LogisticCode':'00000000201705300','OrderCode':'0e9551cde53c4e97b1cb1eefb5047cd8','Reason':'','ShipperCode':'STO','State':'4','Success':true,'Traces':[{'AcceptStation':'深圳市横岗速递营销部已收件，（揽投员姓名：钟某某;联系电话：18000000000）','AcceptTime':'2017-05-30 17:01:21'},{'AcceptStation':'离开深圳市 发往广州市','AcceptTime':'2017-05-30 21:01:21'},{'AcceptStation':'呼和浩特市邮政速递物流分公司金川揽投部安排投递（投递员姓名：安某;联系电话：18800000000）','AcceptTime':'2017-05-31 14:01:21'},{'AcceptStation':'收件人电话无法接通，已转为问题件延迟派送','AcceptTime':'2017-05-31 15:01:21'}]}],'EBusinessID':'1281025','PushTime':'2017-05-31 17:01:21'}";
        JSONObject jobj = (JSONObject) JSONObject.parse(requestPara);
        JSONArray data = (JSONArray) jobj.get("Data");
        JSONObject data0 = (JSONObject) JSONObject.parse(data.get(0).toString());
        JSONArray arrTraces = data0.getJSONArray("Traces");
        String errAcceptStation = arrTraces.getJSONObject(arrTraces.size()-1).getString("AcceptStation");
        System.out.println(data0.get("State"));
        System.out.println(errAcceptStation);
        System.out.println(data0.get("OrderCode"));
        System.out.println(data0.get("Reason"));*/
        
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String AcceptTime = df.format(new Date());
        
        String Traces = "{\"EBusinessID\":\"1281025\",\"UpdateTime\":\"" + AcceptTime + "\",\"Success\": true,\"Reason\": \"\"}";
        System.out.println(Traces);
    }

}
