package com.sipl.rfidtagscanner.dto.dtos;

public class StorageLocationDto {

    private String strLocationCode;
    private String strLocationDesc;
    private Boolean activeFlag;
    private Boolean isBothraPlant;
    private Boolean sapFlag;
    private Boolean isWbAvailable;
    private String createdDate;
    private PlantMasterDto plantMaster;

    public StorageLocationDto(String strLocationCode, Boolean isWbAvailable) {
        this.strLocationCode = strLocationCode;
        this.isWbAvailable = isWbAvailable;
    }

    public StorageLocationDto(String strLocationCode) {
        this.strLocationCode = strLocationCode;
    }

    public StorageLocationDto(String strLocationCode, String strLocationDesc, Boolean activeFlag, Boolean isBothraPlant, Boolean sapFlag, Boolean isWbAvailable, String createdDate, PlantMasterDto plantMaster) {
        this.strLocationCode = strLocationCode;
        this.strLocationDesc = strLocationDesc;
        this.activeFlag = activeFlag;
        this.isBothraPlant = isBothraPlant;
        this.sapFlag = sapFlag;
        this.isWbAvailable = isWbAvailable;
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

    public Boolean getBothraPlant() {
        return isBothraPlant;
    }

    public void setBothraPlant(Boolean bothraPlant) {
        isBothraPlant = bothraPlant;
    }

    public Boolean getSapFlag() {
        return sapFlag;
    }

    public void setSapFlag(Boolean sapFlag) {
        this.sapFlag = sapFlag;
    }

    public Boolean getWbAvailable() {
        return isWbAvailable;
    }

    public void setWbAvailable(Boolean wbAvailable) {
        isWbAvailable = wbAvailable;
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
