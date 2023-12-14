package com.sipl.rfidtagscanner.dto.dtos;

import com.sipl.rfidtagscanner.entites.AuditEntity;

public class VehicleMasterDto {

    private Integer id;
    private String vehicleRegistrationNumber;
    private String ownerName;
    private String registrationDate;
    private String rtoFitnessExpiryDate;
    private String rtoVehiclePermitExpiry;
    private String insuranceExpiryDate;
    private String pucValidity;
    private String regGrossWeight;
    private String regTareWeight;
    private String regCarryingCapacity;
    private String vehicleAge;
    private TransporterMasterDto TransporterMaster;
    private VehicleBlockReason vehicleBlockReason;
    private Boolean isBlock;
    private Boolean isActive;
    private Boolean isDeleted;
    private AuditEntity auditEntity;

    public VehicleMasterDto(Integer id, String vehicleRegistrationNumber, String ownerName, String registrationDate, String rtoFitnessExpiryDate, String rtoVehiclePermitExpiry, String insuranceExpiryDate, String pucValidity, String regGrossWeight, String regTareWeight, String regCarryingCapacity, String vehicleAge, TransporterMasterDto transporterMaster, VehicleBlockReason vehicleBlockReason, Boolean isBlock, Boolean isActive, Boolean isDeleted, AuditEntity auditEntity) {
        this.id = id;
        this.vehicleRegistrationNumber = vehicleRegistrationNumber;
        this.ownerName = ownerName;
        this.registrationDate = registrationDate;
        this.rtoFitnessExpiryDate = rtoFitnessExpiryDate;
        this.rtoVehiclePermitExpiry = rtoVehiclePermitExpiry;
        this.insuranceExpiryDate = insuranceExpiryDate;
        this.pucValidity = pucValidity;
        this.regGrossWeight = regGrossWeight;
        this.regTareWeight = regTareWeight;
        this.regCarryingCapacity = regCarryingCapacity;
        this.vehicleAge = vehicleAge;
        TransporterMaster = transporterMaster;
        this.vehicleBlockReason = vehicleBlockReason;
        this.isBlock = isBlock;
        this.isActive = isActive;
        this.isDeleted = isDeleted;
        this.auditEntity = auditEntity;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getVehicleRegistrationNumber() {
        return vehicleRegistrationNumber;
    }

    public void setVehicleRegistrationNumber(String vehicleRegistrationNumber) {
        this.vehicleRegistrationNumber = vehicleRegistrationNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getRtoFitnessExpiryDate() {
        return rtoFitnessExpiryDate;
    }

    public void setRtoFitnessExpiryDate(String rtoFitnessExpiryDate) {
        this.rtoFitnessExpiryDate = rtoFitnessExpiryDate;
    }

    public String getRtoVehiclePermitExpiry() {
        return rtoVehiclePermitExpiry;
    }

    public void setRtoVehiclePermitExpiry(String rtoVehiclePermitExpiry) {
        this.rtoVehiclePermitExpiry = rtoVehiclePermitExpiry;
    }

    public String getInsuranceExpiryDate() {
        return insuranceExpiryDate;
    }

    public void setInsuranceExpiryDate(String insuranceExpiryDate) {
        this.insuranceExpiryDate = insuranceExpiryDate;
    }

    public String getPucValidity() {
        return pucValidity;
    }

    public void setPucValidity(String pucValidity) {
        this.pucValidity = pucValidity;
    }

    public String getRegGrossWeight() {
        return regGrossWeight;
    }

    public void setRegGrossWeight(String regGrossWeight) {
        this.regGrossWeight = regGrossWeight;
    }

    public String getRegTareWeight() {
        return regTareWeight;
    }

    public void setRegTareWeight(String regTareWeight) {
        this.regTareWeight = regTareWeight;
    }

    public String getRegCarryingCapacity() {
        return regCarryingCapacity;
    }

    public void setRegCarryingCapacity(String regCarryingCapacity) {
        this.regCarryingCapacity = regCarryingCapacity;
    }

    public String getVehicleAge() {
        return vehicleAge;
    }

    public void setVehicleAge(String vehicleAge) {
        this.vehicleAge = vehicleAge;
    }

    public TransporterMasterDto getTransporterMaster() {
        return TransporterMaster;
    }

    public void setTransporterMaster(TransporterMasterDto transporterMaster) {
        TransporterMaster = transporterMaster;
    }

    public VehicleBlockReason getVehicleBlockReason() {
        return vehicleBlockReason;
    }

    public void setVehicleBlockReason(VehicleBlockReason vehicleBlockReason) {
        this.vehicleBlockReason = vehicleBlockReason;
    }

    public Boolean getBlock() {
        return isBlock;
    }

    public void setBlock(Boolean block) {
        isBlock = block;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
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
