package com.sipl.rfidtagscanner.entites;

public class LoadingAdviseLepDto {

    private String lepNumber;
    private String driverName;
    private String driverMobileNo;
    private String driverLicenseNo;
    private String sapGrnNo;
    private String truckNo;
    private String vesselName;
    private Integer truckCapacity;
    private String commodity;


    public LoadingAdviseLepDto(String lepNumber, String driverName, String driverMobileNo, String driverLicenseNo, String sapGrnNo, String truckNo, String vesselName, Integer truckCapacity, String commodity) {
        this.lepNumber = lepNumber;
        this.driverName = driverName;
        this.driverMobileNo = driverMobileNo;
        this.driverLicenseNo = driverLicenseNo;
        this.sapGrnNo = sapGrnNo;
        this.truckNo = truckNo;
        this.vesselName = vesselName;
        this.truckCapacity = truckCapacity;
        this.commodity = commodity;
    }

    public String getLepNumber() {
        return lepNumber;
    }

    public void setLepNumber(String lepNumber) {
        this.lepNumber = lepNumber;
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

    public String getSapGrnNo() {
        return sapGrnNo;
    }

    public void setSapGrnNo(String sapGrnNo) {
        this.sapGrnNo = sapGrnNo;
    }

    public String getTruckNo() {
        return truckNo;
    }

    public void setTruckNo(String truckNo) {
        this.truckNo = truckNo;
    }

    public String getVesselName() {
        return vesselName;
    }

    public void setVesselName(String vesselName) {
        this.vesselName = vesselName;
    }

    public Integer getTruckCapacity() {
        return truckCapacity;
    }

    public void setTruckCapacity(Integer truckCapacity) {
        this.truckCapacity = truckCapacity;
    }

    public String getCommodity() {
        return commodity;
    }

    public void setCommodity(String commodity) {
        this.commodity = commodity;
    }
}
