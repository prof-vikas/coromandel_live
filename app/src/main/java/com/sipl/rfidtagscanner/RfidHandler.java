package com.sipl.rfidtagscanner;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.sipl.rfidtagscanner.fragments.ScanFragment;
import com.sipl.rfidtagscanner.interf.RFIDDataModel;
import com.sipl.rfidtagscanner.interf.RfidUiDataDto;
import com.zebra.rfid.api3.ACCESS_OPERATION_CODE;
import com.zebra.rfid.api3.ACCESS_OPERATION_STATUS;
import com.zebra.rfid.api3.Antennas;
import com.zebra.rfid.api3.ENUM_TRANSPORT;
import com.zebra.rfid.api3.ENUM_TRIGGER_MODE;
import com.zebra.rfid.api3.HANDHELD_TRIGGER_EVENT_TYPE;
import com.zebra.rfid.api3.INVENTORY_STATE;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.OperationFailureException;
import com.zebra.rfid.api3.RFIDReader;
import com.zebra.rfid.api3.ReaderDevice;
import com.zebra.rfid.api3.Readers;
import com.zebra.rfid.api3.RfidEventsListener;
import com.zebra.rfid.api3.RfidReadEvents;
import com.zebra.rfid.api3.RfidStatusEvents;
import com.zebra.rfid.api3.SESSION;
import com.zebra.rfid.api3.SL_FLAG;
import com.zebra.rfid.api3.START_TRIGGER_TYPE;
import com.zebra.rfid.api3.STATUS_EVENT_TYPE;
import com.zebra.rfid.api3.STOP_TRIGGER_TYPE;
import com.zebra.rfid.api3.TagData;
import com.zebra.rfid.api3.TriggerInfo;

import java.util.ArrayList;
import java.util.concurrent.Executors;

public class RfidHandler implements Readers.RFIDReaderEventHandler {

    private final static String TAG = "RFID_SAMPLE";
    private static RFIDReader reader;
    private final Context context;
    private final RFIDDataModel rfidDataModel;
    private Readers readers;
    private EventHandler eventHandler;
    private boolean isReaderConnected;
    private boolean isConnected;
    private boolean triggerPressed;
    private TagData[] tagData;
    private ScanFragment scanFragment;

    public RfidHandler(Context context) {
        this.context = context;
        this.rfidDataModel = RFIDDataModel.getInstance();
    }

    public void InitSDK(ScanFragment connectFragment) {
        this.scanFragment = connectFragment;
        if (isReaderConnected()) {
            new ConnectionTask();
        }
    }

    private synchronized void updateAvailableReaders() {
        Log.d(TAG, "updateAvailableReaders");
        Readers.attach(this);
        try {
            ReaderDevice readerDevice = getAvailableReaders();
            if (readerDevice != null) {
                reader = readerDevice.getRFIDReader();
//                scanFragment.onTextUpdated(readerDevice.getName(), readerDevice.getSerialNumber(), "Connected");
                Log.i(TAG, "updateAvailableReaders: " + readerDevice.getSerialNumber() + readerDevice.getSerialNumber());
            }
        } catch (Exception e) {
            Log.d(TAG, "Exception at updateAvailableReaders" + e.getMessage());
        }
    }

