package com.sipl.rfidtagscanner.dto.response;

import com.sipl.rfidtagscanner.dto.dtos.TransactionsDto;

import java.util.List;

public class TransactionsApiResponse {

    private Boolean error;
    private String message;
    private String status;
    private TransactionsDto transactionsDto;
    private String transactionsDtoPage;
    private List<TransactionsDto> transactionsDtos;

    public TransactionsApiResponse(Boolean error, String message, String status, TransactionsDto transactionsDto, String transactionsDtoPage, List<TransactionsDto> transactionsDtos) {
        this.error = error;
        this.message = message;
        this.status = status;
        this.transactionsDto = transactionsDto;
        this.transactionsDtoPage = transactionsDtoPage;
        this.transactionsDtos = transactionsDtos;
    }

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public TransactionsDto getTransactionsDto() {
        return transactionsDto;
    }

    public void setTransactionsDto(TransactionsDto transactionsDto) {
        this.transactionsDto = transactionsDto;
    }

    public String getTransactionsDtoPage() {
        return transactionsDtoPage;
    }

    public void setTransactionsDtoPage(String transactionsDtoPage) {
        this.transactionsDtoPage = transactionsDtoPage;
    }

    public List<TransactionsDto> getTransactionsDtos() {
        return transactionsDtos;
    }

    public void setTransactionsDtos(List<TransactionsDto> transactionsDtos) {
        this.transactionsDtos = transactionsDtos;
    }
}
