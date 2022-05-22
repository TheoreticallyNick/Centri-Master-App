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

package com.centri.centri_master_app.fragments;

import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.amplifyframework.core.Amplify;
import com.centri.centri_master_app.data.models.User;
import com.centri.centri_master_app.databinding.FragmentSignupAuthBinding;
import com.centri.centri_master_app.databinding.FragmentSignupUserPassBinding;
import com.centri.centri_master_app.utils.Constants;
import com.centri.centri_master_app.utils.DialogUtils;
import com.centri.centri_master_app.utils.FunctionUtils;
import com.centri.centri_master_app.R;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;


public class SignUpAuthFragment extends Fragment implements View.OnClickListener {
    String TAG = SignUpAuthFragment.class.getSimpleName();
    FragmentSignupAuthBinding binding;
    private User user;
    AlertDialog loadingDialog;
    NavController navController;
    AlertDialog wrongAuthCodeDialog;

    public SignUpAuthFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wrongAuthCodeDialog = DialogUtils.buildWrongAuthCode(getActivity());
        Bundle arguments = getArguments();
        if (arguments != null) {
            String json = arguments.getString(Constants.KEY_USER_MODEL);
            user = new Gson().fromJson(json, User.class);
        }
    }

    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup_auth, container, false);
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
        int id = view.getId();
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

        FunctionUtils.hideKeyboard(getActivity());

        if (user.UserCode.isEmpty()) {
            binding.txtUserCode.setError("Required");
            binding.txtUserCode.requestFocus();
            return;
        }

        String user_email = user.UserEmail;
        String user_code = user.UserCode;

        Bundle bundle = new Bundle();
        String json = new Gson().toJson(user);
        bundle.putString(Constants.KEY_USER_MODEL, json);

        Amplify.Auth.confirmSignUp(
                user_email,
                user_code,
                result -> {
                    Log.i("AuthQuickstart", result.isSignUpComplete() ? "Confirm signUp succeeded" : "Confirm sign up not complete");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            navController.navigate(R.id.action_signUpAuthFragment_to_signUpSuccessFragment, bundle);
                        }
                    });
                    return;
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
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.i("AuthQuickstart", error.getCause().toString());
                            }
                        });
                    }
                }
        );
    }
}

