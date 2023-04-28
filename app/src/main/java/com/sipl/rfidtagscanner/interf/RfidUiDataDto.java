package com.sipl.rfidtagscanner.interf;

import com.zebra.rfid.api3.TagData;

public class RfidUiDataDto {
    private boolean isReaderConnected;
    private boolean isConnected;
    private boolean triggerPressed;
    private TagData[] tagData;

    public RfidUiDataDto(boolean isReaderConnected, boolean isConnected, boolean triggerPressed, TagData[] tagData) {
        this.isReaderConnected = isReaderConnected;
        this.isConnected = isConnected;
        this.triggerPressed = triggerPressed;
        this.tagData = tagData;
    }

    public boolean isReaderConnected() {
        return isReaderConnected;
    }

    public void setReaderConnected(boolean readerConnected) {
        isReaderConnected = readerConnected;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public boolean getTriggerPressed() {
        return triggerPressed;
    }

    public void setTriggerPressed(boolean triggerPressed) {
        this.triggerPressed = triggerPressed;
    }

    public TagData[] getTagData() {
        return tagData;
    }

    public void setTagData(TagData[] tagData) {
        this.tagData = tagData;
    }
}
