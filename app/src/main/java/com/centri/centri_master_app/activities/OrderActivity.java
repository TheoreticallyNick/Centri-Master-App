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
import androidx.navigation.NavGraph;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.centri.centri_master_app.databinding.ActivityOrderBinding;
import com.centri.centri_master_app.utils.Constants;
import com.centri.centri_master_app.utils.LogUtil;
import com.centri.centri_master_app.R;

public class OrderActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityOrderBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order);
        setSupportActionBar(binding.toolbar);
        Bundle arguments = getIntent().getExtras();
        int startDestId = 0;
        if (arguments != null) {
            startDestId = arguments.getInt(Constants.KEY_ORDER_ACTIVITY_START_DESTINATION);
        }
        LogUtil.d("OrderActivity", "startDestId >> " + startDestId);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.orderDetailFragment, R.id.orderConfirmFragment, R.id.requestMonitorFragment, R.id.orderSuccessFragment)
                .build();
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        }
        NavGraph navGraph = navController.getNavInflater().inflate(R.navigation.order_navigation);
        if (startDestId == 0) {
            finish();
        } else {
            navGraph.setStartDestination(startDestId);
            navController.setGraph(navGraph);
        }

    }
}