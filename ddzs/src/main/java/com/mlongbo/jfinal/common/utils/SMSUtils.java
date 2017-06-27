package com.mlongbo.jfinal.common.utils;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.emay.sdk.client.api.Client;

/**
 * 短信相关的工具类*
 * @author malongbo
 */
public class SMSUtils {


    /**
     * 检测手机号有效性*
     * @param mobile 手机号码
     * @return 是否有效
     */
    public static final boolean isMobileNo(String mobile){
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobile);
        return m.matches();
    }
    
    /**
     * 生成短信验证码*
     * @param length 长度
     * @return 指定长度的随机短信验证码
     */
    public static final String randomSMSCode(int length) {
        boolean numberFlag = true;
        String retStr = "";
        String strTable = numberFlag ? "1234567890" : "1234567890abcdefghijkmnpqrstuvwxyz";
        int len = strTable.length();
        boolean bDone = true;
        do {
            retStr = "";
            int count = 0;
            for (int i = 0; i < length; i++) {
                double dblR = Math.random() * len;
                int intR = (int) Math.floor(dblR);
                char c = strTable.charAt(intR);
                if (('0' <= c) && (c <= '9')) {
                    count++;
                }
                retStr += strTable.charAt(intR);
            }
            if (count >= 2) {
                bDone = false;
            }
        } while (bDone);
        return retStr;
    }
    
    private static Client client=null;
	
	
	
	
	/**
	 * 发送短信、可以发送定时和即时短信
	 * sendSMS(String[] mobiles,String smsContent, String addSerial, int smsPriority)
	 * 1、mobiles 手机数组长度不能超过1000
	 * 2、smsContent 最多500个汉字或1000个纯英文、请客户不要自行拆分短信内容以免造成混乱、亿美短信平台会根据实际通道自动拆分、计费以实际拆分条数为准、亿美推荐短信长度70字以内 
	 * 3、addSerial 附加码(长度小于15的字符串) 用户可通过附加码自定义短信类别,或添加自定义主叫号码( 联系亿美索取主叫号码列表)
	 * 4、优先级范围1~5，数值越高优先级越高(相对于同一序列号)
	 * 5、其它短信发送请参考使用手册自己尝试使用
	 */
    /**
     * 发送短信验证码*
     * @param mobiles 手机号码数组
     * @param content 验证码
     * @return 是否发送成功
     */
	public static boolean sendCode(String[] mobiles, String content) {
		String regContent = "【蜂鸟打单助手】你的验证是："+content+"，请及时完成注册.";
		try {
			int i=getClient().sendSMS(mobiles, regContent,"蜂鸟打单助手",3);//带扩展码
			if(i == 0){
				return  true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
     * 发送短信验证码*
     * @param mobiles 手机号码数组
     * @param content 验证码
     * @return 是否发送成功
     */
    public static boolean sendOrderSms(String[] mobiles, String content) {
        String rcontent = "【蜂鸟打单助手】："+content;
        try {
            int i=getClient().sendSMS(mobiles, rcontent,"蜂鸟打单助手",3);//带扩展码
            if(i == 0){
                return  true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

   
    public synchronized static Client getClient(){
		ResourceBundle bundle=PropertyResourceBundle.getBundle("smsConfig");
		if(client==null){
			try {
				client=new Client(bundle.getString("softwareSerialNo"),bundle.getString("key"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return client;
	}
}
