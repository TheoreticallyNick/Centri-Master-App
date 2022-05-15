/*
 * Copyright (C) Centri Group Inc.
 * Nick Theoret <nick@centriconnect.com>
 * <p>
 * The Device Detail Fragment shows selected device details information.
 *
 * @author TheoreticallyNick
 * @version 2.0
 * @since 2021-03-17
 */
package com.centri.centri_master_app.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.centri.centri_master_app.R;
import com.centri.centri_master_app.activities.MainActivity;
import com.centri.centri_master_app.custom.views.Range;
import com.centri.centri_master_app.data.ApiManager;
import com.centri.centri_master_app.data.models.DeviceModel;
import com.centri.centri_master_app.databinding.FragmentDeviceDetailBinding;
import com.centri.centri_master_app.interfaces.ApiCallback;
import com.centri.centri_master_app.utils.Constants;
import com.centri.centri_master_app.utils.FunctionUtils;
import com.centri.centri_master_app.utils.LogUtil;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

public class DeviceDetailFragment extends Fragment implements View.OnClickListener {
    String TAG = getClass().getSimpleName();
    private FragmentDeviceDetailBinding binding;
    LineData lineData;
    LineDataSet lineDataSet;
    ArrayList<Entry> lineEntries = new ArrayList<Entry>();
    DeviceModel deviceModel;
    SimpleDateFormat datePreFormatter = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat datePostFormatter = new SimpleDateFormat("M/dd");

    public DeviceDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            String modelString = arguments.getString(Constants.KEY_DEVICE_SERIALIZABLE);
            deviceModel = new Gson().fromJson(modelString, DeviceModel.class);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onClick(View view) {
        NavController navController = Navigation.findNavController(view);
        switch (view.getId()) {
            case R.id.tv_back:
                startActivity(new Intent(getContext(), MainActivity.class));
                break;
            case R.id.btn_order_propane:
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.KEY_ORDER_ACTIVITY_START_DESTINATION,R.id.orderDetailFragment);
                navController.navigate(R.id.nav_order,bundle);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final int itemId = item.getItemId();
        if (itemId == R.id.action_refresh) {
            refreshData();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.adjustDrawerMenuItemVisibility(new int[]{R.id.nav_delete}, new int[]{R.id.nav_add});
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_device_detail, container, false);
        updateUI();
        binding.setOnClick(this);
        return binding.getRoot();
    }

    private void updateUI() {
        if (deviceModel != null) {
            binding.tvDeviceName.setText(deviceModel.deviceName);
            binding.tvDeviceLvl.setText(deviceModel.deviceLevel+"%");
            //binding.tvDailyUses.setText(deviceModel.dailyUsage+" gallons per day");
            //binding.tvFillDate.setText(deviceModel.fillDate);
            //binding.tvProviderName.setText(deviceModel.deviceProvider);
            lineEntries = deviceModel.lineEntries;
        }
        configureHalfGauge();
        configureChartUI();
        populateChartData();
    }

    private void configureHalfGauge() {
        final Range red = new Range();
        red.setFrom(0f);
        red.setTo(25f);
        red.setColor(Color.RED);
        Range yellow = new Range();
        yellow.setFrom(25f);
        yellow.setTo(50f);
        yellow.setColor(Color.YELLOW);
        Range green = new Range();
        green.setFrom(50f);
        green.setTo(100f);
        green.setColor(Color.GREEN);
        binding.halfGauge.addRange(red);
        binding.halfGauge.addRange(yellow);
        binding.halfGauge.addRange(green);
        binding.halfGauge.setMinValue(0.0);
        binding.halfGauge.setMaxValue(100.0);
        if (deviceModel != null && FunctionUtils.isValidData(deviceModel.deviceLevel)) {
            try {
                float lvl = Float.parseFloat(deviceModel.deviceLevel);
                binding.halfGauge.setValue(lvl);
            } catch (Exception e) {

            }

        } else {
            binding.halfGauge.setValue(0.0);
        }

    }

