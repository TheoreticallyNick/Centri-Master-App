/*
 * Copyright (C) Centri Group Inc.
 * Nick Theoret <nick@centriconnect.com>
 * <p>
 * The Account Fragment shows current logged in user information.
 *
 * @author TheoreticallyNick
 * @version 2.0
 * @since 2021-03-27
 */
package com.centri.centri_master_app.fragments;

import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.core.Amplify;
import com.centri.centri_master_app.R;
import com.centri.centri_master_app.data.models.User;
import com.centri.centri_master_app.databinding.FragmentAccountBinding;
import com.centri.centri_master_app.utils.Constants;
import com.centri.centri_master_app.utils.DialogUtils;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

public class AccountFragment extends Fragment implements View.OnClickListener {

    private FragmentAccountBinding binding;
    MutableLiveData<Boolean> isLoading = new MutableLiveData<>(true);
    User user = new User();
    Boolean noInternetBool;
    AlertDialog noInternetConnection;


    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        noInternetConnection = DialogUtils.buildNoInternetConnection(getActivity());
        noInternetBool = false;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_account, container, false);
        binding.setOnClick(this);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        isLoading.observe(getViewLifecycleOwner(), value -> {
            if (value) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.body.setVisibility(View.GONE);
                binding.rlBottom.setVisibility(View.GONE);
            } else {
                binding.progressBar.setVisibility(View.GONE);
                binding.body.setVisibility(View.VISIBLE);
                binding.rlBottom.setVisibility(View.VISIBLE);
            }

        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isLoading.setValue(true);
        Amplify.Auth.fetchUserAttributes(
                attributes -> {
                    Log.i("AuthDemo", "User attributes = " + attributes.toString());
                    for (AuthUserAttribute attribute : attributes) {
                        AuthUserAttributeKey key = attribute.getKey();
                        if (key.equals(AuthUserAttributeKey.email())) {
                            binding.txtUserEmail.setText(attribute.getValue());
                            user.UserEmail = attribute.getValue();
                        } else if (key.equals(AuthUserAttributeKey.familyName())) {
                            binding.txtUserLast.setText(attribute.getValue());
                            user.UserLast = attribute.getValue();
                        } else if (key.equals(AuthUserAttributeKey.name())) {
                            binding.txtUserFirst.setText(attribute.getValue());
                            user.UserFirst = attribute.getValue();
                        } else if (key.equals(AuthUserAttributeKey.phoneNumber())) {
                            binding.txtUserPhone.setText(attribute.getValue());
                            user.UserPhone = attribute.getValue();
                        }

                    }
                    isLoading.postValue(false);

                },
                error -> {
                    Log.e("Amplify", error.getCause().toString());
                    //Handles no internet connection
                    if (error.getCause().toString().contains("AmazonClientException") ) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                noInternetConnection.show();
                            }
                        });
                    }
                    isLoading.postValue(false);
                }
        );
    }

    @Override
    public void onClick(View view) {
        NavController navController = Navigation.findNavController(view);
        switch (view.getId()) {
            case R.id.tv_back:
                Activity activity = getActivity();
                if (activity != null) {
                    activity.finish();
                }
                break;
            case R.id.tv_next:
                String userStr = new Gson().toJson(user);
                Bundle bundle = new Bundle();
                bundle.putString(Constants.KEY_USER, userStr);
                navController.navigate(R.id.action_account_fragment_to_editAccountFragment, bundle);
                break;
        }
    }
}