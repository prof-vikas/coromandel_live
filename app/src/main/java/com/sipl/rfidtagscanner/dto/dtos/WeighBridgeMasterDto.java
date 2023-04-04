package com.sipl.rfidtagscanner.dto.dtos;

import com.sipl.rfidtagscanner.entites.AuditEntity;

public class WeighBridgeMasterDto {

    private AuditEntity auditEntity;
    private StorageLocationDto functionalLocationMaster;
    private Integer id;
    private Boolean isActive;
    private Integer rstat;
    private String wbIpAddress;
    private Integer weighbridgeNo;

    public WeighBridgeMasterDto(AuditEntity auditEntity, StorageLocationDto functionalLocationMaster, Integer id, Boolean isActive, Integer rstat, String wbIpAddress, Integer weighbridgeNo) {
        this.auditEntity = auditEntity;
        this.functionalLocationMaster = functionalLocationMaster;
        this.id = id;
        this.isActive = isActive;
        this.rstat = rstat;
        this.wbIpAddress = wbIpAddress;
        this.weighbridgeNo = weighbridgeNo;
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

    public String getWbIpAddress() {
        return wbIpAddress;
    }

    public void setWbIpAddress(String wbIpAddress) {
        this.wbIpAddress = wbIpAddress;
    }

    public Integer getWeighbridgeNo() {
        return weighbridgeNo;
    }

    public void setWeighbridgeNo(Integer weighbridgeNo) {
        this.weighbridgeNo = weighbridgeNo;
    }
}
