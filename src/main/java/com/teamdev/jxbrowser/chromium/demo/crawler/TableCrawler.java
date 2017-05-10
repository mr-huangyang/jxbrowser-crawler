package com.teamdev.jxbrowser.chromium.demo.crawler;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.demo.config.CrawlerConfig;
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

    private ElementBranchPanel branchPanel;
    private CrawlerConfigPanel configPanel;
    private Browser browser;

    public TableCrawler(Browser browser, ElementBranchPanel branchPanel, CrawlerConfigPanel configPanel) {
        this.browser = browser;
        this.branchPanel = branchPanel;
        this.configPanel = configPanel;
    }

    @Override
    public boolean start() {

        String s = configPanel.validateConfig();
        if (StringUtils.isNotBlank(s)) {
            browser.executeJavaScript(String.format("alert('%s')", s));
            return false;
        }
        return true;
    }

    @Override
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

        int skipFirst =  getConfig().getSkipFirst();
        int skipLast = getConfig().getSkipLast();
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
        return rows;
    }

    @Override
    public CrawlerConfig getConfig() {
        return this.configPanel.getConfig();
    }
}
