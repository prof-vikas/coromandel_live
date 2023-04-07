package com.sipl.rfidtagscanner.dto.dtos;

import com.sipl.rfidtagscanner.entites.AuditEntity;

public class BothraLoadingSupervisorDto {
    private Integer id;
    private String name;
    private AuditEntity auditEntity;

    public BothraLoadingSupervisorDto(Integer id, String name, AuditEntity auditEntity) {
        this.id = id;
        this.name = name;
        this.auditEntity = auditEntity;
    }

    public BothraLoadingSupervisorDto(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AuditEntity getAuditEntity() {
        return auditEntity;
    }

    public void setAuditEntity(AuditEntity auditEntity) {
        this.auditEntity = auditEntity;
    }
}
