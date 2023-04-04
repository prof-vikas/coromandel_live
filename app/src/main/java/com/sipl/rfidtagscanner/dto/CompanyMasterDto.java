package com.sipl.rfidtagscanner.dto;

import com.sipl.rfidtagscanner.entites.AuditEntity;

public class CompanyMasterDto {

    private AuditEntity auditEntity;
    private String companyName;
    private Integer id;
    private boolean isActive;
    private Integer rstat;

    public CompanyMasterDto(AuditEntity auditEntity, String companyName, Integer id, boolean isActive, Integer rstat) {
        this.auditEntity = auditEntity;
        this.companyName = companyName;
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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
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

    public Integer getRstat() {
        return rstat;
    }

    public void setRstat(Integer rstat) {
        this.rstat = rstat;
    }
}
