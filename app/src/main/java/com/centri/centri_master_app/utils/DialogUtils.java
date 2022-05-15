package com.centri.centri_master_app.utils;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.centri.centri_master_app.R;

public class DialogUtils {

    public static AlertDialog buildLoadingDialog(Context context) {

//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setMessage("Loading");
//        AlertDialog dialog = builder.create();
//
//        return dialog;

        return new AlertDialog.Builder(context).setCancelable(false).setView(R.layout.loading_dialog).create();
    }

    public static AlertDialog buildMultiUseInfoDialog(Context context, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg);
        return builder.create();
    }

    public static AlertDialog buildIncorrectLogin(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Email or password is incorrect.");
        AlertDialog dialog = builder.create();

        return dialog;
    }

    public static AlertDialog buildTooManyAttempts(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Too many attempts. Please try again in an hour.");
        AlertDialog dialog = builder.create();

        return dialog;
    }

    public static AlertDialog buildDoesNotExist(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Email does not exist.");
        AlertDialog dialog = builder.create();

        return dialog;
    }

    public static AlertDialog buildEmailInvalid(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Enter a valid email address.");
        AlertDialog dialog = builder.create();

        return dialog;
    }

    public static AlertDialog buildPhoneInvalid(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Enter a valid phone number.");
        AlertDialog dialog = builder.create();

        return dialog;
    }

    public static AlertDialog buildPassNotMatching(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Passwords do not match.");
        AlertDialog dialog = builder.create();

        return dialog;
    }

    public static AlertDialog buildAlreadyExists(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("User already exists. To resend the authentication code, click ");
        AlertDialog dialog = builder.create();

        return dialog;
    }

    public static AlertDialog buildPassInvalid(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Password needs to be 8 characters long or more");
        AlertDialog dialog = builder.create();

        return dialog;
    }

    public static AlertDialog buildNoInternetConnection(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Unable to process the request. Please check your internet connection and try again.");
        AlertDialog dialog = builder.create();

        return dialog;
    }

    public static AlertDialog buildWrongAuthCode(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("The code you entered is incorrect. Please try again.");
        AlertDialog dialog = builder.create();

        return dialog;
    }

    public static AlertDialog buildDialog(Context context, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg);

        return builder.create();
    }
}
