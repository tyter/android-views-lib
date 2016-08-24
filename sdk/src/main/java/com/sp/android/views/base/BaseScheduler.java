package com.sp.android.views.base;


public interface BaseScheduler {

    void runOnUIThread(Runnable runnable);

    void runOnIOThread(Runnable runnable);

    void runOnQueueThread(int queue, Runnable runnable);

}
