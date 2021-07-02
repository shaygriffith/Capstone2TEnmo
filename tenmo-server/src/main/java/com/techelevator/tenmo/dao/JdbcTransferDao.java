package com.techelevator.tenmo.dao;

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
    public void updateApprove(long transferId) {
        String sql = "update transfers set transfer_status_id = 2 where transfer_id = ?;";
        jdbcTemplate.update(sql, transferId);
    }

    @Override
    public Transfer getTransferByTransferId(long transferId) {
        Transfer transfer = new Transfer();
        String sql = "select * from transfers where transfer_id = ?;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, transferId);
        if (result.next()) {
            transfer = mapRowToTransfer(result);
        }
        return transfer;
    }

    @Override
    public void updateReject(long transferId) {
        String sql = "update transfers set transfer_status_id = 3 where transfer_id = ?;";
        jdbcTemplate.update(sql, transferId);
    }

    @Override
    public List<TransferView> getPendingList(long userId) {
        List<TransferView> pendingList = new ArrayList<>();

        String sql = "select totalTable.transfer_id, totalTable.username, 'To' as usertype, totalTable.amount from " +
                "(select * from users " +
                "join accounts on (users.user_id = accounts.user_id) " +
                "join transfers on (transfers.account_to = accounts.account_id)) as totalTable " +
                "where totalTable.account_from = (select account_id from accounts where user_id = ?) and totalTable.transfer_status_id = 1;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
        while (results.next()) {
            pendingList.add(mapRowToTransferView(results));
        }
        return pendingList;
    }

    @Override
    public void create(Transfer transfer) {
        String sql = "insert into transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount) values (?,?,?,?,?);";
        jdbcTemplate.update(sql, transfer.getTransferTypeId(), transfer.getTransferStatusId(), transfer.getFromAccountId(), transfer.getToAccountId(), transfer.getAmount());
    }

    @Override
    public List<TransferView> getTransferDetails(long userId, long transferId) {
        List<TransferView> transferDetails = new ArrayList<>();
        String fromSql = "select  totalTable.transfer_id, totalTable.username, 'From' as usertype, totalTable.amount, totalTable.transfer_type_desc, totalTable.transfer_status_desc from " +
                "(select * from users " +
                "join accounts on (users.user_id = accounts.user_id) " +
                "join transfers on (transfers.account_from = accounts.account_id) " +
                "join transfer_types using (transfer_type_id) " +
                "join transfer_statuses using (transfer_status_id)) as totalTable " +
                "where totalTable.account_to = (select account_id from accounts where user_id = ?) and totalTable.transfer_id = ?;";
        SqlRowSet fromResults = jdbcTemplate.queryForRowSet(fromSql, userId, transferId);
        while (fromResults.next()) {
            transferDetails.add(mapRowToTransferViewDetails(fromResults));
        }

        String toSql = "select  totalTable.transfer_id, totalTable.username, 'To' as usertype, totalTable.amount, totalTable.transfer_type_desc, totalTable.transfer_status_desc from " +
                "(select * from users " +
                "join accounts on (users.user_id = accounts.user_id) " +
                "join transfers on (transfers.account_to = accounts.account_id) " +
                "join transfer_types using (transfer_type_id) " +
                "join transfer_statuses using (transfer_status_id)) as totalTable " +
                "where totalTable.account_from = (select account_id from accounts where user_id = ?) and totalTable.transfer_id = ?;";
        SqlRowSet toResults = jdbcTemplate.queryForRowSet(toSql, userId, transferId);
        while (toResults.next()) {
            transferDetails.add(mapRowToTransferViewDetails(toResults));
        }

        return transferDetails;
    }

    @Override
    public List<TransferView> getFromTransferByUserId(long userId) {
        List<TransferView> fromList = new ArrayList<>();

        String sql = "select totalTable.transfer_id, totalTable.username, 'From' as usertype, totalTable.amount from " +
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
        String sql = "select totalTable.transfer_id, totalTable.username, 'To' as usertype, totalTable.amount from " +
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

    private Transfer mapRowToTransfer(SqlRowSet rs) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(rs.getLong("transfer_id"));
        transfer.setTransferTypeId(rs.getLong("transfer_type_id"));
        transfer.setTransferStatusId(rs.getLong("transfer_status_id"));
        transfer.setFromAccountId(rs.getLong("account_from"));
        transfer.setToAccountId(rs.getLong("account_to"));
        transfer.setAmount(rs.getBigDecimal("amount"));
        return transfer;
    }

    private TransferView mapRowToTransferView(SqlRowSet rs) {
        TransferView transferView = new TransferView();
        transferView.setTransferId(rs.getLong("transfer_id"));
        transferView.setUsername(rs.getString("username"));
        transferView.setUserType(rs.getString("usertype"));
        transferView.setAmount(rs.getBigDecimal("amount"));
        return transferView;
    }

    private TransferView mapRowToTransferViewDetails(SqlRowSet rs) {
        TransferView transferView = new TransferView();
        transferView.setTransferId(rs.getLong("transfer_id"));
        transferView.setUsername(rs.getString("username"));
        transferView.setUserType(rs.getString("usertype"));
        transferView.setTransferType(rs.getString("transfer_type_desc"));
        transferView.setTransferStatus(rs.getString("transfer_status_desc"));
        transferView.setAmount(rs.getBigDecimal("amount"));
        return transferView;
    }
}
