package com.sipl.rfidtagscanner.dto.dtos;

import com.sipl.rfidtagscanner.entites.AuditEntity;

public class UserMasterDto {

    private Integer id;
    private String userId;
    private String name;
    private String password;
    private String loginTime;
    private String logoutTime;
    private Boolean isActive;
    private Boolean isDeleted;
    private Integer rStat;
    private RoleMasterDto role;
    private PlantMasterDto plantMaster;
    private StorageLocationDto storageLocation;
    private AuditEntity auditEntity;

    public UserMasterDto(Integer id) {
        this.id = id;
    }

    public UserMasterDto(Integer id, String userId, String name, String password, String loginTime, String logoutTime, Boolean isActive, Boolean isDeleted, Integer rStat, RoleMasterDto role, PlantMasterDto plantMaster, StorageLocationDto storageLocation, AuditEntity auditEntity) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.password = password;
        this.loginTime = loginTime;
        this.logoutTime = logoutTime;
        this.isActive = isActive;
        this.isDeleted = isDeleted;
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

    public String getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(String loginTime) {
        this.loginTime = loginTime;
    }

    public String getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(String logoutTime) {
        this.logoutTime = logoutTime;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
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
