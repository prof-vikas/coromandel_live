package com.sipl.rfidtagscanner.dto.request;

import com.sipl.rfidtagscanner.dto.SourceMasterDto;
import com.sipl.rfidtagscanner.dto.UserMasterDto;

public class AuditDtrRequestDto {

    private String filePath;
    private Integer id;
    private boolean isActive;
    private Integer recordsCount;
    private Integer rstat;
    private SourceMasterDto sourceMaster;
    private String status;
    private String uploadedBy;
    private String uploadedTime;
    private UserMasterDto user;

    public AuditDtrRequestDto(String filePath, Integer id, boolean isActive, Integer recordsCount, Integer rstat, SourceMasterDto sourceMaster, String status, String uploadedBy, String uploadedTime, UserMasterDto user) {
        this.filePath = filePath;
        this.id = id;
        this.isActive = isActive;
        this.recordsCount = recordsCount;
        this.rstat = rstat;
        this.sourceMaster = sourceMaster;
        this.status = status;
        this.uploadedBy = uploadedBy;
        this.uploadedTime = uploadedTime;
        this.user = user;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Integer getRecordsCount() {
        return recordsCount;
    }

    public void setRecordsCount(Integer recordsCount) {
        this.recordsCount = recordsCount;
    }

    public Integer getRstat() {
        return rstat;
    }

    public void setRstat(Integer rstat) {
        this.rstat = rstat;
    }

    public SourceMasterDto getSourceMaster() {
        return sourceMaster;
    }

    public void setSourceMaster(SourceMasterDto sourceMaster) {
        this.sourceMaster = sourceMaster;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public String getUploadedTime() {
        return uploadedTime;
    }

    public void setUploadedTime(String uploadedTime) {
        this.uploadedTime = uploadedTime;
    }

    public UserMasterDto getUser() {
        return user;
    }

    public void setUser(UserMasterDto user) {
        this.user = user;
    }
}
