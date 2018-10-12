package com.akki.qrcodescanner.home;

import com.akki.qrcodescanner.BasePresenter;
import com.akki.qrcodescanner.BaseView;
import com.google.android.gms.vision.barcode.Barcode;

/**
 * Created by E01106 on 11/11/2017.
 */

interface HomeContract {

    interface View extends BaseView<Presenter> {

        /**
         * This method will be called in case if any error or exception
         *  @param errorMsg String error message
         */
        void showErrorSnackBar(String errorMsg);

        /**
         * Shows progress wheel
         */
        void showProgress(boolean flag);

        /**
         * Hides progress wheel.
         */
        void hideProgress(boolean flag);

        /**
         * This method will be called on start scanning
         */
        void startScanner();

        /**
         * This method will be called on re-start scanning
         */
        void reStartScanner();

        /**
         * This method will be called on stop scanning
         */
        void stopScanner();

        /**
         * This method will be called on pause scanning
         */
        void pauseScanner();

        /**
         * This method will be called on to show scan result
         */
        void showScanResult(boolean scanResult);

    }


    interface Presenter extends BasePresenter {

        /**
         * This method will be called scanner button clicked to start scanning
         */
        void startScanner();

        /**
         * This method will be called on re-start scanning
         */
        void reStartScanner();

        /**
         * This method will be called scanner button clicked to stop scanning
         */
        void stopScanner();


        /**
         * This method will be called on pause scanning
         */
        void pauseScanner();

        /**
         * This method will be called on to show scan result
         */
        void showScanResult(Barcode barcode);
    }
}
