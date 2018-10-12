package com.akki.qrcodescanner.login;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.akki.qrcodescanner.R;
import com.akki.qrcodescanner.home.HomeActivity;
import com.akki.qrcodescanner.helper.GPSTracker;
import com.akki.qrcodescanner.util.AppPreference;
import com.akki.qrcodescanner.util.CommonUtils;
import static com.google.common.base.Preconditions.checkNotNull;
import static android.content.ContentValues.TAG;


/**
 * Created by e01106 on 11/9/2017.
 */

public class LoginView extends LinearLayout implements LoginContract.View {

    private LoginContract.Presenter mPresenter;
    private EditText mEditUsername, mEditPassword;
    private Spinner mMonumentName;
    private ImageView mBtnNext;
    private ProgressBar mProgressBar;
    private ViewFlipper mViewFlipperButton;

    //Temp Code_start
    private TextView mLatText, mLongText;
    //TempCode_end

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    GPSTracker gpsTracker;

    public LoginView(Context context) {
        super(context);
        init();
    }

    public LoginView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        Log.e(TAG, "init()_called");
        // Inflate layout here
        inflate(getContext(), R.layout.login_view_layout,this);
        resolveIds();

    }

    @Override
    public void setPresenter(@NonNull LoginContract.Presenter presenter) {

        mPresenter = checkNotNull(presenter, "Splash presenter is null");

    }

    @Override
    public void onError(String error) {
        hideProgress(true);
        showErrorSnackBar(error);

    }

    @Override
    public void onHostException(String url) {
        hideProgress(true);
        showErrorSnackBar(url);

    }

    @Override
    public void launchHome() {
        hideProgress(true);


        AppPreference.getInstance(getContext()).setSelectedMonument(mMonumentName.getSelectedItem().toString());

        Intent intent = new Intent(getContext(), HomeActivity.class);
        getContext().startActivity(intent);
        ((LoginActivity)getContext()).finish();

    }

    @Override
    public boolean isLoginInProgress() {
        return !(mEditPassword != null && mEditPassword.isEnabled());
    }

    /**
     * This method will be called in case if any error or exception
     *  @param errorMsg String error message
     */
    @Override
    public void showErrorSnackBar(String errorMsg) {
        hideProgress(true);
        if(errorMsg.equalsIgnoreCase(getContext().getString(R.string.msg_internet_unavailable))) {
            CommonUtils.showSnackBarIndefinite(getContext(), mBtnNext, getContext().getString(R.string.error_offline_text));
        } else {
            CommonUtils.showSnackBarIndefinite(getContext(), mBtnNext, errorMsg);
        }

    }

    @Override
    public void showProgress(boolean flag) {
        mEditPassword.setEnabled(false);
        mEditUsername.setEnabled(false);
        mBtnNext.setEnabled(false);
        mViewFlipperButton.setDisplayedChild(1);

    }

    @Override
    public void hideProgress(boolean flag) {
        mEditPassword.setEnabled(true);
        mEditUsername.setEnabled(true);
        mBtnNext.setEnabled(true);
        mViewFlipperButton.setDisplayedChild(0);
    }

    public void resolveIds() {

        mMonumentName = (Spinner) findViewById(R.id.monument_name);
        mEditUsername = (EditText) findViewById(R.id.edit_username);
        mEditPassword = (EditText) findViewById(R.id.edit_password);
        mBtnNext = (ImageView) findViewById(R.id.btn_next);
        mViewFlipperButton = (ViewFlipper) findViewById(R.id.view_flipper_container_button);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        //TempCode_start
        mLatText = (TextView) findViewById(R.id.txt_latitude);
        mLongText = (TextView) findViewById(R.id.txt_longitude);
        //TempCode_end

        mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isInputFieldsValidated()) {
                    /*showProgress(true);
                    mPresenter.startLogin(mMonumentName.getSelectedItem().toString(), mEditUsername.getText().toString(), mEditPassword.getText().toString() );*/


                    //Temp Code_start
                    showProgress(true);
                    AppPreference.getInstance(getContext()).setCurrentUser(mEditUsername.getText().toString());
                    launchHome();
                    //Temp Code_end
                }

            }
        });


        //TODO: Get Location and call Monument list Api
        //fetchUserLocation();

    }


    public void fetchUserLocation() {
        showProgress(true);

        gpsTracker = new GPSTracker(getContext());
        if(gpsTracker.canGetLocation()){
            if(checkLocationPermission()) {
                double latitude = gpsTracker.getLatitude();
                double longitude = gpsTracker.getLongitude();

                //Toast.makeText(getContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();

                //TODO: call monument list api and update the mMonumentName data : later

                hideProgress(true);
                mLatText.setText(String.valueOf(latitude));
                mLongText.setText(String.valueOf(longitude));

            }else{
                // can't get location
                // GPS or Network is not enabled
                // Ask user to enable GPS/network in settings
                gpsTracker.showSettingsAlert();
            }
        }
    }


    public boolean isInputFieldsValidated() {

        /*if(mMonumentName.getSelectedItem().toString().isEmpty()
                || mMonumentName.getSelectedItem().toString().equals(getContext().getString(R.string.default_monument_name))) {
            CommonUtils.showSnackBarIndefinite(getContext(), mBtnNext, getContext().getString(R.string.msg_monument_not_selected));
            return false;
        }*/


        if ( mEditUsername.getText().toString().trim().isEmpty()) {
            CommonUtils.showSnackBarIndefinite(getContext(), mBtnNext, getContext().getString(R.string.msg_username_empty));
            return false;
        }

        if ( !CommonUtils.isValidUserName(mEditUsername.getText().toString().trim())) {
            CommonUtils.showSnackBarIndefinite(getContext(), mBtnNext, getContext().getString(R.string.msg_username_invalid));
            return false;
        }


        if ( mEditPassword.getText().toString().trim().isEmpty()) {
            CommonUtils.showSnackBarIndefinite(getContext(), mBtnNext, getContext().getString(R.string.msg_password_empty));
            return false;
        }

        if ( !CommonUtils.isValidPassword(mEditPassword.getText().toString().trim())) {
            CommonUtils.showSnackBarIndefinite(getContext(), mBtnNext, getContext().getString(R.string.msg_password_invalid));
            return false;
        }

        return true;
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale((LoginActivity)getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getContext())
                        .setTitle("Grant Location Permission")
                        .setMessage("Location Permission Required!")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions((LoginActivity)getContext(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions((LoginActivity)getContext(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

}
