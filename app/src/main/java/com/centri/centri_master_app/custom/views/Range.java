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
 * @since 2020-08-12
 */
package com.centri.centri_master_app.custom.views;

public class Range {

    private double from;
    private double to;
    private int color;

    public double getFrom() {
        return from;
    }

    public void setFrom(double from) {
        this.from = from;
    }

    public double getTo() {
        return to;
    }

    public void setTo(double to) {
        this.to = to;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
