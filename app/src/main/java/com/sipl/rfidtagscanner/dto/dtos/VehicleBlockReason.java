package com.sipl.rfidtagscanner.dto.dtos;

public class VehicleBlockReason {

    private Integer id;
    private String vehicleBlockReason;

    public VehicleBlockReason(Integer id, String vehicleBlockReason) {
        this.id = id;
        this.vehicleBlockReason = vehicleBlockReason;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getVehicleBlockReason() {
        return vehicleBlockReason;
    }

    public void setVehicleBlockReason(String vehicleBlockReason) {
        this.vehicleBlockReason = vehicleBlockReason;
    }
}
