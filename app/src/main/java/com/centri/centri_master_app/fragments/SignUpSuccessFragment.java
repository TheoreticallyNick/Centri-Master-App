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

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;

import com.centri.centri_master_app.activities.LoginActivity;
import com.centri.centri_master_app.data.models.User;
import com.centri.centri_master_app.R;
import com.centri.centri_master_app.databinding.FragmentSignupSuccessBinding;
import com.centri.centri_master_app.utils.Constants;
import com.centri.centri_master_app.utils.DialogUtils;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

public class SignUpSuccessFragment extends Fragment implements View.OnClickListener {
    String TAG = SignUpSuccessFragment.class.getSimpleName();
    FragmentSignupSuccessBinding binding;
    AlertDialog loadingDialog;
    private User user;
    NavController navController;
    AlertDialog phoneInvalidDialog, emailInvalidDialog;

    public SignUpSuccessFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            String json = arguments.getString(Constants.KEY_USER_MODEL);
            user = new Gson().fromJson(json, User.class);
        }
    }

    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup_success, container, false);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnReturn:
                startActivity(new Intent(getContext(), LoginActivity.class));
                break;
        }
    }
}
