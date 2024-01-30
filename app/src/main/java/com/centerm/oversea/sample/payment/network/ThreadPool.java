package com.centerm.oversea.sample.payment.network;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author qiuchunhua@centerm.com
 * @date 2019/4/15 11:28
 */
public class ThreadPool {
    private static ExecutorService fixedThreadPool = Executors.newCachedThreadPool();

    public static void execute(Runnable runnable) {
        fixedThreadPool.execute(runnable);
    }
}
