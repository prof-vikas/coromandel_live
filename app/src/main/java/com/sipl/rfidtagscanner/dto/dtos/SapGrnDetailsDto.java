package com.sipl.rfidtagscanner.dto.dtos;

import java.math.BigDecimal;

public class SapGrnDetailsDto {

    private Integer id;
    private String plant;
    private String plantName;
    private String grnNo;
    private String elementCode;
    private String element;
    private String description;
    private BigDecimal grnQty;
    private BigDecimal balanceQty;
    private String uom;
    private String batch;
    private String grSloc;
    private String grDesc;
    private String vesselName;
    private String billOfLoading;
    private String billOfEntry;
    private String billOfDate;
    private String ewayBillNo;
    private String poNo;
    private String poDate;

    public SapGrnDetailsDto(Integer id, String plant, String plantName, String grnNo, String elementCode, String element, String description, BigDecimal grnQty, BigDecimal balanceQty, String uom, String batch, String grSloc, String grDesc, String vesselName, String billOfLoading, String billOfEntry, String billOfDate, String ewayBillNo, String poNo, String poDate) {
        this.id = id;
        this.plant = plant;
        this.plantName = plantName;
        this.grnNo = grnNo;
        this.elementCode = elementCode;
        this.element = element;
        this.description = description;
        this.grnQty = grnQty;
        this.balanceQty = balanceQty;
        this.uom = uom;
        this.batch = batch;
        this.grSloc = grSloc;
        this.grDesc = grDesc;
        this.vesselName = vesselName;
        this.billOfLoading = billOfLoading;
        this.billOfEntry = billOfEntry;
        this.billOfDate = billOfDate;
        this.ewayBillNo = ewayBillNo;
        this.poNo = poNo;
        this.poDate = poDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPlant() {
        return plant;
    }

    public void setPlant(String plant) {
        this.plant = plant;
    }

    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }

    public String getGrnNo() {
        return grnNo;
    }

    public void setGrnNo(String grnNo) {
        this.grnNo = grnNo;
    }

    public String getElementCode() {
        return elementCode;
    }

    public void setElementCode(String elementCode) {
        this.elementCode = elementCode;
    }

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getGrnQty() {
        return grnQty;
    }

    public void setGrnQty(BigDecimal grnQty) {
        this.grnQty = grnQty;
    }

    public BigDecimal getBalanceQty() {
        return balanceQty;
    }

    public void setBalanceQty(BigDecimal balanceQty) {
        this.balanceQty = balanceQty;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getGrSloc() {
        return grSloc;
    }

    public void setGrSloc(String grSloc) {
        this.grSloc = grSloc;
    }

    public String getGrDesc() {
        return grDesc;
    }

    public void setGrDesc(String grDesc) {
        this.grDesc = grDesc;
    }

    public String getVesselName() {
        return vesselName;
    }

    public void setVesselName(String vesselName) {
        this.vesselName = vesselName;
    }

    public String getBillOfLoading() {
        return billOfLoading;
    }

    public void setBillOfLoading(String billOfLoading) {
        this.billOfLoading = billOfLoading;
    }

    public String getBillOfEntry() {
        return billOfEntry;
    }

    public void setBillOfEntry(String billOfEntry) {
        this.billOfEntry = billOfEntry;
    }

    public String getBillOfDate() {
        return billOfDate;
    }

    public void setBillOfDate(String billOfDate) {
        this.billOfDate = billOfDate;
    }

    public String getEwayBillNo() {
        return ewayBillNo;
    }

    public void setEwayBillNo(String ewayBillNo) {
        this.ewayBillNo = ewayBillNo;
    }

    public String getPoNo() {
        return poNo;
    }

    public void setPoNo(String poNo) {
        this.poNo = poNo;
    }

    public String getPoDate() {
        return poDate;
    }

    public void setPoDate(String poDate) {
        this.poDate = poDate;
    }
}
