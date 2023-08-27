package com.sipl.rfidtagscanner.dto.dtos;

import com.sipl.rfidtagscanner.entites.AuditEntity;

public class RfidLepIssueDto {

    private Integer id;
    private DailyTransportReportModuleDto dailyTransportReportModule;
    private String lepNumber;
    private RfidMasterDto rfidMaster;
    private BerthMasterDto berthMaster;
    private DriverMasterDto driverMaster;
    private UserMasterDto userMaster;
    private StorageLocationDto destinationLocation;
    private String lepIssueDateTime;
    private String relepIssueDateTime;
    private Boolean isActive;
    private Integer printCount;
    private Integer rStat;
    private String cancelledReason;
    private Boolean isLepCancelled;
    private String cancelledBy;
    private Boolean isRfidLepMapped;
    private AuditEntity auditEntity;

    public RfidLepIssueDto(Integer id) {
        this.id = id;
    }

    public RfidLepIssueDto(Integer id, DailyTransportReportModuleDto dailyTransportReportModule, String lepNumber, RfidMasterDto rfidMaster, BerthMasterDto berthMaster, DriverMasterDto driverMaster, UserMasterDto userMaster, StorageLocationDto destinationLocation, String lepIssueDateTime, String relepIssueDateTime, Boolean isActive, Integer printCount, Integer rStat, String cancelledReason, Boolean isLepCancelled, String cancelledBy, Boolean isRfidLepMapped, AuditEntity auditEntity) {
        this.id = id;
        this.dailyTransportReportModule = dailyTransportReportModule;
        this.lepNumber = lepNumber;
        this.rfidMaster = rfidMaster;
        this.berthMaster = berthMaster;
        this.driverMaster = driverMaster;
        this.userMaster = userMaster;
        this.destinationLocation = destinationLocation;
        this.lepIssueDateTime = lepIssueDateTime;
        this.relepIssueDateTime = relepIssueDateTime;
        this.isActive = isActive;
        this.printCount = printCount;
        this.rStat = rStat;
        this.cancelledReason = cancelledReason;
        this.isLepCancelled = isLepCancelled;
        this.cancelledBy = cancelledBy;
        this.isRfidLepMapped = isRfidLepMapped;
        this.auditEntity = auditEntity;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public DailyTransportReportModuleDto getDailyTransportReportModule() {
        return dailyTransportReportModule;
    }

    public void setDailyTransportReportModule(DailyTransportReportModuleDto dailyTransportReportModule) {
        this.dailyTransportReportModule = dailyTransportReportModule;
    }

    public String getLepNumber() {
        return lepNumber;
    }

    public void setLepNumber(String lepNumber) {
        this.lepNumber = lepNumber;
    }

    public RfidMasterDto getRfidMaster() {
        return rfidMaster;
    }

    public void setRfidMaster(RfidMasterDto rfidMaster) {
        this.rfidMaster = rfidMaster;
    }

    public BerthMasterDto getBerthMaster() {
        return berthMaster;
    }

    public void setBerthMaster(BerthMasterDto berthMaster) {
        this.berthMaster = berthMaster;
    }

    public DriverMasterDto getDriverMaster() {
        return driverMaster;
    }

    public void setDriverMaster(DriverMasterDto driverMaster) {
        this.driverMaster = driverMaster;
    }

    public UserMasterDto getUserMaster() {
        return userMaster;
    }

    public void setUserMaster(UserMasterDto userMaster) {
        this.userMaster = userMaster;
    }

    public StorageLocationDto getDestinationLocation() {
        return destinationLocation;
    }

    public void setDestinationLocation(StorageLocationDto destinationLocation) {
        this.destinationLocation = destinationLocation;
    }

    public String getLepIssueDateTime() {
        return lepIssueDateTime;
    }

    public void setLepIssueDateTime(String lepIssueDateTime) {
        this.lepIssueDateTime = lepIssueDateTime;
    }

    public String getRelepIssueDateTime() {
        return relepIssueDateTime;
    }

    public void setRelepIssueDateTime(String relepIssueDateTime) {
        this.relepIssueDateTime = relepIssueDateTime;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Integer getPrintCount() {
        return printCount;
    }

    public void setPrintCount(Integer printCount) {
        this.printCount = printCount;
    }

    public Integer getrStat() {
        return rStat;
    }

    public void setrStat(Integer rStat) {
        this.rStat = rStat;
    }

    public String getCancelledReason() {
        return cancelledReason;
    }

    public void setCancelledReason(String cancelledReason) {
        this.cancelledReason = cancelledReason;
    }

    public Boolean getLepCancelled() {
        return isLepCancelled;
    }

    public void setLepCancelled(Boolean lepCancelled) {
        isLepCancelled = lepCancelled;
    }

    public String getCancelledBy() {
        return cancelledBy;
    }

    public void setCancelledBy(String cancelledBy) {
        this.cancelledBy = cancelledBy;
    }

    public Boolean getRfidLepMapped() {
        return isRfidLepMapped;
    }

    public void setRfidLepMapped(Boolean rfidLepMapped) {
        isRfidLepMapped = rfidLepMapped;
    }

    public AuditEntity getAuditEntity() {
        return auditEntity;
    }

    public void setAuditEntity(AuditEntity auditEntity) {
        this.auditEntity = auditEntity;
    }
}
