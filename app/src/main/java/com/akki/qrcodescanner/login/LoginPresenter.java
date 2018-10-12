package com.akki.qrcodescanner.login;

import android.support.annotation.NonNull;
import android.util.Log;

import com.akki.qrcodescanner.R;
import com.akki.qrcodescanner.model.GenericResponse;
import com.akki.qrcodescanner.network.ApiCallback;
import com.akki.qrcodescanner.network.DataApis;
import com.akki.qrcodescanner.util.AppPreference;
import com.akki.qrcodescanner.util.CommonUtils;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by e01106 on 11/9/2017.
 */

class LoginPresenter implements LoginContract.Presenter {

    private final LoginActivity mContext;
    private final LoginContract.View mView;
    private final DataApis mDataApis;

    LoginPresenter(@NonNull LoginActivity context, @NonNull LoginContract.View view) {

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
    public void startLogin(@NonNull String monumentName, @NonNull String userName, @NonNull String password) {
        Log.d("LoginPresenter", "StartLogin()");
        if ( !CommonUtils.hasInternetConnection(mContext)) {
            mView.showErrorSnackBar(mContext.getString(R.string.msg_internet_unavailable));

        } else {
            doLogin( monumentName, userName, password);
        }
    }

    @Override
    public boolean isLoginInProgress() {
        return mView.isLoginInProgress();
    }

    @Override
    public void showErrorOnBackPressed(String errorMsg) {
        mView.showErrorSnackBar(errorMsg);
    }

    private void doLogin(final String monumentName, final String userName, final String password){

        mDataApis.loginWithServer(monumentName, userName, password, new ApiCallback<GenericResponse>() {
            @Override
            public void onError(String error) {
                mView.onError(error);
            }

            @Override
            public void onHostException(String url) {
                mView.onHostException(url);

            }

            @Override
            public void onSuccess(GenericResponse response) {
                //Save user data in preference if needed for future login
                //TODO:
                AppPreference.getInstance(mContext).setCurrentUser(userName);

                mView.launchHome();

            }
        });
    }
}
