package com.akki.qrcodescanner;

import android.support.annotation.NonNull;

/**
 * Created by e01106 on 11/9/2017.
 */

public interface BaseView <T> {

    void setPresenter(@NonNull T presenter);

    /**
     * @param error String error from server
     */
    void onError(String error);

    /**
     * @param url string url
     */
    void onHostException(String url);


}