package com.sipl.rfidtagscanner.model;

public class ResponseModel {

    private String message;
    private String status;
    private boolean error;

    public ResponseModel(String message, String status, boolean error) {
        this.message = message;
        this.status = status;
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }
}
