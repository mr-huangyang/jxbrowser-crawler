package com.teamdev.jxbrowser.chromium.demo.crawler;

import com.teamdev.jxbrowser.chromium.demo.config.CrawlerConfig;

import java.util.List;

/**
 * Created by huangyang on 17/4/5.
 */
public interface Crawler {

    boolean start();

    List<List<String>> doCrawler();

    CrawlerConfig getConfig();

}
