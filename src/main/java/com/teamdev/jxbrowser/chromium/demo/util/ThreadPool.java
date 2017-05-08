package com.teamdev.jxbrowser.chromium.demo.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by huangyang on 17/5/8.
 */
public class ThreadPool {
    private static final ExecutorService SERVICE;
    static {
        SERVICE = Executors.newFixedThreadPool(5);
        Runtime.getRuntime().addShutdownHook(new Thread(){
            public void run(){
                SERVICE.shutdown();
            }
        });
    }

    public static void invoke(Runnable run){
          SERVICE.submit(run);
    }
}
