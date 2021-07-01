package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferView;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao{
    private JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void create(Transfer transfer) {
        String sql = "insert into transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount) values (?,?,?,?,?);";
        jdbcTemplate.update(sql, transfer.getTransferTypeId(), transfer.getTransferStatusId(), transfer.getFromAccountId(), transfer.getToAccountId(), transfer.getAmount());
    }

    @Override
    public List<TransferView> getFromTransferByUserId(long userId) {
        List<TransferView> fromList = new ArrayList<>();

        String sql = "select totalTable.transfer_id, totalTable.username, totalTable.amount from " +
                "(select * from users " +
                "join accounts on (users.user_id = accounts.user_id) " +
                "join transfers on (transfers.account_from = accounts.account_id)) as totalTable " +
                "where totalTable.account_to = (select account_id from accounts where user_id = ?);";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
        while (results.next()) {
            fromList.add(mapRowToTransferView(results));
        }
        return fromList;
    }

    @Override
    public List<TransferView> getToTransferByUserId(long userId) {
        List<TransferView> toList = new ArrayList<>();
        String sql = "select totalTable.transfer_id, totalTable.username, totalTable.amount from " +
                "(select * from users " +
                "join accounts on (users.user_id = accounts.user_id) " +
                "join transfers on (transfers.account_to = accounts.account_id)) as totalTable " +
                "where totalTable.account_from = (select account_id from accounts where user_id = ?);";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
        while (results.next()) {
            toList.add(mapRowToTransferView(results));
        }
        return toList;
    }

    private TransferView mapRowToTransferView(SqlRowSet rs) {
        TransferView transferView = new TransferView();
        transferView.setTransferId(rs.getLong("transfer_id"));
        transferView.setUsername(rs.getString("username"));
        transferView.setAmount(rs.getBigDecimal("amount"));
        return transferView;
    }
}
