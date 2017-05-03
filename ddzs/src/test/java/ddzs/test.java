package ddzs;

public abstract class test {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        String url = " E:\\git\\ddzs\\src\\main\\webapp\\upload\\QR_zxh_4846509074d540db89ce7de5e658c7e0.gif ";
        int index = url.lastIndexOf("\\");
        String outUrl = url.substring(index+1, url.length());
        System.out.println(outUrl);
    }

}
