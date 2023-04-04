package com.sipl.rfidtagscanner.entites;


public class AuditEntity {

    private String createdBy;
    private String createdTime;
    private String modifiedBy;
    private String modifiedTime;

    public AuditEntity(String createdBy, String createdTime, String modifiedBy, String modifiedTime) {
        this.createdBy = createdBy;
        this.createdTime = createdTime;
        this.modifiedBy = modifiedBy;
        this.modifiedTime = modifiedTime;
    }


    public AuditEntity(String createdBy, String createdTime) {
        this.createdBy = createdBy;
        this.createdTime = createdTime;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public String getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(String modifiedTime) {
        this.modifiedTime = modifiedTime;
    }
}
