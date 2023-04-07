package com.sipl.rfidtagscanner.dto.response;

import com.sipl.rfidtagscanner.dto.dtos.StorageLocationDto;

import java.util.List;

public class DestinationLocationApiResponse {

    private String functionalLocationMasterDtoPage;
    private String status;
    private String message;
    private boolean error;
    private List<StorageLocationDto> functionalLocationMasterDtos;
    private StorageLocationDto functionalLocationMasterDto;

    public DestinationLocationApiResponse(String functionalLocationMasterDtoPage, String status, String message, boolean error, List<StorageLocationDto> functionalLocationMasterDtos, StorageLocationDto functionalLocationMasterDto) {
        this.functionalLocationMasterDtoPage = functionalLocationMasterDtoPage;
        this.status = status;
        this.message = message;
        this.error = error;
        this.functionalLocationMasterDtos = functionalLocationMasterDtos;
        this.functionalLocationMasterDto = functionalLocationMasterDto;
    }

    public String getFunctionalLocationMasterDtoPage() {
        return functionalLocationMasterDtoPage;
    }

    public void setFunctionalLocationMasterDtoPage(String functionalLocationMasterDtoPage) {
        this.functionalLocationMasterDtoPage = functionalLocationMasterDtoPage;
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

    public List<StorageLocationDto> getFunctionalLocationMasterDtos() {
        return functionalLocationMasterDtos;
    }

    public void setFunctionalLocationMasterDtos(List<StorageLocationDto> functionalLocationMasterDtos) {
        this.functionalLocationMasterDtos = functionalLocationMasterDtos;
    }

    public StorageLocationDto getFunctionalLocationMasterDto() {
        return functionalLocationMasterDto;
    }

    public void setFunctionalLocationMasterDto(StorageLocationDto functionalLocationMasterDto) {
        this.functionalLocationMasterDto = functionalLocationMasterDto;
    }
}
