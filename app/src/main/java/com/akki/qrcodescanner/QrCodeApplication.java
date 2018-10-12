package com.akki.qrcodescanner;

import android.support.multidex.MultiDexApplication;

/**
 * Created by e01106 on 11/9/2017.
 */

public class QrCodeApplication extends MultiDexApplication {

    private static QrCodeApplication appContext;

    public static QrCodeApplication getInstance() {
        return appContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;

    }

    public QrCodeApplication getAppContext() {
        return this;
    }

}
