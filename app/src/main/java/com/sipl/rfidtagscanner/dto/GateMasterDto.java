package com.sipl.rfidtagscanner.dto;

import com.sipl.rfidtagscanner.entites.AuditEntity;

public class GateMasterDto {

    private AuditEntity auditEntity;
    private StorageLocationDto functionalLocationMaster;
    private Integer gateNumber;
    private Integer id;
    private Boolean isActive;
    private Integer rstat;

    public GateMasterDto(AuditEntity auditEntity, StorageLocationDto functionalLocationMaster, Integer gateNumber, Integer id, Boolean isActive, Integer rstat) {
        this.auditEntity = auditEntity;
        this.functionalLocationMaster = functionalLocationMaster;
        this.gateNumber = gateNumber;
        this.id = id;
        this.isActive = isActive;
        this.rstat = rstat;
    }

    public AuditEntity getAuditEntity() {
        return auditEntity;
    }

    public void setAuditEntity(AuditEntity auditEntity) {
        this.auditEntity = auditEntity;
    }

    public StorageLocationDto getFunctionalLocationMaster() {
        return functionalLocationMaster;
    }

    public void setFunctionalLocationMaster(StorageLocationDto functionalLocationMaster) {
        this.functionalLocationMaster = functionalLocationMaster;
    }

    public Integer getGateNumber() {
        return gateNumber;
    }

    public void setGateNumber(Integer gateNumber) {
        this.gateNumber = gateNumber;
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
}
