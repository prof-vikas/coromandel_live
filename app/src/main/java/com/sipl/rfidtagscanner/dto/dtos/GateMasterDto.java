package com.sipl.rfidtagscanner.dto.dtos;

import com.sipl.rfidtagscanner.entites.AuditEntity;

public class GateMasterDto {

    private Integer id;
    private String gateNumber;
    private String gateDescription;
    private Boolean isExternal;
    private Boolean isActive;
    private Integer rStat;
    private PlantMasterDto plantMaster;
    private AuditEntity auditEntity;

    public GateMasterDto(Integer id, String gateNumber, String gateDescription, Boolean isExternal, Boolean isActive, Integer rStat, PlantMasterDto plantMaster, AuditEntity auditEntity) {
        this.id = id;
        this.gateNumber = gateNumber;
        this.gateDescription = gateDescription;
        this.isExternal = isExternal;
        this.isActive = isActive;
        this.rStat = rStat;
        this.plantMaster = plantMaster;
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

    public String getGateDescription() {
        return gateDescription;
    }

    public void setGateDescription(String gateDescription) {
        this.gateDescription = gateDescription;
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

    public AuditEntity getAuditEntity() {
        return auditEntity;
    }

    public void setAuditEntity(AuditEntity auditEntity) {
        this.auditEntity = auditEntity;
    }
}
