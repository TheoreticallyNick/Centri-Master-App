/*
 * Copyright (C) Centri Group Inc.
 * Nick Theoret <nick@centriconnect.com>
 * <p>
 * The Add Monitor Fragment takes monitoring name from user.
 *
 * @author TheoreticallyNick
 * @version 2.0
 * @since 2021-02-10
 */

package com.centri.centri_master_app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.centri.centri_master_app.R;
import com.centri.centri_master_app.activities.AddDeviceActivity;
import com.centri.centri_master_app.databinding.FragmentAddDeviceBinding;

public class AddDeviceFragment extends Fragment implements View.OnClickListener {

    FragmentAddDeviceBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_device, container, false);
        binding.setLifecycleOwner(this);
        binding.setOnClick(this);

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        NavController navController = Navigation.findNavController(view);
        switch (view.getId()) {
            case R.id.tv_back:
                getActivity().finish();
                break;
            case R.id.btnInfo:
                navController.navigate(R.id.requestMonitorFragment);
                break;
            case R.id.tv_next:
                String devName = binding.etDeviceName.getText().toString();
                if (devName.isEmpty()) {
                    binding.etDeviceName.setError("Name is required");
                    return;
                }
                AddDeviceActivity activity = (AddDeviceActivity) getActivity();
                if (activity != null) {
                    activity.deviceName = devName;
                    navController.navigate(R.id.action_nav_add_to_deviceIdFragment);
                }

                break;
        }
    }
}