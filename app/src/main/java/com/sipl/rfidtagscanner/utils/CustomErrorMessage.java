package com.sipl.rfidtagscanner.utils;

import android.util.Log;

public class CustomErrorMessage {
    public static String setErrorMessage(String condition) {
        String failToConnect = "failed to connect to";
        String timeOut = "timeout";
        String resolveHostIssue = "Unable to resolve host";
        if (condition != null) {
            if (condition.contains(failToConnect)) {
                return "Unable to connect. \nPlease check your internet connection";
            } else if (condition.contains(timeOut)) {
                return "Unable to connect due to slow internet speed";
            } else if (condition.contains(resolveHostIssue)) {
                return "Please make sure to turn on your VPN";
            } else {
                return condition;
            }
        }
        return "Something went wrong";
    }
}
