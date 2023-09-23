package com.sipl.rfidtagscanner.utils;

public class CustomErrorMessage {
    public static String setErrorMessage(String condition) {
        String failToConnect = "failed to connect to";
        String timeOut = "timeout";
        if (condition != null) {
            if (condition.contains(failToConnect)) {
                return "Unable to connect. \nPlease check your internet connection";
            } else if (condition.contains(timeOut)) {
                return "Unable to connect due to slow internet speed";
            } else {
                return condition;
            }
        }
        return "Something went wrong";
    }
}
