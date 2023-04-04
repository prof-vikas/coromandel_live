package com.sipl.rfidtagscanner.dto.response;

import com.sipl.rfidtagscanner.dto.StorageLocationDto;

import java.util.List;

public class RmgNumberApiResponse {

    private String status;
    private String message;
    private List<StorageLocationDto> storageLocationDtos;
    private String storageLocationDtoPage;
    private StorageLocationDto storageLocationDto;
    private Boolean error;

    public RmgNumberApiResponse(String status, String message, List<StorageLocationDto> storageLocationDtos, String storageLocationDtoPage, StorageLocationDto storageLocationDto, Boolean error) {
        this.status = status;
        this.message = message;
        this.storageLocationDtos = storageLocationDtos;
        this.storageLocationDtoPage = storageLocationDtoPage;
        this.storageLocationDto = storageLocationDto;
        this.error = error;
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

    public List<StorageLocationDto> getStorageLocationDtos() {
        return storageLocationDtos;
    }

    public void setStorageLocationDtos(List<StorageLocationDto> storageLocationDtos) {
        this.storageLocationDtos = storageLocationDtos;
    }

    public String getStorageLocationDtoPage() {
        return storageLocationDtoPage;
    }

    public void setStorageLocationDtoPage(String storageLocationDtoPage) {
        this.storageLocationDtoPage = storageLocationDtoPage;
    }

    public StorageLocationDto getStorageLocationDto() {
        return storageLocationDto;
    }

    public void setStorageLocationDto(StorageLocationDto storageLocationDto) {
        this.storageLocationDto = storageLocationDto;
    }

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }
}
