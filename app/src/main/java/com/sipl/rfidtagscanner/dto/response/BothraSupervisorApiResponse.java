package com.sipl.rfidtagscanner.dto.response;

import com.sipl.rfidtagscanner.dto.BothraLoadingSupervisorDto;
import com.sipl.rfidtagscanner.dto.UserMasterDto;

import java.util.List;

public class BothraSupervisorApiResponse {

    private BothraLoadingSupervisorDto bothraLoadingSupervisorDto;
    private List<BothraLoadingSupervisorDto> bothraLoadingSupervisorDtos;
    private String bothraLoadingSupervisorDtoPage;
    private String status;
    private String message;
    private boolean error;

    public BothraSupervisorApiResponse(BothraLoadingSupervisorDto bothraLoadingSupervisorDto, List<BothraLoadingSupervisorDto> bothraLoadingSupervisorDtos, String bothraLoadingSupervisorDtoPage, String status, String message, boolean error) {
        this.bothraLoadingSupervisorDto = bothraLoadingSupervisorDto;
        this.bothraLoadingSupervisorDtos = bothraLoadingSupervisorDtos;
        this.bothraLoadingSupervisorDtoPage = bothraLoadingSupervisorDtoPage;
        this.status = status;
        this.message = message;
        this.error = error;
    }

    public BothraLoadingSupervisorDto getBothraLoadingSupervisorDto() {
        return bothraLoadingSupervisorDto;
    }

    public void setBothraLoadingSupervisorDto(BothraLoadingSupervisorDto bothraLoadingSupervisorDto) {
        this.bothraLoadingSupervisorDto = bothraLoadingSupervisorDto;
    }

    public List<BothraLoadingSupervisorDto> getBothraLoadingSupervisorDtos() {
        return bothraLoadingSupervisorDtos;
    }

    public void setBothraLoadingSupervisorDtos(List<BothraLoadingSupervisorDto> bothraLoadingSupervisorDtos) {
        this.bothraLoadingSupervisorDtos = bothraLoadingSupervisorDtos;
    }

    public String getBothraLoadingSupervisorDtoPage() {
        return bothraLoadingSupervisorDtoPage;
    }

    public void setBothraLoadingSupervisorDtoPage(String bothraLoadingSupervisorDtoPage) {
        this.bothraLoadingSupervisorDtoPage = bothraLoadingSupervisorDtoPage;
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
