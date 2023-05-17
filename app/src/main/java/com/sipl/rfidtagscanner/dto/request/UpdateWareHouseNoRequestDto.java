package com.sipl.rfidtagscanner.dto.request;

import com.sipl.rfidtagscanner.dto.dtos.RemarksDto;
import com.sipl.rfidtagscanner.dto.dtos.RfidLepIssueDto;
import com.sipl.rfidtagscanner.dto.dtos.StorageLocationDto;
import com.sipl.rfidtagscanner.entites.AuditEntity;

public class UpdateWareHouseNoRequestDto {

    private AuditEntity auditEntity;
    private StorageLocationDto priviousWarehouse;
    private StorageLocationDto warehouse;
    private RfidLepIssueDto rfidLepIssueModel;
    private RemarksDto remarkMaster;
    private Integer transactionFlag;
//    private String unloadingTime;
    private String vehicleOutTime;
    private String vehicleInTime;

    public UpdateWareHouseNoRequestDto(AuditEntity auditEntity, StorageLocationDto priviousWarehouse, StorageLocationDto warehouse, RfidLepIssueDto rfidLepIssueModel, RemarksDto remarkMaster, Integer transactionFlag, String vehicleOutTime, String vehicleInTime) {
        this.auditEntity = auditEntity;
        this.priviousWarehouse = priviousWarehouse;
        this.warehouse = warehouse;
        this.rfidLepIssueModel = rfidLepIssueModel;
        this.remarkMaster = remarkMaster;
        this.transactionFlag = transactionFlag;
        this.vehicleOutTime = vehicleOutTime;
        this.vehicleInTime = vehicleInTime;
    }

    public AuditEntity getAuditEntity() {
        return auditEntity;
    }

    public void setAuditEntity(AuditEntity auditEntity) {
        this.auditEntity = auditEntity;
    }

    public StorageLocationDto getPriviousWarehouse() {
        return priviousWarehouse;
    }

    public void setPriviousWarehouse(StorageLocationDto priviousWarehouse) {
        this.priviousWarehouse = priviousWarehouse;
    }

    public StorageLocationDto getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(StorageLocationDto warehouse) {
        this.warehouse = warehouse;
    }

    public RfidLepIssueDto getRfidLepIssueModel() {
        return rfidLepIssueModel;
    }

    public void setRfidLepIssueModel(RfidLepIssueDto rfidLepIssueModel) {
        this.rfidLepIssueModel = rfidLepIssueModel;
    }

    public RemarksDto getRemarkMaster() {
        return remarkMaster;
    }

    public void setRemarkMaster(RemarksDto remarkMaster) {
        this.remarkMaster = remarkMaster;
    }

    public Integer getTransactionFlag() {
        return transactionFlag;
    }

    public void setTransactionFlag(Integer transactionFlag) {
        this.transactionFlag = transactionFlag;
    }

    public String getVehicleOutTime() {
        return vehicleOutTime;
    }

    public void setVehicleOutTime(String vehicleOutTime) {
        this.vehicleOutTime = vehicleOutTime;
    }

    public String getVehicleInTime() {
        return vehicleInTime;
    }

    public void setVehicleInTime(String vehicleInTime) {
        this.vehicleInTime = vehicleInTime;
    }
}
