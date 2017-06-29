package com.teamdev.jxbrowser.chromium.demo.facade;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.demo.config.CrawlerConfig;
import com.teamdev.jxbrowser.chromium.demo.crawler.Crawler;
import com.teamdev.jxbrowser.chromium.demo.crawler.TableCrawler;
import com.teamdev.jxbrowser.chromium.demo.excel.Excel;
import com.teamdev.jxbrowser.chromium.demo.util.ThreadUtil;
import com.teamdev.jxbrowser.chromium.demo.widget.CrawlerConfigPanel;
import com.teamdev.jxbrowser.chromium.demo.widget.ElementBranchPanel;
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


    public TableCrawlerFacadeImpl(Browser browser , ElementBranchPanel branchPanel, CrawlerConfigPanel configPanel) {
        this.browser = browser;
        this.configPanel = configPanel;
        this.tableCrawler = new TableCrawler(browser,branchPanel,configPanel);
        this.syncPageStrategy = new SyncPageStrategy(this);
        this.asyncPageStrategy = new AsyncPageStrategy(this);
    }

    @Override
    public void crawl() {
        if(running){
            boolean async = tableCrawler.getConfig().isAsyn();
            getPageCrawlingStrategy(async).crawl();
        }
    }


    @Override
    public void start() {
        running = tableCrawler.start();
        if (running) {
            CrawlerConfig config = configPanel.getConfig();
            counter = config.getStartPage();
            boolean async = config.isAsyn();
            getPageCrawlingStrategy(async).start();
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    private synchronized void getAndSave(String fileName) {
        CrawlerConfig config = configPanel.getConfig();
        //get data in table
        List<List<String>> rows = tableCrawler.doCrawler();
        if(rows.isEmpty()){
            running = false ;
            LOGGER.info("=======no more data found,i'm going to shutdown========");
            doJs(config.getGotoJs(),counter + "");
            return ;
        }
        if (counter > config.getStartPage()) {
            //remove the head in table when not the first page
            rows.remove(0);
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

    private synchronized void doJs(String js, String param) {
        browser.executeJavaScriptAndReturnValue(js.replace("$1", param));//
    }


   /**** inner class ****/

    private abstract class PageCrawlingStrategy {
       public abstract void crawl();
       public abstract void start();
    }

    private class SyncPageStrategy extends PageCrawlingStrategy {
        CrawlerFacade crawlerFacade;

        public SyncPageStrategy(CrawlerFacade crawlerFacade) {
            this.crawlerFacade = crawlerFacade;
        }

        @Override
        public void crawl() {
            CrawlerConfig config = configPanel.getConfig();
            int nextTimes = config.getLastPage();
            String fileName = config.getFileName() + ".xlsx";
            getAndSave(fileName);

            if (counter > nextTimes) {
                doJs("alert('$1')", "哈里路呀,抓取完毕!文件路径:" + Excel.getDir() + fileName);
                return;
            }
            //next page
            String gotoJs = config.getGotoJs();
            ThreadUtil.sleep(config.getSleepTime());
            doJs(gotoJs, counter + "");
        }

        @Override
        public void start() {
            String gotoJs = tableCrawler.getConfig().getGotoJs();
            doJs(gotoJs, counter+"");
        }
    }

    private class AsyncPageStrategy extends PageCrawlingStrategy {
        CrawlerFacade crawlerFacade;

        public AsyncPageStrategy(CrawlerFacade crawlerFacade) {
            this.crawlerFacade = crawlerFacade;
        }

        @Override
        public void crawl() {
            CrawlerConfig config = configPanel.getConfig();
            int nextTimes = config.getLastPage();
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
            doJs("alert('$1')", "哈里路呀,抓取完毕!文件路径:" + Excel.getDir() + fileName);
        }

        @Override
        public void start() {
            crawl();
        }
    }
}
