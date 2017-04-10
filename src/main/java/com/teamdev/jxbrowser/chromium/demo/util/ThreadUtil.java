package com.teamdev.jxbrowser.chromium.demo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by huangyang on 17/4/6.
 */
public class ThreadUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadUtil.class);

    public static  void sleep(long time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
              LOGGER.error("",e);
        }
    }


}
