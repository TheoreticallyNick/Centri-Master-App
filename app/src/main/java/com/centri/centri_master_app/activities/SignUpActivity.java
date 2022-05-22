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

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.centri.centri_master_app.databinding.ActivitySignupBinding;
import com.centri.centri_master_app.utils.Constants;
import com.centri.centri_master_app.utils.LogUtil;
import com.centri.centri_master_app.R;

public class SignUpActivity extends AppCompatActivity {

    //private AppBarConfiguration mAppBarConfiguration;
    private ActivitySignupBinding binding;
    private NavController navController;
    NavHostFragment navHostFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup);
        binding.setLifecycleOwner(this);

        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        NavGraph navGraph = navController.getNavInflater().inflate(R.navigation.signup_navigation);
        navGraph.setStartDestination(R.id.signUpUserInfoFragment);
        navController.setGraph(navGraph);
        //NavigationUI.setupWithNavController(binding.bottomNavView, navController);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }
}


/*
public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    String TAG = SignUpActivity.class.getSimpleName();
    ActivitySignupBinding binding;
    User user = new User();
    AlertDialog passNotMatchingDialog, phoneInvalidDialog, emailInvalidDialog, userExistsDialog, passInvalidDialog, loadingDialog, noInternetConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup_1);
        binding.setLifecycleOwner(this);
        binding.setOnClick(this);
        binding.setUser(user);
        passNotMatchingDialog = DialogUtils.buildPassNotMatching(this);
        phoneInvalidDialog = DialogUtils.buildPhoneInvalid(this);
        emailInvalidDialog = DialogUtils.buildEmailInvalid(this);
        userExistsDialog = DialogUtils.buildAlreadyExists(this);
        passInvalidDialog = DialogUtils.buildPassInvalid(this);
        loadingDialog=DialogUtils.buildLoadingDialog(this);
        noInternetConnection = DialogUtils.buildNoInternetConnection(this);
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed Called");
        startActivity(new Intent(SignUpActivity.this, com.centri.centri_master_app.activities.LoginActivity.class));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnSignUp:
                signUpUserCognito();
                break;
            case R.id.btnLogin:
                startActivity(new Intent(SignUpActivity.this, com.centri.centri_master_app.activities.LoginActivity.class));
                finish();
                break;
        }
    }

    public void signUpUserCognito() {

        FunctionUtils.hideKeyboard(this);

        if (user.UserFirst.isEmpty()) {
            binding.txtUserFirst.setError("Required");
            binding.txtUserFirst.requestFocus();
            return;
        }

        if (user.UserLast.isEmpty()) {
            binding.txtUserLast.setError("Required");
            binding.txtUserLast.requestFocus();
            return;
        }

        if (user.UserPhone.isEmpty()) {
            binding.txtUserPhone.setError("Required");
            binding.txtUserPhone.requestFocus();
            return;
        }

        if (user.UserEmail.isEmpty()) {
            binding.txtUserEmail.setError("Required");
            binding.txtUserEmail.requestFocus();
            return;
        }

        if (user.UserPass.isEmpty()) {
            binding.txtUserPass.setError("Required");
            binding.txtUserPass.requestFocus();
            return;
        }

        if (user.UserPass.length() < 8) {
            passInvalidDialog.show();
            return;
        }

        if (user.UserPassConf.isEmpty()) {
            binding.txtUserPassConf.setError("Required");
            binding.txtUserPassConf.requestFocus();
            return;
        }

        //string conversions
        String user_first = user.UserFirst;
        String user_last = user.UserLast;
        String user_phone = user.UserPhone;
        String user_email = user.UserEmail;
        String user_password = user.UserPass;
        String user_passconf = user.UserPassConf;

        if (!user_password.equals(user_passconf)) {
            passNotMatchingDialog.show();
            return;
        }

        if (!PhoneUtil.isValidPhoneNumber(user_phone)) {
            phoneInvalidDialog.show();
            return;
        }

        LogUtil.i("PhoneUtil", "Prior to Formatting:" + user_phone);
        user_phone = PhoneUtil.formatPhoneNumber(user_phone);
        LogUtil.i("PhoneUtil", "Post to Formatting:" + user_phone);

        if (!EmailUtil.validateEmailAddress(user_email)) {
            emailInvalidDialog.show();
            return;
        }

        ArrayList<AuthUserAttribute> attributes = new ArrayList<>();
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.email(), user_email));
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.familyName(), user_last));
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.name(), user_first));
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.phoneNumber(), user_phone));

        loadingDialog.show();
        Amplify.Auth.signUp(
                user_email,
                user_password,
                AuthSignUpOptions.builder().userAttributes(attributes).build(),
                result -> {
                    Log.i("AmplifyAuth", "Result: " + result.toString());
                    loadingDialog.dismiss();
                    Intent intent = new Intent(SignUpActivity.this, SignUpAuthActivity.class);
                    Bundle bun = new Bundle();
                    bun.putSerializable("user", user);
                    intent.putExtras(bun);
                    startActivity(intent);
                    finish();
                },
                error -> {
                    loadingDialog.dismiss();
                    Log.e("AmplifyAuth", error.toString());
                    //Handles incorrect username/password combo
                    if (error.getCause().toString().contains("UsernameExistsException") ) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                userExistsDialog.show();
                            }
                        });
                        return;
                    }
                    //Handles incorrect password format input
                    if (error.getCause().toString().contains("InvalidParameterException") ) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                passInvalidDialog.show();
                            }
                        });
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
                }
        );
    }
}
*/