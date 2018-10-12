package com.akki.qrcodescanner;

/**
 * Created by e01106 on 11/9/2017.
 */

public interface BasePresenter {

    void start();

    void onError(String error);

    void onHostException(String url);
}
