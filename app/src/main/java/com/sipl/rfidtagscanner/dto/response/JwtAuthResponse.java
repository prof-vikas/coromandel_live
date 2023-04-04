package com.sipl.rfidtagscanner.dto.response;

import com.sipl.rfidtagscanner.dto.dtos.UserMasterDto;

public class JwtAuthResponse {
    private String token;
    private UserMasterDto user;

    public JwtAuthResponse(String token, UserMasterDto user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserMasterDto getUser() {
        return user;
    }

    public void setUser(UserMasterDto user) {
        this.user = user;
    }
}
