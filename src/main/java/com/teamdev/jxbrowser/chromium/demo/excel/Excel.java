package com.teamdev.jxbrowser.chromium.demo.excel;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * 抓取的数据默认保存在 /tmp/excel目录,后续可配置
 *
 */
public class Excel {

    private final static String dir = "/tmp/excel/";

    private final static Logger LOGGER = LoggerFactory.getLogger(Excel.class);

    private int fileIndex;

    private String fileName;

    static {
        if (Files.notExists(Paths.get(dir))) {
            try {
                Files.createDirectories(Paths.get(dir));
            } catch (IOException e) {
                LOGGER.error(dir, e);
            }
        }
    }

    private Excel(String fileName) {
        this.fileName = fileName;
    }

    public static Excel getFile(String fileName) {
        return new Excel(fileName);
    }


    public Excel saveOne(List<String> cells) {

        if(cells==null || cells.isEmpty()) return this;
        Workbook workBook = getWorkBook(fileName);
        save(workBook,cells);
        close(workBook);
        return this;
    }

    private void save(Workbook workBook ,List<String> cells){
        Sheet she = workBook.getSheetAt(0);
        int lastRow = she.getPhysicalNumberOfRows();
        if (lastRow > 60000) {//excel最大不超过60000行数据
            this.fileIndex++;
            fileName = fileName.replace(".xlsx", "-" + this.fileIndex + ".xlsx");
            workBook = this.getWorkBook(fileName);
        }
        Row excelRow = she.createRow( lastRow==0?0:lastRow ); //从已有行的下一行开始
        for (int j = 0; j < cells.size(); j++) {
            excelRow.createCell(j, CellType.STRING).setCellValue(cells.get(j));
        }
        try {
            workBook.write(Files.newOutputStream(Paths.get(dir + fileName)));
        } catch (IOException e) {
            LOGGER.error("保存文件失败");
            throw new RuntimeException(String.format("=====保存%s失败======", fileName));
        } catch (IllegalArgumentException exe) {
            LOGGER.error("", exe);
        }
    }

    public Excel save(List<List<String>> rows) {
        if(rows==null || rows.isEmpty()) return this;
        Workbook workBook = getWorkBook(fileName);
        for (int i = 0 ; i < rows.size();i++){
           save(workBook,rows.get(i));
        }
        close(workBook);
        return this;
    }

    private static Workbook getWorkBook(String fileName) {
        String path = dir + fileName;
        try {
            boolean exists = Files.exists(Paths.get(path));
            Workbook workbook;
            if (!exists) {
                //创建
                workbook = new XSSFWorkbook();
                workbook.createSheet();
                workbook.write(Files.newOutputStream(Paths.get(path)));
            }else {
                //读取文件
                workbook = new XSSFWorkbook(Files.newInputStream(Paths.get(path)));
            }
            return workbook;

        } catch (Exception e) {
            throw new RuntimeException(String.format("============创建 %s excel文件异常=============", path), e);
        }
    }

    private static void close(Workbook workbook) {
        try {
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete() throws IOException {
        Files.delete(Paths.get(dir + fileName));
    }

}
