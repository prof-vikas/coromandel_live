package com.sipl.rfidtagscanner.dto.response;

import com.sipl.rfidtagscanner.dto.dtos.UserMasterDto;

public class JwtAuthResponse {
    private String token;
    private String status;
    private String message;
    private boolean error;

    public JwtAuthResponse(String token, String status, String message, boolean error) {
        this.token = token;
        this.status = status;
        this.message = message;
        this.error = error;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }
}
