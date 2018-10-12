package com.akki.qrcodescanner.login;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.akki.qrcodescanner.R;


/**
 * Created by e01106 on 11/9/2017.
 */

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    LoginPresenter mLoginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        LoginView loginView = (LoginView) findViewById(R.id.layout_login_view);
        mLoginPresenter = new LoginPresenter(LoginActivity.this, loginView);
        loginView.setPresenter(mLoginPresenter);


    }


   /* @Override
    public void onResume() {
        super.onResume();
        mLoginPresenter.initiateLogin();
    }*/


    @Override
    public void onBackPressed() {
        if (!mLoginPresenter.isLoginInProgress()) {
            super.onBackPressed();
        } else {
            mLoginPresenter.showErrorOnBackPressed(getString(R.string.api_in_progress_message));
        }
    }
}
