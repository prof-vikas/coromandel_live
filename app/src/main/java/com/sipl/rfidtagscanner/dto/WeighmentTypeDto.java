package com.sipl.rfidtagscanner.dto;

import com.sipl.rfidtagscanner.entites.AuditEntity;

public class WeighmentTypeDto {

    private AuditEntity auditEntity;
    private Integer id;
    private Boolean isActive;
    private Integer rstat;
    private String weighmentType;

    public WeighmentTypeDto(AuditEntity auditEntity, Integer id, Boolean isActive, Integer rstat, String weighmentType) {
        this.auditEntity = auditEntity;
        this.id = id;
        this.isActive = isActive;
        this.rstat = rstat;
        this.weighmentType = weighmentType;
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

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Integer getRstat() {
        return rstat;
    }

    public void setRstat(Integer rstat) {
        this.rstat = rstat;
    }

    public String getWeighmentType() {
        return weighmentType;
    }

    public void setWeighmentType(String weighmentType) {
        this.weighmentType = weighmentType;
    }
}
