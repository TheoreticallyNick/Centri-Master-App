package com.centri.centri_master_app.utils;

import android.text.TextUtils;
import android.util.Log;

public class LogUtil {
    private static String TAG = LogUtil.class.getSimpleName();

    public static boolean showLog = true;

    public static void d(String tag, String msg) {
        if (showLog) {
            if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(msg)) {
                Log.d(TAG, "Logging tag or msg can not be empty");
            } else {
                Log.d(tag, msg);
            }
        }
    }

    public static void e(String tag, String msg) {
        if (showLog) {
            if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(msg)) {
                Log.e(TAG, "Logging tag or msg can not be empty");
            } else {
                Log.e(tag, msg);
            }
        }

    }

    public static void i(String tag, String msg) {
        if (showLog) {
            if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(msg)) {
                Log.i(TAG, "Logging tag or msg can not be empty");
            } else {
                Log.i(tag, msg);
            }
        }

    }

    public static void w(String tag, String msg) {
        if (showLog) {
            if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(msg)) {
                Log.w(TAG, "Logging tag or msg can not be empty");
            } else {
                Log.w(tag, msg);
            }
        }

    }

    public static void v(String tag, String msg) {
        if (showLog) {
            if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(msg)) {
                Log.v(TAG, "Logging tag or msg can not be empty");
            } else {
                Log.v(tag, msg);
            }
        }

    }

}
