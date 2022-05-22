package com.centri.centri_master_app.fragments;

import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.core.Amplify;
import com.centri.centri_master_app.R;
import com.centri.centri_master_app.data.models.Order;
import com.centri.centri_master_app.data.models.User;
import com.centri.centri_master_app.databinding.FragmentSignupUserPassBinding;
import com.centri.centri_master_app.utils.Constants;
import com.centri.centri_master_app.utils.DialogUtils;
import com.centri.centri_master_app.utils.FunctionUtils;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class SignUpUserPassFragment extends Fragment implements View.OnClickListener {
    String TAG = SignUpUserPassFragment.class.getSimpleName();
    FragmentSignupUserPassBinding binding;
    MutableLiveData<Boolean> isLoading = new MutableLiveData<>(true);
    private User user;
    AlertDialog loadingDialog;
    NavController navController;
    AlertDialog passNotMatchingDialog, userExistsDialog, passInvalidDialog, noInternetConnection;

    public SignUpUserPassFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        passNotMatchingDialog = DialogUtils.buildPassNotMatching(getActivity());
        Bundle arguments = getArguments();
        if (arguments != null) {
            String json = arguments.getString(Constants.KEY_USER_MODEL);
            user = new Gson().fromJson(json, User.class);
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup_user_pass, container, false);
        binding.setOnClick(this);
        binding.setUser(user);
        loadingDialog = DialogUtils.buildLoadingDialog(getContext());
        binding.setLifecycleOwner(getViewLifecycleOwner());
        /*isLoading.observe(getViewLifecycleOwner(), value -> {
            if (value) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.svBody.setVisibility(View.GONE);
            } else {
                binding.progressBar.setVisibility(View.GONE);
                binding.svBody.setVisibility(View.VISIBLE);
            }

        });*/
        return binding.getRoot();
    }

    @Override
    public void onClick(View view) {
        navController = Navigation.findNavController(view);
        switch (view.getId()) {
            case R.id.tv_back:
                navController.popBackStack();
                break;
            case R.id.tv_SignUp:
                signUpUserPassInput();
                break;

            case R.id.tv_login:
                Activity activity = getActivity();
                if (activity != null) {
                    startActivity(new Intent(activity, com.centri.centri_master_app.activities.LoginActivity.class));
                    activity.finish();
                }
                break;
        }
    }

    public void signUpUserPassInput() {
        FunctionUtils.hideKeyboard(getActivity());

        if (user.UserPass.isEmpty()) {
            binding.txtUserPass.setError("Required");
            binding.txtUserPass.requestFocus();
            return;
        }

        if (user.UserPassConf.isEmpty()) {
            binding.txtUserPassConf.setError("Required");
            binding.txtUserPassConf.requestFocus();
            return;
        }

        String user_password = user.UserPass;
        String user_passconf = user.UserPassConf;

        if (!user_password.equals(user_passconf)) {
            passNotMatchingDialog.show();
            return;
        } else {
            signUpSubmit();
        }

    }

    public void signUpSubmit() {

        //string conversions
        String user_first = user.UserFirst;
        String user_last = user.UserLast;
        String user_phone = user.UserPhone;
        String user_email = user.UserEmail;
        String user_password = user.UserPass;
        String user_street = user.UserStreet;
        String user_city = user.UserCity;
        String user_state = user.UserState;
        String user_zip = user.UserZip;

        Bundle bundle = new Bundle();
        String json = new Gson().toJson(user);
        bundle.putString(Constants.KEY_USER_MODEL, json);

        ArrayList<AuthUserAttribute> attributes = new ArrayList<>();

        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.email(), user_email));
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.familyName(), user_last));
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.name(), user_first));
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.phoneNumber(), user_phone));
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.custom("custom:street"), user_street));
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.custom("custom:city"), user_city));
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.custom("custom:state"), user_state));
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.custom("custom:zip"), user_zip));

        loadingDialog.show();
        Amplify.Auth.signUp(
                user_email,
                user_password,
                AuthSignUpOptions.builder().userAttributes(attributes).build(),
                result -> {
                    Log.i("AmplifyAuth", "Result: " + result.toString());
                    loadingDialog.dismiss();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            navController.navigate(R.id.action_signUpUserPassFragment_to_signUpAuthFragment, bundle);
                        }
                    });
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
