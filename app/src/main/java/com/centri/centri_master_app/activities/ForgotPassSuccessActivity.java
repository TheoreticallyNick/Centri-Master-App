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
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.centri.centri_master_app.databinding.ActivityForgotpassReturnBinding;
import com.centri.centri_master_app.R;

public class ForgotPassSuccessActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityForgotpassReturnBinding binding;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgotpass_return);
        binding.setLifecycleOwner(this);
        binding.setOnClick(this);
        //dialog = DialogUtils.buildLoadingDialog(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnReturn:
                Intent intent = new Intent(ForgotPassSuccessActivity.this, com.centri.centri_master_app.activities.LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}