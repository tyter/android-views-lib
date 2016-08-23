package com.sp.android.views.scheduler;

import android.os.Handler;
import android.os.Looper;

import com.sp.android.views.base.BaseScheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolScheduler implements BaseScheduler {

    private static ThreadPoolScheduler sInstance;
    public static ThreadPoolScheduler getInstance() {
        if (sInstance == null) {
            synchronized (ThreadPoolScheduler.class) {
                if (sInstance == null) {
                    sInstance = new ThreadPoolScheduler();
                }
            }
        }
        return sInstance;
    }

    private ExecutorService mExecutorService;
    private Handler mHandler;

    public ThreadPoolScheduler() {
        mExecutorService = Executors.newCachedThreadPool();
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void runOnUIThread(Runnable runnable) {
        mHandler.post(runnable);
    }

    @Override
    public void runOnIOThread(Runnable runnable) {
        mExecutorService.execute(runnable);
    }
}
