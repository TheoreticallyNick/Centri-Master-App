/*
 * Copyright (C) Centri Group Inc.
 * Nick Theoret <nick@centriconnect.com>
 *
 * The Add Device Activity prompts the user to add a device.
 * Once the user inputs device name and device ID, they are taken to the Authentication Fragment
 * to enter the 6 digit code sent to their email address for authentication. Once the authentication
 * done, they are taken to success , where they are prompted
 * to return to the Dashboard screen.
 *
 * @author  TheoreticallyNick
 * @version 2.0
 * @since   2021-03-27
 */

package com.centri.centri_master_app.activities;

import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;

import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.centri.centri_master_app.databinding.ActivityAddDeviceBinding;
import com.centri.centri_master_app.R;

public class AddDeviceActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityAddDeviceBinding binding;
    private NavController navController;
    public String deviceName;
    public String deviceId;
    public String deviceAuth;
    public Document device;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_device);
        //setSupportActionBar(binding.toolbar);

        NavHostFragment navHostFragment = (NavHostFragment)getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_add,R.id.deviceIdFragment,R.id.deviceAuthFragment,R.id.successFragment,R.id.requestMonitorFragment)
                .build();
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            //NavigationUI.setupActionBarWithNavController(this, navController,mAppBarConfiguration);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_device, menu);
        return true;
    }
}