package com.sipl.rfidtagscanner.dto.response;

import com.sipl.rfidtagscanner.dto.dtos.UserPermissionsResponseDto;

import java.util.List;

public class GenericeApiResponse {

    private UserPermissionsResponseDto response;
    private List<UserPermissionsResponseDto> responseList;
    private String status;
    private String message;
    private boolean error;

    public GenericeApiResponse(UserPermissionsResponseDto response, List<UserPermissionsResponseDto> responseList, String status, String message, boolean error) {
        this.response = response;
        this.responseList = responseList;
        this.status = status;
        this.message = message;
        this.error = error;
    }

    public UserPermissionsResponseDto getResponse() {
        return response;
    }

    public void setResponse(UserPermissionsResponseDto response) {
        this.response = response;
    }

    public List<UserPermissionsResponseDto> getResponseList() {
        return responseList;
    }

    public void setResponseList(List<UserPermissionsResponseDto> responseList) {
        this.responseList = responseList;
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
