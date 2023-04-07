package com.sipl.rfidtagscanner.dto.response;

import com.sipl.rfidtagscanner.dto.dtos.SourceMasterDto;

import java.util.List;

public class SourceLocationApiResponse {
    private List<SourceMasterDto> sourceDtos;
    private String status;
    private String message;
    private boolean error;
    private SourceMasterDto sourceDto;
    private String sourceMasterDtoPage;

    public SourceLocationApiResponse(List<SourceMasterDto> sourceDtos, String status, String message, boolean error, SourceMasterDto sourceDto, String sourceMasterDtoPage) {
        this.sourceDtos = sourceDtos;
        this.status = status;
        this.message = message;
        this.error = error;
        this.sourceDto = sourceDto;
        this.sourceMasterDtoPage = sourceMasterDtoPage;
    }

    public List<SourceMasterDto> getSourceDtos() {
        return sourceDtos;
    }

    public void setSourceDtos(List<SourceMasterDto> sourceDtos) {
        this.sourceDtos = sourceDtos;
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

    public SourceMasterDto getSourceDto() {
        return sourceDto;
    }

    public void setSourceDto(SourceMasterDto sourceDto) {
        this.sourceDto = sourceDto;
    }

    public String getSourceMasterDtoPage() {
        return sourceMasterDtoPage;
    }

    public void setSourceMasterDtoPage(String sourceMasterDtoPage) {
        this.sourceMasterDtoPage = sourceMasterDtoPage;
    }
}
