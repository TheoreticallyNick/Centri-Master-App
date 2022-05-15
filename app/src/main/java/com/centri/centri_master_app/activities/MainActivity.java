/*
 * Copyright (C) Centri Group Inc.
 * Nick Theoret <nick@centriconnect.com>
 * <p>
 * The Main Activity works as a coordinator for all other activities and fragments.
 *
 * @author TheoreticallyNick
 * @version 2.0
 * @since 2021-02-10
 */

package com.centri.centri_master_app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.amplifyframework.core.Amplify;
import com.centri.centri_master_app.CentriApp;
import com.centri.centri_master_app.data.models.DeviceModel;
import com.centri.centri_master_app.databinding.ActivityMainBinding;
import com.centri.centri_master_app.fragments.DashboardFragment;
import com.centri.centri_master_app.fragments.DeviceDetailFragment;
import com.centri.centri_master_app.utils.Constants;
import com.centri.centri_master_app.R;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    String TAG = MainActivity.class.getSimpleName();
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private NavController navController;
    NavHostFragment navHostFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Called onCreate");
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setLifecycleOwner(this);
        setSupportActionBar(binding.appBarMain.toolbar);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_dashboard, R.id.nav_add, R.id.nav_delete, R.id.nav_account,
                R.id.nav_logout, R.id.deviceDetailFragment, R.id.nav_account_activity, R.id.nav_order)
                .setOpenableLayout(binding.drawerLayout)
                .build();
        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(binding.navView, navController);
        }
        binding.navView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "Called onBackPressed");
        moveTaskToBack(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Log.d(TAG, "Called onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.nav_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d(TAG, "Called onOptionsItemSelected");
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        Log.d(TAG, "Called onSupportNavigateUp");
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void adjustDrawerMenuItemVisibility(int[] visibleItems, int[] hiddenItems) {
        Log.d(TAG, "Called adjustDrawerMenuItemVisibility");
        for (int id : visibleItems) {
            MenuItem menuItem = binding.navView.getMenu().findItem(id);
            if (menuItem != null) {
                menuItem.setVisible(true);
            }
        }
        for (int id : hiddenItems) {
            MenuItem menuItem = binding.navView.getMenu().findItem(id);
            if (menuItem != null) {
                menuItem.setVisible(false);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.d(TAG, "Called onNavigationItemSelected");
        Fragment primaryNavigationFragment = navHostFragment.getChildFragmentManager().getPrimaryNavigationFragment();
        switch (item.getItemId()) {
            case R.id.nav_logout:
                Amplify.Auth.signOut(
                        () -> {
                            Log.e("AmplifyAuth", "Sign out success");
                            startActivity(new Intent(MainActivity.this, com.centri.centri_master_app.activities.LoginActivity.class));
                            finish();

                        },
                        error -> {
                            Log.e("AmplifyAuth", error.toString());
                        }
                );
                break;
            case R.id.nav_delete:
                binding.drawerLayout.close();

                if (primaryNavigationFragment instanceof DeviceDetailFragment) {
                    final DeviceModel deviceModel = ((DeviceDetailFragment) primaryNavigationFragment).getDeviceModel();
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.KEY_USER_ID, CentriApp.userId);
                    bundle.putString(Constants.KEY_DEVICE_ID, deviceModel.deviceId);
                    bundle.putString(Constants.KEY_DEVICE_NAME, deviceModel.deviceName);
                    navController.navigate(R.id.deleteDeviceFragment, bundle);
                }
                break;
            case R.id.nav_dashboard:
                binding.drawerLayout.close();
                if (primaryNavigationFragment instanceof DashboardFragment) {
                    return true;
                }
                navController.navigate(R.id.nav_dashboard);
                break;
            case R.id.nav_account:
                binding.drawerLayout.close();
                navController.navigate(R.id.nav_account_activity);
                break;
            case R.id.nav_add:
                binding.drawerLayout.close();
                navController.navigate(R.id.action_nav_dashboard_to_nav_add);
                break;
            case R.id.nav_order:
                binding.drawerLayout.close();
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.KEY_ORDER_ACTIVITY_START_DESTINATION,R.id.orderDetailFragment);
                navController.navigate(R.id.nav_order,bundle);
                break;
            case R.id.nav_request:
                binding.drawerLayout.close();
                bundle = new Bundle();
                bundle.putInt(Constants.KEY_ORDER_ACTIVITY_START_DESTINATION, R.id.requestMonitorFragment);
                navController.navigate(R.id.nav_order,bundle);
                break;
        }
        return false;
    }
}