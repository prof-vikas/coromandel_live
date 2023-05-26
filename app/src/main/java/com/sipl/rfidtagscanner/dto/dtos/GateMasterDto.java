package com.sipl.rfidtagscanner.dto.dtos;

import com.sipl.rfidtagscanner.entites.AuditEntity;

public class GateMasterDto {

    private Integer id;
    private String gateNumber;
    private Boolean isActive;
    private Integer rStat;
    private PlantMasterDto plantMaster;
    private StorageLocationDto storageLocation;
    private AuditEntity auditEntity;

    public GateMasterDto(Integer id, String gateNumber, Boolean isActive, Integer rStat, PlantMasterDto plantMaster, StorageLocationDto storageLocation, AuditEntity auditEntity) {
        this.id = id;
        this.gateNumber = gateNumber;
        this.isActive = isActive;
        this.rStat = rStat;
        this.plantMaster = plantMaster;
        this.storageLocation = storageLocation;
        this.auditEntity = auditEntity;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGateNumber() {
        return gateNumber;
    }

    public void setGateNumber(String gateNumber) {
        this.gateNumber = gateNumber;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Integer getrStat() {
        return rStat;
    }

    public void setrStat(Integer rStat) {
        this.rStat = rStat;
    }

    public PlantMasterDto getPlantMaster() {
        return plantMaster;
    }

    public void setPlantMaster(PlantMasterDto plantMaster) {
        this.plantMaster = plantMaster;
    }

    public StorageLocationDto getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(StorageLocationDto storageLocation) {
        this.storageLocation = storageLocation;
    }

    public AuditEntity getAuditEntity() {
        return auditEntity;
    }

    public void setAuditEntity(AuditEntity auditEntity) {
        this.auditEntity = auditEntity;
    }
}