    private void configureChartUI() {

        YAxis yAxis = binding.chart.getAxisLeft();
        yAxis.setDrawZeroLine(false);
        yAxis.setDrawGridLines(true);
        yAxis.setDrawLabels(true);
        yAxis.setAxisLineColor(Color.BLACK);
        yAxis.setAxisLineWidth(1.25f);
        yAxis.setAxisMaximum(100);
        yAxis.setAxisMinimum(0);
        yAxis.setGranularity(20);

        XAxis xAxis = binding.chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawLabels(true);
        xAxis.setAxisLineColor(Color.BLACK);
        xAxis.setAxisLineWidth(1.25f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int x = (int)value;
                if (x >= 0 && x < deviceModel.xLabels.size()) {
                    String date = deviceModel.xLabels.get((int)value);
                    return date;
                } else {
                    return "";
                }

            }
        });
        //xAxis.setGranularity(1);
        binding.chart.getLegend().setEnabled(false);
        binding.chart.getAxisRight().setEnabled(false);
        binding.chart.getDescription().setEnabled(false);
        binding.chart.setTouchEnabled(false);
        binding.chart.setDoubleTapToZoomEnabled(false);
    }

    private void populateChartData() {

        lineDataSet = new LineDataSet(lineEntries, "");
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setDrawCircles(false);
        //lineDataSet.setDrawCircles(false);
        lineDataSet.setLineWidth(2);
        lineDataSet.setDrawValues(false);
        lineDataSet.setColor(Color.parseColor("#1284C4"));
        lineData = new LineData(lineDataSet);
        lineData.setHighlightEnabled(false);
        binding.chart.setData(lineData);
        //binding.chart.getXAxis().setLabelCount(5);
        binding.chart.invalidate();
    }

    private void getEntries() {
        lineEntries = new ArrayList<>();
        lineEntries.add(new Entry(1f, 0));
        lineEntries.add(new Entry(2f, 10));
        lineEntries.add(new Entry(4f, 30));
        lineEntries.add(new Entry(6f, 20));
        lineEntries.add(new Entry(7f, 55));
        lineEntries.add(new Entry(9f, 39));
        lineEntries.add(new Entry(10f, 49));
    }

    public DeviceModel getDeviceModel() {
        return deviceModel;
    }

    private void refreshData() {
        if(deviceModel==null) return;
        ApiManager.getInstance().getDeviceStatus(deviceModel.deviceId, new ApiCallback<Map<String, String>>() {
            @Override
            public void onSuccess(Map<String, String> result) {
                if (result != null) {

                    String deviceLevel = result.get("DeviceLevel");
                    if (deviceLevel != null) {
                        deviceModel.deviceLevel = deviceLevel;
                    }

                    String deviceStreet = result.get("DeviceStreet");
                    if (deviceStreet != null) {
                        deviceModel.deviceStreet = deviceStreet;
                    }
                    updateUI();
                }
            }

            @Override
            public void onError(Exception exception) {

            }

            @Override
            public void onStarted() {

            }
        });

        String days = "30";

        ApiManager.getInstance().getDeviceTimeseries(deviceModel.deviceId, days, new ApiCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                LogUtil.e(TAG, "loadData >> found " + result);
                ArrayList<Entry> lineEntries = new ArrayList<>();
                ArrayList<String> xLabel = new ArrayList<String>();

                Iterator keys = result.keys();
                int i = 1;

                while (keys.hasNext()) {

                    String currentDynamicKey = (String) keys.next();
                    String currentDynamicValue = null;

                    try {
                        currentDynamicValue = result.getString(currentDynamicKey);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String dateString = currentDynamicKey.substring(0, 10);
                    Date dateTime = null;

                    try {
                        dateTime = datePreFormatter.parse(dateString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    String formattedTime = datePostFormatter.format(dateTime);
                    xLabel.add(formattedTime);

                    float lvl = Float.parseFloat(currentDynamicValue);
                    lineEntries.add(new Entry(i, lvl));

                    i++;

                }
                deviceModel.lineEntries = lineEntries;
                deviceModel.xLabels = xLabel;
                updateUI();

            }
            @Override
            public void onError(Exception exception) {
                LogUtil.d(TAG, "getDeviceTimeSeriesData >> error " + exception.getMessage());

            }
            @Override
            public void onStarted() {
                LogUtil.d(TAG, "getDeviceTimeSeriesData >> started");

            }
        });

    }


}