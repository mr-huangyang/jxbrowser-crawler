package com.teamdev.jxbrowser.chromium.demo.util;

/**
 * Created by huangyang on 17/4/6.
 */
public class ThreadUtil {

    public static  void sleep(long time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {

        }
    }


}
