package com.sp.android.views.error;


import java.util.ArrayList;
import java.util.List;

public class ErrorEmitter {

    public static final int CODE_JSON_FORMAT_ERROR = 0;

    public interface Monitor {
        void onError(int code, String message);
    }

    private static ErrorEmitter sInstance;
    public static ErrorEmitter getInstance() {
        if (sInstance == null) {
            synchronized (ErrorEmitter.class) {
                if (sInstance == null) {
                    sInstance = new ErrorEmitter();
                }
            }
        }
        return sInstance;
    }

    private List<Monitor> mMonitors;

    private ErrorEmitter() {
        mMonitors = new ArrayList<>();
    }

    public synchronized void registerMonitor(Monitor monitor) {
        if (monitor != null && !mMonitors.contains(monitor)) {
            mMonitors.add(monitor);
        }
    }

    public synchronized void unRegisterMonitor(Monitor monitor) {
        if (monitor != null) {
            mMonitors.remove(monitor);
        }
    }

    public synchronized void emit(int code, String message) {
        for (Monitor monitor : mMonitors) {
            monitor.onError(code, message);
        }
    }

    public void emit(int code) {
        String message = getCodeMessage(code);
        emit(code, message);
    }

    private String getCodeMessage(int code) {
        switch (code) {
            case CODE_JSON_FORMAT_ERROR:
                return "JSON 数据格式错误";
            default:
                return "未知错误";
        }
    }
}
