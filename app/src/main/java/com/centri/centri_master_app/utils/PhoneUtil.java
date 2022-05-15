package com.centri.centri_master_app.utils;

import android.telephony.PhoneNumberUtils;

public class PhoneUtil {

    public static boolean isValidPhoneNumber(String phoneNo) {
            //validate phone numbers of format "1234567890"
        if (phoneNo.matches("\\d{10}")) return true;
            //validating phone number with -, . or spaces
        else if(phoneNo.matches("\\d{3}[-.\\s]\\d{3}[-.\\s]\\d{4}")) return true;
            //validating phone number with extension length from 3 to 5
        else if(phoneNo.matches("\\d{3}-\\d{3}-\\d{4}\\s(x|(ext))\\d{3,5}")) return true;
            //validating phone number where area code is in braces ()
        else if(phoneNo.matches("\\(\\d{3}\\)-\\d{3}-\\d{4}")) return true;
            //return false if nothing matches the input
        else if(phoneNo.matches("\\+\\d{11}")) return true;
            //return false if nothing matches the input
        else if(phoneNo.matches("\\d{11}")) return true;
            //return false if nothing matches the input
        else if(phoneNo.matches("\\d{1}-\\d{3}[-.\\s]\\d{3}[-.\\s]\\d{4}")) return true;
            //return false if nothing matches the input
        else if(phoneNo.matches("\\+\\d{1}-\\d{3}[-.\\s]\\d{3}[-.\\s]\\d{4}")) return true;
            //return false if nothing matches the input
        else if(phoneNo.matches("\\+\\d{10}")) return true;
            //return false if nothing matches the input
        else return false;

    }

    public static String formatPhoneNumber(String phoneNo) {
        String formatPhone = PhoneNumberUtils.formatNumberToE164(phoneNo, "US");
        return formatPhone;
    }
}
