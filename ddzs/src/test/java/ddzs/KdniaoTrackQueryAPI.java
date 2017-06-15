package ddzs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map; 

/**
 *
 * 快递鸟物流轨迹即时查询接口
 *
 * @技术QQ群: 456320272
 * @see: http://www.kdniao.com/YundanChaxunAPI.aspx
 * @copyright: 深圳市快金数据技术服务有限公司
 *
 * DEMO中的电商ID与私钥仅限测试使用，正式环境请单独注册账号
 * 单日超过500单查询量，建议接入我方物流轨迹订阅推送接口
 * 
 * ID和Key请到官网申请：http://www.kdniao.com/ServiceApply.aspx
 */

public class KdniaoTrackQueryAPI {
	
	//DEMO
	public static void main(String[] args) {
		KdniaoTrackQueryAPI api = new KdniaoTrackQueryAPI();
		try {
			String result = api.getOrderTracesByJson("ANE", "210001633605");
			System.out.print(result);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//电商ID
	private String EBusinessID="1281025";
	//电商加密私钥，快递鸟提供，注意保管，不要泄漏
	private String AppKey="87c84d45-2134-472e-92e7-6a258b9d978e";
	//请求url
	private String ReqURL="http://localhost:8183/order/dist";	
 
	/**
     * Json方式 查询订单物流轨迹
	 * @throws Exception 
     */
	public String getOrderTracesByJson(String expCode, String expNo) throws Exception{
		String requestData= "{'Count':1,'Data':[{'CallBack':'3','EBusinessID':'1281025','LogisticCode':'00000000201704270','OrderCode':'','Reason':'','ShipperCode':'STO','State':'2','Success':true,'Traces':[{'AcceptStation':'深圳市横岗速递营销部已收件，（揽投员姓名：钟某某;联系电话：18000000000）','AcceptTime':'2017-04-27 10:15:24'},{'AcceptStation':'离开深圳市 发往广州市','AcceptTime':'2017-04-27 14:15:24'},{'AcceptStation':'呼和浩特市邮政速递物流分公司金川揽投部安排投递（投递员姓名：安某;联系电话：18800000000）','AcceptTime':'2017-04-28 07:15:24'}]}],'EBusinessID':'1281025','PushTime':'2017-04-28 10:15:24'}";
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("RequestData", urlEncoder(requestData, "UTF-8"));
		params.put("EBusinessID", EBusinessID);
		params.put("RequestType", "1002");
		String dataSign="ZTk1MGFkMDM5N2ZjZWE0MDE0OGI1YjY3N2MwNjZlYjg%3d&RequestData=%7b%22Count%22%3a1%2c%22Data%22%3a%5b%7b%22CallBack%22%3a%223%22%2c%22EBusinessID%22%3a%221281025%22%2c%22LogisticCode%22%3a%2200000000201704270%22%2c%22OrderCode%22%3a%22%22%2c%22Reason%22%3a%22%22%2c%22ShipperCode%22%3a%22STO%22%2c%22State%22%3a%222%22%2c%22Success%22%3atrue%2c%22Traces%22%3a%5b%7b%22AcceptStation%22%3a%22%e6%b7%b1%e5%9c%b3%e5%b8%82%e6%a8%aa%e5%b2%97%e9%80%9f%e9%80%92%e8%90%a5%e9%94%80%e9%83%a8%e5%b7%b2%e6%94%b6%e4%bb%b6%ef%bc%8c%ef%bc%88%e6%8f%bd%e6%8a%95%e5%91%98%e5%a7%93%e5%90%8d%ef%bc%9a%e9%92%9f%e6%9f%90%e6%9f%90%3b%e8%81%94%e7%b3%bb%e7%94%b5%e8%af%9d%ef%bc%9a18000000000%ef%bc%89%22%2c%22AcceptTime%22%3a%222017-04-27+10%3a15%3a24%22%7d%2c%7b%22AcceptStation%22%3a%22%e7%a6%bb%e5%bc%80%e6%b7%b1%e5%9c%b3%e5%b8%82+%e5%8f%91%e5%be%80%e5%b9%bf%e5%b7%9e%e5%b8%82%22%2c%22AcceptTime%22%3a%222017-04-27+14%3a15%3a24%22%7d%2c%7b%22AcceptStation%22%3a%22%e5%91%bc%e5%92%8c%e6%b5%a9%e7%89%b9%e5%b8%82%e9%82%ae%e6%94%bf%e9%80%9f%e9%80%92%e7%89%a9%e6%b5%81%e5%88%86%e5%85%ac%e5%8f%b8%e9%87%91%e5%b7%9d%e6%8f%bd%e6%8a%95%e9%83%a8%e5%ae%89%e6%8e%92%e6%8a%95%e9%80%92%ef%bc%88%e6%8a%95%e9%80%92%e5%91%98%e5%a7%93%e5%90%8d%ef%bc%9a%e5%ae%89%e6%9f%90%3b%e8%81%94%e7%b3%bb%e7%94%b5%e8%af%9d%ef%bc%9a18800000000%ef%bc%89%22%2c%22AcceptTime%22%3a%222017-04-28+07%3a15%3a24%22%7d%5d%7d%5d%2c%22EBusinessID%22%3a%221281025%22%2c%22PushTime%22%3a%222017-04-28+10%3a15%3a24%22%7d&RequestType=102";
		params.put("DataSign", urlEncoder(dataSign, "UTF-8"));
		params.put("DataType", "2");
		
		String result=sendPost(ReqURL, params);	
		
		//根据公司业务处理返回的信息......
		
		return result;
	}
	

 
	/**
     * MD5加密
     * @param str 内容       
     * @param charset 编码方式
	 * @throws Exception 
     */
	@SuppressWarnings("unused")
	private String MD5(String str, String charset) throws Exception {
	    MessageDigest md = MessageDigest.getInstance("MD5");
	    md.update(str.getBytes(charset));
	    byte[] result = md.digest();
	    StringBuffer sb = new StringBuffer(32);
	    for (int i = 0; i < result.length; i++) {
	        int val = result[i] & 0xff;
	        if (val <= 0xf) {
	            sb.append("0");
	        }
	        sb.append(Integer.toHexString(val));
	    }
	    return sb.toString().toLowerCase();
	}
	
	/**
     * base64编码
     * @param str 内容       
     * @param charset 编码方式
	 * @throws UnsupportedEncodingException 
     */
	private String base64(String str, String charset) throws UnsupportedEncodingException{
		String encoded = base64Encode(str.getBytes(charset));
		return encoded;    
	}	
	
	@SuppressWarnings("unused")
	private String urlEncoder(String str, String charset) throws UnsupportedEncodingException{
		String result = URLEncoder.encode(str, charset);
		return result;
	}
	
	/**
     * 电商Sign签名生成
     * @param content 内容   
     * @param keyValue Appkey  
     * @param charset 编码方式
	 * @throws UnsupportedEncodingException ,Exception
	 * @return DataSign签名
     */
	@SuppressWarnings("unused")
	private String encrypt (String content, String keyValue, String charset) throws UnsupportedEncodingException, Exception
	{
		if (keyValue != null)
		{
			return base64(MD5(content + keyValue, charset), charset);
		}
		return base64(MD5(content, charset), charset);
	}
	
	 /**
     * 向指定 URL 发送POST方法的请求     
     * @param url 发送请求的 URL    
     * @param params 请求的参数集合     
     * @return 远程资源的响应结果
     */
	@SuppressWarnings("unused")
	private String sendPost(String url, Map<String, String> params) {
        OutputStreamWriter out = null;
        BufferedReader in = null;        
        StringBuilder result = new StringBuilder(); 
        try {
            URL realUrl = new URL(url);
            HttpURLConnection conn =(HttpURLConnection) realUrl.openConnection();
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // POST方法
            conn.setRequestMethod("POST");
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.connect();
            // 获取URLConnection对象对应的输出流
            out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            // 发送请求参数            
            if (params != null) {
		          StringBuilder param = new StringBuilder(); 
		          for (Map.Entry<String, String> entry : params.entrySet()) {
		        	  if(param.length()>0){
		        		  param.append("&");
		        	  }	        	  
		        	  param.append(entry.getKey());
		        	  param.append("=");
		        	  param.append(entry.getValue());		        	  
		        	  //System.out.println(entry.getKey()+":"+entry.getValue());
		          }
		          //System.out.println("param:"+param.toString());
		          out.write(param.toString());
            }
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {            
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result.toString();
    }
	
	
    private static char[] base64EncodeChars = new char[] { 
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 
        'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 
        'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 
        'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 
        'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 
        'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 
        'w', 'x', 'y', 'z', '0', '1', '2', '3', 
        '4', '5', '6', '7', '8', '9', '+', '/' }; 
	
    public static String base64Encode(byte[] data) { 
        StringBuffer sb = new StringBuffer(); 
        int len = data.length; 
        int i = 0; 
        int b1, b2, b3; 
        while (i < len) { 
            b1 = data[i++] & 0xff; 
            if (i == len) 
            { 
                sb.append(base64EncodeChars[b1 >>> 2]); 
                sb.append(base64EncodeChars[(b1 & 0x3) << 4]); 
                sb.append("=="); 
                break; 
            } 
            b2 = data[i++] & 0xff; 
            if (i == len) 
            { 
                sb.append(base64EncodeChars[b1 >>> 2]); 
                sb.append(base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]); 
                sb.append(base64EncodeChars[(b2 & 0x0f) << 2]); 
                sb.append("="); 
                break; 
            } 
            b3 = data[i++] & 0xff; 
            sb.append(base64EncodeChars[b1 >>> 2]); 
            sb.append(base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]); 
            sb.append(base64EncodeChars[((b2 & 0x0f) << 2) | ((b3 & 0xc0) >>> 6)]); 
            sb.append(base64EncodeChars[b3 & 0x3f]); 
        } 
        return sb.toString(); 
    }
}
