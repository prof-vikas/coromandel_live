package com.sipl.rfidtagscanner.dto;

public class StorageLocationDto {

    private String strLocationCode;
    private String strLocationDesc;
    private Boolean activeFlag;
    private Boolean sapFlag;
    private String createdDate;
    private PlantMasterDto plantMaster;

    public StorageLocationDto(String strLocationCode) {
        this.strLocationCode = strLocationCode;
    }

    public StorageLocationDto(String strLocationCode, String strLocationDesc, Boolean activeFlag, Boolean sapFlag, String createdDate, PlantMasterDto plantMaster) {
        this.strLocationCode = strLocationCode;
        this.strLocationDesc = strLocationDesc;
        this.activeFlag = activeFlag;
        this.sapFlag = sapFlag;
        this.createdDate = createdDate;
        this.plantMaster = plantMaster;
    }

    public String getStrLocationCode() {
        return strLocationCode;
    }

    public void setStrLocationCode(String strLocationCode) {
        this.strLocationCode = strLocationCode;
    }

    public String getStrLocationDesc() {
        return strLocationDesc;
    }

    public void setStrLocationDesc(String strLocationDesc) {
        this.strLocationDesc = strLocationDesc;
    }

    public Boolean getActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(Boolean activeFlag) {
        this.activeFlag = activeFlag;
    }

    public Boolean getSapFlag() {
        return sapFlag;
    }

    public void setSapFlag(Boolean sapFlag) {
        this.sapFlag = sapFlag;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public PlantMasterDto getPlantMaster() {
        return plantMaster;
    }

    public void setPlantMaster(PlantMasterDto plantMaster) {
        this.plantMaster = plantMaster;
    }
}
