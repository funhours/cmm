package com.mlongbo.jfinal.kit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ExcelKit {
    
    public static Sheet getSheetByNum(Workbook book,int number){  
        Sheet sheet = null;  
        try {  
            sheet = book.getSheetAt(number);  
//          if(sheet == null){  
//              sheet = book.createSheet("Sheet"+number);  
//          }  
        } catch (Exception e) {  
            throw new RuntimeException(e.getMessage());  
        }  
        return sheet;  
    }  
    public static Workbook getExcelWorkbook(String filePath) throws IOException{  
        Workbook book = null;  
        File file  = null;  
        FileInputStream fis = null;   
          
        try {  
            file = new File(filePath);  
            if(!file.exists()){  
                throw new RuntimeException("文件不存在");  
            }else{  
                fis = new FileInputStream(file);  
                book = WorkbookFactory.create(fis); 
            }  
        } catch (Exception e) {  
            throw new RuntimeException(e.getMessage());  
        } finally {  
            if(fis != null){  
                fis.close();  
            }  
        }  
        return book;  
    } 
}
