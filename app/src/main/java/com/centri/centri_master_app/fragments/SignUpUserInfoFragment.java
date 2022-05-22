package com.centri.centri_master_app.fragments;

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
import com.amplifyframework.core.Amplify;
import com.centri.centri_master_app.R;
import com.centri.centri_master_app.activities.LoginActivity;
import com.centri.centri_master_app.data.models.Order;
import com.centri.centri_master_app.data.models.User;
import com.centri.centri_master_app.databinding.FragmentOrderDetailBinding;
import com.centri.centri_master_app.databinding.FragmentSignupUserInfoBinding;
import com.centri.centri_master_app.utils.Constants;
import com.centri.centri_master_app.utils.DialogUtils;
import com.centri.centri_master_app.utils.EmailUtil;
import com.centri.centri_master_app.utils.FunctionUtils;
import com.centri.centri_master_app.utils.LogUtil;
import com.centri.centri_master_app.utils.PhoneUtil;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class SignUpUserInfoFragment extends Fragment implements View.OnClickListener {
    String TAG = SignUpUserInfoFragment.class.getSimpleName();
    FragmentSignupUserInfoBinding binding;
    MutableLiveData<Boolean> isLoading = new MutableLiveData<>(true);
    private User user = new User();
    AlertDialog loadingDialog;
    NavController navController;
    AlertDialog phoneInvalidDialog, emailInvalidDialog;


    public SignUpUserInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup_user_info, container, false);
        binding.setOnClick(this);
        binding.setUser(user);
        phoneInvalidDialog = DialogUtils.buildPhoneInvalid(getActivity());
        loadingDialog = DialogUtils.buildLoadingDialog(getContext());
        binding.setLifecycleOwner(getViewLifecycleOwner());
        /*
        isLoading.observe(getViewLifecycleOwner(), value -> {
            if (value) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.svBody.setVisibility(View.GONE);
            } else {
                binding.progressBar.setVisibility(View.GONE);
                binding.svBody.setVisibility(View.VISIBLE);
            }

        });

         */
        return binding.getRoot();
    }

    @Override
    public void onClick(View view) {
        navController = Navigation.findNavController(view);
        switch (view.getId()) {
            case R.id.tv_back:

            case R.id.tv_login:
                Activity activity = getActivity();
                if (activity != null) {
                    startActivity(new Intent(activity, com.centri.centri_master_app.activities.LoginActivity.class));
                    activity.finish();
                }
                break;

            case R.id.tv_next:
                signUpUserInfo();
                break;
        }
    }

    public void signUpUserInfo() {

        FunctionUtils.hideKeyboard(getActivity());

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

        //string conversions
        String user_phone = user.UserPhone;
        String user_email = user.UserEmail;

        if (!PhoneUtil.isValidPhoneNumber(user_phone)) {
            phoneInvalidDialog.show();
            return;
        }

        if (!EmailUtil.validateEmailAddress(user_email)) {
            emailInvalidDialog.show();
            return;
        }

        user.UserPhone = PhoneUtil.formatPhoneNumber(user_phone);

        Bundle bundle = new Bundle();
        String json = new Gson().toJson(user);
        bundle.putString(Constants.KEY_USER_MODEL, json);
        navController.navigate(R.id.action_signUpUserInfoFragment_to_signUpUserAddressFragment, bundle);
    }
}
