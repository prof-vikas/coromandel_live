package com.sipl.rfidtagscanner.dto.dtos;

import com.sipl.rfidtagscanner.entites.AuditEntity;

public class LepNumberMasterDto {

    private AuditEntity auditEntity;
    private Integer id;
    private boolean isActive;
    private String lepNumber;
    private Integer rstat;

    public LepNumberMasterDto(AuditEntity auditEntity, Integer id, boolean isActive, String lepNumber, Integer rstat) {
        this.auditEntity = auditEntity;
        this.id = id;
        this.isActive = isActive;
        this.lepNumber = lepNumber;
        this.rstat = rstat;
    }

    public AuditEntity getAuditEntity() {
        return auditEntity;
    }

    public void setAuditEntity(AuditEntity auditEntity) {
        this.auditEntity = auditEntity;
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

    public String getLepNumber() {
        return lepNumber;
    }

    public void setLepNumber(String lepNumber) {
        this.lepNumber = lepNumber;
    }

    public Integer getRstat() {
        return rstat;
    }

    public void setRstat(Integer rstat) {
        this.rstat = rstat;
    }
}
