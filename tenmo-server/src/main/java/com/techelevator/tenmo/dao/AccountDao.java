package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.math.BigDecimal;
import java.util.List;

public interface AccountDao {
    Account getByAccountId(Long userId);
    void updateAccount(Long fromUserId, Long toUserId, BigDecimal money);
}
