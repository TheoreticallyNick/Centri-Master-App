package com.centri.centri_master_app;

import android.content.Context;
import android.content.SharedPreferences;

public class AppSharedPrefUtils {
    private String NAME = "com.centri.centri_main_app";
    private String USER = "user";

    private SharedPreferences preferences;
    // A way in which one can store and retrieve small amounts of primitive data as key/value pairs
    // to a file on the device storage such as String, int, float, Boolean that make up your preferences
    // in an XML file inside the app on the device storage. Shared Preferences can be thought of as a
    // dictionary or a key/value pair.


    public AppSharedPrefUtils(Context context) {
        preferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);

    }

    public void saveUser(String user) {
        preferences.edit().putString(USER, user).apply();
    }

    public String getUser() {
        return preferences.getString(USER, "");
    }
}