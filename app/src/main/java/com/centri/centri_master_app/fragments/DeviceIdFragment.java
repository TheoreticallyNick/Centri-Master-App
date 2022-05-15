/*
 * Copyright (C) Centri Group Inc.
 * Nick Theoret <nick@centriconnect.com>
 *
 * The Device Id Fragment takes monitoring device ID by either typing or scanning QR Code.
 *
 * @author  TheoreticallyNick
 * @version 2.0
 * @since   2021-03-17
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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.centri.centri_master_app.R;
import com.centri.centri_master_app.activities.AddDeviceActivity;
import com.centri.centri_master_app.data.ApiManager;
import com.centri.centri_master_app.databinding.FragmentDeviceIdBinding;
import com.centri.centri_master_app.interfaces.ApiCallback;
import com.centri.centri_master_app.utils.DialogUtils;
import com.centri.centri_master_app.utils.FunctionUtils;
import com.centri.centri_master_app.utils.LogUtil;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Map;

public class DeviceIdFragment extends Fragment implements View.OnClickListener {

    String TAG = DeviceIdFragment.class.getSimpleName();
    FragmentDeviceIdBinding binding;
    AlertDialog noInternetConnection;

    public DeviceIdFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LogUtil.d(TAG, "Called onCreate");
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_device_id, container, false);
        binding.setLifecycleOwner(this);
        binding.setOnClick(this);
        noInternetConnection = DialogUtils.buildNoInternetConnection(getActivity());
        return binding.getRoot();
    }

    @Override
    public void onClick(View view) {
        NavController navController = Navigation.findNavController(view);

        switch (view.getId()) {
            case R.id.tv_back:
                navController.popBackStack();
                break;
            case R.id.tv_next:
                String devId = binding.etDeviceId.getText().toString();
                if (devId.isEmpty()) {
                    binding.etDeviceId.setError("Device Id is required");
                    return;
                }

                FunctionUtils.hideKeyboard(getActivity());

                AddDeviceActivity activity = (AddDeviceActivity) getActivity();

                if (activity != null) {
                    activity.deviceId = devId;
                    AlertDialog alertDialog = DialogUtils.buildLoadingDialog(activity);

                    ApiManager.getInstance().getDeviceAuth(devId, new ApiCallback<Map<String, String>>() {
                        @Override
                        public void onSuccess(Map<String, String> result) {
                            if (result != null) {

                                String deviceAuth = result.get("DeviceAuth");

                                if (deviceAuth != null) {
                                    LogUtil.e(TAG, "getDevice >> " + deviceAuth);
                                    activity.deviceAuth = deviceAuth;
                                    navController.navigate(R.id.action_deviceIdFragment_to_deviceAuthFragment);

                                } else {
                                    DialogUtils.buildMultiUseInfoDialog(getContext(), "Device does not exists").show();
                                }
                            }
                        }

                        @Override
                        public void onError(Exception exception) {

                        }

                        @Override
                        public void onStarted() {

                        }
                    });

                }

                break;

            case R.id.img_qr:
                IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
                //integrator.setCaptureActivity(AnyOrientationCaptureActivity.class);
                integrator.setCameraId(0);  // Use a specific camera of the device
                integrator.setBeepEnabled(false);
                integrator.setOrientationLocked(false);
                integrator.initiateScan();
                break;
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        LogUtil.e(TAG, "onActivityResult >> " + requestCode);
        if (result != null) {
            if (result.getContents() == null) {
                DialogUtils.buildMultiUseInfoDialog(getContext(), "Cancelled").show();
            } else {
                //Toast.makeText(getContext(), "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                binding.etDeviceId.setText(result.getContents());
                binding.etDeviceId.setSelection(result.getContents().length());
                binding.etDeviceId.requestFocus();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}