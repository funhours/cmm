package com.mlongbo.jfinal.order;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
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
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.upload.UploadFile;
import com.mlongbo.jfinal.common.utils.DateUtils;
import com.mlongbo.jfinal.kit.ExcelImportUtil;
import com.mlongbo.jfinal.model.Orders;
import com.mlongbo.jfinal.model.OrdersJd;
import com.mlongbo.jfinal.model.OrdersTb;
import com.mlongbo.jfinal.vo.AjaxResult;

public class OrderController extends Controller {
    private final AjaxResult result = new AjaxResult();
    static Orders dao = new Orders().dao();
    public void excelPage() {
        String userId = getPara("userId");
        Page<Orders> ordersPage = dao.paginate(getParaToInt("p", 1), 10,"select * ","from orders where 1=1 and relationUser = '"+userId+"' order by creationDate");
        setAttr("ordersPage", ordersPage);
        Page<Orders> ordersTbPage = dao.paginate(getParaToInt("p", 1), 10,"select * ","from orders_tb where 1=1 and relationUser = '"+userId+"' order by creationDate");
        setAttr("ordersTbPage", ordersTbPage);
        render("excelImport.html");
    }
    
    /**
     * 
     * @Description (模板下载)
     */
    public void downLoadOrderTemplateExcel(){
        File file = new File(PathKit.getWebRootPath() + "/upload/orderTemplate/订单模板.rar");
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
        String userId = getPara("userId");
        
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
                .set("recipient",map.get(4))
                .set("recipientTel",map.get(5))
                .set("recipientAddress",map.get(6))
                .set("shipper",map.get(7))
                .set("shipperTel",map.get(8))
                .set("orderEntry",map.get(9))
                .set("remarks",map.get(10))
                .set("orderStatus",map.get(11))
                .set("relationUser",userId)
                .set("creationDate",DateUtils.getNowTimeStamp())
                .save();
                successCount++;
            } catch (Exception e) {
                failCount++;
            }
        }
                
        // 返回结果
        JSONObject resultJson = new JSONObject();
        resultJson.put("successCount", successCount);
        resultJson.put("failCount", failCount);
        result.success(resultJson);
        renderJson(result);
    }
    
    @Before(Tx.class)
    public void importJdExcel() {
        String userId = getPara("userId");
        String _shipperInfo = getPara("shipperInfo");
        String[] shipperInfo = _shipperInfo.split(",");
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
        List<Map<Integer, String>> list = jdDealDataByPath(path); // 分析EXCEL数据
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
                .set("recipient",map.get(4))
                .set("recipientTel",map.get(5))
                .set("recipientAddress",map.get(6))
                .set("shipper", shipperInfo[0])// 寄件人
                .set("shipperTel", shipperInfo[1])// 寄件人电话
                .set("orderEntry", userId)// 录入者
                .set("relationUser", userId)
                .set("remarks",map.get(10))
                .set("orderStatus",map.get(11))
                .set("relationUser",userId)
                .set("creationDate",DateUtils.getNowTimeStamp())
                .save();
                successCount++;
            } catch (Exception e) {
                failCount++;
            }
        }
                
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
     *//*
    @Before(Tx.class)
    public void importJdExcel() throws Exception {
        String userId = getPara("userId");
        String _shipperInfo = getPara("shipperInfo");
        String[] shipperInfo = _shipperInfo.split(",");
        
        // 获取文件
        UploadFile file = getFile("jdOrderFile");
        String extension = file.getOriginalFileName().substring(file.getOriginalFileName().lastIndexOf("."));
        if (".xls".equals(extension)){
            result.addError("上传的文件必须为2007版的excel表格！");
            renderJson(result);
            return;
        }
        String path = file.getUploadPath() +  "\\"+file.getFileName();
        InputStream is = new FileInputStream(path);
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
        int totalSheet = xssfWorkbook.getNumberOfSheets();
        
        OrdersJd order = null;
        int successCount = 0;
        int failCount = 0;
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
                       String orderId = xssfRow.getCell(0)==null?xssfRow.getCell(0).toString():"";
                       String productName = xssfRow.getCell(2)==null?xssfRow.getCell(2).toString():"";
                       String productCount = xssfRow.getCell(3)==null?xssfRow.getCell(3).toString():"";
                       String productPrice = xssfRow.getCell(8)==null?xssfRow.getCell(8).toString():"";
                       String recipient = xssfRow.getCell(14)==null?xssfRow.getCell(14).toString():"";
                       String recipientTel = xssfRow.getCell(16)==null?xssfRow.getCell(16).toString():"";
                       String recipientAddress = xssfRow.getCell(15)==null?xssfRow.getCell(15).toString():"";
                       String remarks = xssfRow.getCell(17)==null?xssfRow.getCell(17).toString():"";
                       String orderStatus = xssfRow.getCell(11)==null?xssfRow.getCell(11).toString():"";
                       
                       order.set("orderId", orderId)
                       .set("productName", productName)
                       .set("productCount", productCount)
                       .set("productPrice", productPrice)
                       .set("recipient", recipient)
                       .set("recipientTel", recipientTel)
                       .set("recipientAddress", recipientAddress)
                       .set("remarks", remarks)
                       .set("orderStatus", orderStatus)
                       .set("creationDate", DateUtils.getNowTimeStamp())
                       .set("shipper", shipperInfo[0])// 寄件人
                       .set("shipperTel", shipperInfo[1])// 寄件人电话
                       .set("orderEntry", userId)// 录入者
                       .set("relationUser", userId)
                       .save();
                       successCount++;
               } catch (Exception e) {
                   e.printStackTrace();
                   failCount++;
               }
               }
            }
        }
                
        // 返回结果
        JSONObject resultJson = new JSONObject();
        resultJson.put("successCount", successCount);
        resultJson.put("failCount", failCount);
        result.success(resultJson);
        renderJson(result);
    }*/
    
    
    
    
    
    
    
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
           String userId = getPara("userId");
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
                    .save();
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                }
            }
            r.close();
         // 返回结果
            JSONObject resultJson = new JSONObject();
            resultJson.put("successCount", successCount);
            resultJson.put("failCount", failCount);
            result.success(resultJson);
            renderJson(result);
        }
}