package com.techelevator.tenmo.dao.tenmo.dao;

import com.techelevator.tenmo.dao.tenmo.model.Transfer;
import com.techelevator.tenmo.dao.tenmo.model.TransferView;

import java.util.List;

public interface TransferDao {
    int create(Transfer transfer);
    List<TransferView> getFromTransferByUserId(long userId);
    List<TransferView> getToTransferByUserId(long userId);
    List<TransferView> getTransferDetails(long userId, long transferId);
    List<TransferView> getPendingList(long userId);
    void updateReject(long transferId);
    Transfer getTransferByTransferId(long transferId);
    void updateApprove(long transferId);
}
