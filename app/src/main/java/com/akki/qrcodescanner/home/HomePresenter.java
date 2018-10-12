package com.akki.qrcodescanner.home;


import android.support.annotation.NonNull;
import android.util.Log;

import com.akki.qrcodescanner.network.DataApis;
import com.google.android.gms.vision.barcode.Barcode;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by E01106 on 11/11/2017.
 */

class HomePresenter implements HomeContract.Presenter {

    private static final String TAG = HomePresenter.class.getSimpleName();

    private final HomeActivity mContext;
    private final HomeContract.View mView;
    private final DataApis mDataApis;

    HomePresenter(@NonNull HomeActivity context, @NonNull HomeContract.View view) {

        mContext = checkNotNull(context);
        mView = checkNotNull(view);

        mDataApis = new DataApis(context);


    }


    @Override
    public void start() {

    }

    @Override
    public void onError(String error) {

    }

    @Override
    public void onHostException(String url) {

    }

    @Override
    public void startScanner() {
        mView.showProgress(true);
        mView.startScanner();
    }

    @Override
    public void reStartScanner() {
        mView.showProgress(true);
        mView.reStartScanner();
    }

    @Override
    public void pauseScanner() {
        mView.pauseScanner();
    }

    @Override
    public void stopScanner() {
        mView.hideProgress(true);
        mView.stopScanner();
    }

    @Override
    public void showScanResult(Barcode barcode) {
        //TODO: Data Api call for scan result: and then show result: scan success or failure

        Log.d(TAG, "showScanResult()");
        mView.showScanResult(true); //Passing scan result as true for testing
    }

}
