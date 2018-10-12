package com.akki.qrcodescanner.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.akki.qrcodescanner.R;


/**
 * Created by e01106 on 11/8/2017.
 */

public class CommonUtils {


    public static boolean hasInternetConnection(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null) { // connected to the internet
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    // connected to wifi
                    return true;
                } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                    // connected to the mobile provider's data plan
                    return true;
                }
                return false;
            } else {
                // not connected to the internet
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public static Snackbar showSnackBarIndefinite(Context context, View view, String message) {
        Snackbar snackbar = null;

        try {
            snackbar = Snackbar
                    .make(view, message, Snackbar.LENGTH_INDEFINITE)
                    .setAction("dismiss", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.d("Event", "OnClick invoked !");
                        }
                    });
            // Changing message text color
            snackbar.setActionTextColor(ContextCompat.getColor(context, R.color.colorAccent));

            // Changing action button text color
            View sbView = snackbar.getView();
            for (int i = 0; i < ((Snackbar.SnackbarLayout) sbView).getChildCount(); i++) {
                if (((Snackbar.SnackbarLayout) sbView).getChildAt(i) instanceof TextView) {
                    ((TextView) ((Snackbar.SnackbarLayout) sbView).getChildAt(i)).setTextColor(ContextCompat.getColor(context, R.color.textColorPrimary));
                    ((TextView) ((Snackbar.SnackbarLayout) sbView).getChildAt(i)).setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.text_size_regular));
                    ((TextView) ((Snackbar.SnackbarLayout) sbView).getChildAt(i)).setMaxLines(10);
                    break;
                }
            }
            snackbar.show();
        } catch (Exception e) {
            Log.i("showSnackBar", "exception during display snackbar", e);
        }
        return snackbar;
    }


    /**
     * To check if given user name is a valid or not
     * @param  userName String
     * @return true/false
     */
    public static boolean isValidUserName(String userName) {
        //return userName != null && android.util.Patterns.EMAIL_ADDRESS.matcher(userName).matches();

        //Doing this temporarily
        return userName != null && userName.equalsIgnoreCase("admin");

    }

    /**
     * To check if given password is a valid or not
     * @param  password String
     * @return true/false
     */
    public static boolean isValidPassword(String password) {

        //Doing this temporarily
        return password != null && password.equalsIgnoreCase("admin");

    }
}
