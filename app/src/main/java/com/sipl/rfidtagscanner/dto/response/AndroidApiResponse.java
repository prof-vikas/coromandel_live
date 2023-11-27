package com.sipl.rfidtagscanner.dto.response;

public class AndroidApiResponse {

    private Boolean versionChanged;
    private String url;
    private String newVersion;
    private String status;
    private String message;

    public AndroidApiResponse(Boolean versionChanged, String url, String newVersion, String status, String message) {
        this.versionChanged = versionChanged;
        this.url = url;
        this.newVersion = newVersion;
        this.status = status;
        this.message = message;
    }

    public Boolean getVersionChanged() {
        return versionChanged;
    }

    public void setVersionChanged(Boolean versionChanged) {
        this.versionChanged = versionChanged;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNewVersion() {
        return newVersion;
    }

    public void setNewVersion(String newVersion) {
        this.newVersion = newVersion;
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
}
