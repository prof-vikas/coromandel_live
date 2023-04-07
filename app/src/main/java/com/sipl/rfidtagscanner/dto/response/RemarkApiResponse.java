package com.sipl.rfidtagscanner.dto.response;

import com.sipl.rfidtagscanner.dto.dtos.RemarksDto;

import java.util.List;

public class RemarkApiResponse {

    private String status;
    private List<RemarksDto> remarksDtos;
    private String remarksDtoPage;
    private RemarksDto remarksDto;
    private String message;
    private Boolean error;

    public RemarkApiResponse(String status, List<RemarksDto> remarksDtos, String remarksDtoPage, RemarksDto remarksDto, String message, Boolean error) {
        this.status = status;
        this.remarksDtos = remarksDtos;
        this.remarksDtoPage = remarksDtoPage;
        this.remarksDto = remarksDto;
        this.message = message;
        this.error = error;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<RemarksDto> getRemarksDtos() {
        return remarksDtos;
    }

    public void setRemarksDtos(List<RemarksDto> remarksDtos) {
        this.remarksDtos = remarksDtos;
    }

    public String getRemarksDtoPage() {
        return remarksDtoPage;
    }

    public void setRemarksDtoPage(String remarksDtoPage) {
        this.remarksDtoPage = remarksDtoPage;
    }

    public RemarksDto getRemarksDto() {
        return remarksDto;
    }

    public void setRemarksDto(RemarksDto remarksDto) {
        this.remarksDto = remarksDto;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }
}
