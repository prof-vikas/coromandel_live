package com.sipl.rfidtagscanner.dto.dtos;

import com.sipl.rfidtagscanner.dto.request.AuditDtrRequestDto;
import com.sipl.rfidtagscanner.entites.AuditEntity;

public class DailyTransportReportModuleDto {

    private Integer id;
    private String commodity;
    private AuditDtrRequestDto auditDtrRequest;
    private String truckNumber;
    private String vesselName;
    private Integer sapGrNumber;
    private Integer truckCapacity;
    private String date;
    private String batch;
    private Integer grSloc;
    private Integer billNo;
    private String uom;
    private Integer billOfEntry;
    private String billOfDate;
    private String status;
    private Boolean isActive;
    private Integer rStat;
    private AuditEntity auditEntity;
    private Boolean isProcessed;
    private String remark;

    public DailyTransportReportModuleDto(Integer id, String commodity, AuditDtrRequestDto auditDtrRequest, String truckNumber, String vesselName, Integer sapGrNumber, Integer truckCapacity, String date, String batch, Integer grSloc, Integer billNo, String uom, Integer billOfEntry, String billOfDate, String status, Boolean isActive, Integer rStat, AuditEntity auditEntity, Boolean isProcessed, String remark) {
        this.id = id;
        this.commodity = commodity;
        this.auditDtrRequest = auditDtrRequest;
        this.truckNumber = truckNumber;
        this.vesselName = vesselName;
        this.sapGrNumber = sapGrNumber;
        this.truckCapacity = truckCapacity;
        this.date = date;
        this.batch = batch;
        this.grSloc = grSloc;
        this.billNo = billNo;
        this.uom = uom;
        this.billOfEntry = billOfEntry;
        this.billOfDate = billOfDate;
        this.status = status;
        this.isActive = isActive;
        this.rStat = rStat;
        this.auditEntity = auditEntity;
        this.isProcessed = isProcessed;
        this.remark = remark;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCommodity() {
        return commodity;
    }

    public void setCommodity(String commodity) {
        this.commodity = commodity;
    }

    public AuditDtrRequestDto getAuditDtrRequest() {
        return auditDtrRequest;
    }

    public void setAuditDtrRequest(AuditDtrRequestDto auditDtrRequest) {
        this.auditDtrRequest = auditDtrRequest;
    }

    public String getTruckNumber() {
        return truckNumber;
    }

    public void setTruckNumber(String truckNumber) {
        this.truckNumber = truckNumber;
    }

    public String getVesselName() {
        return vesselName;
    }

    public void setVesselName(String vesselName) {
        this.vesselName = vesselName;
    }

    public Integer getSapGrNumber() {
        return sapGrNumber;
    }

    public void setSapGrNumber(Integer sapGrNumber) {
        this.sapGrNumber = sapGrNumber;
    }

    public Integer getTruckCapacity() {
        return truckCapacity;
    }

    public void setTruckCapacity(Integer truckCapacity) {
        this.truckCapacity = truckCapacity;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public Integer getGrSloc() {
        return grSloc;
    }

    public void setGrSloc(Integer grSloc) {
        this.grSloc = grSloc;
    }

    public Integer getBillNo() {
        return billNo;
    }

    public void setBillNo(Integer billNo) {
        this.billNo = billNo;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public Integer getBillOfEntry() {
        return billOfEntry;
    }

    public void setBillOfEntry(Integer billOfEntry) {
        this.billOfEntry = billOfEntry;
    }

    public String getBillOfDate() {
        return billOfDate;
    }

    public void setBillOfDate(String billOfDate) {
        this.billOfDate = billOfDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Boolean getProcessed() {
        return isProcessed;
    }

    public void setProcessed(Boolean processed) {
        isProcessed = processed;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
