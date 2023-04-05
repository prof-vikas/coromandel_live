package com.sipl.rfidtagscanner.dto.dtos;

public class RemarksDto {

    private Integer id;
    private Boolean isActive;
    private String remarks;



    public RemarksDto(Integer id, Boolean isActive, String remarks) {
        this.id = id;
        this.isActive = isActive;
        this.remarks = remarks;
    }

    public RemarksDto(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
