package com.teamdev.jxbrowser.chromium.demo.crawler;

/**
 * Created by huangyang on 17/4/5.
 */
public interface Crawler {

    void start();

    void count();

    int getCount();

    boolean isRunning();

    void stop();

}
