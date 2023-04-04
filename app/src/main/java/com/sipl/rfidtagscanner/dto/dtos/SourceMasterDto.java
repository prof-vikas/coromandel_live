package com.sipl.rfidtagscanner.dto.dtos;

import com.sipl.rfidtagscanner.entites.AuditEntity;

public class SourceMasterDto {
    private Integer id;
    private String locationName;
    private Boolean isActive;
    private Integer rStat;
    private AuditEntity auditEntity;

    public SourceMasterDto(Integer id, String locationName, Boolean isActive, Integer rStat, AuditEntity auditEntity) {
        this.id = id;
        this.locationName = locationName;
        this.isActive = isActive;
        this.rStat = rStat;
        this.auditEntity = auditEntity;
    }

    public SourceMasterDto(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
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

    public AuditEntity getAuditEntity() {
        return auditEntity;
    }

    public void setAuditEntity(AuditEntity auditEntity) {
        this.auditEntity = auditEntity;
    }
}
