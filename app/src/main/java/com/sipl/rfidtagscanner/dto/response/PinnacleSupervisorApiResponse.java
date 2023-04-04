package com.sipl.rfidtagscanner.dto.response;

import com.sipl.rfidtagscanner.dto.dtos.PinnacleLoadingSupervisorDto;

import java.util.List;

public class PinnacleSupervisorApiResponse {

    private PinnacleLoadingSupervisorDto pinnacleLoadingSupervisorDto;
    private List<PinnacleLoadingSupervisorDto> pinnacleLoadingSupervisorDtos;
    private String pinnacleLoadingSupervisorDtoPage;
    private String status;
    private String message;
    private boolean error;

    public PinnacleSupervisorApiResponse(PinnacleLoadingSupervisorDto pinnacleLoadingSupervisorDto, List<PinnacleLoadingSupervisorDto> pinnacleLoadingSupervisorDtos, String pinnacleLoadingSupervisorDtoPage, String status, String message, boolean error) {
        this.pinnacleLoadingSupervisorDto = pinnacleLoadingSupervisorDto;
        this.pinnacleLoadingSupervisorDtos = pinnacleLoadingSupervisorDtos;
        this.pinnacleLoadingSupervisorDtoPage = pinnacleLoadingSupervisorDtoPage;
        this.status = status;
        this.message = message;
        this.error = error;
    }

    public PinnacleLoadingSupervisorDto getPinnacleLoadingSupervisorDto() {
        return pinnacleLoadingSupervisorDto;
    }

    public void setPinnacleLoadingSupervisorDto(PinnacleLoadingSupervisorDto pinnacleLoadingSupervisorDto) {
        this.pinnacleLoadingSupervisorDto = pinnacleLoadingSupervisorDto;
    }

    public List<PinnacleLoadingSupervisorDto> getPinnacleLoadingSupervisorDtos() {
        return pinnacleLoadingSupervisorDtos;
    }

    public void setPinnacleLoadingSupervisorDtos(List<PinnacleLoadingSupervisorDto> pinnacleLoadingSupervisorDtos) {
        this.pinnacleLoadingSupervisorDtos = pinnacleLoadingSupervisorDtos;
    }

    public String getPinnacleLoadingSupervisorDtoPage() {
        return pinnacleLoadingSupervisorDtoPage;
    }

    public void setPinnacleLoadingSupervisorDtoPage(String pinnacleLoadingSupervisorDtoPage) {
        this.pinnacleLoadingSupervisorDtoPage = pinnacleLoadingSupervisorDtoPage;
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
