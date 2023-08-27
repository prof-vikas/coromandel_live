package com.sipl.rfidtagscanner.dto.response;

import com.sipl.rfidtagscanner.dto.dtos.DailyTransportReportModuleDto;

import java.util.List;

public class DailyTransportReportModuleApiResponse {

    private DailyTransportReportModuleDto dailyTransportReportModuleDto;
    private List<DailyTransportReportModuleDto> dailyTransportReportModuleDtos;
    private String dtrModuleDtoPage;
    private String status;
    private String message;
    private boolean error;

    public DailyTransportReportModuleApiResponse(DailyTransportReportModuleDto dailyTransportReportModuleDto, List<DailyTransportReportModuleDto> dailyTransportReportModuleDtos, String dtrModuleDtoPage, String status, String message, boolean error) {
        this.dailyTransportReportModuleDto = dailyTransportReportModuleDto;
        this.dailyTransportReportModuleDtos = dailyTransportReportModuleDtos;
        this.dtrModuleDtoPage = dtrModuleDtoPage;
        this.status = status;
        this.message = message;
        this.error = error;
    }

    public DailyTransportReportModuleDto getDailyTransportReportModuleDto() {
        return dailyTransportReportModuleDto;
    }

    public void setDailyTransportReportModuleDto(DailyTransportReportModuleDto dailyTransportReportModuleDto) {
        this.dailyTransportReportModuleDto = dailyTransportReportModuleDto;
    }

    public List<DailyTransportReportModuleDto> getDailyTransportReportModuleDtos() {
        return dailyTransportReportModuleDtos;
    }

    public void setDailyTransportReportModuleDtos(List<DailyTransportReportModuleDto> dailyTransportReportModuleDtos) {
        this.dailyTransportReportModuleDtos = dailyTransportReportModuleDtos;
    }

    public String getDtrModuleDtoPage() {
        return dtrModuleDtoPage;
    }

    public void setDtrModuleDtoPage(String dtrModuleDtoPage) {
        this.dtrModuleDtoPage = dtrModuleDtoPage;
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
