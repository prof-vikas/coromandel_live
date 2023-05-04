package com.sipl.rfidtagscanner.entites;

public class BothraWHDto {

    private String lepNo;
    private String truckNo;
    private String driverName;
    private String commodity;
    private String grossWeight;
    private String previousRMGNo;
    private String previousRMGNoDesc;

    public BothraWHDto(String lepNo, String truckNo, String driverName, String commodity, String grossWeight, String previousRMGNo, String previousRMGNoDesc) {
        this.lepNo = lepNo;
        this.truckNo = truckNo;
        this.driverName = driverName;
        this.commodity = commodity;
        this.grossWeight = grossWeight;
        this.previousRMGNo = previousRMGNo;
        this.previousRMGNoDesc = previousRMGNoDesc;
    }

    public String getLepNo() {
        return lepNo;
    }

    public void setLepNo(String lepNo) {
        this.lepNo = lepNo;
    }

    public String getTruckNo() {
        return truckNo;
    }

    public void setTruckNo(String truckNo) {
        this.truckNo = truckNo;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getCommodity() {
        return commodity;
    }

    public void setCommodity(String commodity) {
        this.commodity = commodity;
    }

    public String getGrossWeight() {
        return grossWeight;
    }

    public void setGrossWeight(String grossWeight) {
        this.grossWeight = grossWeight;
    }

    public String getPreviousRMGNo() {
        return previousRMGNo;
    }

    public void setPreviousRMGNo(String previousRMGNo) {
        this.previousRMGNo = previousRMGNo;
    }

    public String getPreviousRMGNoDesc() {
        return previousRMGNoDesc;
    }

    public void setPreviousRMGNoDesc(String previousRMGNoDesc) {
        this.previousRMGNoDesc = previousRMGNoDesc;
    }
}
