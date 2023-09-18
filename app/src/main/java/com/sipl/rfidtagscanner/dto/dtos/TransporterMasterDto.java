package com.sipl.rfidtagscanner.dto.dtos;

import com.sipl.rfidtagscanner.entites.AuditEntity;

public class TransporterMasterDto {

    private Integer id;
    private String companyName;
    private String gstNumber;
    private String address;
    private String emailId;
    private String contactNumber;
    private Boolean isDeleted;
    private AuditEntity auditEntity;

    public TransporterMasterDto(Integer id, String companyName, String gstNumber, String address, String emailId, String contactNumber, Boolean isDeleted, AuditEntity auditEntity) {
        this.id = id;
        this.companyName = companyName;
        this.gstNumber = gstNumber;
        this.address = address;
        this.emailId = emailId;
        this.contactNumber = contactNumber;
        this.isDeleted = isDeleted;
        this.auditEntity = auditEntity;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getGstNumber() {
        return gstNumber;
    }

    public void setGstNumber(String gstNumber) {
        this.gstNumber = gstNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public AuditEntity getAuditEntity() {
        return auditEntity;
    }

    public void setAuditEntity(AuditEntity auditEntity) {
        this.auditEntity = auditEntity;
    }
}
