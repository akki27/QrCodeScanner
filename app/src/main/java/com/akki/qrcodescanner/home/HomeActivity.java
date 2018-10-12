package com.akki.qrcodescanner.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.akki.qrcodescanner.R;
import com.akki.qrcodescanner.login.LoginActivity;
import com.akki.qrcodescanner.scan.BarcodeGraphicTracker;
import com.akki.qrcodescanner.util.AppPreference;
import com.google.android.gms.vision.barcode.Barcode;

/**
 * Created by e01106 on 11/9/2017.
 */

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, BarcodeGraphicTracker.BarcodeUpdateListener   {

    private static final String TAG = HomeActivity.class.getSimpleName();

    HomePresenter mHomePresenter;
    private ImageView mStartStopScanner;
    private String mLoggedInUser;
    private boolean mScanInProgress = false;
    private Toolbar mToolbar;
    private Switch mOnOffSwitch;
    private ImageView mFlashView;
    private boolean mFlashStatus = false;
    private boolean mResultShowing = false;

    private Barcode mBarcodeObject; //Temp code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");

        setContentView(R.layout.activity_home);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        resolveIds();

        /* MVP Initializations */
        HomeView homeView = (HomeView) findViewById(R.id.layout_home_view);
        mHomePresenter = new HomePresenter(HomeActivity.this, homeView);
        homeView.setPresenter(mHomePresenter);



    }

    /**
     * start camera view the camera.
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        mHomePresenter.startScanner();

    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
        mHomePresenter.pauseScanner();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
        mHomePresenter.stopScanner();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed()");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(mResultShowing) {
                mHomePresenter.reStartScanner();
            } else {
                super.onBackPressed();
            }
        }
    }


    /* Resolve Ids */
    public void resolveIds() {
        //Drawer layout events
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);

        TextView welcomeText = (TextView) header.findViewById(R.id.txt_welcome);
        TextView userNameText = (TextView) header.findViewById(R.id.txt_user_name);

        /* Check user login status and set the text correspondingly */
        if(isUserLoggedIn()) {
            //TODO: Set the user name for userNameText and enable the Logout button
            welcomeText.setText(getResources().getString(R.string.txt_user_welcome));
            userNameText.setText(mLoggedInUser);

            //Show logout menu item
            navigationView.getMenu().findItem(R.id.nav_logoff).setVisible(true);

        } else {
            welcomeText.setText(getResources().getString(R.string.txt_guest_welcome));
            userNameText.setText(getResources().getString(R.string.txt_login_or_register));

            //Hide logout menu item
            navigationView.getMenu().findItem(R.id.nav_logoff).setVisible(false);
        }

        /* Login or Register Text click event: event will fire only if user is not already logged in */
        userNameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isUserLoggedIn()) {
                    launchLoginActivity();
                }
            }
        });


        /* Set Current monument Name */
        String curMonumentName = AppPreference.getInstance(this).getSelectedMonument();
        TextView mMonumentName = (TextView) findViewById(R.id.txt_monument_name);
        mMonumentName.setText(curMonumentName);

         /* Start/Stop scanner icon click event */
        mStartStopScanner = (ImageView)findViewById(R.id.scanner_icon_toolbar);
        mStartStopScanner.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //start-stop QR Code scanning
                Log.d(TAG, "isScanInProgress():" +isScanInProgress());
                if(isScanInProgress()) {
                    mHomePresenter.stopScanner();
                } else {
                    mHomePresenter.reStartScanner();
                }
            }
        });

        /*mOnOffSwitch = (Switch) findViewById(R.id.flash_on_off);
        mOnOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.v(TAG, "Switch State: " +isChecked);
                if(isChecked) {
                    setFlashStatus(true);
                } else {
                    setFlashStatus(false);
                }
                mHomePresenter.stopScanner();
                mHomePresenter.reStartScanner();
            }
        });*/

        mFlashView = (ImageView) findViewById(R.id.flash_on_off);
        mFlashView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mFlashStatus) {
                    setFlashStatus(true);
                    mFlashView.setImageResource(R.drawable.ic_flash_on_black_32dp);
                } else {
                    setFlashStatus(false);
                    mFlashView.setImageResource(R.drawable.ic_flash_off_black_32dp);
                }

                if(isScanInProgress()) {
                    mHomePresenter.stopScanner();
                    mHomePresenter.reStartScanner();
                }
            }
        });

    }


    public void launchLoginActivity() {
        //Stop scanner if active
        mHomePresenter.stopScanner();

        //launch Login Activity
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /* Navigation drawer icon click events */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_scanned_stats) {
            //Handle show scanned tickets event
            return false; //Temp: remove when event gets decided

        } else if (id == R.id.nav_manage) {
            //Open settings event. Remove this if not needed
            return false; //Temp: remove when event gets decided

        } else if (id == R.id.nav_share) {
            //Share scanned image event
            return false; //Temp: remove when event gets decided

        } else if (id == R.id.nav_send) {
            //Send scanned image event
            return false; //Temp: remove when event gets decided

        } else if (id == R.id.nav_report) {
            //report issue event
            return false; //Temp: remove when event gets decided

        } else if (id == R.id.nav_logoff) {
            //Logout click event
            //TODO: logout event: Clear the login preference data and refresh the Home Ui or move back to login screen ?? Currently moving back to login screen
            AppPreference.getInstance(getApplicationContext()).eraseUserData();
            launchLoginActivity();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /* return the user login status */
    public boolean isUserLoggedIn() {
        //TODO: check the user login status

        mLoggedInUser = AppPreference.getInstance(this).getCurrentUser();

        return !mLoggedInUser.isEmpty();

    }


    /* Set Start/Stop scanner UI */
    public void setScannerIcon(boolean flag) {
        Log.d(TAG, "setScannerUI()_flag: " +flag);

        if(flag) {
            mStartStopScanner.setImageResource(R.drawable.ic_stop_scanner_black_32dp);
            mScanInProgress = true;
        } else {
            mStartStopScanner.setImageResource(R.drawable.ic_start_scanner_black_32dp);
            mScanInProgress = false;
        }
    }

    /* Check if scan is in-progress */
    public boolean isScanInProgress() {
       return mScanInProgress;
    }

    @Override
    public void onBarcodeDetected(final Barcode barcode) {
        Log.d(TAG, "BarcodeDetected_BarcodeData: \n"
                +barcode.format + "::\n"
                +barcode.valueFormat + "::\n"
                + barcode.displayValue /*+ "::\n"
                +barcode.describeContents() + "::\n"
                + barcode.rawValue + "::\n"
                +barcode.calendarEvent + "::\n"
                +barcode.contactInfo + "::\n"
                +barcode.email + ":" +barcode.phone*/


        );

        if(mScanInProgress) {
            mScanInProgress = false;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setBarCodeObject(barcode); //Temp Code
                    mHomePresenter.showScanResult(barcode);
                }
            });
        }

    }

    /* Temp function */
    public void setBarCodeObject(Barcode barCodeObject) {
        mBarcodeObject = barCodeObject;
    }

    /* Temp Function */
    public Barcode getBarCodeObject() {
        return mBarcodeObject;
    }

    /* Set Flash Status */
    public void setFlashStatus(boolean flag) {
        mFlashStatus = flag;
    }
    /* Returns current flash status */
    public boolean getFlashStatus() {
        return mFlashStatus;
    }

    public void setIfResultShowing(boolean flag) {
        Log.d(TAG, "setIfResultShowing()_flag: " +flag);
        mResultShowing = flag;
    }
}
