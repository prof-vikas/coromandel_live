package com.sipl.rfidtagscanner.dto.request;

import com.sipl.rfidtagscanner.dto.BothraLoadingSupervisorDto;
import com.sipl.rfidtagscanner.dto.PinnacleLoadingSupervisorDto;
import com.sipl.rfidtagscanner.dto.RfidLepIssueDto;
import com.sipl.rfidtagscanner.dto.StorageLocationDto;
import com.sipl.rfidtagscanner.dto.UserMasterDto;
import com.sipl.rfidtagscanner.entites.AuditEntity;

public class LoadingAdviseRequestDto {

    private AuditEntity auditEntity;
    private BothraLoadingSupervisorDto bothraLoadingSupervisor;
    private UserMasterDto coromandelLoadingSupervisor;
    private PinnacleLoadingSupervisorDto pinnacleLoadingSupervisor;
    private StorageLocationDto sourceMaster;
    private StorageLocationDto functionalLocationDestinationMaster;
    private String loadingTime;
    private RfidLepIssueDto rfidLepIssueModel;
    private Integer transactionFlag;
    private Boolean isActive;
    private Integer rStat;

    public LoadingAdviseRequestDto(AuditEntity auditEntity, UserMasterDto coromandelLoadingSupervisor, StorageLocationDto sourceMaster, StorageLocationDto functionalLocationDestinationMaster, String loadingTime, RfidLepIssueDto rfidLepIssueModel, Integer transactionFlag, Boolean isActive, Integer rStat) {
        this.auditEntity = auditEntity;
        this.coromandelLoadingSupervisor = coromandelLoadingSupervisor;
        this.sourceMaster = sourceMaster;
        this.functionalLocationDestinationMaster = functionalLocationDestinationMaster;
        this.loadingTime = loadingTime;
        this.rfidLepIssueModel = rfidLepIssueModel;
        this.transactionFlag = transactionFlag;
        this.isActive = isActive;
        this.rStat = rStat;
    }

    public LoadingAdviseRequestDto(AuditEntity auditEntity, BothraLoadingSupervisorDto bothraLoadingSupervisor, UserMasterDto coromandelLoadingSupervisor, PinnacleLoadingSupervisorDto pinnacleLoadingSupervisor, StorageLocationDto sourceMaster, StorageLocationDto functionalLocationDestinationMaster, String loadingTime, RfidLepIssueDto rfidLepIssueModel, Integer transactionFlag, Boolean isActive, Integer rStat) {
        this.auditEntity = auditEntity;
        this.bothraLoadingSupervisor = bothraLoadingSupervisor;
        this.coromandelLoadingSupervisor = coromandelLoadingSupervisor;
        this.pinnacleLoadingSupervisor = pinnacleLoadingSupervisor;
        this.sourceMaster = sourceMaster;
        this.functionalLocationDestinationMaster = functionalLocationDestinationMaster;
        this.loadingTime = loadingTime;
        this.rfidLepIssueModel = rfidLepIssueModel;
        this.transactionFlag = transactionFlag;
        this.isActive = isActive;
        this.rStat = rStat;
    }

    public AuditEntity getAuditEntity() {
        return auditEntity;
    }

    public void setAuditEntity(AuditEntity auditEntity) {
        this.auditEntity = auditEntity;
    }

    public BothraLoadingSupervisorDto getBothraLoadingSupervisor() {
        return bothraLoadingSupervisor;
    }

    public void setBothraLoadingSupervisor(BothraLoadingSupervisorDto bothraLoadingSupervisor) {
        this.bothraLoadingSupervisor = bothraLoadingSupervisor;
    }

    public UserMasterDto getCoromandelLoadingSupervisor() {
        return coromandelLoadingSupervisor;
    }

    public void setCoromandelLoadingSupervisor(UserMasterDto coromandelLoadingSupervisor) {
        this.coromandelLoadingSupervisor = coromandelLoadingSupervisor;
    }

    public PinnacleLoadingSupervisorDto getPinnacleLoadingSupervisor() {
        return pinnacleLoadingSupervisor;
    }

    public void setPinnacleLoadingSupervisor(PinnacleLoadingSupervisorDto pinnacleLoadingSupervisor) {
        this.pinnacleLoadingSupervisor = pinnacleLoadingSupervisor;
    }

    public StorageLocationDto getSourceMaster() {
        return sourceMaster;
    }

    public void setSourceMaster(StorageLocationDto sourceMaster) {
        this.sourceMaster = sourceMaster;
    }

    public StorageLocationDto getFunctionalLocationDestinationMaster() {
        return functionalLocationDestinationMaster;
    }

    public void setFunctionalLocationDestinationMaster(StorageLocationDto functionalLocationDestinationMaster) {
        this.functionalLocationDestinationMaster = functionalLocationDestinationMaster;
    }

    public String getLoadingTime() {
        return loadingTime;
    }

    public void setLoadingTime(String loadingTime) {
        this.loadingTime = loadingTime;
    }

    public RfidLepIssueDto getRfidLepIssueModel() {
        return rfidLepIssueModel;
    }

    public void setRfidLepIssueModel(RfidLepIssueDto rfidLepIssueModel) {
        this.rfidLepIssueModel = rfidLepIssueModel;
    }

    public Integer getTransactionFlag() {
        return transactionFlag;
    }

    public void setTransactionFlag(Integer transactionFlag) {
        this.transactionFlag = transactionFlag;
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
}

