package ddzs;

import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class test {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
      /*  String url = " E:\\git\\ddzs\\src\\main\\webapp\\upload\\QR_zxh_4846509074d540db89ce7de5e658c7e0.gif ";
        int index = url.lastIndexOf("\\");
        String outUrl = url.substring(index+1, url.length());
        System.out.println(outUrl);*/
       /*Calendar now = Calendar.getInstance();
       String i = now.get(Calendar.YEAR)+""+(now.get(Calendar.MONTH) + 1)+""+now.get(Calendar.DAY_OF_MONTH)+"" + RandomUtils.randomString(5)+"";
       System.out.println(i);*/
        
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        System.out.println(df.format(new Date()));// new Date()为获取当前系统时间
    }

}
