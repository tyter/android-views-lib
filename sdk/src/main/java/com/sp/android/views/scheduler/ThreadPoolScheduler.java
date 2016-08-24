package com.sp.android.views.scheduler;

import android.os.Handler;
import android.os.Looper;

import com.sp.android.views.base.BaseScheduler;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
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
    private Map<Integer, ExecutorService> mQueueThreads;

    public ThreadPoolScheduler() {
        mExecutorService = Executors.newCachedThreadPool();
        mHandler = new Handler(Looper.getMainLooper());
        mQueueThreads = new ConcurrentHashMap<>();
    }

    @Override
    public void runOnUIThread(Runnable runnable) {
        mHandler.post(runnable);
    }

    @Override
    public void runOnIOThread(Runnable runnable) {
        mExecutorService.execute(runnable);
    }

    @Override
    public synchronized void runOnQueueThread(int queue, Runnable runnable) {
        Set<Integer> keySet = mQueueThreads.keySet();
        ExecutorService executor = null;
        for (Integer key : keySet) {
            if (key.intValue() == queue) {
                executor = mQueueThreads.get(key);
                break;
            }
        }
        if (executor == null) {
            executor = Executors.newSingleThreadExecutor();
            mQueueThreads.put(queue, executor);
        }

        executor.execute(runnable);
    }
}
