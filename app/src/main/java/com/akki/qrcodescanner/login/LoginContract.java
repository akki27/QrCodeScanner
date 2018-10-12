package com.akki.qrcodescanner.login;

import android.support.annotation.NonNull;

import com.akki.qrcodescanner.BasePresenter;
import com.akki.qrcodescanner.BaseView;

/**
 * Created by e01106 on 11/9/2017.
 */

interface LoginContract {

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
         * This method will be called on login success
         */
        void launchHome();

        boolean isLoginInProgress();

    }


    interface Presenter extends BasePresenter {

        /**
         * This method will be called login image button gets clicked
         * @param monumentName, userName , password
         */
        void startLogin(@NonNull String monumentName, @NonNull String userName, @NonNull String password);

        boolean isLoginInProgress();

        void showErrorOnBackPressed(String errorMsg);

    }
}
