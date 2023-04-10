package com.sipl.rfidtagscanner.dto.dtos;

import com.sipl.rfidtagscanner.entites.AuditEntity;

import java.math.BigDecimal;

public class TransactionsDto {
    private Integer id;
    private RfidLepIssueDto rfidLepIssueModel;
    private UserMasterDto coromandelLoadingSupervisor;
    private PinnacleLoadingSupervisorDto pinnacleLoadingSupervisor;
    private BothraLoadingSupervisorDto bothraLoadingSupervisor;
    private StorageLocationDto sourceMaster;
    private StorageLocationDto functionalLocationDestinationMaster;
    private StorageLocationDto priviousWarehouse;
    private StorageLocationDto warehouse;
    private GateMasterDto gateInMaster;
    private GateMasterDto gateOutMaster;
    private WeighBridgeMasterDto weighBridgeGrossMaster;
    private WeighBridgeMasterDto weighBridgeTareMaster;
    private WeighmentTypeDto weighmentTypeMaster;
    private RemarksDto remarkMaster;
    private String loadingTime;
    private BigDecimal grossWeight;
    private String grossWeightTime;
    private Integer grossWtPrintCount;
    private BigDecimal tareWeight;
    private Integer tareWtPrintCount;
    private BigDecimal netWeight;
    private String netWeightTime;
    private BigDecimal bothraGrossWeight;
    private String bothraGrossWeightTime;
    private Integer bothraGrossWtPrintCount;
    private BigDecimal bothraTareWeight;
    private Integer bothraTareWtPrintCount;
    private BigDecimal bothraNetWeight;
    private String bothraNetWeightTime;
    private String vehicleInTime;
    private String vehicleOutTime;
    private String whSupervisorRemark;
    private String unloadingTime;
    private Integer transactionFlag;
    private Boolean isActive;
    private Integer rStat;
    private AuditEntity auditEntity;

