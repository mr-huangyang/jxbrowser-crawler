package com.teamdev.jxbrowser.chromium.demo.crawler;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.demo.excel.Excel;
import com.teamdev.jxbrowser.chromium.demo.util.ThreadUtil;
import com.teamdev.jxbrowser.chromium.demo.util.XpathUtility;
import com.teamdev.jxbrowser.chromium.demo.widget.CrawlerConfigPanel;
import com.teamdev.jxbrowser.chromium.demo.widget.ElementBranchPanel;
import com.teamdev.jxbrowser.chromium.dom.By;
import com.teamdev.jxbrowser.chromium.dom.DOMDocument;
import com.teamdev.jxbrowser.chromium.dom.DOMElement;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * table data crawler
 */
public class TableCrawler implements Crawler {

    private static final Logger LOGGER = LoggerFactory.getLogger(TableCrawler.class);

    private boolean running = false;
    private int counting;
    private boolean block = true;

    private ElementBranchPanel branchPanel;
    private CrawlerConfigPanel configPanel;
    private Browser browser;


    public TableCrawler(Browser browser, ElementBranchPanel branchPanel, CrawlerConfigPanel configPanel) {
        this.browser = browser;
        this.branchPanel = branchPanel;
        this.configPanel = configPanel;
    }

    @Override
    public void start() {

        String s = configPanel.validateConfig();
        if (StringUtils.isNotBlank(s)) {
            browser.executeJavaScript(String.format("alert('%s')", s));
            return;
        }

        this.running = true;

        //首先获取目标table
        List<List<String>> rows = doCrawler();
        if (rows.isEmpty()) return;

        Excel.getFile(configPanel.getFileName() + ".xlsx").save(rows);
        int pageNo = configPanel.getPageNo();
        String goToExpression = configPanel.getGoToExpression();
        boolean asyncFlag = this.configPanel.isAsyncPage();

        for (int j = 0; j < pageNo; j++) {
            this.browser.executeJavaScript(goToExpression.replace("$1", (j + 2) + ""));
            if (asyncFlag) { //当table是ajax动态更新时,执行
                asyncPage();
            } else {
                syncPage();
            }
        }
        this.browser.executeJavaScript(String.format("alert('%s')","哈里路呀,抓取完毕!"));
    }

    private void syncPage() {
        while (block) {//保证页面加载完成后,再进行下次的抓取.
            ThreadUtil.sleep(this.configPanel.getSleep());
        }
        block = true;
    }

    private void asyncPage() {
        ThreadUtil.sleep(configPanel.getSleep());
        List<List<String>> rows = doCrawler();
        if (rows.size() > 0)
            rows.remove(0);//去掉表头
        Excel.getFile(configPanel.getFileName() + ".xlsx").save(rows);
    }


    @Override
    public void count() {
        this.counting++;
    }

    @Override
    public int getCount() {
        return counting;
    }

    @Override
    public boolean isRunning() {
        return this.running;
    }

    @Override
    public void stop() {
        this.running = false;
    }


    public List<List<String>> doCrawler() {
        DOMElement element = branchPanel.getLockedElement();

        if (element == null) {
            browser.executeJavaScript(String.format("alert('%s')", "请选择要抓取的表格"));
            return Collections.emptyList();
        }

        String table = XpathUtility.xpath(element);

        DOMDocument document = browser.getDocument();
        DOMElement dataElement = document.findElement(By.xpath(table));
        java.util.List<DOMElement> tr = dataElement.findElements(By.tagName("tr"));
        List<List<String>> rows = new ArrayList<>();

        int skipFirst = this.configPanel.getSkipFirst();
        int skipLast = this.configPanel.getSkipLast();
        int size = tr.size() - (skipLast == 0 ? 0 : skipLast);
        for (int i = (skipFirst == 0 ? 0 : skipFirst); i < size; i++) {

            List<DOMElement> tds = tr.get(i).findElements(By.tagName("td"));
            if (tds == null || tds.isEmpty()) {
                tds = tr.get(i).findElements(By.tagName("th"));
            }
            List<String> cells = new ArrayList<>();
            tds.forEach(ele -> {
                cells.add(ele.getInnerText());
            });
            if (!cells.isEmpty()) {
                rows.add(cells);
            }
        }
        this.count();
        configPanel.redrawCount(getCount());
        LOGGER.info("已抓取第[{}]页", getCount());
        return rows;
    }

    @Override
    public void unblock() {
        this.block = false;
    }
}
