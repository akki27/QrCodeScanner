package com.akki.qrcodescanner.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by SadyAkki on 11/11/2017.
 */

public class AppPreference {

    private static AppPreference sAppPreference;
    private static final String APP_PREFERENCES = "app_preferences";

    private static final String ALL_MONUMENT_NAME = "all_monument_name";
    private static final String SELECTED_MONUMENT_NAME = "selected_monument_name";

    private static final String CURRENT_USER_NAME = "current_user_name";

    private Context mContext;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private AppPreference(Context context) {
        this.mContext = context;
        mPreferences = mContext.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
    }


    public static AppPreference getInstance(Context context) {
        if (sAppPreference == null) {
            sAppPreference = new AppPreference(context);
        }

        // IF NOT NULL THIS WILL RETURN YOU EXISTING ONE
        return sAppPreference;
    }


    public void setAllMonumentName(String monumentName) {
        mEditor.putString(ALL_MONUMENT_NAME, monumentName).apply();
    }

    /*public MonumentResult getAllMonumentName() {
        String monumentBase =  mPreferences.getString(ALL_MONUMENT_NAME, "");
        MonumentBase monument = new Gson().fromJson(monumentBase, MonumentBase.class);
        return monument.data;
    }*/

    public void setSelectedMonument(String monumentName) {
        mEditor.putString(SELECTED_MONUMENT_NAME, monumentName).apply();
    }

    public String getSelectedMonument() {
        return  mPreferences.getString(SELECTED_MONUMENT_NAME, "");
    }

    public void setCurrentUser(String userName) {
        mEditor.putString(CURRENT_USER_NAME, userName).apply();
    }

    public String getCurrentUser() {
        return  mPreferences.getString(CURRENT_USER_NAME, "");
    }


    public void eraseUserData() {
        mEditor.putString(CURRENT_USER_NAME, "").apply();
    }

    public void eraseEveryThings(  ) {
        mContext.getSharedPreferences(APP_PREFERENCES, 0).edit().clear().apply();
        //mContext.getSharedPreferences(LoginSharedPreferences.NAME, 0).edit().clear().apply();
    }
}
