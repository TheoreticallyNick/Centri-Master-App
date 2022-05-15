/*
 * Copyright (C) Centri Group Inc.
 * Nick Theoret <nick@centriconnect.com>
 *
 * The Delete Fragment takes user confirmation before deleting a device.
 *
 * @author  TheoreticallyNick
 * @version 2.0
 * @since   2021-03-17
 */
package com.centri.centri_master_app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.centri.centri_master_app.R;
import com.centri.centri_master_app.data.ApiManager;
import com.centri.centri_master_app.databinding.FragmentDeleteDeviceBinding;
import com.centri.centri_master_app.interfaces.ApiCallback;
import com.centri.centri_master_app.utils.Constants;
import com.centri.centri_master_app.utils.DialogUtils;

import org.jetbrains.annotations.NotNull;

public class DeleteDeviceFragment extends Fragment implements View.OnClickListener {
    String userId;
    String deviceId;
    String deviceName;
    FragmentDeleteDeviceBinding binding;
    AlertDialog dialog, noInternetConnection;

    public DeleteDeviceFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle arguments = getArguments();
        if (arguments != null) {
            userId = arguments.getString(Constants.KEY_USER_ID);
            deviceId = arguments.getString(Constants.KEY_DEVICE_ID);
            deviceName = arguments.getString(Constants.KEY_DEVICE_NAME);
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_delete_device, container, false);
        binding.setLifecycleOwner(this);
        binding.setOnClick(this);
        noInternetConnection = DialogUtils.buildNoInternetConnection(getActivity());
        binding.tvHint.setText(String.format(getString(R.string.delete_device_notice), deviceName));
        dialog = DialogUtils.buildLoadingDialog(getContext());
        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_add_device, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onClick(View view) {
        NavController navController = Navigation.findNavController(view);
        final int id = view.getId();
        if (id == R.id.tv_yes) {

            ApiManager.getInstance().postUnpairDevice(deviceId, userId, new ApiCallback<String>() {

                @Override
                public void onSuccess(String result) {
                    if (result.equals("Success")) {
                        navController.popBackStack(R.id.nav_dashboard, false);
                    } else {
                        DialogUtils.buildMultiUseInfoDialog(getContext(), "Operation failed").show();
                    }
                }

                @Override
                public void onError(Exception exception) {

                }

                @Override
                public void onStarted() {

                }
            });

        } else if (id == R.id.tv_back) {
            navController.popBackStack();
        }
    }
}