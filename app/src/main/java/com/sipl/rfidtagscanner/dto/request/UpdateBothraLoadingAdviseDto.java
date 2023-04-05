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
    private String loadingTime;
    private Integer transactionFlag;

    public UpdateBothraLoadingAdviseDto(AuditEntity auditEntity, UserMasterDto coromandelLoadingSupervisor, StorageLocationDto functionalLocationDestinationMaster, StorageLocationDto sourceMaster, RfidLepIssueDto rfidLepIssueModel, Boolean isActive, String loadingTime, Integer transactionFlag) {
        this.auditEntity = auditEntity;
        this.coromandelLoadingSupervisor = coromandelLoadingSupervisor;
        this.functionalLocationDestinationMaster = functionalLocationDestinationMaster;
        this.sourceMaster = sourceMaster;
        this.rfidLepIssueModel = rfidLepIssueModel;
        this.isActive = isActive;
        this.loadingTime = loadingTime;
        this.transactionFlag = transactionFlag;
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

    public String getLoadingTime() {
        return loadingTime;
    }

    public void setLoadingTime(String loadingTime) {
        this.loadingTime = loadingTime;
    }

    public Integer getTransactionFlag() {
        return transactionFlag;
    }

    public void setTransactionFlag(Integer transactionFlag) {
        this.transactionFlag = transactionFlag;
    }
}
