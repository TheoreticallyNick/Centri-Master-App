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
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.centri.centri_master_app.data.models.User;
import com.centri.centri_master_app.databinding.ActivitySignupReturnBinding;
import com.centri.centri_master_app.R;

public class SignUpSuccessActivity extends AppCompatActivity implements View.OnClickListener {

    User user;
    ActivitySignupReturnBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup_return);
        binding.setLifecycleOwner(this);
        binding.setOnClick(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnReturn:
                startActivity(new Intent(SignUpSuccessActivity.this, com.centri.centri_master_app.activities.LoginActivity.class));
                finish();
                break;
        }
    }
}
