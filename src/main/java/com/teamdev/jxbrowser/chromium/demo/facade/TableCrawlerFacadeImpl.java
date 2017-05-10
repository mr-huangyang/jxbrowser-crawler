package com.teamdev.jxbrowser.chromium.demo.facade;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.demo.config.CrawlerConfig;
import com.teamdev.jxbrowser.chromium.demo.crawler.Crawler;
import com.teamdev.jxbrowser.chromium.demo.excel.Excel;
import com.teamdev.jxbrowser.chromium.demo.util.ThreadUtil;
import com.teamdev.jxbrowser.chromium.demo.widget.CrawlerConfigPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TableCrawlerFacadeImpl implements CrawlerFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(TableCrawlerFacadeImpl.class);

    private Crawler tableCrawler;
    private Browser browser;
    private CrawlerConfigPanel configPanel;
    private int counter;
    private boolean running = false;

    private PageCrawlingStrategy syncPageStrategy;
    private PageCrawlingStrategy asyncPageStrategy;


    public TableCrawlerFacadeImpl(Crawler crawler, Browser browser, CrawlerConfigPanel configPanel) {
        this.tableCrawler = crawler;
        this.browser = browser;
        this.configPanel = configPanel;
        this.syncPageStrategy = new SyncPageStrategy(this);
        this.asyncPageStrategy = new AsyncPageStrategy(this);
    }

    @Override
    public void crawl() {
        boolean async = tableCrawler.getConfig().isAsyn();
        getPageCrawlingStrategy(async).crawl();
    }

    private synchronized void getAndSave(String fileName) {
        List<List<String>> rows = tableCrawler.doCrawler();//get data in table
        if (counter > 1) {
            rows.remove(0);//remove the head in table when not the first page
        }
        Excel.getFile(fileName).save(rows);//saving data
        configPanel.redrawCount(counter);
        LOGGER.info("====== 已抓取第[{}]页 ======", counter);
        counter++;
    }

    private PageCrawlingStrategy getPageCrawlingStrategy(boolean async) {
        if (async) return asyncPageStrategy;
        return syncPageStrategy;
    }

    @Override
    public void start() {
        running = tableCrawler.start();
        if (running) {
            counter = 1;
            boolean async = tableCrawler.getConfig().isAsyn();
            getPageCrawlingStrategy(async).start();
        }
    }

    private synchronized void doJs(String js, String param) {
        browser.executeJavaScriptAndReturnValue(js.replace("$1", param));//
    }


    @Override
    public boolean isRunning() {
        return running;
    }


    private abstract class PageCrawlingStrategy {
        abstract void crawl();
        abstract void start();
    }

    private class SyncPageStrategy extends PageCrawlingStrategy {
        CrawlerFacade crawlerFacade;

        public SyncPageStrategy(CrawlerFacade crawlerFacade) {
            this.crawlerFacade = crawlerFacade;
        }

        @Override
        public void crawl() {
            CrawlerConfig config = tableCrawler.getConfig();
            int nextTimes = config.getNextTimes();
            String fileName = config.getFileName() + ".xlsx";
            getAndSave(fileName);

            if (counter > nextTimes) {
                doJs("alert('$1')", "哈里路呀,抓取完毕!");
                return;
            }
            //next page
            String gotoJs = config.getGotoJs();
            doJs(gotoJs, counter + "");
            ThreadUtil.sleep(config.getSleepTime());
        }

        @Override
        void start() {
            String gotoJs = tableCrawler.getConfig().getGotoJs();
            doJs(gotoJs, "1");//todo
        }
    }

    private class AsyncPageStrategy extends PageCrawlingStrategy {
        CrawlerFacade crawlerFacade;

        public AsyncPageStrategy(CrawlerFacade crawlerFacade) {
            this.crawlerFacade = crawlerFacade;
        }

        @Override
        public void crawl() {
            CrawlerConfig config = tableCrawler.getConfig();
            int nextTimes = config.getNextTimes();
            String fileName = config.getFileName() + ".xlsx";
            getAndSave(fileName);

            //next page
            String gotoJs = config.getGotoJs();
            while (counter <= nextTimes) {
                doJs(gotoJs, counter + "");
                //take time to sleep for data prepared
                ThreadUtil.sleep(config.getSleepTime());
                getAndSave(fileName);
            }
            doJs("alert('$1')", "哈里路呀,抓取完毕!");
        }

        @Override
        void start() {
            crawl();
        }
    }
}
