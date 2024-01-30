package com.centerm.oversea.sample.payment.application;

import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * @author qiuchunhua@centerm.com
 * @date 2019/4/30 16:59
 */
public class CentermPosApplication extends Application {
    private static final String TAG = "CentermPosApplication";
    private static Context mContext;

    @Override
    public void onCreate() {
        Log.i(TAG, "CentermPosApplication onCreate");
        super.onCreate();
        mContext = this;
    }

    @Override
    public void onTerminate() {
        Log.i(TAG, "CentermPosApplication onTerminate");
        super.onTerminate();
    }

    public static Context getContext() {
        return mContext;
    }
}
