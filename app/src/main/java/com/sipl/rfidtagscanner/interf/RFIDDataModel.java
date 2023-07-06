package com.sipl.rfidtagscanner.interf;

import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


public class RFIDDataModel {

    private static final MutableLiveData<RfidUiDataDto> RfidUiDataDtoMutableLiveData = new MutableLiveData<>();
    private static RFIDDataModel rfidDataModel;


    public static synchronized RFIDDataModel getInstance() {
        if (rfidDataModel == null) {
            rfidDataModel = new RFIDDataModel();
        }
        return rfidDataModel;
    }

    public LiveData<RfidUiDataDto> getRFIDStatus() {
        return RfidUiDataDtoMutableLiveData;
    }

    public void setRFIDStatus(RfidUiDataDto rfidUiDataDto) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            RfidUiDataDtoMutableLiveData.setValue(rfidUiDataDto);
        } else {
            RfidUiDataDtoMutableLiveData.postValue(rfidUiDataDto);
        }
    }

}
