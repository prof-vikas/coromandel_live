package com.sipl.rfidtagscanner.dto.request;

import com.sipl.rfidtagscanner.dto.dtos.RfidLepIssueDto;
import com.sipl.rfidtagscanner.dto.dtos.StorageLocationDto;
import com.sipl.rfidtagscanner.dto.dtos.UserMasterDto;
import com.sipl.rfidtagscanner.entites.AuditEntity;

public class UpdateBothraLoadingAdviseDto {

    private AuditEntity auditEntity;
    private UserMasterDto coromandelLoadingSupervisor;
    private StorageLocationDto functionalLocationDestinationMaster;
    private StorageLocationDto sourceMaster;
    private RfidLepIssueDto rfidLepIssueModel;
    private Boolean isActive;
    private Integer transactionFlag;
    private String inLoadingTime;
    private String outLoadingTime;

    public UpdateBothraLoadingAdviseDto(AuditEntity auditEntity, UserMasterDto coromandelLoadingSupervisor, StorageLocationDto functionalLocationDestinationMaster, StorageLocationDto sourceMaster, RfidLepIssueDto rfidLepIssueModel, Boolean isActive, Integer transactionFlag, String inLoadingTime, String outLoadingTime) {
        this.auditEntity = auditEntity;
        this.coromandelLoadingSupervisor = coromandelLoadingSupervisor;
        this.functionalLocationDestinationMaster = functionalLocationDestinationMaster;
        this.sourceMaster = sourceMaster;
        this.rfidLepIssueModel = rfidLepIssueModel;
        this.isActive = isActive;
        this.transactionFlag = transactionFlag;
        this.inLoadingTime = inLoadingTime;
        this.outLoadingTime = outLoadingTime;
    }

    public AuditEntity getAuditEntity() {
        return auditEntity;
    }

    public void setAuditEntity(AuditEntity auditEntity) {
        this.auditEntity = auditEntity;
    }

    public UserMasterDto getCoromandelLoadingSupervisor() {
        return coromandelLoadingSupervisor;
    }

    public void setCoromandelLoadingSupervisor(UserMasterDto coromandelLoadingSupervisor) {
        this.coromandelLoadingSupervisor = coromandelLoadingSupervisor;
    }

    public StorageLocationDto getFunctionalLocationDestinationMaster() {
        return functionalLocationDestinationMaster;
    }

    public void setFunctionalLocationDestinationMaster(StorageLocationDto functionalLocationDestinationMaster) {
        this.functionalLocationDestinationMaster = functionalLocationDestinationMaster;
    }

    public StorageLocationDto getSourceMaster() {
        return sourceMaster;
    }

    public void setSourceMaster(StorageLocationDto sourceMaster) {
        this.sourceMaster = sourceMaster;
    }

    public RfidLepIssueDto getRfidLepIssueModel() {
        return rfidLepIssueModel;
    }

    public void setRfidLepIssueModel(RfidLepIssueDto rfidLepIssueModel) {
        this.rfidLepIssueModel = rfidLepIssueModel;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Integer getTransactionFlag() {
        return transactionFlag;
    }

    public void setTransactionFlag(Integer transactionFlag) {
        this.transactionFlag = transactionFlag;
    }

    public String getInLoadingTime() {
        return inLoadingTime;
    }

    public void setInLoadingTime(String inLoadingTime) {
        this.inLoadingTime = inLoadingTime;
    }

    public String getOutLoadingTime() {
        return outLoadingTime;
    }

    public void setOutLoadingTime(String outLoadingTime) {
        this.outLoadingTime = outLoadingTime;
    }
}
