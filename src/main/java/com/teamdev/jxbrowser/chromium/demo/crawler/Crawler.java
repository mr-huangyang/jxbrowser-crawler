package com.teamdev.jxbrowser.chromium.demo.crawler;

import java.util.List;

/**
 * Created by huangyang on 17/4/5.
 */
public interface Crawler {

    void start();

    void count();

    int getCount();

    boolean isRunning();

    void stop();

    List<List<String>> doCrawler();

    void unblock();

}
