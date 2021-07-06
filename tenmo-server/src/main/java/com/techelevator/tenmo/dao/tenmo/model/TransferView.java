package com.techelevator.tenmo.dao.tenmo.model;

import java.math.BigDecimal;
/**
 * This class is for storing transfer view object
 * */
public class TransferView {
    private Long transferId;
    private String username;
    private String userType;
    private String transferType;
    private String transferStatus;
    private BigDecimal amount;

    public TransferView() {

    }

    public TransferView(Long transferId, String username, String userType, String transferType, String transferStatus, BigDecimal amount) {
        this.transferId = transferId;
        this.username = username;
        this.userType = userType;
        this.transferType = transferType;
        this.transferStatus = transferStatus;
        this.amount = amount;
    }

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }

    public String getTransferStatus() {
        return transferStatus;
    }

    public void setTransferStatus(String transferStatus) {
        this.transferStatus = transferStatus;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public Long getTransferId() {
        return transferId;
    }

    public void setTransferId(Long transferId) {
        this.transferId = transferId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