    public TransactionsDto(Integer id, RfidLepIssueDto rfidLepIssueModel, UserMasterDto coromandelLoadingSupervisor, PinnacleLoadingSupervisorDto pinnacleLoadingSupervisor, BothraLoadingSupervisorDto bothraLoadingSupervisor, StorageLocationDto sourceMaster, StorageLocationDto functionalLocationDestinationMaster, StorageLocationDto priviousWarehouse, StorageLocationDto warehouse, GateMasterDto gateInMaster, GateMasterDto gateOutMaster, WeighBridgeMasterDto weighBridgeGrossMaster, WeighBridgeMasterDto weighBridgeTareMaster, WeighmentTypeDto weighmentTypeMaster, RemarksDto remarkMaster, String loadingTime, BigDecimal grossWeight, String grossWeightTime, Integer grossWtPrintCount, BigDecimal tareWeight, Integer tareWtPrintCount, BigDecimal netWeight, String netWeightTime, BigDecimal bothraGrossWeight, String bothraGrossWeightTime, Integer bothraGrossWtPrintCount, BigDecimal bothraTareWeight, Integer bothraTareWtPrintCount, BigDecimal bothraNetWeight, String bothraNetWeightTime, String vehicleInTime, String vehicleOutTime, String whSupervisorRemark, String unloadingTime, Integer transactionFlag, Boolean isActive, Integer rStat, AuditEntity auditEntity) {
        this.id = id;
        this.rfidLepIssueModel = rfidLepIssueModel;
        this.coromandelLoadingSupervisor = coromandelLoadingSupervisor;
        this.pinnacleLoadingSupervisor = pinnacleLoadingSupervisor;
        this.bothraLoadingSupervisor = bothraLoadingSupervisor;
        this.sourceMaster = sourceMaster;
        this.functionalLocationDestinationMaster = functionalLocationDestinationMaster;
        this.priviousWarehouse = priviousWarehouse;
        this.warehouse = warehouse;
        this.gateInMaster = gateInMaster;
        this.gateOutMaster = gateOutMaster;
        this.weighBridgeGrossMaster = weighBridgeGrossMaster;
        this.weighBridgeTareMaster = weighBridgeTareMaster;
        this.weighmentTypeMaster = weighmentTypeMaster;
        this.remarkMaster = remarkMaster;
        this.loadingTime = loadingTime;
        this.grossWeight = grossWeight;
        this.grossWeightTime = grossWeightTime;
        this.grossWtPrintCount = grossWtPrintCount;
        this.tareWeight = tareWeight;
        this.tareWtPrintCount = tareWtPrintCount;
        this.netWeight = netWeight;
        this.netWeightTime = netWeightTime;
        this.bothraGrossWeight = bothraGrossWeight;
        this.bothraGrossWeightTime = bothraGrossWeightTime;
        this.bothraGrossWtPrintCount = bothraGrossWtPrintCount;
        this.bothraTareWeight = bothraTareWeight;
        this.bothraTareWtPrintCount = bothraTareWtPrintCount;
        this.bothraNetWeight = bothraNetWeight;
        this.bothraNetWeightTime = bothraNetWeightTime;
        this.vehicleInTime = vehicleInTime;
        this.vehicleOutTime = vehicleOutTime;
        this.whSupervisorRemark = whSupervisorRemark;
        this.unloadingTime = unloadingTime;
        this.transactionFlag = transactionFlag;
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

    public RfidLepIssueDto getRfidLepIssueModel() {
        return rfidLepIssueModel;
    }

    public void setRfidLepIssueModel(RfidLepIssueDto rfidLepIssueModel) {
        this.rfidLepIssueModel = rfidLepIssueModel;
    }

    public UserMasterDto getCoromandelLoadingSupervisor() {
        return coromandelLoadingSupervisor;
    }

    public void setCoromandelLoadingSupervisor(UserMasterDto coromandelLoadingSupervisor) {
        this.coromandelLoadingSupervisor = coromandelLoadingSupervisor;
    }

    public PinnacleLoadingSupervisorDto getPinnacleLoadingSupervisor() {
        return pinnacleLoadingSupervisor;
    }

    public void setPinnacleLoadingSupervisor(PinnacleLoadingSupervisorDto pinnacleLoadingSupervisor) {
        this.pinnacleLoadingSupervisor = pinnacleLoadingSupervisor;
    }

    public BothraLoadingSupervisorDto getBothraLoadingSupervisor() {
        return bothraLoadingSupervisor;
    }

    public void setBothraLoadingSupervisor(BothraLoadingSupervisorDto bothraLoadingSupervisor) {
        this.bothraLoadingSupervisor = bothraLoadingSupervisor;
    }

    public StorageLocationDto getSourceMaster() {
        return sourceMaster;
    }

    public void setSourceMaster(StorageLocationDto sourceMaster) {
        this.sourceMaster = sourceMaster;
    }

    public StorageLocationDto getFunctionalLocationDestinationMaster() {
        return functionalLocationDestinationMaster;
    }

    public void setFunctionalLocationDestinationMaster(StorageLocationDto functionalLocationDestinationMaster) {
        this.functionalLocationDestinationMaster = functionalLocationDestinationMaster;
    }

    public StorageLocationDto getPriviousWarehouse() {
        return priviousWarehouse;
    }

    public void setPriviousWarehouse(StorageLocationDto priviousWarehouse) {
        this.priviousWarehouse = priviousWarehouse;
    }

    public StorageLocationDto getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(StorageLocationDto warehouse) {
        this.warehouse = warehouse;
    }

    public GateMasterDto getGateInMaster() {
        return gateInMaster;
    }

    public void setGateInMaster(GateMasterDto gateInMaster) {
        this.gateInMaster = gateInMaster;
    }

    public GateMasterDto getGateOutMaster() {
        return gateOutMaster;
    }

    public void setGateOutMaster(GateMasterDto gateOutMaster) {
        this.gateOutMaster = gateOutMaster;
    }

    public WeighBridgeMasterDto getWeighBridgeGrossMaster() {
        return weighBridgeGrossMaster;
    }

    public void setWeighBridgeGrossMaster(WeighBridgeMasterDto weighBridgeGrossMaster) {
        this.weighBridgeGrossMaster = weighBridgeGrossMaster;
    }

    public WeighBridgeMasterDto getWeighBridgeTareMaster() {
        return weighBridgeTareMaster;
    }

    public void setWeighBridgeTareMaster(WeighBridgeMasterDto weighBridgeTareMaster) {
        this.weighBridgeTareMaster = weighBridgeTareMaster;
    }

    public WeighmentTypeDto getWeighmentTypeMaster() {
        return weighmentTypeMaster;
    }

    public void setWeighmentTypeMaster(WeighmentTypeDto weighmentTypeMaster) {
        this.weighmentTypeMaster = weighmentTypeMaster;
    }

    public RemarksDto getRemarkMaster() {
        return remarkMaster;
    }

    public void setRemarkMaster(RemarksDto remarkMaster) {
        this.remarkMaster = remarkMaster;
    }

    public String getLoadingTime() {
        return loadingTime;
    }

    public void setLoadingTime(String loadingTime) {
        this.loadingTime = loadingTime;
    }

    public BigDecimal getGrossWeight() {
        return grossWeight;
    }

    public void setGrossWeight(BigDecimal grossWeight) {
        this.grossWeight = grossWeight;
    }

    public String getGrossWeightTime() {
        return grossWeightTime;
    }

    public void setGrossWeightTime(String grossWeightTime) {
        this.grossWeightTime = grossWeightTime;
    }

    public Integer getGrossWtPrintCount() {
        return grossWtPrintCount;
    }

    public void setGrossWtPrintCount(Integer grossWtPrintCount) {
        this.grossWtPrintCount = grossWtPrintCount;
    }

    public BigDecimal getTareWeight() {
        return tareWeight;
    }

    public void setTareWeight(BigDecimal tareWeight) {
        this.tareWeight = tareWeight;
    }

    public Integer getTareWtPrintCount() {
        return tareWtPrintCount;
    }

    public void setTareWtPrintCount(Integer tareWtPrintCount) {
        this.tareWtPrintCount = tareWtPrintCount;
    }

    public BigDecimal getNetWeight() {
        return netWeight;
    }

    public void setNetWeight(BigDecimal netWeight) {
        this.netWeight = netWeight;
    }

    public String getNetWeightTime() {
        return netWeightTime;
    }

    public void setNetWeightTime(String netWeightTime) {
        this.netWeightTime = netWeightTime;
    }

    public BigDecimal getBothraGrossWeight() {
        return bothraGrossWeight;
    }

    public void setBothraGrossWeight(BigDecimal bothraGrossWeight) {
        this.bothraGrossWeight = bothraGrossWeight;
    }

    public String getBothraGrossWeightTime() {
        return bothraGrossWeightTime;
    }

    public void setBothraGrossWeightTime(String bothraGrossWeightTime) {
        this.bothraGrossWeightTime = bothraGrossWeightTime;
    }

    public Integer getBothraGrossWtPrintCount() {
        return bothraGrossWtPrintCount;
    }

    public void setBothraGrossWtPrintCount(Integer bothraGrossWtPrintCount) {
        this.bothraGrossWtPrintCount = bothraGrossWtPrintCount;
    }

    public BigDecimal getBothraTareWeight() {
        return bothraTareWeight;
    }

    public void setBothraTareWeight(BigDecimal bothraTareWeight) {
        this.bothraTareWeight = bothraTareWeight;
    }

    public Integer getBothraTareWtPrintCount() {
        return bothraTareWtPrintCount;
    }

    public void setBothraTareWtPrintCount(Integer bothraTareWtPrintCount) {
        this.bothraTareWtPrintCount = bothraTareWtPrintCount;
    }

    public BigDecimal getBothraNetWeight() {
        return bothraNetWeight;
    }

    public void setBothraNetWeight(BigDecimal bothraNetWeight) {
        this.bothraNetWeight = bothraNetWeight;
    }

    public String getBothraNetWeightTime() {
        return bothraNetWeightTime;
    }

    public void setBothraNetWeightTime(String bothraNetWeightTime) {
        this.bothraNetWeightTime = bothraNetWeightTime;
    }

    public String getVehicleInTime() {
        return vehicleInTime;
    }

    public void setVehicleInTime(String vehicleInTime) {
        this.vehicleInTime = vehicleInTime;
    }

    public String getVehicleOutTime() {
        return vehicleOutTime;
    }

    public void setVehicleOutTime(String vehicleOutTime) {
        this.vehicleOutTime = vehicleOutTime;
    }

    public String getWhSupervisorRemark() {
        return whSupervisorRemark;
    }

    public void setWhSupervisorRemark(String whSupervisorRemark) {
        this.whSupervisorRemark = whSupervisorRemark;
    }

    public String getUnloadingTime() {
        return unloadingTime;
    }

    public void setUnloadingTime(String unloadingTime) {
        this.unloadingTime = unloadingTime;
    }

    public Integer getTransactionFlag() {
        return transactionFlag;
    }

    public void setTransactionFlag(Integer transactionFlag) {
        this.transactionFlag = transactionFlag;
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
