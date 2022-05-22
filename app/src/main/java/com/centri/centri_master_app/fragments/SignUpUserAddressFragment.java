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
import com.centri.centri_master_app.data.models.Order;
import com.centri.centri_master_app.data.models.User;
import com.centri.centri_master_app.databinding.FragmentOrderDetailBinding;
import com.centri.centri_master_app.databinding.FragmentSignupUserAddressBinding;
import com.centri.centri_master_app.databinding.FragmentSignupUserInfoBinding;
import com.centri.centri_master_app.utils.Constants;
import com.centri.centri_master_app.utils.DialogUtils;
import com.centri.centri_master_app.utils.EmailUtil;
import com.centri.centri_master_app.utils.FunctionUtils;
import com.centri.centri_master_app.utils.PhoneUtil;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

public class SignUpUserAddressFragment extends Fragment implements View.OnClickListener {
    String TAG = SignUpUserAddressFragment.class.getSimpleName();
    FragmentSignupUserAddressBinding binding;
    private User user;
    MutableLiveData<Boolean> isLoading = new MutableLiveData<>(true);
    AlertDialog loadingDialog;
    NavController navController;

    public SignUpUserAddressFragment() {
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

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup_user_address, container, false);
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

        });
         */
        return binding.getRoot();
    }

    @Override
    public void onClick(View view) {
        navController = Navigation.findNavController(view);
        switch (view.getId()) {
            case R.id.tv_back:
                navController.popBackStack();
                break;

            case R.id.tv_login:
                Activity activity = getActivity();
                if (activity != null) {
                    startActivity(new Intent(activity, com.centri.centri_master_app.activities.LoginActivity.class));
                    activity.finish();
                }
                break;

            case R.id.tv_next:
                signUpUserAddressInput();
                break;
        }
    }

    public void signUpUserAddressInput() {

        FunctionUtils.hideKeyboard(getActivity());

        if (user.UserStreet.isEmpty()) {
            binding.txtUserStreet.setError("Required");
            binding.txtUserStreet.requestFocus();
            return;
        }

        if (user.UserCity.isEmpty()) {
            binding.txtUserCity.setError("Required");
            binding.txtUserCity.requestFocus();
            return;
        }

        if (user.UserState.isEmpty()) {
            binding.txtUserState.setError("Required");
            binding.txtUserState.requestFocus();
            return;
        }

        if (user.UserEmail.isEmpty()) {
            binding.txtUserZip.setError("Required");
            binding.txtUserZip.requestFocus();
            return;
        }

        Bundle bundle = new Bundle();
        String json = new Gson().toJson(user);
        bundle.putString(Constants.KEY_USER_MODEL, json);
        navController.navigate(R.id.action_signUpUserAddressFragment_to_signUpUserPassFragment, bundle);
    }
}