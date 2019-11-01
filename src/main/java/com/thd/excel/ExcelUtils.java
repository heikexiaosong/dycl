package com.thd.excel;

import org.apache.poi.ss.usermodel.Cell;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExcelUtils {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0");

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

    public static Date getDateValue(Cell cell){
        if ( cell==null ){
            return null;
        }
        Date cellvalue = null;
        switch (cell.getCellTypeEnum()) {
            case NUMERIC:
                if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                    cellvalue =  cell.getDateCellValue();
                }
                break;
            default:
        }

        return cellvalue;
    }

    public static String getCellValue(Cell cell, String defaultValue){
        if ( cell==null ){
            return defaultValue;
        }
        String cellvalue = null;
        switch (cell.getCellTypeEnum()) {
            case STRING:
                cellvalue = cell.getStringCellValue();
                break;
            case BOOLEAN:
                cellvalue = String.valueOf(cell.getBooleanCellValue());
                break;
            case NUMERIC:
                if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                    cellvalue = DATE_FORMAT.format(cell.getDateCellValue());
                } else {
                    cellvalue = String.valueOf(cell.getNumericCellValue());
                }
                break;
            default:
                cellvalue = defaultValue;
        }

        if ( cellvalue==null || cellvalue.length()==0 ){
            cellvalue = defaultValue;
        }

        return cellvalue.trim();
    }
    

    public static String getCellValue(Cell cell){
        return getCellValue(cell, "");
    }



    public static String getValue(Cell cell){
        if ( cell == null ){
            return "";
        }

        Object obj = cellValue(cell);
        if ( obj == null ){
            return "";
        }

        if ( obj instanceof Number ){
            return  DECIMAL_FORMAT.format(obj).trim();
        }

        return obj.toString().trim();
    }

    public static Double getFloatValue(Cell cell) {
        if ( cell == null ){
            return 0D;
        }

        Object obj = cellValue(cell);
        if ( obj == null ){
            return 0D;
        }

        if ( obj instanceof Number ){
            return  ((Number)obj).doubleValue();
        }

        if ( obj instanceof String  && "".equalsIgnoreCase(obj.toString()) ){
            return null;
        }

       try {
           return Double.parseDouble(obj.toString().trim());
       } catch (Exception e){
            e.printStackTrace();
           return 0D;
       }
    }



    private static Object cellValue(Cell cell){
        if ( cell==null ){
            return null;
        }
        switch (cell.getCellTypeEnum()) {
            case _NONE:
                return null;
            case NUMERIC:
                return cell.getNumericCellValue();
            case STRING:
                return cell.getStringCellValue();
            case FORMULA:
                try {
                    return cell.getNumericCellValue();
                } catch (Exception e){
                    try {
                        return cell.getStringCellValue();
                    } catch (Exception e1){
                        return cell.getBooleanCellValue();
                    }
                }
            case BLANK:
                return cell.getStringCellValue();
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case ERROR:
                return cell.getErrorCellValue();
        }

        return null;
    }
}
