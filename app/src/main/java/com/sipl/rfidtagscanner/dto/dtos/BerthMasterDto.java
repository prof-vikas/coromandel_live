package com.sipl.rfidtagscanner.dto.dtos;

import com.sipl.rfidtagscanner.entites.AuditEntity;

public class BerthMasterDto {

    private int berthId;
    private String berthNumber;
    private String berthDescription;
    private String berthType;
    private AuditEntity auditEntity;
    private Boolean isActive;
    private int rStat;

    public BerthMasterDto(int berthId, String berthNumber, String berthDescription, String berthType, AuditEntity auditEntity, Boolean isActive, int rStat) {
        this.berthId = berthId;
        this.berthNumber = berthNumber;
        this.berthDescription = berthDescription;
        this.berthType = berthType;
        this.auditEntity = auditEntity;
        this.isActive = isActive;
        this.rStat = rStat;
    }

    public int getBerthId() {
        return berthId;
    }

    public void setBerthId(int berthId) {
        this.berthId = berthId;
    }

    public String getBerthNumber() {
        return berthNumber;
    }

    public void setBerthNumber(String berthNumber) {
        this.berthNumber = berthNumber;
    }

    public String getBerthDescription() {
        return berthDescription;
    }

    public void setBerthDescription(String berthDescription) {
        this.berthDescription = berthDescription;
    }

    public String getBerthType() {
        return berthType;
    }

    public void setBerthType(String berthType) {
        this.berthType = berthType;
    }

    public AuditEntity getAuditEntity() {
        return auditEntity;
    }

    public void setAuditEntity(AuditEntity auditEntity) {
        this.auditEntity = auditEntity;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public int getrStat() {
        return rStat;
    }

    public void setrStat(int rStat) {
        this.rStat = rStat;
    }
}
