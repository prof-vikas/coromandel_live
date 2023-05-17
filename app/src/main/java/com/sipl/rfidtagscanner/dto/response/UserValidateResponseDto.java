package com.sipl.rfidtagscanner.dto.response;

import com.sipl.rfidtagscanner.dto.dtos.UserMasterDto;

import java.util.List;

public class UserValidateResponseDto {

    private UserMasterDto userDto;
    private List<UserMasterDto> userDtos;
    private String userDtoPage;
    private String status;
    private String message;
    private Boolean error;

    public UserValidateResponseDto(UserMasterDto userDto, List<UserMasterDto> userDtos, String userDtoPage, String status, String message, Boolean error) {
        this.userDto = userDto;
        this.userDtos = userDtos;
        this.userDtoPage = userDtoPage;
        this.status = status;
        this.message = message;
        this.error = error;
    }

    public UserMasterDto getUserDto() {
        return userDto;
    }

    public void setUserDto(UserMasterDto userDto) {
        this.userDto = userDto;
    }

    public List<UserMasterDto> getUserDtos() {
        return userDtos;
    }

    public void setUserDtos(List<UserMasterDto> userDtos) {
        this.userDtos = userDtos;
    }

    public String getUserDtoPage() {
        return userDtoPage;
    }

    public void setUserDtoPage(String userDtoPage) {
        this.userDtoPage = userDtoPage;
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

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }
}
