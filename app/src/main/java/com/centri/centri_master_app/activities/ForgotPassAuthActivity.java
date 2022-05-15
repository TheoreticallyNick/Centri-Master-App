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
import com.centri.centri_master_app.databinding.ActivityForgotpassAuthBinding;
import com.centri.centri_master_app.utils.DialogUtils;
import com.centri.centri_master_app.R;

public class ForgotPassAuthActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityForgotpassAuthBinding binding;
    User user;
    AlertDialog dialog, wrongAuthCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgotpass_auth);
        binding.setLifecycleOwner(this);
        binding.setOnClick(this);
        user = (User) getIntent().getSerializableExtra("user");
        binding.setUser(user);
        dialog = DialogUtils.buildLoadingDialog(this);
        wrongAuthCode = DialogUtils.buildWrongAuthCode(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnConfirm:
                updatePass();
                break;
            case R.id.btnCancel:
                startActivity(new Intent(ForgotPassAuthActivity.this, LoginActivity.class));
                finish();
                break;
        }
    }

    public void updatePass() {

        if (user.UserCode.isEmpty()) {
            binding.txtCode.setError("Required");
            binding.txtCode.requestFocus();
            return;
        }

        if (user.UserPass.isEmpty()) {
            binding.txtNewPass.setError("Required");
            binding.txtNewPass.requestFocus();
            return;
        }

        if (user.UserPass.length() < 8) {
            DialogUtils.buildPassInvalid(ForgotPassAuthActivity.this).show();
            return;
        }
        if (user.UserPassConf.isEmpty()) {
            binding.txtConfNewPass.setError("Required");
            binding.txtConfNewPass.requestFocus();
            return;
        }

        String user_code = user.UserCode;
        String user_password = user.UserPass;
        String user_passconf = user.UserPassConf;

        if (!user_password.equals(user_passconf)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Passwords do not match!");
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }

        Amplify.Auth.confirmResetPassword(
                user_password,
                user_code,
                () -> {
                    Log.i("AuthQuickstart", "New password confirmed");
                    Intent intent = new Intent(ForgotPassAuthActivity.this, ForgotPassSuccessActivity.class);
                    startActivity(intent);
                    finish();
                },
                error -> {
                    Log.e("AuthQuickstart", error.toString());
                    if (error.getCause().toString().contains("CodeMismatchException")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                wrongAuthCode.show();
                            }
                        });
                        return;
                    }

                }
        );
    }
}
