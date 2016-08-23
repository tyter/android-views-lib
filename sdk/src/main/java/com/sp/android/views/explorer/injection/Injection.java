package com.sp.android.views.explorer.injection;


import android.content.Context;

import com.sp.android.views.explorer.model.MediaTask;

public class Injection {
    public static MediaTask provideMediaTask(Context context) {
        return new MediaTask(context);
    }
}
