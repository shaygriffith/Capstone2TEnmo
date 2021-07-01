package com.techelevator.tenmo.model;

import java.math.BigDecimal;
/**
 * This class is for storing transfer view object
 * */
public class TransferView {
    private Long transferId;
    private String username;
    private BigDecimal amount;

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
