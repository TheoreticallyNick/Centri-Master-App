/**
 * Copyright (C) Centri Group Inc.
 * Nick Theoret <nick@centriconnect.com>
 * <p>
 * The Dashboard Fragment shows device list and also shows button for adding new device.
 *
 * @author TheoreticallyNick
 * @version 2.0
 * @since 2021-02-10
 */
package com.centri.centri_master_app.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.centri.centri_master_app.CentriApp;
import com.centri.centri_master_app.R;
import com.centri.centri_master_app.activities.AddDeviceActivity;
import com.centri.centri_master_app.activities.MainActivity;
import com.centri.centri_master_app.adapters.DeviceListAdapter;
import com.centri.centri_master_app.data.ApiManager;
import com.centri.centri_master_app.data.models.DeviceModel;
import com.centri.centri_master_app.databinding.FragmentDashboardBinding;
import com.centri.centri_master_app.interfaces.ApiCallback;
import com.centri.centri_master_app.utils.Constants;
import com.centri.centri_master_app.utils.LogUtil;

import java.util.List;
import java.util.Map;

public class DashboardFragment extends Fragment implements View.OnClickListener {

    String TAG = DashboardFragment.class.getSimpleName();
    private FragmentDashboardBinding binding;
    private DeviceListAdapter deviceListAdapter;
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("receiver", "Got message: ");
            String action = intent.getAction();
            if (action != null && action.equals(Constants.ACTION_AWS_CREDENTIAL_REFRESHED)) {
                loadData();
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtil.d(TAG, "Called onCreate");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        LogUtil.d(TAG, "Called onCreateView");
        LogUtil.d(TAG, "onCreateView >> DashboardFragment ");
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dashboard, container, false);
        setHasOptionsMenu(true);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setOnClick(this);
        binding.rvDeviceList.setLayoutManager(new LinearLayoutManager(getContext()));
        deviceListAdapter = new DeviceListAdapter();
        binding.rvDeviceList.setAdapter(deviceListAdapter);
        binding.llAdd.setVisibility(View.GONE);
        binding.fabAddDevice.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.noNetwork.setVisibility(View.GONE);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        LogUtil.d(TAG, "Called onResume");
        super.onResume();
        LogUtil.d(TAG, "onResume >> DashboardFragment ");
        MainActivity activity = (MainActivity) getActivity();

        if (activity != null) {
            activity.adjustDrawerMenuItemVisibility(new int[]{R.id.nav_add}, new int[]{R.id.nav_delete});
        }

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(mMessageReceiver,
                new IntentFilter(Constants.ACTION_AWS_CREDENTIAL_REFRESHED));

        if (CentriApp.credentials != null) {
            loadData();
        }
    }

    @Override
    public void onPause() {
        LogUtil.d(TAG, "Called onPause");
        super.onPause();
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(mMessageReceiver);
    }

    @Override
    public void onStart() {
        LogUtil.d(TAG, "Called onStart");
        super.onStart();
        LogUtil.d(TAG, "onStart >> DashboardFragment");
    }

    @Override
    public void onStop() {
        LogUtil.d(TAG, "Called onStop");
        super.onStop();
        LogUtil.d(TAG, "onStop >> DashboardFragment");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        LogUtil.d(TAG, "Called onOptionsSelected");
        final int itemId = item.getItemId();
        if (itemId == R.id.action_refresh) {
            if (CentriApp.credentials != null) {
                loadData();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadData() {
        LogUtil.d(TAG, "Called loadData");

        LogUtil.d(TAG, "getDeviceList API Call Initiation");
        ApiManager.getInstance().getDeviceList(new ApiCallback<List<Map<String, String>>>() {
            @Override
            public void onSuccess(List<Map<String, String>> result) {
                LogUtil.d(TAG, "getDeviceList API Call Success");
                LogUtil.d(TAG, "getDeviceList API Call data >> " + result);
                if (result.size() > 0) {
                    for (Map<String, String> item : result) {
                        DeviceModel deviceModel = new DeviceModel();
                        String deviceId = item.get("DeviceID");
                        if (deviceId != null) {
                            deviceModel.deviceId = deviceId;
                        }
                        String deviceName = item.get("DeviceName");
                        if (deviceName != null) {
                            deviceModel.deviceName = deviceName;
                        }

                        deviceListAdapter.addDevice(deviceModel);
                    }
                    binding.llAdd.setVisibility(View.GONE);
                    binding.fabAddDevice.setVisibility(View.VISIBLE);
                    binding.progressBar.setVisibility(View.GONE);
                    binding.noNetwork.setVisibility(View.GONE);
                } else {
                    binding.llAdd.setVisibility(View.VISIBLE);
                    binding.fabAddDevice.setVisibility(View.GONE);
                    binding.progressBar.setVisibility(View.GONE);
                    binding.noNetwork.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(Exception exception) {
                LogUtil.d(TAG, "getDeviceList API Call Failure");
                LogUtil.e(TAG, "getDeviceList API Call Error: " + exception.getMessage());
                if (deviceListAdapter != null) {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.llAdd.setVisibility(View.GONE);
                    binding.fabAddDevice.setVisibility(View.VISIBLE);
                    binding.noNetwork.setVisibility(View.VISIBLE);
                } else {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.llAdd.setVisibility(View.VISIBLE);
                    binding.fabAddDevice.setVisibility(View.GONE);
                    binding.noNetwork.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onStarted() {
                LogUtil.d(TAG, "getDeviceList API Call Started");
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.llAdd.setVisibility(View.GONE);
                binding.fabAddDevice.setVisibility(View.GONE);
            }
        });

        if (deviceListAdapter != null) {
            binding.progressBar.setVisibility(View.GONE);
            binding.llAdd.setVisibility(View.GONE);
            binding.fabAddDevice.setVisibility(View.VISIBLE);
            binding.noNetwork.setVisibility(View.GONE);
        } else {
            binding.progressBar.setVisibility(View.GONE);
            binding.llAdd.setVisibility(View.VISIBLE);
            binding.fabAddDevice.setVisibility(View.GONE);
            binding.noNetwork.setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_add_device_center:
                startActivity(new Intent(getContext(), AddDeviceActivity.class));
                break;
            case R.id.fab_add_device:
                startActivity(new Intent(getContext(), AddDeviceActivity.class));
                break;
        }
    }
}