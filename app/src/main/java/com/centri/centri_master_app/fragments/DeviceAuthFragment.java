/*
 * Copyright (C) Centri Group Inc.
 * Nick Theoret <nick@centriconnect.com>
 * <p>
 * The Device Auth Fragment takes the auth code that was sent to user email.
 *
 * @author TheoreticallyNick
 * @version 2.0
 * @since 2021-03-17
 */
package com.centri.centri_master_app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.centri.centri_master_app.CentriApp;
import com.centri.centri_master_app.R;
import com.centri.centri_master_app.activities.AddDeviceActivity;
import com.centri.centri_master_app.data.ApiManager;
import com.centri.centri_master_app.databinding.FragmentDeviceAuthBinding;
import com.centri.centri_master_app.interfaces.ApiCallback;
import com.centri.centri_master_app.utils.DialogUtils;
import com.centri.centri_master_app.utils.Enums;
import com.centri.centri_master_app.utils.FunctionUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DeviceAuthFragment extends Fragment implements View.OnClickListener {

    String TAG = DeviceAuthFragment.class.getSimpleName();
    FragmentDeviceAuthBinding binding;

    public DeviceAuthFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_device_auth, container, false);
        binding.setLifecycleOwner(this);
        binding.setOnClick(this);
        return binding.getRoot();
    }

    @Override
    public void onClick(View view) {
        NavController navController = Navigation.findNavController(view);

        switch (view.getId()) {
            case R.id.tv_back:
                navController.popBackStack();
                break;
            case R.id.tv_done:
                String devAuth = binding.etDeviceAuth.getText().toString();
                if (devAuth.isEmpty()) {
                    binding.etDeviceAuth.setError("Device Activation Code is required");
                    return;
                }

                AddDeviceActivity activity = (AddDeviceActivity) getActivity();

                if (activity != null) {
                    FunctionUtils.hideKeyboard(activity);
                    if (!devAuth.equals(activity.deviceAuth)) {
                        DialogUtils.buildMultiUseInfoDialog(getContext(), "Activation Code does not match").show();
                        return;
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                    long currentTimeMillis = System.currentTimeMillis();
                    String ts = sdf.format(new Date(currentTimeMillis));

                    AlertDialog alertDialog = DialogUtils.buildLoadingDialog(activity);

                    ApiManager.getInstance().postPairDevice(activity.deviceId, CentriApp.userId, activity.deviceName, new ApiCallback<String>() {

                        @Override
                        public void onSuccess(String result) {
                            if (result.equals("Success")) {
                                Bundle bundle = new Bundle();
                                bundle.putString(SuccessFragment.MSG, "Your device was successfully added.");
                                bundle.putString(SuccessFragment.BUTTON_TITLE, "Back to Main Dashboard");
                                bundle.putSerializable(SuccessFragment.NAV_TYPE, Enums.SuccessNavigationType.DASHBOARD);
                                navController.navigate(R.id.successFragment, bundle);
                            } else {
                                DialogUtils.buildMultiUseInfoDialog(getContext(), "Failed").show();
                            }
                        }

                        @Override
                        public void onError(Exception exception) {

                        }

                        @Override
                        public void onStarted() {

                        }


                    /*
                    Document uDev = new Document();
                    uDev.put("PK", "USER#" + CentriApp.userId);
                    uDev.put("SK", "DEV#" + activity.deviceId);
                    uDev.put("CreatedDate", ts);
                    uDev.put("DeviceName", activity.deviceName);

                    Document devU = new Document();
                    devU.put("PK", "DEV#" + activity.deviceId);
                    devU.put("SK", "USER#" + CentriApp.userId);
                    devU.put("CreatedDate", ts);


                    DynamoDbManager.getInstance(CentriApp.credentials).addDevice(uDev, devU, new DynamoDbCallback<Document>() {
                        @Override
                        public void onSuccess(Document result) {
                            alertDialog.dismiss();
                            if (result != null) {
                                //navController.navigate(R.id.action_deviceIdFragment_to_deviceAuthFragment);
                                Bundle bundle = new Bundle();
                                bundle.putString(SuccessFragment.MSG, "Your device was successfully added.");
                                bundle.putString(SuccessFragment.BUTTON_TITLE, "Back to Main Dashboard");
                                bundle.putSerializable(SuccessFragment.NAV_TYPE, Enums.SuccessNavigationType.DASHBOARD);
                                navController.navigate(R.id.successFragment, bundle);
                            } else {
                                DialogUtils.buildMultiUseInfoDialog(getContext(),"Failed").show();
                            }

                        }

                        @Override
                        public void onError(Exception exception) {
                            alertDialog.dismiss();
                            LogUtil.e(TAG, "addDevice >> " + exception.getMessage());

                        }

                        @Override
                        public void onStarted() {
                            LogUtil.e(TAG, "addDevice >> started");
                            alertDialog.show();

                        }
                    });

                     */


                    });
                }

                break;
            }

    }
}