    public ReaderDevice getAvailableReaders() {
        try {
            readers = new Readers(context, ENUM_TRANSPORT.SERVICE_USB);
            ArrayList<ReaderDevice> readersArrayList = readers.GetAvailableRFIDReaderList();
            if (readersArrayList.size() > 0) {
                return readersArrayList.get(0);
            }
            return null;
        } catch (InvalidUsageException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void RFIDReaderAppeared(ReaderDevice readerDevice) {
        Log.d(TAG, "RFIDReaderAppeared " + readerDevice.getName());
        isReaderConnected();
        new ConnectionTask();
        updateStatus();
    }

    @Override
    public void RFIDReaderDisappeared(ReaderDevice readerDevice) {
        Log.d(TAG, "RFIDReaderDisappeared " + readerDevice.getName());
        isReaderConnected();
        isConnected = false;
        if (readerDevice.getName().equals(reader.getHostName())) disconnect();
        updateStatus();
//        scanFragment.onTextUpdated("---", "---", "Disconnected");
    }

    private synchronized String connectDevice() {
        if (reader != null) {
            Log.d(TAG, "connect " + reader.getHostName());
            try {
                if (!reader.isConnected()) {
                    reader.connect();
                    makeConfiguration();
                    return "Connected";
                }
                isConnected = true;
                updateStatus();
            } catch (OperationFailureException e) {
                e.printStackTrace();
                Log.d(TAG, "OperationFailureException " + e.getVendorMessage());
                String des = e.getResults().toString();
                return "Connection failed" + e.getVendorMessage() + " " + des;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    private void makeConfiguration() {
        Log.i(TAG, "makeConfiguration: " + reader.isConnected());
        if (reader.isConnected()) {
            TriggerInfo triggerInfo = new TriggerInfo();
            triggerInfo.StartTrigger.setTriggerType(START_TRIGGER_TYPE.START_TRIGGER_TYPE_IMMEDIATE);
            triggerInfo.StopTrigger.setTriggerType(STOP_TRIGGER_TYPE.STOP_TRIGGER_TYPE_IMMEDIATE);
            try {
                if (eventHandler == null) eventHandler = new EventHandler();
                reader.Events.addEventsListener(eventHandler);
                reader.Events.setHandheldEvent(true);
                reader.Events.setTagReadEvent(true);
                reader.Events.setAttachTagDataWithReadEvent(false);
                reader.Config.setTriggerMode(ENUM_TRIGGER_MODE.RFID_MODE, true);
                reader.Config.setStartTrigger(triggerInfo.StartTrigger);
                reader.Config.setStopTrigger(triggerInfo.StopTrigger);
                Antennas.AntennaRfConfig config = reader.Config.Antennas.getAntennaRfConfig(1);
                config.setTransmitPowerIndex(getMaxPower());
                config.setrfModeTableIndex(0);
                config.setTari(0);
                reader.Config.Antennas.setAntennaRfConfig(1, config);
                Antennas.SingulationControl s1_singulationControl = reader.Config.Antennas.getSingulationControl(1);
                s1_singulationControl.setSession(SESSION.SESSION_S0);
                s1_singulationControl.Action.setInventoryState(INVENTORY_STATE.INVENTORY_STATE_A);
                s1_singulationControl.Action.setSLFlag(SL_FLAG.SL_ALL);
                reader.Config.Antennas.setSingulationControl(1, s1_singulationControl);
                reader.Actions.PreFilters.deleteAll();
            } catch (InvalidUsageException | OperationFailureException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void dispose() {
        try {
            if (readers != null) {
                reader = null;
                readers.Dispose();
                readers = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void disconnect() {
        Log.d(TAG, "disconnect " + reader);
        try {
            if (reader != null) {
                reader.Events.removeEventsListener(eventHandler);
                reader.disconnect();
//                scanFragment.onTextUpdated("---", "---", "Disconnected");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void performInventory() {
        if (!isConnected && !isReaderConnected) return;
        try {
            reader.Actions.Inventory.perform();
        } catch (InvalidUsageException | OperationFailureException e) {
            e.printStackTrace();
        }
    }

    public synchronized void stopInventory() {
        if (!isConnected && !isReaderConnected) return;
        try {
            if (isConnected) reader.Actions.Inventory.stop();
        } catch (InvalidUsageException | OperationFailureException e) {
            e.printStackTrace();
        }
    }

    public void updateStatus() {
        RfidUiDataDto rfidUiDataDto = new RfidUiDataDto(isReaderConnected, isConnected, triggerPressed, tagData);
        rfidDataModel.setRFIDStatus(rfidUiDataDto);
    }

    public boolean isReaderConnected() {
        isReaderConnected = getAvailableReaders() != null;
        Log.d(TAG, (isReaderConnected ? "Connected" : "Not Connected"));
        updateStatus();
        return isReaderConnected;
    }

    public void onResume() {
        Log.d(TAG, "onResume");
        connectDevice();
    }

    public void onPause() {
        Log.d(TAG, "onPause");
        disconnect();
    }

    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        dispose();
    }

    public int getMaxPower() {
        try {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            if (sharedPreferences.contains("rfid_power_level")) {
                String value = sharedPreferences.getString("rfid_power_level", null);
                return Integer.parseInt(value);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private class ConnectionTask {
        public ConnectionTask() {
            Executors.newSingleThreadExecutor().execute(() -> {
                Log.d(TAG, "ConnectionTask Sample");
                updateAvailableReaders();
                if (reader != null) {
                    String str = connectDevice();
                    Log.i(TAG, "ConnectionTask -> " + str);
                    return;
                }
                Log.e(TAG, "Failed to find or connect reader");
            });
        }
    }

    public class EventHandler implements RfidEventsListener {
        public void eventReadNotify(RfidReadEvents e) {
            TagData[] scannedTags = reader.Actions.getReadTags(100);
            if (scannedTags != null && scannedTags.length > 0) {
                for (TagData tag : scannedTags) {
                    if (tag.getOpCode() == ACCESS_OPERATION_CODE.ACCESS_OPERATION_READ && tag.getOpStatus() == ACCESS_OPERATION_STATUS.ACCESS_SUCCESS) {
                        if (tag.getMemoryBankData().length() > 0) {
                            Log.d(TAG, " Mem Bank Data " + tag.getMemoryBankData());
                        }
                    }
                    if (tag.isContainsLocationInfo()) {
                        short dist = tag.LocationInfo.getRelativeDistance();
                        Log.d(TAG, "Tag relative distance " + dist);
                    }
                }
                tagData = scannedTags;
                updateStatus();
            }
        }

        //scanFragment.handleTriggerPress(true)
        public void eventStatusNotify(RfidStatusEvents rfidStatusEvents) {
            if (rfidStatusEvents.StatusEventData.getStatusEventType() == STATUS_EVENT_TYPE.HANDHELD_TRIGGER_EVENT) {
                if (rfidStatusEvents.StatusEventData.HandheldTriggerEventData.getHandheldEvent() == HANDHELD_TRIGGER_EVENT_TYPE.HANDHELD_TRIGGER_PRESSED) {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        triggerPressed = true;
                        scanFragment.updateButtonStatus(true);
                    });
                } else if (rfidStatusEvents.StatusEventData.HandheldTriggerEventData.getHandheldEvent() == HANDHELD_TRIGGER_EVENT_TYPE.HANDHELD_TRIGGER_RELEASED) {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        triggerPressed = false;
                        tagData = null;
                        scanFragment.updateButtonStatus(false);
                    });
                }
                updateStatus();
            }
        }
    }
}
