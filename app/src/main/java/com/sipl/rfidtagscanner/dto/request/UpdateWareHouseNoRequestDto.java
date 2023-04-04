package com.sipl.rfidtagscanner.dto.request;

import com.sipl.rfidtagscanner.dto.RfidLepIssueDto;
import com.sipl.rfidtagscanner.dto.StorageLocationDto;
import com.sipl.rfidtagscanner.entites.AuditEntity;

public class UpdateWareHouseNoRequestDto {

    private AuditEntity auditEntity;
    private StorageLocationDto priviousWarehouse;
    private StorageLocationDto warehouse;
    private RfidLepIssueDto rfidLepIssueModel;
    private Integer whSupervisorRemark;
    private Integer transactionFlag;
    private String unloadingTime;



    public UpdateWareHouseNoRequestDto(AuditEntity auditEntity, StorageLocationDto priviousWarehouse, StorageLocationDto warehouse, RfidLepIssueDto rfidLepIssueModel, Integer whSupervisorRemark, Integer transactionFlag, String unloadingTime) {
        this.auditEntity = auditEntity;
        this.priviousWarehouse = priviousWarehouse;
        this.warehouse = warehouse;
        this.rfidLepIssueModel = rfidLepIssueModel;
        this.whSupervisorRemark = whSupervisorRemark;
        this.transactionFlag = transactionFlag;
        this.unloadingTime = unloadingTime;
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

    public Integer getWhSupervisorRemark() {
        return whSupervisorRemark;
    }

    public void setWhSupervisorRemark(Integer whSupervisorRemark) {
        this.whSupervisorRemark = whSupervisorRemark;
    }

    public Integer getTransactionFlag() {
        return transactionFlag;
    }

    public void setTransactionFlag(Integer transactionFlag) {
        this.transactionFlag = transactionFlag;
    }

    public String getUnloadingTime() {
        return unloadingTime;
    }

    public void setUnloadingTime(String unloadingTime) {
        this.unloadingTime = unloadingTime;
    }
}
