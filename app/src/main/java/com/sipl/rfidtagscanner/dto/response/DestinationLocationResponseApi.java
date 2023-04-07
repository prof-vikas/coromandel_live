package com.sipl.rfidtagscanner.dto.response;

import com.sipl.rfidtagscanner.dto.dtos.StorageLocationDto;

import java.util.List;

public class DestinationLocationResponseApi {

    private String storageLocationDtoPage;
    private String status;
    private String message;
    private boolean error;
    private List<StorageLocationDto> storageLocationDtos;
    private StorageLocationDto storageLocationDto;

    public DestinationLocationResponseApi(String storageLocationDtoPage, String status, String message, boolean error, List<StorageLocationDto> storageLocationDtos, StorageLocationDto storageLocationDto) {
        this.storageLocationDtoPage = storageLocationDtoPage;
        this.status = status;
        this.message = message;
        this.error = error;
        this.storageLocationDtos = storageLocationDtos;
        this.storageLocationDto = storageLocationDto;
    }

    public String getStorageLocationDtoPage() {
        return storageLocationDtoPage;
    }

    public void setStorageLocationDtoPage(String storageLocationDtoPage) {
        this.storageLocationDtoPage = storageLocationDtoPage;
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

    public List<StorageLocationDto> getStorageLocationDtos() {
        return storageLocationDtos;
    }

    public void setStorageLocationDtos(List<StorageLocationDto> storageLocationDtos) {
        this.storageLocationDtos = storageLocationDtos;
    }

    public StorageLocationDto getStorageLocationDto() {
        return storageLocationDto;
    }

    public void setStorageLocationDto(StorageLocationDto storageLocationDto) {
        this.storageLocationDto = storageLocationDto;
    }
}
