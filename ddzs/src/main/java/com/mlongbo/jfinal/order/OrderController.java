package com.mlongbo.jfinal.order;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.alibaba.fastjson.JSONObject;
import com.csvreader.CsvReader;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.jfinal.aop.Before;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.upload.UploadFile;
import com.mlongbo.jfinal.common.utils.DateUtils;
import com.mlongbo.jfinal.common.utils.RandomUtils;
import com.mlongbo.jfinal.common.weixin.QRUtil;
import com.mlongbo.jfinal.common.weixin.WeiXinPayUtil;
import com.mlongbo.jfinal.config.AppProperty;
import com.mlongbo.jfinal.config.Context;
import com.mlongbo.jfinal.controller.BaseController;
import com.mlongbo.jfinal.kit.ExcelImportUtil;
import com.mlongbo.jfinal.model.OrderBuyConfig;
import com.mlongbo.jfinal.model.OrderStore;
import com.mlongbo.jfinal.model.Orders;
import com.mlongbo.jfinal.model.OrdersJd;
import com.mlongbo.jfinal.model.OrdersTb;
import com.mlongbo.jfinal.model.User;
import com.mlongbo.jfinal.vo.AjaxResult;

public class OrderController extends BaseController {
    private final AjaxResult result = new AjaxResult();
    static Orders dao = new Orders().dao();
    static OrdersTb daoTb = new OrdersTb().dao();
    static OrdersJd daoJd = new OrdersJd().dao();
    static OrderBuyConfig obcDao = new OrderBuyConfig().dao();
    Calendar now = Calendar.getInstance(); 
    
    /**
     * 
     * @Description (所有系统订单)
     */
    public void allOrder(){
        String userId = getLoginUserId();
        Page<Orders> allOrdersPage = dao.paginate(getParaToInt("p", 1), 10,"select * ","from orders where 1=1 and relationUser = '"+userId+"' order by creationDate");
        setAttr("allOrdersPage", allOrdersPage);
        render("allOrdersPage.html");
    }
    
    public void excelPage() {
        String userId = getLoginUserId();
        Page<Orders> ordersPage = dao.paginate(getParaToInt("xp", 1), 10,"select * ","from orders where 1=1 and relationUser = '"+userId+"' order by creationDate");
        setAttr("ordersPage", ordersPage);
        Page<OrdersTb> ordersTbPage = daoTb.paginate(getParaToInt("tp", 1), 10,"select * ","from orders_tb where 1=1 and relationUser = '"+userId+"' order by creationDate");
        setAttr("ordersTbPage", ordersTbPage);
        Page<OrdersJd> ordersJdPage = daoJd.paginate(getParaToInt("jp", 1), 10,"select * ","from orders_jd where 1=1 and relationUser = '"+userId+"' order by creationDate");
        setAttr("ordersJdPage", ordersJdPage);
        render("excelImport.html");
    }
    
    /**
     * 
     * @Description (作废订单)
     */
    public void invalid(){
        String userId = getLoginUserId();
        Page<Orders> ordersPage = dao.paginate(getParaToInt("p", 1), 10,"select * ","from orders where 1=1 and relationUser = '"+userId+"' order by creationDate");
        setAttr("ordersPage", ordersPage);
        render("orderInvalid.html");
    }
    
    /**
     * 
     * @Description (异常单)
     */
    public void exceptionOrder(){
        String userId = getLoginUserId();
        Page<Orders> ordersPage = dao.paginate(getParaToInt("p", 1), 10,"select * ","from orders where 1=1 and orderStatus = 7 and relationUser = '"+userId+"' order by creationDate");
        setAttr("exceptionOrdersPage", ordersPage);
        render("orderException.html");
    }
    
    /**
     * 
     * @Description (未审核单)
     */
    public void unReviewedOrder(){
        String userId = getLoginUserId();
        Page<Orders> reviewedPage = dao.paginate(getParaToInt("p", 1), 10,"select * ","from orders where 1=1 and orderStatus = 9 and relationUser = '"+userId+"' order by creationDate");
        setAttr("reviewedPage", reviewedPage);
        render("reviewed.html");
    }
    
