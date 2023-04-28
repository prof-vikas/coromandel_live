package com.sipl.rfidtagscanner.interf;

public interface MyListener {

    void onTextUpdated(String name, String serialNo, String status);
}
