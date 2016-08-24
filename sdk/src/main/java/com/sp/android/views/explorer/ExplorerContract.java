package com.sp.android.views.explorer;


import com.sp.android.views.base.BasePresenter;
import com.sp.android.views.base.BaseView;
import com.sp.android.views.explorer.model.bean.MediaMeta;

import java.util.List;

public class ExplorerContract {

    public interface Presenter extends BasePresenter {

        void loadInternal(int type, boolean flat);

        void loadExternal(String path, boolean flat);
    }

    public interface View extends BaseView<Presenter> {

        void onLoadStart(int type);

        void onLoad(int type, List<MediaMeta> meta);

        void onLoadEnd(int type);

        void onError(int code, String msg);

    }
}
