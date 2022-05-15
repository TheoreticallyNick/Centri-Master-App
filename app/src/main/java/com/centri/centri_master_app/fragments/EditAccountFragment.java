/*
 * Copyright (C) Centri Group Inc.
 * Nick Theoret <nick@centriconnect.com>
 * <p>
 * The Edit Account Fragment shows a form with current logged in user information.
 * User can update his information from this screen
 *
 * @author TheoreticallyNick
 * @version 2.0
 * @since 2021-03-27
 */
package com.centri.centri_master_app.fragments;

import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;
import static com.amplifyframework.auth.result.step.AuthUpdateAttributeStep.CONFIRM_ATTRIBUTE_WITH_CODE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.amplifyframework.auth.AuthUserAttribute;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.result.AuthUpdateAttributeResult;
import com.amplifyframework.auth.result.step.AuthNextUpdateAttributeStep;
import com.amplifyframework.auth.result.step.AuthUpdateAttributeStep;
import com.amplifyframework.core.Amplify;
import com.centri.centri_master_app.R;
import com.centri.centri_master_app.data.models.User;
import com.centri.centri_master_app.databinding.FragmentEditAccountBinding;
import com.centri.centri_master_app.utils.Constants;
import com.centri.centri_master_app.utils.DialogUtils;
import com.centri.centri_master_app.utils.EmailUtil;
import com.centri.centri_master_app.utils.Enums;
import com.centri.centri_master_app.utils.FunctionUtils;
import com.centri.centri_master_app.utils.LogUtil;
import com.centri.centri_master_app.utils.PhoneUtil;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class EditAccountFragment extends Fragment implements View.OnClickListener {
    FragmentEditAccountBinding binding;
    private User user;
    AlertDialog loadingDialog;
    NavController navController;

    public EditAccountFragment() {
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_account, container, false);
        binding.setOnClick(this);
        Bundle arguments = getArguments();
        if (arguments != null) {
            String userStr = arguments.getString(Constants.KEY_USER);
            user = new Gson().fromJson(userStr, User.class);
        }
        if (user != null) {
            binding.txtUserEmail.setText(user.UserEmail);
            binding.txtUserFirst.setText(user.UserFirst);
            binding.txtUserLast.setText(user.UserLast);
            binding.txtUserPhone.setText(user.UserPhone.replace("+1", ""));
        }
        loadingDialog = DialogUtils.buildLoadingDialog(getContext());
        return binding.getRoot();
    }

    @Override
    public void onClick(View view) {
        navController = Navigation.findNavController(view);
        switch (view.getId()) {
            case R.id.tv_back:
                navController.popBackStack();
                break;
            case R.id.tv_next:
                updateUserInfo();
                break;
        }
    }

    private void updateUserInfo() {
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
        String user_first = binding.txtUserFirst.getText().toString();
        String user_last = binding.txtUserLast.getText().toString();
        String user_phone = binding.txtUserPhone.getText().toString();
        String user_email = binding.txtUserEmail.getText().toString();


        if (!PhoneUtil.isValidPhoneNumber(user_phone)) {
            DialogUtils.buildDialog(getContext(), "Enter a valid phone number.").show();
            return;
        }

        //LogUtil.i("PhoneUtil", "Prior to Formatting:" + user_phone);
        user_phone = PhoneUtil.formatPhoneNumber(user_phone);
        //LogUtil.i("PhoneUtil", "Post to Formatting:" + user_phone);

        if (!EmailUtil.validateEmailAddress(user_email)) {
            DialogUtils.buildDialog(getContext(), "Enter a valid email.").show();
            return;
        }

        ArrayList<AuthUserAttribute> attributes = new ArrayList<>();
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.email(), user_email));
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.familyName(), user_last));
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.name(), user_first));
        attributes.add(new AuthUserAttribute(AuthUserAttributeKey.phoneNumber(), user_phone));

        loadingDialog.show();

        Amplify.Auth.updateUserAttributes(
                attributes, // attributes is a list of AuthUserAttribute
                result -> {
                    Set<Map.Entry<AuthUserAttributeKey, AuthUpdateAttributeResult>> entries = result.entrySet();
                    LogUtil.i("AuthDemo", "Updated user attributes size = " + entries.size() + " attributes = " + entries.toString());
                    if (entries.size() != attributes.size()) {
                        LogUtil.i("AuthDemo", "Updated user attributes = " + entries.size());
                        return;
                    }
                    boolean hasConfirm = false;
                    for (Map.Entry<AuthUserAttributeKey, AuthUpdateAttributeResult> entry : entries) {
                        AuthUpdateAttributeResult value = entry.getValue();
                        AuthNextUpdateAttributeStep nextStep = value.getNextStep();
                        AuthUpdateAttributeStep updateAttributeStep = nextStep.getUpdateAttributeStep();
                        LogUtil.e("AuthDemo", "Update step " + entry.toString());
                        LogUtil.e("AuthDemo", "Update step " + updateAttributeStep.name());
                        if (updateAttributeStep.equals(CONFIRM_ATTRIBUTE_WITH_CODE)) {
                            LogUtil.e("AuthDemo", "goto confirm  " + updateAttributeStep);
                            hasConfirm = true;
                        }

                    }
                    loadingDialog.dismiss();
                    if (hasConfirm)
                        navigateTo(R.id.verificationFragment);
                    else
                        navigateTo(R.id.successFragment);

                },
                error -> {
                    loadingDialog.dismiss();
                    LogUtil.e("AuthDemo", "Failed to update user attributes " + error);
                    navigateTo(0);
                }
        );
    }

    private void navigateTo(int id) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingDialog.dismiss();
                if (id == 0) {
                    DialogUtils.buildMultiUseInfoDialog(getContext(), "Operation failed").show();
                } else if (id == R.id.successFragment) {
                    Bundle bundle = new Bundle();
                    bundle.putString(SuccessFragment.MSG, "Account information has successfully been updated.");
                    bundle.putString(SuccessFragment.BUTTON_TITLE, "Back to Main Dashboard");
                    bundle.putSerializable(SuccessFragment.NAV_TYPE, Enums.SuccessNavigationType.DASHBOARD);
                    navController.navigate(id, bundle);
                }else if (id == R.id.verificationFragment) {
                    navController.navigate(id);
                }
            }
        });

    }
}