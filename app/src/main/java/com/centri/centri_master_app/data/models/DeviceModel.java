/*
 * Copyright (C) Centri Group Inc.
 * Nick Theoret <nick@centriconnect.com>
 * <p>
 * The Add Device Activity prompts the user to add a device.
 * Once the user inputs device name and device ID, they are taken to the Authentication Fragment
 * to enter the 6 digit code sent to their email address for authentication. Once the authentication
 * done, they are taken to success , where they are prompted
 * to return to the Dashboard screen.
 *
 * @author TheoreticallyNick
 * @version 2.0
 * @since 2021-04-17
 */
package com.centri.centri_master_app.data.models;

import androidx.annotation.Keep;

import com.github.mikephil.charting.data.Entry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

@Keep
public class DeviceModel implements Serializable {
    public String deviceId = "";
    public String deviceName = "";
    public String fillDate = "";
    public String deviceLevel = "";
    public String dailyUsage = "";
    public String deviceStreet = "";
    public String deviceProvider = "";
    public String createdDate = "";
    public ArrayList<Entry> lineEntries = new ArrayList<>();
    public ArrayList<String> xLabels = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceModel that = (DeviceModel) o;
        return Objects.equals(deviceId, that.deviceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deviceId);
    }
}
