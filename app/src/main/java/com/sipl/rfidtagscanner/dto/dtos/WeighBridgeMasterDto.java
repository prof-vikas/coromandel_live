package com.sipl.rfidtagscanner.dto.dtos;

import com.sipl.rfidtagscanner.entites.AuditEntity;

public class WeighBridgeMasterDto {

    private Integer id;
    private String wbIpAddress;
    private String weighbridgeName;
    private String weighbrideDescription;
    private Boolean isExternal;
    private Boolean isActive;
    private Integer rStat;
    private PlantMasterDto plantMaster;
    private StorageLocationDto storageLocation;
    private AuditEntity auditEntity;

    public WeighBridgeMasterDto(Integer id, String wbIpAddress, String weighbridgeName, String weighbrideDescription, Boolean isExternal, Boolean isActive, Integer rStat, PlantMasterDto plantMaster, StorageLocationDto storageLocation, AuditEntity auditEntity) {
        this.id = id;
        this.wbIpAddress = wbIpAddress;
        this.weighbridgeName = weighbridgeName;
        this.weighbrideDescription = weighbrideDescription;
        this.isExternal = isExternal;
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

    public String getWbIpAddress() {
        return wbIpAddress;
    }

    public void setWbIpAddress(String wbIpAddress) {
        this.wbIpAddress = wbIpAddress;
    }

    public String getWeighbridgeName() {
        return weighbridgeName;
    }

    public void setWeighbridgeName(String weighbridgeName) {
        this.weighbridgeName = weighbridgeName;
    }

    public String getWeighbrideDescription() {
        return weighbrideDescription;
    }

    public void setWeighbrideDescription(String weighbrideDescription) {
        this.weighbrideDescription = weighbrideDescription;
    }

    public Boolean getExternal() {
        return isExternal;
    }

    public void setExternal(Boolean external) {
        isExternal = external;
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
