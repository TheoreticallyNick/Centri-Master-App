package com.centri.centri_master_app;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.amazonaws.auth.AWSCredentials;
import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.auth.AuthException;
import com.amplifyframework.auth.AuthSession;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.Consumer;
import com.centri.centri_master_app.utils.Constants;
import com.centri.centri_master_app.utils.LogUtil;

public class CentriApp extends Application implements LifecycleEventObserver {
    public static AWSCredentials credentials;
    public static String idToken;
    public static String userId;
    public static boolean isSplashDone = false;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d("CentriApp", "App class created");
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        try {
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.configure(getApplicationContext());
            Log.i("Amplify - Init", "Initialized Amplify");
        } catch (AmplifyException error) {
            Log.e("Amplify - Init", "Could not initialize Amplify", error);
        }
    }

    public void fetchCredential() {
        Amplify.Auth.fetchAuthSession(new Consumer<AuthSession>() {
            @Override
            public void accept(@NonNull AuthSession value) {
                if (value.isSignedIn()) {
                    AWSCognitoAuthSession cognitoAuthSession = (AWSCognitoAuthSession) value;
                    switch (cognitoAuthSession.getIdentityId().getType()) {
                        case SUCCESS:
                            Log.i("Amplify - Auth", "IdentityId: " + cognitoAuthSession.getIdentityId().getValue());
                            //Log.i("AuthQuickStart", "Token: " + cognitoAuthSession.getUserPoolTokens().getValue());
                            //Log.i("AuthQuickStart", "Creds: " + cognitoAuthSession.getAWSCredentials().getValue());
                            CentriApp.credentials = cognitoAuthSession.getAWSCredentials().getValue();
                            if (cognitoAuthSession.getUserPoolTokens().getValue() != null) {
                                CentriApp.idToken = cognitoAuthSession.getUserPoolTokens().getValue().getIdToken();
                            }
                            CentriApp.userId = Amplify.Auth.getCurrentUser().getUserId();
                            Intent intent = new Intent(Constants.ACTION_AWS_CREDENTIAL_REFRESHED);
                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                            break;
                        case FAILURE:
                            Log.i("Amplify - Auth", "IdentityId not present because: " + cognitoAuthSession.getIdentityId().getError().toString());
                            break;
                        //if(isSplashDone) {
                        //    startActivity(new Intent(getApplicationContext(), LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        //}
                    }

                } else {
                    //if(isSplashDone) {
                    //    startActivity(new Intent(getApplicationContext(), LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    //}
                    return;
                }
            }
        }, new Consumer<AuthException>() {
            @Override
            public void accept(@NonNull AuthException value) {
                //if(isSplashDone) {
                //startActivity(new Intent(getApplicationContext(), LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                //}
                return;
            }
        });
    }


    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        LogUtil.d("CentriApp", "App lifecycle event " + event);
        if (event == Lifecycle.Event.ON_START) {
            fetchCredential();
        }


    }
}