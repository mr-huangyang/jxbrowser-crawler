package com.teamdev.jxbrowser.chromium.demo.config;


public class CrawlerConfig {
    private int skipFirst ; //skip pages in head side
    private int skipLast; // skip pages in tail side
    private String gotoJs; // js for going to next page
    private int startPage; //
    private int lastPage;
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

    public int getStartPage() {
        return startPage;
    }

    public void setStartPage(int startPage) {
        this.startPage = startPage;
    }

    public int getLastPage() {
        return lastPage;
    }

    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
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
