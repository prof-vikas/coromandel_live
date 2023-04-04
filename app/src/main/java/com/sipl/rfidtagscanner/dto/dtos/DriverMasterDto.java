package com.sipl.rfidtagscanner.dto.dtos;

import com.sipl.rfidtagscanner.entites.AuditEntity;

public class DriverMasterDto {

    private Integer id;
    private String driverName;
    private String driverMobileNo;
    private String driverLicenseNo;
    private String driverLicenseExpiryDate;
    private String driverAadharNo;
    private Boolean isActive;
    private Integer rStat;
    private AuditEntity auditEntity;

    public DriverMasterDto(Integer id, String driverName, String driverMobileNo, String driverLicenseNo, String driverLicenseExpiryDate, String driverAadharNo, Boolean isActive, Integer rStat, AuditEntity auditEntity) {
        this.id = id;
        this.driverName = driverName;
        this.driverMobileNo = driverMobileNo;
        this.driverLicenseNo = driverLicenseNo;
        this.driverLicenseExpiryDate = driverLicenseExpiryDate;
        this.driverAadharNo = driverAadharNo;
        this.isActive = isActive;
        this.rStat = rStat;
        this.auditEntity = auditEntity;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverMobileNo() {
        return driverMobileNo;
    }

    public void setDriverMobileNo(String driverMobileNo) {
        this.driverMobileNo = driverMobileNo;
    }

    public String getDriverLicenseNo() {
        return driverLicenseNo;
    }

    public void setDriverLicenseNo(String driverLicenseNo) {
        this.driverLicenseNo = driverLicenseNo;
    }

    public String getDriverLicenseExpiryDate() {
        return driverLicenseExpiryDate;
    }

    public void setDriverLicenseExpiryDate(String driverLicenseExpiryDate) {
        this.driverLicenseExpiryDate = driverLicenseExpiryDate;
    }

    public String getDriverAadharNo() {
        return driverAadharNo;
    }

    public void setDriverAadharNo(String driverAadharNo) {
        this.driverAadharNo = driverAadharNo;
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
}
