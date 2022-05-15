/*
 * Copyright (C) Centri Group Inc.
 * Nick Theoret <nick@centriconnect.com>
 * <p>
 * The Login Activity prompts the user to sign in and then make an AWS
 * Amplify call to authenticate and login the user.
 *
 * @author TheoreticallyNick
 * @version 2.0
 * @since 2021-01-31
 */

package com.centri.centri_master_app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.amplifyframework.auth.AuthException;
import com.amplifyframework.auth.AuthSession;
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.Consumer;
import com.centri.centri_master_app.CentriApp;
import com.centri.centri_master_app.data.models.User;
import com.centri.centri_master_app.databinding.ActivityLoginBinding;
import com.centri.centri_master_app.utils.DialogUtils;
import com.centri.centri_master_app.utils.EmailUtil;
import com.centri.centri_master_app.utils.FunctionUtils;
import com.centri.centri_master_app.R;

import java.util.Objects;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityLoginBinding binding; // Binds activity & objects to the layout xml file
    User user = new User();
    AlertDialog incorrectLoginDialog, tooManyAttemptsDialog, doesNotExistDialog, emailInvalidDialog, loadingDialog, noInternetConnection;
    EmailUtil formatEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.setLifecycleOwner(this);
        binding.setOnClick(this);
        binding.setUser(user);
        noInternetConnection = DialogUtils.buildNoInternetConnection(this);
        incorrectLoginDialog = DialogUtils.buildIncorrectLogin(this);
        tooManyAttemptsDialog = DialogUtils.buildTooManyAttempts(this);
        doesNotExistDialog = DialogUtils.buildDoesNotExist(this);
        emailInvalidDialog = DialogUtils.buildEmailInvalid(this);
        loadingDialog = DialogUtils.buildLoadingDialog(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSignup:
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                finish();
                break;

            case R.id.btnLogin:
                loginUserCognito();
                break;

            case R.id.btnForgotPass:
                startActivity(new Intent(LoginActivity.this, com.centri.centri_master_app.activities.ForgotPassActivity.class));
                break;
        }
    }

    private void loginUserCognito() {

        FunctionUtils.hideKeyboard(this);
        if (user.UserEmail.isEmpty()) {
            binding.txtEmail.setError("Required");
            binding.txtEmail.requestFocus();
            return;
        }
        if (user.UserPass.isEmpty()) {
            binding.txtPassword.setError("Required");
            binding.txtPassword.requestFocus();
            return;
        }

        user.UserEmail = user.UserEmail.toLowerCase();

        String user_email = user.UserEmail;
        String user_pass = user.UserPass;

        if (!EmailUtil.validateEmailAddress(user_email)) {
            emailInvalidDialog.show();
            return;
        }

        loadingDialog.show();
        Amplify.Auth.signIn(
                user_email,
                user_pass,
                result -> {
                    Log.i("AmplifyAuth", result.isSignInComplete() ? "Sign in succeeded" : "Sign in not complete");
                    Amplify.Auth.fetchAuthSession(new Consumer<AuthSession>() {
                        @Override
                        public void accept(@NonNull AuthSession value) {
                            if (value.isSignedIn()) {
                                AWSCognitoAuthSession cognitoAuthSession = (AWSCognitoAuthSession) value;
                                switch (cognitoAuthSession.getIdentityId().getType()) {
                                    case SUCCESS:
                                        Log.i("AuthQuickStart", "IdentityId: " + cognitoAuthSession.getIdentityId().getValue());
                                        Log.i("AuthQuickStart", "Token: " + cognitoAuthSession.getUserPoolTokens().getValue());
                                        CentriApp.credentials = cognitoAuthSession.getAWSCredentials().getValue();
                                        CentriApp.userId = Amplify.Auth.getCurrentUser().getUserId();
                                        CentriApp.idToken = cognitoAuthSession.getUserPoolTokens().getValue().getIdToken();
                                        loadingDialog.dismiss();
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        finish();
                                        break;
                                    case FAILURE:
                                        loadingDialog.dismiss();
                                        Log.i("AuthQuickStart", "IdentityId not present because: " + cognitoAuthSession.getIdentityId().getError().toString());
                                }
                            } else {
                                loadingDialog.dismiss();
                            }
                        }
                    }, new Consumer<AuthException>() {
                        @Override
                        public void accept(@NonNull AuthException value) {
                            loadingDialog.dismiss();
                            Log.i("AuthQuickStart", "Auth exception: " + value.toString());

                        }
                    });
                },
                error -> {
                    loadingDialog.dismiss();
                    Log.e("AmplifyAuth", error.toString());
                    //Handles incorrect username/password combo
                    if (Objects.requireNonNull(error.getCause()).toString().contains("Incorrect username or password")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                incorrectLoginDialog.show();
                            }
                        });
                        return;
                    }
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
                    //Handles too many login attempts
                    if (error.getCause().toString().contains("Password attempts exceeded")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tooManyAttemptsDialog.show();
                            }
                        });
                        return;
                    }
                    //Handles when the email user does not exist in the user pool
                    if (error.getCause().toString().contains("User does not exist")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                doesNotExistDialog.show();
                            }
                        });
                    }
                }
        );
    }
}
