/*
 * Copyright (C) Centri Group Inc.
 * Nick Theoret <nick@centriconnect.com>
 *
 * The Sign Up Activity prompts the user to sign up for an account.
 * Once the user inputs their information, they are taken to the Authentication Activity
 * to enter the 6 digit code sent to their email address for authentication. Once the user is
 * authentication, they are prompted with a Successful Sign-Up Activity, where they are prompted
 * to return to the login screen.
 * This activity uses AWS Amplify to sign up the user and create a Cognito User ID.
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
import com.centri.centri_master_app.databinding.ActivitySignupAuthBinding;
import com.centri.centri_master_app.utils.DialogUtils;
import com.centri.centri_master_app.utils.FunctionUtils;
import com.centri.centri_master_app.R;


public class SignUpAuthActivity extends AppCompatActivity implements View.OnClickListener {

    String TAG = SignUpAuthActivity.class.getSimpleName();
    ActivitySignupAuthBinding binding;
    User user;
    AlertDialog wrongAuthCodeDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup_auth);
        binding.setLifecycleOwner(this);
        binding.setOnClick(this);
        user = (User) getIntent().getSerializableExtra("user");
        binding.setUser(user);
        wrongAuthCodeDialog = DialogUtils.buildWrongAuthCode(this);
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed Called");
        moveTaskToBack(true);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnConfirm:
                authSignUp();
                break;
//            case R.id.btnBack:
//                startActivity(new Intent(SignUpAuthActivity.this, SignUpActivity.class));
//                finish();
//                break;
        }
    }

    public void authSignUp() {

        FunctionUtils.hideKeyboard(this);

        if (user.UserCode.isEmpty()) {
            binding.txtUserCode.setError("Required");
            binding.txtUserCode.requestFocus();
            return;
        }

        String user_email = user.UserEmail;
        String user_code = user.UserCode;

        Amplify.Auth.confirmSignUp(
                user_email,
                user_code,
                result -> {
                    Log.i("AuthQuickstart", result.isSignUpComplete() ? "Confirm signUp succeeded" : "Confirm sign up not complete");
                    startActivity(new Intent(SignUpAuthActivity.this, com.centri.centri_master_app.activities.SignUpSuccessActivity.class));
                    finish();
                },
                error -> {
                    if (error.getCause().toString().contains("CodeMismatchException")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                wrongAuthCodeDialog.show();
                            }
                        });
                        return;
                    }
                }
        );
    }
}

