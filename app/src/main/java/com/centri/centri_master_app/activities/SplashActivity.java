/*
 * Copyright (C) Centri Group Inc.
 * Nick Theoret <nick@centriconnect.com>
 * <p>
 * Opening activity of MyPropane App
 *
 * @author TheoreticallyNick
 * @version 2.0
 * @since 2021-01-31
 */

package com.centri.centri_master_app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.amplifyframework.auth.AuthException;
import com.amplifyframework.auth.AuthSession;
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.Consumer;
import com.centri.centri_master_app.CentriApp;
import com.centri.centri_master_app.R;

public class SplashActivity extends AppCompatActivity {
    //Launched when the app begins

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //...upon the app being created
        setContentView(R.layout.activity_splash); //...sets the content view to "activity_splash" layout (logo)
        //Log.e("UUID","time "+GetUnixTime());


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Amplify.Auth.fetchAuthSession(new Consumer<AuthSession>() {
                    @Override
                    public void accept(@NonNull AuthSession value) {
                        if (value.isSignedIn()) {
                            AWSCognitoAuthSession cognitoAuthSession = (AWSCognitoAuthSession) value;
                            switch (cognitoAuthSession.getIdentityId().getType()) {
                                case SUCCESS:
                                    Log.i("AuthQuickStart", "IdentityId: " + cognitoAuthSession.getIdentityId().getValue());
                                    //Log.i("AuthQuickStart", "Token: " + cognitoAuthSession.getUserPoolTokens().getValue());
                                    //Log.i("AuthQuickStart", "Creds: " + cognitoAuthSession.getAWSCredentials().getValue());
                                    CentriApp.credentials = cognitoAuthSession.getAWSCredentials().getValue();
                                    CentriApp.idToken = cognitoAuthSession.getUserPoolTokens().getValue().getIdToken();
                                    CentriApp.userId = Amplify.Auth.getCurrentUser().getUserId();
                                    CentriApp.isSplashDone = true;
                                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                    break;
                                case FAILURE:
                                    Log.i("AuthQuickStart", "IdentityId not present because: " + cognitoAuthSession.getIdentityId().getError().toString());
                                    CentriApp.isSplashDone = true;
                                    startActivity(new Intent(SplashActivity.this, com.centri.centri_master_app.activities.LoginActivity.class));
                            }

                        } else {
                            CentriApp.isSplashDone = true;
                            startActivity(new Intent(SplashActivity.this, com.centri.centri_master_app.activities.LoginActivity.class));
                        }
                        finish();
                    }
                }, new Consumer<AuthException>() {
                    @Override
                    public void accept(@NonNull AuthException value) {
                        CentriApp.isSplashDone = true;
                        startActivity(new Intent(SplashActivity.this, com.centri.centri_master_app.activities.LoginActivity.class));
                        finish();
                    }
                });

            }
        }, 1000);
    }

}