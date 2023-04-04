package com.sipl.rfidtagscanner.dto;

import com.sipl.rfidtagscanner.entites.AuditEntity;

public class RoleMasterDto {

    private AuditEntity auditEntity;
    private Integer id;
    private boolean isActive;
    private String name;
    private Integer rstat;

    public RoleMasterDto(AuditEntity auditEntity, Integer id, boolean isActive, String name, Integer rstat) {
        this.auditEntity = auditEntity;
        this.id = id;
        this.isActive = isActive;
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRstat() {
        return rstat;
    }

    public void setRstat(Integer rstat) {
        this.rstat = rstat;
    }
}
