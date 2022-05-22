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
 * @since 2021-02-05
 */
package com.centri.centri_master_app.data.models;

import androidx.annotation.Keep;

import java.io.Serializable;

@Keep
public class User implements Serializable {
    public String UserLast, UserFirst, UserEmail, UserPass, UserPassConf, UserPhone, UserCode, UserProvider, UserStreet, UserCity, UserState, UserZip;

    public User() {
        this.UserLast = "";
        this.UserFirst = "";
        this.UserEmail = "";
        this.UserPass = "";
        this.UserPassConf = "";
        this.UserPhone = "";
        this.UserCode = "";
        this.UserStreet = "";
        this.UserCity = "";
        this.UserState = "";
        this.UserZip = "";
    }
}