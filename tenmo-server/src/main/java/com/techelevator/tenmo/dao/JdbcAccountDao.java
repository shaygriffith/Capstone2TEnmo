package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;


@Component
public class JdbcAccountDao implements AccountDao{

    private JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void updateAccountById(Long fromAccountId, Long toAccountId, BigDecimal money) {
        String sql = "update accounts set balance = case account_id when ? then balance - ? when ? then balance + ? end where account_id in (?,?);";
        jdbcTemplate.update(sql, fromAccountId, money, toAccountId, money, fromAccountId, toAccountId);
    }

    @Override
    public Account getAccountBytransferId(Long transferId) {
        Account account = new Account();
        String sql = "select * from accounts where account_id = " +
                "(select account_to from transfers where transfer_id = ?);";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, transferId);
        while (result.next()) {
            account = mapRowToAccount(result);
        }
        return account;
    }

    @Override
    public Account getByAccountId(Long userId) {
        Account account = new Account();
        String sql = "select * from accounts where user_id = ?;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, userId);
        while (result.next()) {
            account = mapRowToAccount(result);
        }
        return account;
    }

    @Override
    public void updateAccount(Long fromUserId, Long toUserId, BigDecimal money) {
        String sql = "update accounts set balance = case user_id when ? then balance - ? when ? then balance + ? end where user_id in (?,?);";
        jdbcTemplate.update(sql, fromUserId, money, toUserId, money, fromUserId, toUserId);
    }

    private Account mapRowToAccount(SqlRowSet rs) {
        Account account = new Account();
        account.setAccount_id(rs.getLong("account_id"));
        account.setUser_id(rs.getLong("user_id"));
        account.setBalance(rs.getBigDecimal("balance"));
        return account;
    }
}
