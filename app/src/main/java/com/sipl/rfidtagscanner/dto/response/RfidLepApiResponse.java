package com.sipl.rfidtagscanner.dto.response;

import com.sipl.rfidtagscanner.dto.RfidLepIssueDto;

import java.util.List;

public class RfidLepApiResponse {

    private RfidLepIssueDto rfidLepIssueDto;
    private List<RfidLepIssueDto> rfidLepIssueDtos;
    private String rfidLepIssueDtoPage;
    private String status;
    private String message;
    private boolean error;

    public RfidLepApiResponse(RfidLepIssueDto rfidLepIssueDto, List<RfidLepIssueDto> rfidLepIssueDtos, String rfidLepIssueDtoPage, String status, String message, boolean error) {
        this.rfidLepIssueDto = rfidLepIssueDto;
        this.rfidLepIssueDtos = rfidLepIssueDtos;
        this.rfidLepIssueDtoPage = rfidLepIssueDtoPage;
        this.status = status;
        this.message = message;
        this.error = error;
    }

    public RfidLepIssueDto getRfidLepIssueDto() {
        return rfidLepIssueDto;
    }

    public void setRfidLepIssueDto(RfidLepIssueDto rfidLepIssueDto) {
        this.rfidLepIssueDto = rfidLepIssueDto;
    }

    public List<RfidLepIssueDto> getRfidLepIssueDtos() {
        return rfidLepIssueDtos;
    }

    public void setRfidLepIssueDtos(List<RfidLepIssueDto> rfidLepIssueDtos) {
        this.rfidLepIssueDtos = rfidLepIssueDtos;
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
