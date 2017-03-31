package com.mlongbo.jfinal.kit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.jfinal.plugin.activerecord.Db;

/**
 * excel 导入通用类
 * @author jodenhe
 *
 */
public class ExcelImportUtil {

	/**
	 * excel导入操作
	 * @param filename
	 * @param sql
	 * @param ColCount
	 * @param condition 
	 * @return
	 */
	public static Map<String, Object> ReadExcel2003(String filename, String sql,int ColCount) {

		Map<String, Object> map = null;
		int cells = 0;
		int rows=0;
		try {
			HSSFWorkbook wookbook = new HSSFWorkbook(new FileInputStream(filename));
			HSSFSheet sheet = wookbook.getSheetAt(0);
			rows = sheet.getPhysicalNumberOfRows();
			Object[][] paras = new Object[rows-1][ColCount];
			for (int i = 0; i < rows; i++) {
				HSSFRow row = sheet.getRow(i);
				cells = row.getPhysicalNumberOfCells();
				if (row != null) {
					if (i==0) {
						if (cells!=ColCount) {
							map = new HashMap<String, Object>();
							map.put("msg", "数据有误，请查看注意事项！");
							return map;
						}
					}
					else {
						for (int j = 0; j < cells; j++) {
							HSSFCell cell = row.getCell(j);
							if (cell != null) {
								switch (cell.getCellType()) {
								case HSSFCell.CELL_TYPE_FORMULA:
									break;
								case HSSFCell.CELL_TYPE_NUMERIC:
									paras[i-1][j] = cell.getNumericCellValue();
									break;
								case HSSFCell.CELL_TYPE_STRING:
									paras[i-1][j] = cell.getStringCellValue();
									break;
								default:
									paras[i-1][j] = null;
									break;
								}
							}
						}
					}
				}
			}
			int[] ret=null;
			try {
				ret = Db.batch(sql, paras, 100);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println();
				int s = 0, l = 0;
				for (int i = 0; i < ret.length; i++)
					if (ret[i] == 1)
						s++;
					else
						l++;
				map = new HashMap<String, Object>();
				map.put("success", s);
				map.put("lost", l);
				map.put("count", ret.length);
				wookbook = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * excel导入操作
	 * @param filename
	 * @param sql
	 * @param ColCount
	 * @return
	 */
	public static Map<String, Object> ReadExcel2007(String filename, String sql,int ColCount) {

		Map<String, Object> map = null;
		try {
			XSSFWorkbook wookbook = new XSSFWorkbook(new FileInputStream(filename));
			XSSFSheet sheet = wookbook.getSheetAt(0);
			int rows = sheet.getPhysicalNumberOfRows();
			Object[][] paras = new Object[rows-1][ColCount];
			for (int i = 0; i < rows; i++) {
				XSSFRow row = sheet.getRow(i);
				int cells = row.getPhysicalNumberOfCells();
				if (row != null) {
					if (i==0) {
						if (cells!=ColCount) {
							map = new HashMap<String, Object>();
							map.put("success", "数据有误，请查看注意事项！");
							return map;
						}
					}
					else {
						for (int j = 0; j < cells; j++) {
							XSSFCell cell = row.getCell(j);
							if (cell != null) {
								switch (cell.getCellType()) {
								case HSSFCell.CELL_TYPE_FORMULA:
									break;
								case HSSFCell.CELL_TYPE_NUMERIC:
									paras[i-1][j] = cell.getNumericCellValue();
									break;
								case HSSFCell.CELL_TYPE_STRING:
									paras[i-1][j] = cell.getStringCellValue();
									break;
								default:
									paras[i-1][j] = null;
									break;
								}
							}
						}
					}
				}
			}
			int[] ret = Db.batch(sql, paras, 100);
			int s = 0, l = 0;
			for (int i = 0; i < ret.length; i++)
				if (ret[i] == 1)
					s++;
				else
					l++;
			map = new HashMap<String, Object>();
			map.put("success", s);
			map.put("lost", l);
			map.put("count", ret.length);
			wookbook = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 删除upload目录下的所有文件
	 * @param dir
	 * @return
	 */
	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			//递归删除目录中的子目录下
			for (int i=0; i<children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		// 目录此时为空，可以删除
		return dir.delete();
	}

	/**
	 * 删除文件
	 * @param file
	 * @return
	 */
	public static boolean deleteFile(File file) {
		return file.delete();
	}
}
