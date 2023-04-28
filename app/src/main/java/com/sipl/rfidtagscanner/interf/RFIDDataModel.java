package com.sipl.rfidtagscanner.interf;

import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;



public class RFIDDataModel {

    private static final String TAG = RFIDDataModel.class.getSimpleName();
    private static final MutableLiveData<RfidUiDataDto> RfidUiDataDtoMutableLiveData = new MutableLiveData<>();
    private static RFIDDataModel rfidDataModel;


    public static synchronized RFIDDataModel getInstance() {
        Log.d(TAG, "getInstance() called");
        if (rfidDataModel == null) {
            rfidDataModel = new RFIDDataModel();
        }
        return rfidDataModel;
    }

    public LiveData<RfidUiDataDto> getRFIDStatus() {
        Log.d(TAG, "getRFIDStatus() called");
        return RfidUiDataDtoMutableLiveData;
    }


    public void setRFIDStatus(RfidUiDataDto rfidUiDataDto) {
        Log.d(TAG, "setRFIDStatus() called");
        if (Looper.myLooper() == Looper.getMainLooper()) {
            RfidUiDataDtoMutableLiveData.setValue(rfidUiDataDto);
        } else {
            RfidUiDataDtoMutableLiveData.postValue(rfidUiDataDto);
        }
    }

}
