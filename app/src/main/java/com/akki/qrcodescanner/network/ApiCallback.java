package com.akki.qrcodescanner.network;

/**
 * Created by E01106 on 11/10/2017.
 */

public interface ApiCallback<T> {

    void onError(String error);

    void onHostException(String url);

    void onSuccess(T t);
}
