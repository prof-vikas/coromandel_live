package com.sipl.rfidtagscanner.dto.dtos;

import com.sipl.rfidtagscanner.entites.AuditEntity;

public class RfidMasterDto {
    private Integer id;
    private String rfidNumber;
    private Boolean isActive;
    private Boolean isDeleted;
    private AuditEntity auditEntity;

    public RfidMasterDto(Integer id, String rfidNumber, Boolean isActive, Boolean isDeleted, AuditEntity auditEntity) {
        this.id = id;
        this.rfidNumber = rfidNumber;
        this.isActive = isActive;
        this.isDeleted = isDeleted;
        this.auditEntity = auditEntity;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRfidNumber() {
        return rfidNumber;
    }

    public void setRfidNumber(String rfidNumber) {
        this.rfidNumber = rfidNumber;
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

    public AuditEntity getAuditEntity() {
        return auditEntity;
    }

    public void setAuditEntity(AuditEntity auditEntity) {
        this.auditEntity = auditEntity;
    }
}
