package com.teamdev.jxbrowser.chromium.demo.config;


public class CrawlerConfig {
    private int skipFirst ; //skip pages in head side
    private int skipLast; // skip pages in tail side
    private String gotoJs; // js for going to next page
    private int nextTimes; // times to change page
    private int sleepTime ; // sleep for some time
    private String fileName; // data stored in it

    private boolean isAsyn;

    public int getSkipFirst() {
        return skipFirst;
    }

    public void setSkipFirst(int skipFirst) {
        this.skipFirst = skipFirst;
    }

    public int getSkipLast() {
        return skipLast;
    }

    public void setSkipLast(int skipLast) {
        this.skipLast = skipLast;
    }

    public String getGotoJs() {
        return gotoJs;
    }

    public void setGotoJs(String gotoJs) {
        this.gotoJs = gotoJs;
    }

    public int getNextTimes() {
        return nextTimes;
    }

    public void setNextTimes(int nextTimes) {
        this.nextTimes = nextTimes;
    }

    public int getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isAsyn() {
        return isAsyn;
    }

    public void setAsyn(boolean asyn) {
        isAsyn = asyn;
    }
}
