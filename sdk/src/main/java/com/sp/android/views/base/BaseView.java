package com.sp.android.views.base;


public interface BaseView<T extends BasePresenter> {

    void setPresenter(T presenter);

}
