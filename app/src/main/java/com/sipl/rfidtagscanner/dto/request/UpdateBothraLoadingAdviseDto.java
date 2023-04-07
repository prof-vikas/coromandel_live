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


}
