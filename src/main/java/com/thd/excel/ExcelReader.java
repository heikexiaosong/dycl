package com.thd.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import com.thd.db.Process;

public class ExcelReader {

    public static List<Process> load(String filename) throws IOException {
        return load(new File(filename));
    }

    public static List<Process> load(File file) throws IOException {
        InputStream inputStream=new FileInputStream(file);//创建一个输入流读取单元格
        Workbook wb=new XSSFWorkbook(inputStream);
        Sheet sheet=wb.getSheetAt(1);//获取第一个sheet页
        System.out.println("\t[" + file.getAbsolutePath() + "]数据配置Sheet: " + sheet.getSheetName());

        List<Process> result = new ArrayList<Process>();

        for ( Row row: sheet ) {
            if ( row.getCell(0) == null
                    || row.getCell(1) == null ){
                continue;
            }
            String station = ExcelUtils.getCellValue(row.getCell(0));
            String part = ExcelUtils.getCellValue(row.getCell(1));
            String title = ExcelUtils.getCellValue(row.getCell(2));

            Process process = new Process();
            process.setStation(station);
            process.setPart(part);
            process.setTitle(title);
            result.add(process);
        }

        wb.close();

        inputStream.close();

        return result;

    }

    public static void main(String[] args) throws IOException {

        List<Process> processes = load("data.xlsx");

        for (Process process : processes) {
            System.out.println(process);
        }

    }
}