    /**
     * 
     * @Description (审核通过)
     */
    public void reviewedById(){
        String orderId = getPara("orderId");
        boolean isReviewed = false;
        Orders order = dao.findById(orderId);
        order.set("orderStatus", 1);
        isReviewed = order.update();
        result.success(isReviewed);
        renderJson(result);
    }
    /**
     * 
     * @Description (审核不通过)
     */
    public void refuseById(){
        String orderId = getPara("orderId");
        boolean isReviewed = false;
        Orders order = dao.findById(orderId);
        order.set("orderStatus", 0);
        isReviewed = order.update();
        result.success(isReviewed);
        renderJson(result);
    }
    
    /**
     * 多个审核通过
     */
    public void reviewedByIds(){
        String _delsList = getPara("delsList");
        String delsList[] = _delsList.split(",");
        int resultCount = 0;
        for (int i = 0; i < delsList.length; i++) {
            Orders order = dao.findById(delsList[i]); 
            order.set("orderStatus", 1);
            order.update();
            resultCount ++;
        }
        result.setData(resultCount);
        renderJson(result);
    }
    
    /**
     * 多个审核不通过
     */
    public void refuseByIds(){
        String _delsList = getPara("delsList");
        String delsList[] = _delsList.split(",");
        int resultCount = 0;
        for (int i = 0; i < delsList.length; i++) {
            Orders order = dao.findById(delsList[i]); 
            order.set("orderStatus", 0);
            order.update();
            resultCount ++;
        }
        result.setData(resultCount);
        renderJson(result);
    }
    
    
    /**
     * 单个作废订单
     */
    public void invalidById(){
        String orderId = getPara("orderId");
        int orderFrom = getParaToInt("orderFrom");
        boolean isInvalid = false;
        try {
            switch (orderFrom) {
            case 1://系统订单
                Orders order = dao.findById(orderId);
                order.set("orderStatus", 8);
                isInvalid = order.update();
                result.success(isInvalid);
                break;
            case 2://淘宝订单
                OrdersTb orderTb = daoTb.findById(orderId);
                orderTb.set("orderStatus", 8);
                isInvalid = orderTb.update();
                result.success(isInvalid);
                break;
            case 3://京东订单
                OrdersJd orderJd = daoJd.findById(orderId);
                orderJd.set("orderStatus", 8);
                isInvalid = orderJd.update();
                result.success(isInvalid);
                break;
                
            default:
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        renderJson(result);
    }
    
    
    
    /**
     * 单个删除
     * @Description (TODO这里用一句话描述这个方法的作用)
     */
    public void deleteById(){
        String orderId = getPara("orderId");
        int orderFrom = getParaToInt("orderFrom");
        boolean isDelete = false;
        try {
            switch (orderFrom) {
            case 1://系统订单
                Orders order = dao.findById(orderId);
                isDelete = order.delete();
                result.success(isDelete);
                break;
            case 2://淘宝订单
                OrdersTb orderTb = daoTb.findById(orderId);
                isDelete = orderTb.delete();
                result.success(isDelete);
                break;
            case 3://京东订单
                OrdersJd orderJd = daoJd.findById(orderId);
                isDelete = orderJd.delete();
                result.success(isDelete);
                break;
                
            default:
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        renderJson(result);
    }
    
    /**
     * 多个删除
     */
    public void delByIds(){
        String _delsList = getPara("delsList");
        String delsList[] = _delsList.split(",");
        int resultCount = 0;
        for (int i = 0; i < delsList.length; i++) {
            Orders order = dao.findById(delsList[i]); 
            order.delete();
            resultCount ++;
        }
        result.setData(resultCount);
        renderJson(result);
    }
    
    
    /**
     * 多个作废
     */
    public void invalidByIds(){
        String _delsList = getPara("delsList");
        String delsList[] = _delsList.split(",");
        int resultCount = 0;
        for (int i = 0; i < delsList.length; i++) {
            Orders orders = dao.findById(delsList[i]); 
            orders.set("orderStatus", 8);
            orders.update();
            resultCount ++;
        }
        result.setData(resultCount);
        renderJson(result);
    }
    
    
    
    /**
     * 
     * @Description (模板下载)
     */
    public void downLoadOrderTemplateExcel(){
        File file = new File(PathKit.getWebRootPath() + "/upload/orderTemplate.rar");
        this.renderFile(file);
    }

    public void initimport(){
        ExcelImportUtil.deleteDir(new File(PathKit.getWebRootPath() + "/upload"));//清空upload的目录，减少服务器负压
        UploadFile files = getFile("orderFile");
          Map<String, Object> map=new LinkedHashMap<String, Object>();
          String extension = "";
          if (files==null) {
              map.put("msg", "请选择文件！");
              System.out.println(map);
          }
          else {
              extension = files.getOriginalFileName().substring(files.getOriginalFileName().lastIndexOf("."));
              String filename = PathKit.getWebRootPath() + "/upload/"
                      + files.getFileName();
              if (".xls".equals(extension)) {
                  filename = filename.replaceAll("\\\\", "/");
                  map = ExcelImportUtil.ReadExcel2003(filename,"insert into `order` (orderId,productName,productCount,productPrice,recipient,recipientTel,recipientAddress,shipper,shipperTel,orderEntry,remarks,orderStatus) values(?,?,?,?,?,?,?,?,?,?,?,?)",12);
                  System.out.println(map);
              }
              else{
                  map.put("msg", "上传的文件必须为2003版的excel表格！");
              }
              ExcelImportUtil.deleteFile(new File(filename));//录入完将上传的文件删除
          }
          result.success("导入成功");
          renderJson(result);
    }
    
    
    @Before(Tx.class)
    public void importExcel() {
        String userId = getLoginUserId();
        User user = new User().dao().findFirst("select * from t_user where userId = '"+ userId+"'" );
        User relationUser = new User().dao().findFirst(" select * from t_user where userId = '" + user.getStr("userId")+"'");
        // 获取文件
        UploadFile file = getFile("orderFile");
        String extension = file.getOriginalFileName().substring(file.getOriginalFileName().lastIndexOf("."));
        if (".xlsx".equals(extension)){
            result.addError("上传的文件必须为2003版的excel表格！");
            renderJson(result);
            return;
        }
        String path = file.getUploadPath() +  "\\"+file.getFileName();

        // 处理导入数据
        List<Map<Integer, String>> list = dealDataByPath(path); // 分析EXCEL数据
        int successCount = 0;
        int failCount = 0;
        for (int i = 1; i < list.size(); i++) {
            try {
                Map<Integer,String> map = list.get(i);
                Orders orders = new Orders();
                orders.set("orderId", map.get(0))
                .set("productName",map.get(1))
                .set("productCount",map.get(2))
                .set("productPrice",map.get(3))
                .set("productWeight",map.get(4))
                .set("recipient",map.get(5))
                .set("recipientTel",map.get(6))
                .set("provinceName",map.get(7))
                .set("cityName",map.get(8))
                .set("expAreaName",map.get(9))
                .set("recipientAddress",map.get(10))
                .set("shipper",map.get(11))
                .set("shipperTel",map.get(12))
                .set("orderEntry",map.get(13))
                .set("remarks",map.get(14))
                .set("orderStatus",map.get(15))
                .set("relationUser",relationUser.getStr("userId"))
                .set("creationDate",DateUtils.getNowTimeStamp())
                .set("year", now.get(Calendar.YEAR))
                .set("month", (now.get(Calendar.MONTH) + 1))
                .set("day", now.get(Calendar.DAY_OF_MONTH))
                .save();
                successCount++;
            } catch (Exception e) {
                failCount++;
            }
        }
        
        //更改订单使用量
        String oStoreSql = "select * from order_store where 1=1 and userId = '"+userId+"'";
        OrderStore orderStore = new OrderStore().dao().findFirst(oStoreSql);
        int remaining = orderStore.getInt("remaining");
        orderStore.set("remaining", remaining - successCount).update();
                
        // 返回结果
        JSONObject resultJson = new JSONObject();
        resultJson.put("successCount", successCount);
        resultJson.put("failCount", failCount);
        result.success(resultJson);
        renderJson(result);
    }
    
    /**
     * 
     * @throws Exception 
     * @Description (导入京东订单)
     */
   @Before(Tx.class)
    public void importJdExcel() {
        String userId = getLoginUserId();
        String _shipperInfo = getPara("shipperInfo");
        String[] shipperInfo = _shipperInfo.split(",");
        // 获取文件
        UploadFile file = getFile("jdOrderFile");
        String path = file.getUploadPath() +  "\\"+file.getFileName();

        // 处理导入数据
        List<Map<Integer, String>> list = jdDealDataByPath(path); // 分析EXCEL数据
        int successCount = 0;
        int failCount = 0;
        for (int i = 1; i < list.size(); i++) {
            try {
                Map<Integer,String> map = list.get(i);
                OrdersJd orders = new OrdersJd();
                orders.set("orderId", map.get(0))
                .set("productName",map.get(2))
                .set("productCount",map.get(3))
                .set("productPrice",map.get(10))
                .set("recipient",map.get(14))
                .set("recipientTel",map.get(16))
                .set("recipientAddress",map.get(15))
                .set("shipper", shipperInfo[0])// 寄件人
                .set("shipperTel", shipperInfo[1])// 寄件人电话
                .set("orderEntry", userId)// 录入者
                .set("relationUser", userId)
                .set("remarks",map.get(17))
                .set("orderStatus",map.get(10))
                .set("relationUser",userId)
                .set("creationDate",DateUtils.getNowTimeStamp())
                .set("year", now.get(Calendar.YEAR))
                .set("month", (now.get(Calendar.MONTH) + 1))
                .set("day", now.get(Calendar.DAY_OF_MONTH))
                .save();
                successCount++;
            } catch (Exception e) {
                e.printStackTrace();
                failCount++;
            }
        }
        
        //更改订单使用量
        String oStoreSql = "select * from order_store where 1=1 and userId = '"+userId+"'";
        OrderStore orderStore = new OrderStore().dao().findFirst(oStoreSql);
        int remaining = orderStore.getInt("remaining");
        orderStore.set("remaining", remaining - successCount).update();
                
        // 返回结果
        JSONObject resultJson = new JSONObject();
        resultJson.put("successCount", successCount);
        resultJson.put("failCount", failCount);
        result.success(resultJson);
        renderJson(result);
    }

    

   /**
    * 
    * @Description (编辑购买模板)
    */
   public void editBuyConfig(){
       int obcEditType = getParaToInt("obcEditType");
       if(obcEditType == 1){//修改
           String obcId = getPara("obcId");
           String obcPrice = getPara("obcPrice");
           
           OrderBuyConfig orderBuyConfig = OrderBuyConfig.dao.findById(obcId);
           orderBuyConfig.set("price", obcPrice).update();
           
           result.success("保存成功");
       }else if(obcEditType == 0){//新增
           OrderBuyConfig orderBuyConfig = getModel(OrderBuyConfig.class);
           orderBuyConfig.set("id", RandomUtils.randomCustomUUID()).save();
           
           result.success("保存成功");
       }
     
       renderJson(result);
   }
   
   /**
    * 
    * @Description (删除)
    */
   public void delObcById(){
       String obcId = getPara("obcId");
       boolean isDel = OrderBuyConfig.dao.findById(obcId).delete();
       if(isDel){
           result.success("删除成功");
       }else{
           result.addError("删除失败");
       }
       renderJson(result);
   }
    
   /**
    * 1.获取信息组装
    * 2.提交到支付数据
    * 3.TODO--记录日志----回调函数写
    * 4.TODO--更改用户短信条数----回调函数
    * @Description (购买订单)
    */
   public void buyOrder(){
       String userId = "";
       try {
           userId = getLoginUserId();
        
        } catch (Exception e) {
            userId = getPara("userId");
        }
       String orderBuyId = getPara("orderBuyId");
       
       OrderBuyConfig obConfig = obcDao.findById(orderBuyId);
       
       String order_price = obConfig.getBigDecimal("price").toString().replace(".",""); 
       String body = "蜂鸟订单系统订单条数购买："+obConfig.getInt("count"); 
       String out_trade_no = RandomUtils.randomCustomUUID();
       
       
       WeiXinPayUtil wxpy = new WeiXinPayUtil();
       String QrCode = "";
       String QrCodeUrl = "";
       String outUrl = "";
       try {
           QrCode = wxpy.weixin_pay(order_price,body,out_trade_no);
       } catch (Exception e) {
           e.printStackTrace();
       }
       
     //二维码宽高设置
   int width = 300; 
   int height = 300; 
   //二维码的图片格式
   String format = "gif"; 
   Hashtable hints = new Hashtable(); 
   //内容所使用编码 
   hints.put(EncodeHintType.CHARACTER_SET, "utf-8"); 
   BitMatrix bitMatrix;
   try {
       bitMatrix = new MultiFormatWriter().encode(QrCode,BarcodeFormat.QR_CODE, width, height, hints);
       //生成二维码 
//       String path = Class .class.getResource("/").toURI().getPath();
       String path = "";
       //表示存放在tomcat应用目录中
       if (AppProperty.me().appPath() == 1) {
           path = Context.me().getRequest().getSession().getServletContext().getRealPath("/");
       }
       
       path += AppProperty.me().uploadRooPath();
       
       File outputFile = new File(path+File.separator+"QR_zxh_"+userId+".gif");
       QRUtil.writeToFile(bitMatrix, format, outputFile); 
       QrCodeUrl = outputFile.getPath();
       int index = QrCodeUrl.lastIndexOf("\\");
       outUrl = QrCodeUrl.substring(index+1, QrCodeUrl.length());
   } catch (Exception e) {
       e.printStackTrace();
   } 

       result.success(outUrl);
       renderJson(result);
   }
    
    
    
    
    
    //--------------------------------------------------------------------------------------------

        /**
         * 分析excel的内容
         * @param path
         * @return
         */
        private List<Map<Integer, String>> dealDataByPath(String path) {
            List<Map<Integer,String>> list = new ArrayList<Map<Integer,String>>();
            // 工作簿
            HSSFWorkbook hwb = null;
            try {
                hwb = new HSSFWorkbook(new FileInputStream(new File(path)));
                
                HSSFSheet sheet = hwb.getSheetAt(0);    // 获取到第一个sheet中数据
                
                for(int i = 0;i<sheet.getLastRowNum() + 1; i++) {// 第二行开始取值，第一行为标题行
                    
                    HSSFRow row = sheet.getRow(i);      // 获取到第i列的行数据(表格行)
                    
                    Map<Integer, String> map = new HashMap<Integer, String>();

                    for(int j=0;j<row.getLastCellNum(); j++) {
                        
                        HSSFCell cell = row.getCell(j); // 获取到第j行的数据(单元格)

                        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                        
                        map.put(j, cell.getStringCellValue());
                    }
                    
                    list.add(map);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            return list;
        }
        
        /**
         * 分析excel的内容
         * @param path
         * @return
         */
        private List<Map<Integer, String>> jdDealDataByPath(String path) {
            List<Map<Integer,String>> list = new ArrayList<Map<Integer,String>>();
            // 工作簿
            XSSFWorkbook hwb = null;
            try {
                hwb = new XSSFWorkbook(new FileInputStream(new File(path)));
                
                XSSFSheet sheet = hwb.getSheetAt(0);    // 获取到第一个sheet中数据
                
                for(int i = 0;i<sheet.getLastRowNum() + 1; i++) {// 第二行开始取值，第一行为标题行
                    
                    XSSFRow row = sheet.getRow(i);      // 获取到第i列的行数据(表格行)
                    
                    Map<Integer, String> map = new HashMap<Integer, String>();

                    for(int j=0;j<row.getLastCellNum(); j++) {
                        
                        XSSFCell cell = row.getCell(j); // 获取到第j行的数据(单元格)
                        
                        if(cell == null){
                            map.put(j, "");
                            continue;
                        }

                        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                        
                        map.put(j, cell.getStringCellValue());
                    }
                    
                    list.add(map);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            return list;
        }
        
        /**
         * 分析excel的内容
         * @param path
         * @return
         * @throws Exception 
         */
        private List<OrdersJd> dealDataByPath2007(String path) throws Exception {
            InputStream is = new FileInputStream(path);
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
            int totalSheet = xssfWorkbook.getNumberOfSheets();
            System.out.println(totalSheet);
            
            OrdersJd order = null;
            List<OrdersJd> list = new ArrayList<OrdersJd>();
            for (int numSheet = 0; numSheet < totalSheet; numSheet++) {
                XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
                if (xssfSheet == null) {
                 continue;
                }
                // Read the Row
                for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
                   XSSFRow xssfRow = xssfSheet.getRow(rowNum);
                   if (xssfRow != null) {
                       try {
                           order = new OrdersJd();
                           XSSFCell orderId = xssfRow.getCell(0);
                           XSSFCell productName = xssfRow.getCell(2);
                           XSSFCell productCount = xssfRow.getCell(3);
                           XSSFCell productPrice = xssfRow.getCell(8);
                           XSSFCell recipient = xssfRow.getCell(14);
                           XSSFCell recipientTel = xssfRow.getCell(16);
                           XSSFCell recipientAddress = xssfRow.getCell(15);
                           XSSFCell remarks = xssfRow.getCell(18);
                           XSSFCell orderStatus = xssfRow.getCell(11);
                           
                           order.set("orderId", orderId);
                           order.set("productName", productName);
                           order.set("productCount", productCount);
                           order.set("productPrice", productPrice);
                           order.set("recipient", recipient);
                           order.set("recipientTel", recipientTel);
                           order.set("recipientAddress", recipientAddress);
                           order.set("remarks", remarks);
                           order.set("orderStatus", orderStatus);
                           order.set("creationDate", DateUtils.getNowTimeStamp());
                           list.add(order);
                       } catch (Exception e) {
                           e.printStackTrace();
                       }
                   }
                }
            }
            return list;
        }
        
        /**
         * 导入CSV
         * @param path
         * @return
         * @throws Exception 
         */
        @Before(Tx.class)
        public void csvReader() throws Exception {
           String userId = getLoginUserId();
           String _shipperInfo = getPara("shipperInfo");
           String[] shipperInfo = _shipperInfo.split(",");

            // 获取文件
            UploadFile file = getFile("csvOrderFile");
            String path = file.getUploadPath() +  "\\"+file.getFileName();
            CsvReader r = new CsvReader(path, ',',Charset.forName("GBK"));
            r.readHeaders();
            //逐条读取记录，直至读完
            OrdersTb order = null;
            int successCount = 0;
            int failCount = 0;
            while (r.readRecord()) {
                String recTel = r.get(16);
                if(recTel.substring(0).equals(",")){
                    recTel.substring(1,recTel.length());
                }
                try {
                    order = new OrdersTb();
                    order.set("orderId", r.get(0))
                    .set("productName",r.get(19))
                    .set("productCount",r.get(24))
                    .set("productPrice",r.get(8))
                    .set("recipient",r.get(12))
                    .set("recipientTel",recTel)
                    .set("recipientAddress",r.get(13))
                    .set("shipper",shipperInfo[0])//寄件人
                    .set("shipperTel",shipperInfo[1])//寄件人电话
                    .set("orderEntry",userId)//录入者
                    .set("remarks",r.get(23))
                    .set("orderStatus",r.get(10))
                    .set("relationUser",userId)
                    .set("creationDate",DateUtils.getNowTimeStamp())
                    .set("year", now.get(Calendar.YEAR))
                    .set("month", (now.get(Calendar.MONTH) + 1))
                    .set("day", now.get(Calendar.DAY_OF_MONTH))
                    .save();
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                }
            }
            r.close();
            
            //更改订单使用量
            String oStoreSql = "select * from order_store where 1=1 and userId = '"+userId+"'";
            OrderStore orderStore = new OrderStore().dao().findFirst(oStoreSql);
            int remaining = orderStore.getInt("remaining");
            orderStore.set("remaining", remaining - successCount).update();
            // 返回结果
            JSONObject resultJson = new JSONObject();
            resultJson.put("successCount", successCount);
            resultJson.put("failCount", failCount);
            result.success(resultJson);
            renderJson(result);
        }
}