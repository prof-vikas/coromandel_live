package com.sipl.rfidtagscanner.dto.response;

import com.sipl.rfidtagscanner.dto.dtos.RfidLepIssueDto;

import java.util.List;

public class LoadingAdvisePostApiResponse {

    private boolean error;
    private String message;
    private RfidLepIssueDto rfidLepIssueDto;
    private String rfidLepIssueDtoPage;
    private String status;
    private List<RfidLepIssueDto> rfidLepIssueDtos;

    public LoadingAdvisePostApiResponse(boolean error, String message, RfidLepIssueDto rfidLepIssueDto, String rfidLepIssueDtoPage, String status, List<RfidLepIssueDto> rfidLepIssueDtos) {
        this.error = error;
        this.message = message;
        this.rfidLepIssueDto = rfidLepIssueDto;
        this.rfidLepIssueDtoPage = rfidLepIssueDtoPage;
        this.status = status;
        this.rfidLepIssueDtos = rfidLepIssueDtos;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public RfidLepIssueDto getRfidLepIssueDto() {
        return rfidLepIssueDto;
    }

    public void setRfidLepIssueDto(RfidLepIssueDto rfidLepIssueDto) {
        this.rfidLepIssueDto = rfidLepIssueDto;
    }

    public String getRfidLepIssueDtoPage() {
        return rfidLepIssueDtoPage;
    }

    public void setRfidLepIssueDtoPage(String rfidLepIssueDtoPage) {
        this.rfidLepIssueDtoPage = rfidLepIssueDtoPage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<RfidLepIssueDto> getRfidLepIssueDtos() {
        return rfidLepIssueDtos;
    }

    public void setRfidLepIssueDtos(List<RfidLepIssueDto> rfidLepIssueDtos) {
        this.rfidLepIssueDtos = rfidLepIssueDtos;
    }
}
