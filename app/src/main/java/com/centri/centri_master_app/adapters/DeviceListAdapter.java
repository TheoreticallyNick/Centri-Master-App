/*
 * Copyright (C) Centri Group Inc.
 * Nick Theoret <nick@centriconnect.com>
 * <p>
 * The Device List Adapter holds all the previously add devices of logged in user.
 *
 * @author TheoreticallyNick
 * @version 2.0
 * @since 2021-03-20
 */
package com.centri.centri_master_app.adapters;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.centri.centri_master_app.R;
import com.centri.centri_master_app.data.ApiManager;
import com.centri.centri_master_app.data.models.DeviceModel;
import com.centri.mypropane.databinding.ItemDeviceBinding;
import com.centri.centri_master_app.interfaces.ApiCallback;
import com.centri.centri_master_app.utils.Constants;
import com.centri.centri_master_app.utils.LogUtil;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.DeviceViewHolder> {
    String TAG = DeviceListAdapter.class.getSimpleName();
    List<DeviceModel> data = new ArrayList<>();
    SimpleDateFormat datePreFormatter = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat datePostFormatter = new SimpleDateFormat("M/dd");

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LogUtil.d(TAG, "Called onCreateViewHolder");
        ItemDeviceBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_device, parent, false);
        return new DeviceViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        LogUtil.d(TAG, "Called onBindViewHolder");
        DeviceModel item = data.get(position);
        LogUtil.d(TAG, "onBind >> " + item.toString());
        holder.binding.tvDeviceName.setText(item.deviceName);
        holder.binding.tvTankLevelValue.setText(item.deviceLevel);
        holder.binding.tvConsumption.setText(item.dailyUsage);

        if (item.deviceStreet.isEmpty()) {
            holder.binding.tvSub.setVisibility(View.GONE);
        } else {
            holder.binding.tvSub.setVisibility(View.VISIBLE);
            holder.binding.tvSub.setText(item.deviceStreet);
        }

        holder.executePendingBind(item.lineEntries);
        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String json = new Gson().toJson(item);
                Bundle bundle = new Bundle();
                bundle.putString(Constants.KEY_DEVICE_SERIALIZABLE, json);
                Navigation.findNavController(view).navigate(R.id.deviceDetailFragment,bundle);
            }
        });

    }

    @Override
    public int getItemCount() {
        LogUtil.d(TAG, "Called getItemCount");
        return data.size();
    }

    public List<DeviceModel> getData() {
        LogUtil.d(TAG, "Called getData");
        return data;
    }

    public void setData(List<DeviceModel> data) {
        LogUtil.d(TAG, "Called setData");
        this.data = data;
    }

    public void addDevice(DeviceModel dm) {
        LogUtil.d(TAG, "Called addDevice");
        LogUtil.d(TAG, "addDevice >> " + data.contains(dm));
        int position = data.size();
        if (!data.contains(dm)) {
            data.add(dm);
            notifyItemInserted(position);
            fetchDetails(dm, position);
        } else {
            fetchDetails(dm, data.indexOf(dm));
        }
    }

    private void fetchDetails(DeviceModel deviceModel, int position) {
        LogUtil.d(TAG, "Called fetchDetails");

        LogUtil.d(TAG, "getDeviceStatus API Call Initiation");
        ApiManager.getInstance().getDeviceStatus(deviceModel.deviceId, new ApiCallback<Map<String, String>>() {
            @Override
            public void onSuccess(Map<String, String> result) {
                LogUtil.d(TAG, "getDeviceStatus API Call Success");
                if (result != null) {

                    String deviceLevel = result.get("DeviceLevel");
                    if (deviceLevel != null) {
                        data.get(position).deviceLevel = deviceLevel;
                    }

                    String deviceStreet = result.get("DeviceStreet");
                    if (deviceStreet != null) {
                        data.get(position).deviceStreet = deviceStreet;
                    }

                    notifyItemChanged(position);
                }
            }

            @Override
            public void onError(Exception exception) {
                LogUtil.d(TAG, "getDeviceStatus API Call Failure");

            }

            @Override
            public void onStarted() {
                LogUtil.d(TAG, "getDeviceStatus API Call onStarted");
            }
        });

        String days = "30";

        LogUtil.d(TAG, "getDeviceTimeseries API Call Initiated");
        ApiManager.getInstance().getDeviceTimeseries(deviceModel.deviceId, days, new ApiCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                LogUtil.d(TAG, "getDeviceTimeseries API Call Success");
                if (result != null) {
                    LogUtil.d(TAG, "getDeviceTimeseries API Call data >> " + result);
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
                    notifyItemChanged(position);
                } else {}

            }
            @Override
            public void onError(Exception exception) {
                LogUtil.d(TAG, "getDeviceTimeseries API Call Failure");
                LogUtil.d(TAG, "getDeviceTimeseries API Call Error Message " + exception.getMessage());

            }
            @Override
            public void onStarted() {
                LogUtil.d(TAG, "getDeviceTimeseries API Call Started");
            }
        });
    }

    static class DeviceViewHolder extends RecyclerView.ViewHolder {
        LineData lineData;
        LineDataSet lineDataSet;
        ArrayList<Entry> lineEntries = new ArrayList<Entry>();
        ItemDeviceBinding binding;

        public DeviceViewHolder(@NonNull ItemDeviceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void executePendingBind(ArrayList<Entry> lineEntries) {
            this.lineEntries = lineEntries;
            configureChartUI();
            populateChartData();
        }

        private void configureChartUI() {

            YAxis yAxis = binding.chart.getAxisLeft();
            yAxis.setDrawZeroLine(false);
            yAxis.setDrawGridLines(false);
            yAxis.setDrawLabels(false);
            yAxis.setAxisLineColor(Color.BLACK);
            yAxis.setAxisLineWidth(1.25f);
            yAxis.setAxisMaximum(100);
            yAxis.setAxisMinimum(0);
            yAxis.setGranularity(20);

            XAxis xAxis = binding.chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setDrawAxisLine(true);
            xAxis.setDrawLabels(false);
            xAxis.setAxisLineColor(Color.BLACK);
            xAxis.setAxisLineWidth(1.25f);
            //xAxis.setValueFormatter(new XAxisValueFormatter());
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
            lineDataSet.setLineWidth(1.5f);
            lineDataSet.setDrawValues(false);
            lineDataSet.setColor(Color.parseColor("#1284C4"));
            lineData = new LineData(lineDataSet);
            lineData.setHighlightEnabled(false);
            binding.chart.setData(lineData);
            //binding.chart.getXAxis().setLabelCount(lineData.getEntryCount());
            binding.chart.invalidate();
        }

        /*
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
         */
    }

}
