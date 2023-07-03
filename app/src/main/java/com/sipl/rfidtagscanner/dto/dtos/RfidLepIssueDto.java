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
    private Boolean isActive;
    private Integer printCount;
    private Integer rstat;
    private Boolean isRfidLepMapped;
    private AuditEntity auditEntity;

    public RfidLepIssueDto(Integer id) {
        this.id = id;
    }

    public RfidLepIssueDto(Integer id, DailyTransportReportModuleDto dailyTransportReportModule, String lepNumber, RfidMasterDto rfidMaster, BerthMasterDto berthMaster, DriverMasterDto driverMaster, UserMasterDto userMaster, StorageLocationDto destinationLocation, String lepIssueDateTime, Boolean isActive, Integer printCount, Integer rstat, Boolean isRfidLepMapped, AuditEntity auditEntity) {
        this.id = id;
        this.dailyTransportReportModule = dailyTransportReportModule;
        this.lepNumber = lepNumber;
        this.rfidMaster = rfidMaster;
        this.berthMaster = berthMaster;
        this.driverMaster = driverMaster;
        this.userMaster = userMaster;
        this.destinationLocation = destinationLocation;
        this.lepIssueDateTime = lepIssueDateTime;
        this.isActive = isActive;
        this.printCount = printCount;
        this.rstat = rstat;
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

    public Integer getRstat() {
        return rstat;
    }

    public void setRstat(Integer rstat) {
        this.rstat = rstat;
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
