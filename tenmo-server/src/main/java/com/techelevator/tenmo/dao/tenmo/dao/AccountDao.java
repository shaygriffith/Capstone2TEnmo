package com.techelevator.tenmo.dao.tenmo.dao;

import com.techelevator.tenmo.dao.tenmo.model.Account;

import java.math.BigDecimal;


public interface AccountDao {
    Account getByAccountId(Long userId);
    void updateAccount(Long fromUserId, Long toUserId, BigDecimal money);
    Account getAccountBytransferId(Long transferId);
    void updateAccountById(Long fromAccountId, Long toAccountId, BigDecimal money);

}
