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
    private Integer grossWeight;
    private String grossWeightTime;
    private Integer grossWtPrintCount;
    private BigDecimal tareWeight;
    private Integer tareWtPrintCount;
    private BigDecimal netWeight;
    private String netWeightTime;
    private Integer sourceGrossWeight;
    private String sourceGrossWeightTime;
    private Integer sourceGrossWtPrintCount;
    private BigDecimal sourceTareWeight;
    private Integer sourceTareWtPrintCount;
    private BigDecimal sourceNetWeight;
    private String sourceNetWeightTime;
    private String weighBridgeNo;
    private String vehicleInTime;
    private String vehicleOutTime;
    private String whSupervisorRemark;
    private String unloadingTime;
    private Integer transactionFlag;
    private Boolean isActive;
    private Integer rStat;
    private AuditEntity auditEntity;
    private String tareWeightTime;
    private String sourceTareWeightTime;
    private String strBothraLoadingSupervisor;
    private String strPinnacleLoadingSupervisor;
    private String inLoadingTime;
    private String outLoadingTime;
    private String inUnLoadingTime;
    private String outUnLoadingTime;

    public TransactionsDto(Integer id, RfidLepIssueDto rfidLepIssueModel, UserMasterDto coromandelLoadingSupervisor, PinnacleLoadingSupervisorDto pinnacleLoadingSupervisor, BothraLoadingSupervisorDto bothraLoadingSupervisor, StorageLocationDto sourceMaster, StorageLocationDto functionalLocationDestinationMaster, StorageLocationDto priviousWarehouse, StorageLocationDto warehouse, GateMasterDto gateInMaster, GateMasterDto gateOutMaster, WeighBridgeMasterDto weighBridgeGrossMaster, WeighBridgeMasterDto weighBridgeTareMaster, WeighmentTypeDto weighmentTypeMaster, RemarksDto remarkMaster, String loadingTime, Integer grossWeight, String grossWeightTime, Integer grossWtPrintCount, BigDecimal tareWeight, Integer tareWtPrintCount, BigDecimal netWeight, String netWeightTime, Integer sourceGrossWeight, String sourceGrossWeightTime, Integer sourceGrossWtPrintCount, BigDecimal sourceTareWeight, Integer sourceTareWtPrintCount, BigDecimal sourceNetWeight, String sourceNetWeightTime, String weighBridgeNo, String vehicleInTime, String vehicleOutTime, String whSupervisorRemark, String unloadingTime, Integer transactionFlag, Boolean isActive, Integer rStat, AuditEntity auditEntity, String tareWeightTime, String sourceTareWeightTime, String strBothraLoadingSupervisor, String strPinnacleLoadingSupervisor, String inLoadingTime, String outLoadingTime, String inUnLoadingTime, String outUnLoadingTime) {
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
        this.sourceGrossWeight = sourceGrossWeight;
        this.sourceGrossWeightTime = sourceGrossWeightTime;
        this.sourceGrossWtPrintCount = sourceGrossWtPrintCount;
        this.sourceTareWeight = sourceTareWeight;
        this.sourceTareWtPrintCount = sourceTareWtPrintCount;
        this.sourceNetWeight = sourceNetWeight;
        this.sourceNetWeightTime = sourceNetWeightTime;
        this.weighBridgeNo = weighBridgeNo;
        this.vehicleInTime = vehicleInTime;
        this.vehicleOutTime = vehicleOutTime;
        this.whSupervisorRemark = whSupervisorRemark;
        this.unloadingTime = unloadingTime;
        this.transactionFlag = transactionFlag;
        this.isActive = isActive;
        this.rStat = rStat;
        this.auditEntity = auditEntity;
        this.tareWeightTime = tareWeightTime;
        this.sourceTareWeightTime = sourceTareWeightTime;
        this.strBothraLoadingSupervisor = strBothraLoadingSupervisor;
        this.strPinnacleLoadingSupervisor = strPinnacleLoadingSupervisor;
        this.inLoadingTime = inLoadingTime;
        this.outLoadingTime = outLoadingTime;
        this.inUnLoadingTime = inUnLoadingTime;
        this.outUnLoadingTime = outUnLoadingTime;
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

    public Integer getGrossWeight() {
        return grossWeight;
    }

    public void setGrossWeight(Integer grossWeight) {
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

    public Integer getSourceGrossWeight() {
        return sourceGrossWeight;
    }

    public void setSourceGrossWeight(Integer sourceGrossWeight) {
        this.sourceGrossWeight = sourceGrossWeight;
    }

    public String getSourceGrossWeightTime() {
        return sourceGrossWeightTime;
    }

    public void setSourceGrossWeightTime(String sourceGrossWeightTime) {
        this.sourceGrossWeightTime = sourceGrossWeightTime;
    }

    public Integer getSourceGrossWtPrintCount() {
        return sourceGrossWtPrintCount;
    }

    public void setSourceGrossWtPrintCount(Integer sourceGrossWtPrintCount) {
        this.sourceGrossWtPrintCount = sourceGrossWtPrintCount;
    }

    public BigDecimal getSourceTareWeight() {
        return sourceTareWeight;
    }

    public void setSourceTareWeight(BigDecimal sourceTareWeight) {
        this.sourceTareWeight = sourceTareWeight;
    }

    public Integer getSourceTareWtPrintCount() {
        return sourceTareWtPrintCount;
    }

    public void setSourceTareWtPrintCount(Integer sourceTareWtPrintCount) {
        this.sourceTareWtPrintCount = sourceTareWtPrintCount;
    }

    public BigDecimal getSourceNetWeight() {
        return sourceNetWeight;
    }

    public void setSourceNetWeight(BigDecimal sourceNetWeight) {
        this.sourceNetWeight = sourceNetWeight;
    }

    public String getSourceNetWeightTime() {
        return sourceNetWeightTime;
    }

    public void setSourceNetWeightTime(String sourceNetWeightTime) {
        this.sourceNetWeightTime = sourceNetWeightTime;
    }

    public String getWeighBridgeNo() {
        return weighBridgeNo;
    }

    public void setWeighBridgeNo(String weighBridgeNo) {
        this.weighBridgeNo = weighBridgeNo;
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

    public String getTareWeightTime() {
        return tareWeightTime;
    }

    public void setTareWeightTime(String tareWeightTime) {
        this.tareWeightTime = tareWeightTime;
    }

    public String getSourceTareWeightTime() {
        return sourceTareWeightTime;
    }

    public void setSourceTareWeightTime(String sourceTareWeightTime) {
        this.sourceTareWeightTime = sourceTareWeightTime;
    }

    public String getStrBothraLoadingSupervisor() {
        return strBothraLoadingSupervisor;
    }

    public void setStrBothraLoadingSupervisor(String strBothraLoadingSupervisor) {
        this.strBothraLoadingSupervisor = strBothraLoadingSupervisor;
    }

    public String getStrPinnacleLoadingSupervisor() {
        return strPinnacleLoadingSupervisor;
    }

    public void setStrPinnacleLoadingSupervisor(String strPinnacleLoadingSupervisor) {
        this.strPinnacleLoadingSupervisor = strPinnacleLoadingSupervisor;
    }

    public String inLoadingTime() {
        return inLoadingTime;
    }

    public void setInLoadingTime(String inLoadingTime) {
        this.inLoadingTime = inLoadingTime;
    }

    public String getOutLoadingTime() {
        return outLoadingTime;
    }

    public void setOutLoadingTime(String outLoadingTime) {
        this.outLoadingTime = outLoadingTime;
    }

    public String getInUnLoadingTime() {
        return inUnLoadingTime;
    }

    public void setInUnLoadingTime(String inUnLoadingTime) {
        this.inUnLoadingTime = inUnLoadingTime;
    }

    public String getOutUnLoadingTime() {
        return outUnLoadingTime;
    }

    public void setOutUnLoadingTime(String outUnLoadingTime) {
        this.outUnLoadingTime = outUnLoadingTime;
    }
}