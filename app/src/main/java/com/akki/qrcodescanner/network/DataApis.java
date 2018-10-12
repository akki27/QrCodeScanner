package com.akki.qrcodescanner.network;

import android.content.Context;
import android.util.Log;


import com.akki.qrcodescanner.model.GenericResponse;
import com.akki.qrcodescanner.util.CommonUtils;

import static android.content.ContentValues.TAG;

/**
 * Created by E01106 on 11/10/2017.
 */

public class DataApis {
    private Context mContext;
    String networkError = "Network not found!";

    /**
     * Constructor of DataApis
     * @param context The context of the class which instantiate it
     */
    public DataApis(Context context){

        this.mContext = context;



    }


    public void loginWithServer(final String monumentName, final String userName, final String password, final ApiCallback<GenericResponse> callback){
        Log.d(TAG,"loginWithServer()");
        if (CommonUtils.hasInternetConnection(mContext)) {
            if (monumentName != null && userName != null && password != null) {
                //Execute Api here and send the callbacks to the caller
            }
        } else {
            callback.onError(networkError);
        }
    }
}
