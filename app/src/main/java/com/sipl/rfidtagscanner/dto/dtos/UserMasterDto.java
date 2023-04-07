package com.sipl.rfidtagscanner.dto.dtos;

import com.sipl.rfidtagscanner.entites.AuditEntity;

public class UserMasterDto {

    private Integer id;
    private String userId;
    private String name;
    private String password;
    private Boolean isActive;
    private Integer rStat;
    private RoleMasterDto role;
    private PlantMasterDto plantMaster;
    private StorageLocationDto storageLocation;
    private AuditEntity auditEntity;

    public UserMasterDto(Integer id) {
        this.id = id;
    }

    public UserMasterDto(Integer id, String userId, String name, String password, Boolean isActive, Integer rStat, RoleMasterDto role, PlantMasterDto plantMaster, StorageLocationDto storageLocation, AuditEntity auditEntity) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.password = password;
        this.isActive = isActive;
        this.rStat = rStat;
        this.role = role;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public RoleMasterDto getRole() {
        return role;
    }

    public void setRole(RoleMasterDto role) {
        this.role = role;
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
