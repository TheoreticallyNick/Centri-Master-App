/*
 * Copyright (C) Centri Group Inc.
 * Nick Theoret <nick@centriconnect.com>
 *
 * The Forgot Password Activity prompts the user to enter their email address.
 * Once they enter their email address, they are prompted to the Authentication Activity
 * to enter the code they received via email and their new password. Once the user enters the
 * correct code and the new password, they are prompted to the Change of Password Success Activity
 * and prompted back to the login screen.
 *
 * @author  TheoreticallyNick
 * @version 2.0
 * @since   2021-01-31
 */

package com.centri.centri_master_app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.amplifyframework.core.Amplify;
import com.centri.centri_master_app.data.models.User;
import com.centri.centri_master_app.databinding.ActivityForgotpassBinding;
import com.centri.centri_master_app.utils.DialogUtils;
import com.centri.centri_master_app.utils.FunctionUtils;
import com.centri.centri_master_app.R;

public class ForgotPassActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityForgotpassBinding binding;
    User user = new User();
    AlertDialog incorrectLoginDialog, tooManyAttemptsDialog, doesNotExistDialog, emailInvalidDialog, loadingDialog, noInternetConnection, attemptLimitExceeded, wrongAuthCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgotpass);
        binding.setLifecycleOwner(this);
        binding.setOnClick(this);
        binding.setUser(user);
        noInternetConnection = DialogUtils.buildNoInternetConnection(this);
        attemptLimitExceeded = DialogUtils.buildTooManyAttempts(this);
        wrongAuthCode = DialogUtils.buildWrongAuthCode(this);
        //dialog = DialogUtils.buildLoadingDialog(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnResetPassword:
                forgotPass();
                break;
            case R.id.btnCancel:
                startActivity(new Intent(ForgotPassActivity.this, LoginActivity.class));
                finish();
                break;
        }
    }

    public void forgotPass() {

        FunctionUtils.hideKeyboard(this);

        if (user.UserEmail.isEmpty()){
            binding.txtEmail.setError("Required");
            binding.txtEmail.requestFocus();
            return;
        }

        String user_email = user.UserEmail;

        Amplify.Auth.resetPassword(
                user_email,
                result -> {
                    Log.i("AuthQuickstart", result.toString());
                    Intent intent = new Intent(ForgotPassActivity.this, ForgotPassAuthActivity.class);
                    Bundle bun = new Bundle();
                    bun.putSerializable("user", user);
                    intent.putExtras(bun);
                    startActivity(intent);
                    finish();
                },
                error -> {
                    //Handle errors with login here
                    Log.e("AuthQuickstart", error.toString());
                    //Handles no internet connection
                    if (error.getCause().toString().contains("AmazonClientException") ) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                noInternetConnection.show();
                            }
                        });
                        return;
                    }



                    if (error.getCause().toString().contains("LimitExceededException") ) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                attemptLimitExceeded.show();
                            }
                        });
                        return;
                    }
                }
        );
    }
}
