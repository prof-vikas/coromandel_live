package com.sipl.rfidtagscanner.dto.request;

import com.sipl.rfidtagscanner.dto.dtos.BothraLoadingSupervisorDto;
import com.sipl.rfidtagscanner.dto.dtos.PinnacleLoadingSupervisorDto;
import com.sipl.rfidtagscanner.dto.dtos.RfidLepIssueDto;
import com.sipl.rfidtagscanner.dto.dtos.StorageLocationDto;
import com.sipl.rfidtagscanner.dto.dtos.UserMasterDto;
import com.sipl.rfidtagscanner.entites.AuditEntity;

public class LoadingAdviseRequestDto {
    private AuditEntity auditEntity;
    private String strBothraLoadingSupervisor;
    private String strPinnacleLoadingSupervisor;
    private UserMasterDto coromandelLoadingSupervisor;
    private StorageLocationDto sourceMaster;
    private StorageLocationDto functionalLocationDestinationMaster;
    private RfidLepIssueDto rfidLepIssueModel;
    private Integer transactionFlag;
    private Boolean isActive;
    private Integer rStat;
    private String inLoadingTime;
    private String outLoadingTime;

    public LoadingAdviseRequestDto(AuditEntity auditEntity, String strBothraLoadingSupervisor, String strPinnacleLoadingSupervisor, UserMasterDto coromandelLoadingSupervisor, StorageLocationDto sourceMaster, StorageLocationDto functionalLocationDestinationMaster, RfidLepIssueDto rfidLepIssueModel, Integer transactionFlag, Boolean isActive, Integer rStat, String inLoadingTime, String outLoadingTime) {
        this.auditEntity = auditEntity;
        this.strBothraLoadingSupervisor = strBothraLoadingSupervisor;
        this.strPinnacleLoadingSupervisor = strPinnacleLoadingSupervisor;
        this.coromandelLoadingSupervisor = coromandelLoadingSupervisor;
        this.sourceMaster = sourceMaster;
        this.functionalLocationDestinationMaster = functionalLocationDestinationMaster;
        this.rfidLepIssueModel = rfidLepIssueModel;
        this.transactionFlag = transactionFlag;
        this.isActive = isActive;
        this.rStat = rStat;
        this.inLoadingTime = inLoadingTime;
        this.outLoadingTime = outLoadingTime;
    }

    public AuditEntity getAuditEntity() {
        return auditEntity;
    }

    public void setAuditEntity(AuditEntity auditEntity) {
        this.auditEntity = auditEntity;
    }

    public String getStrBothraLoadingSupervisor() {
        return strBothraLoadingSupervisor;
    }

    public void setStrBothraLoadingSupervisor(String strBothraLoadingSupervisor) {
        this.strBothraLoadingSupervisor = strBothraLoadingSupervisor;
    }

    public String getStrPinnacleLoadingSupervisor() {
        return strPinnacleLoadingSupervisor;
    }

    public void setStrPinnacleLoadingSupervisor(String strPinnacleLoadingSupervisor) {
        this.strPinnacleLoadingSupervisor = strPinnacleLoadingSupervisor;
    }

    public UserMasterDto getCoromandelLoadingSupervisor() {
        return coromandelLoadingSupervisor;
    }

    public void setCoromandelLoadingSupervisor(UserMasterDto coromandelLoadingSupervisor) {
        this.coromandelLoadingSupervisor = coromandelLoadingSupervisor;
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

