package com.lg;

import com.teamdev.jxbrowser.chromium.demo.excel.Excel;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by huangyang on 17/4/5.
 */
public class ExcelTest {

    @Test
    public void testExcel() throws IOException {
        Excel excel = Excel.getFile("test.xlsx");
        excel.saveOne(Arrays.asList("1","2"));
        excel.delete();
    }

}
