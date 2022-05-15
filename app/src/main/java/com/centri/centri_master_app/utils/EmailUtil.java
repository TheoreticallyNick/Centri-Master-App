package com.centri.centri_master_app.utils;

public class EmailUtil {

    public static boolean validateEmailAddress(String email) {
        boolean result = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        return result;
    }
}
