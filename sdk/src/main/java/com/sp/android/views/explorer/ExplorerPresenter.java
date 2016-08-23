package com.sp.android.views.explorer;


import com.sp.android.views.base.BaseScheduler;
import com.sp.android.views.error.ErrorEmitter;
import com.sp.android.views.explorer.model.MediaTask;
import com.sp.android.views.explorer.model.bean.MediaMeta;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

public class ExplorerPresenter implements ExplorerContract.Presenter, ErrorEmitter.Monitor {

    private WeakReference<ExplorerContract.View> mView;
    private BaseScheduler mScheduler;
    private MediaTask mMediaTask;

    public ExplorerPresenter(BaseScheduler scheduler, ExplorerContract.View view, MediaTask mediaTask) {
        mScheduler = scheduler;
        mView = new WeakReference<>(view);
        mMediaTask = mediaTask;
    }

    @Override
    public void start() {
        ErrorEmitter.getInstance().registerMonitor(this);
    }

    @Override
    public void stop() {
        ErrorEmitter.getInstance().unRegisterMonitor(this);
    }

    @Override
    public void loadExternal(final String path, boolean flat) {
        mScheduler.runOnIOThread(new Runnable() {
            @Override
            public void run() {
                List<MediaMeta> data = mMediaTask.queryExternal(path);
                onMediaMetaChanged(MediaMeta.MEDIA_TYPE_OTHER, data);
            }
        });
    }

    @Override
    public void loadInternal(final int type, boolean flat) {
        mScheduler.runOnIOThread(new Runnable() {
            @Override
            public void run() {
                int offset = 0;
                List<MediaMeta> list = new LinkedList<>();
                List<MediaMeta> data;
                do {
                    data = mMediaTask.query(type, offset, MediaTask.QUERY_COUNT);
                    if (data == null) {
                        break;
                    }
                    list.addAll(data);
                    offset += MediaTask.QUERY_COUNT;
                } while (data.size() == MediaTask.QUERY_COUNT);
                onMediaMetaChanged(type, list);
            }
        });
    }

    @Override
    public void onError(final int code, final String message) {
        mScheduler.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                ExplorerContract.View view = mView.get();
                if (view != null) {
                    view.onError(code, message);
                }
            }
        });
    }

    private void onMediaMetaChanged(final int type, final List<MediaMeta> data) {
        mScheduler.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                ExplorerContract.View view = mView.get();
                if (view != null) {
                    if (data == null || data.size() == 0) {
                        view.onMediaMetaNotAvailable(type);
                    } else {
                        view.onMediaMetaChanged(type, data);
                    }
                }
            }
        });
    }
}
