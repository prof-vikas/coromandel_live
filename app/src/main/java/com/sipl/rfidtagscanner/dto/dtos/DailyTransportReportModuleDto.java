package com.sipl.rfidtagscanner.dto.dtos;

import com.sipl.rfidtagscanner.dto.request.AuditDtrRequestDto;
import com.sipl.rfidtagscanner.entites.AuditEntity;

public class DailyTransportReportModuleDto {

    private Integer id;
    private SapGrnDetailsDto sapGrnDetailsEntity;
    private VehicleMasterDto vehicleMaster;
    private Boolean isActive;
    private Integer rStat;
    private Boolean isAssigned;
    private Boolean isProcessed;
    private AuditEntity auditEntity;
    private String remark;

    public DailyTransportReportModuleDto(Integer id, SapGrnDetailsDto sapGrnDetailsEntity, VehicleMasterDto vehicleMaster, Boolean isActive, Integer rStat, Boolean isAssigned, Boolean isProcessed, AuditEntity auditEntity, String remark) {
        this.id = id;
        this.sapGrnDetailsEntity = sapGrnDetailsEntity;
        this.vehicleMaster = vehicleMaster;
        this.isActive = isActive;
        this.rStat = rStat;
        this.isAssigned = isAssigned;
        this.isProcessed = isProcessed;
        this.auditEntity = auditEntity;
        this.remark = remark;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public SapGrnDetailsDto getSapGrnDetailsEntity() {
        return sapGrnDetailsEntity;
    }

    public void setSapGrnDetailsEntity(SapGrnDetailsDto sapGrnDetailsEntity) {
        this.sapGrnDetailsEntity = sapGrnDetailsEntity;
    }

    public VehicleMasterDto getVehicleMaster() {
        return vehicleMaster;
    }

    public void setVehicleMaster(VehicleMasterDto vehicleMaster) {
        this.vehicleMaster = vehicleMaster;
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

    public Boolean getAssigned() {
        return isAssigned;
    }

    public void setAssigned(Boolean assigned) {
        isAssigned = assigned;
    }

    public Boolean getProcessed() {
        return isProcessed;
    }

    public void setProcessed(Boolean processed) {
        isProcessed = processed;
    }

    public AuditEntity getAuditEntity() {
        return auditEntity;
    }

    public void setAuditEntity(AuditEntity auditEntity) {
        this.auditEntity = auditEntity;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
