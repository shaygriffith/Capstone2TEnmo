package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferView;

import java.util.List;

public interface TransferDao {
    void create(Transfer transfer);
    List<TransferView>  getFromTransferByUserId(long userId);
    List<TransferView>  getToTransferByUserId(long userId);
}
