package com.centri.centri_master_app.utils;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.Calendar;

public class FunctionUtils {

    public static long getUnixTimeAsMillis() {
        Calendar calendar = Calendar.getInstance();
        long now = calendar.getTimeInMillis();
        return (now / 1000L);

    }

    public static void hideKeyboard(Activity activity) {
        if (activity == null)
            return;
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static boolean isValidData(String target) {
        return (!TextUtils.isEmpty(target) && !target.equalsIgnoreCase("null"));
    }
}