/*
 * Copyright (C) Centri Group Inc.
 * Nick Theoret <nick@centriconnect.com>
 * <p>
 * The Success Fragment shows success message.
 *
 * @author TheoreticallyNick
 * @version 2.0
 * @since 2021-04-02
 */

package com.centri.centri_master_app.fragments;

import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.core.Amplify;
import com.centri.centri_master_app.R;
import com.centri.centri_master_app.databinding.FragmentVerificationBinding;
import com.centri.centri_master_app.utils.DialogUtils;
import com.centri.centri_master_app.utils.Enums;
import com.centri.centri_master_app.utils.FunctionUtils;

public class VerificationFragment extends Fragment implements View.OnClickListener {

    FragmentVerificationBinding binding;
    AlertDialog loadingDialog;
    NavController navController;

    public VerificationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_verification, container, false);
        binding.setOnClick(this);
        loadingDialog = DialogUtils.buildLoadingDialog(getContext());
        return binding.getRoot();
    }

    @Override
    public void onClick(View view) {
        navController = Navigation.findNavController(view);
        switch (view.getId()) {
            case R.id.tv_cancel:
                navController.popBackStack();
                break;
            case R.id.btnConfirm:
                FunctionUtils.hideKeyboard(getActivity());
                String code = binding.txtCode.getText().toString();
                if (code.isEmpty()) {
                    DialogUtils.buildDialog(getContext(), "Please enter the code").show();
                    return;
                }
                Amplify.Auth.confirmUserAttribute(AuthUserAttributeKey.email(), code,
                        () -> {
                            Log.i("AuthDemo", "Confirmed user attribute with correct code.");
                            navigateTo(R.id.successFragment);
                        },
                        error -> {
                            Log.e("AuthDemo", "Failed to confirm user attribute. Bad code?", error);
                            navigateTo(0);
                        }
                );

                break;
        }

    }

    private void navigateTo(int id) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingDialog.dismiss();
                if (id == 0) {
                    DialogUtils.buildMultiUseInfoDialog(getContext(), "Wrong auth code").show();
                } else if (id == R.id.successFragment) {
                    Bundle bundle = new Bundle();
                    bundle.putString(com.centri.centri_master_app.fragments.SuccessFragment.MSG, "Account information has successfully been updated.");
                    bundle.putString(com.centri.centri_master_app.fragments.SuccessFragment.BUTTON_TITLE, "Back to Main Dashboard");
                    bundle.putSerializable(com.centri.centri_master_app.fragments.SuccessFragment.NAV_TYPE, Enums.SuccessNavigationType.DASHBOARD);
                    navController.navigate(id, bundle);
                }
            }
        });

    }
}