package com.sipl.rfidtagscanner.dto.dtos;

import java.util.List;

public class UserPermissionsResponseDto {

    private Integer userMasterId;
    private String userId;
    private Integer roleId;
    private String roleName;
    private String plantCode;
    private Boolean isBerth;
    private Boolean isSourceLocation;
    private Boolean isDestinationLocation;
    private Boolean isGate;
    private Boolean isWeighbridge;
    private List<GenericIntegerData> berth;
    private List<GenericData> sourceLocation;
    private List<GenericData> destinationLocation;
    private List<GenericIntegerData> gate;
    private List<GenericIntegerData> weighbridge;

    public UserPermissionsResponseDto(Integer userMasterId, String userId, Integer roleId, String roleName, String plantCode, Boolean isBerth, Boolean isSourceLocation, Boolean isDestinationLocation, Boolean isGate, Boolean isWeighbridge, List<GenericIntegerData> berth, List<GenericData> sourceLocation, List<GenericData> destinationLocation, List<GenericIntegerData> gate, List<GenericIntegerData> weighbridge) {
        this.userMasterId = userMasterId;
        this.userId = userId;
        this.roleId = roleId;
        this.roleName = roleName;
        this.plantCode = plantCode;
        this.isBerth = isBerth;
        this.isSourceLocation = isSourceLocation;
        this.isDestinationLocation = isDestinationLocation;
        this.isGate = isGate;
        this.isWeighbridge = isWeighbridge;
        this.berth = berth;
        this.sourceLocation = sourceLocation;
        this.destinationLocation = destinationLocation;
        this.gate = gate;
        this.weighbridge = weighbridge;
    }

    public Integer getUserMasterId() {
        return userMasterId;
    }

    public void setUserMasterId(Integer userMasterId) {
        this.userMasterId = userMasterId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getPlantCode() {
        return plantCode;
    }

    public void setPlantCode(String plantCode) {
        this.plantCode = plantCode;
    }


    public List<GenericIntegerData> getAllBerth(){
        return berth;
    }

    public Boolean getBerth() {
        return isBerth;
    }

    public void setBerth(List<GenericIntegerData> berth) {
        this.berth = berth;
    }

    public void setBerth(Boolean berth) {
        isBerth = berth;
    }



    public Boolean getSourceLocation() {
        return isSourceLocation;
    }

    public void setSourceLocation(List<GenericData> sourceLocation) {
        this.sourceLocation = sourceLocation;
    }

    public List<GenericData> getSourceLocationDto(){
        return sourceLocation;
    }

    public void setSourceLocation(Boolean sourceLocation) {
        isSourceLocation = sourceLocation;
    }

    public Boolean getDestinationLocation() {
        return isDestinationLocation;
    }

    public void setDestinationLocation(List<GenericData> destinationLocation) {
        this.destinationLocation = destinationLocation;
    }

    public List<GenericData> getDestinationLocationDto(){
        return destinationLocation;
    }

    public void setDestinationLocation(Boolean destinationLocation) {
        isDestinationLocation = destinationLocation;
    }

    public Boolean getGate() {
        return isGate;
    }

    public void setGate(List<GenericIntegerData> gate) {
        this.gate = gate;
    }

    public List<GenericIntegerData> getGateDto(){
        return gate;
    }

    public void setGate(Boolean gate) {
        isGate = gate;
    }

    public Boolean getWeighbridge() {
        return isWeighbridge;
    }

    public void setWeighbridge(List<GenericIntegerData> weighbridge) {
        this.weighbridge = weighbridge;
    }

    public List<GenericIntegerData> getWeighBridgeDto(){
        return weighbridge;
    }

    public void setWeighbridge(Boolean weighbridge) {
        isWeighbridge = weighbridge;
    }
}
