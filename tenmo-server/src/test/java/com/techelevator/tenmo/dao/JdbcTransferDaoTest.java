package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.dao.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.dao.tenmo.model.Transfer;
import com.techelevator.tenmo.dao.tenmo.model.TransferView;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class JdbcTransferDaoTest extends TenMoDaoTest{

    private static final Transfer TRANSFER_1 = new Transfer(1l, 1l, 1l, 1l, 2l, BigDecimal.valueOf(100));
    private static final Transfer TRANSFER_2 = new Transfer(2l, 2l, 2l, 2l, 1l, BigDecimal.valueOf(200));
    private static final Transfer TRANSFER_3 = new Transfer(3l, 2l, 2l, 2l, 3l, BigDecimal.valueOf(200));
    private static final Transfer TRANSFER_4 = new Transfer(4l, 1l, 1l, 4l, 3l, BigDecimal.valueOf(200));
    private static final TransferView TRANSFER_VIEW_2 = new TransferView(2l, "test1", "To", "Send", "Approved", BigDecimal.valueOf(200));
    private static final TransferView FROM_TRANSFER_VIEW_1 = new TransferView(1l, "test1", "From", null, null, BigDecimal.valueOf(100));
    private static final TransferView TO_TRANSFER_VIEW_1 = new TransferView(2l, "test1", "To", null, null, BigDecimal.valueOf(200));
    private static final TransferView TO_TRANSFER_VIEW_3 = new TransferView(3l, "test3", "To", null, null, BigDecimal.valueOf(200));
    private static final TransferView PENDING_TRANSFER_VIEW_1 = new TransferView(1l, "test2", "To", null, null, BigDecimal.valueOf(100));


    private JdbcTransferDao sut;
    private Transfer transfer = new Transfer();
    private TransferView transferView;

    @Before
    public void setup() {
        sut = new JdbcTransferDao(new JdbcTemplate(dataSource));
        transfer.setTransferTypeId(2l);
        transfer.setTransferStatusId(2l);
        transfer.setFromAccountId(1l);
        transfer.setToAccountId(2l);
        transfer.setAmount(BigDecimal.valueOf(50));
    }

    @Test
    public void getTransferByTransferId_return_correct_for_right_transferId() {
        Transfer transfer = new Transfer();
        transfer = sut.getTransferByTransferId(2l);
        assertTransferMatch(TRANSFER_2, transfer);
    }

    @Test
    public void getTransferByTransferId_return_null_for_notExistTransferId() {
        Transfer transfer = new Transfer();
        transfer = sut.getTransferByTransferId(9l);
        assertTransferMatchNull(transfer);
    }

    @Test
    public void create_return_correct_transfer() {
         int number = sut.create(transfer);
         Assert.assertEquals(number, 1);
    }

    @Test
    public void getTransferDetails_return_correct_transferview() {
        List<TransferView> transferViewList = new ArrayList<>();
        transferViewList = sut.getTransferDetails(2, 2);
        assertTransferViewMatch(TRANSFER_VIEW_2, transferViewList.get(0));
    }

    @Test
    public void getTransferDetails_return_null_notexistid() {
        List<TransferView> transferViewList = new ArrayList<>();
        transferViewList = sut.getTransferDetails(2, 9);
        Assert.assertEquals(0, transferViewList.size());
    }

    @Test
    public void getFromTransferByUserId_return_correct_transferview() {
        List<TransferView> transferViewList = new ArrayList<>();
        transferViewList = sut.getFromTransferByUserId(2l);
        Assert.assertEquals(1, transferViewList.size());
        assertTransferViewMatch(FROM_TRANSFER_VIEW_1, transferViewList.get(0));
    }

    @Test
    public void getFromTransferByUserId_return_null_notexisttransferview() {
        List<TransferView> transferViewList = new ArrayList<>();
        transferViewList = sut.getFromTransferByUserId(9l);
        Assert.assertEquals(0, transferViewList.size());
    }

    @Test
    public void getToTransferByUserId_return_correct_transferview() {
        List<TransferView> transferViewList = new ArrayList<>();
        transferViewList = sut.getToTransferByUserId(2l);
        Assert.assertEquals(2, transferViewList.size());
        assertTransferViewMatch(TO_TRANSFER_VIEW_1, transferViewList.get(0));
        assertTransferViewMatch(TO_TRANSFER_VIEW_3, transferViewList.get(1));
    }

    @Test
    public void getToTransferByUserId_return_null_notexisttransferview() {
        List<TransferView> transferViewList = new ArrayList<>();
        transferViewList = sut.getToTransferByUserId(3l);
        Assert.assertEquals(0, transferViewList.size());
    }

    @Test
    public void getPendingList_return_correct_transferview() {
        List<TransferView> transferViewList = new ArrayList<>();
        transferViewList = sut.getPendingList(1l);
        Assert.assertEquals(1, transferViewList.size());
        assertTransferViewMatch(PENDING_TRANSFER_VIEW_1, transferViewList.get(0));
    }

    @Test
    public void getPendingList_return_null_notexistid() {
        List<TransferView> transferViewList = new ArrayList<>();
        transferViewList = sut.getPendingList(3l);
        Assert.assertEquals(0, transferViewList.size());
    }

    @Test
    public void updateApprove_return_correct_transfer() {
        Transfer updateTransfer1 = sut.getTransferByTransferId(4l);

        sut.updateApprove(updateTransfer1.getTransferId());

        Transfer transfer1 = TRANSFER_4;
        transfer1.setTransferStatusId(2l);

        assertTransferMatch(transfer1, sut.getTransferByTransferId(4l));

    }

    @Test
    public void updateReject_return_correct_transfer() {
        Transfer updateTransfer1 = sut.getTransferByTransferId(1l);

        sut.updateReject(updateTransfer1.getTransferId());

        Transfer transfer1 = TRANSFER_1;
        transfer1.setTransferStatusId(3l);

        assertTransferMatch(transfer1, sut.getTransferByTransferId(1l));
    }



    private void assertTransferMatch(Transfer expected, Transfer actual) {
        Assert.assertEquals(expected.getTransferId(), actual.getTransferId());
        Assert.assertEquals(expected.getTransferTypeId(), actual.getTransferTypeId());
        Assert.assertEquals(expected.getTransferStatusId(), actual.getTransferStatusId());
        Assert.assertEquals(expected.getFromAccountId(), actual.getFromAccountId());
        Assert.assertEquals(expected.getToAccountId(), actual.getToAccountId());
        Assert.assertEquals(expected.getFromUserId(), actual.getFromUserId());
        Assert.assertEquals(expected.getToUserId(), actual.getToUserId());
        Assert.assertEquals(expected.getAmount().doubleValue(), actual.getAmount().doubleValue(), 0.01);
    }

    private void assertTransferMatchNull(Transfer actual) {
        Assert.assertEquals(null, actual.getTransferId());
        Assert.assertEquals(null, actual.getTransferTypeId());
        Assert.assertEquals(null, actual.getTransferStatusId());
        Assert.assertEquals(null, actual.getFromAccountId());
        Assert.assertEquals(null, actual.getToAccountId());
        Assert.assertEquals(null, actual.getFromUserId());
        Assert.assertEquals(null, actual.getToUserId());
        Assert.assertEquals(null, actual.getAmount());
    }

    private void assertTransferViewMatch(TransferView expected, TransferView actual) {
        Assert.assertEquals(expected.getTransferId(), actual.getTransferId());
        Assert.assertEquals(expected.getTransferStatus(), actual.getTransferStatus());
        Assert.assertEquals(expected.getTransferType(), actual.getTransferType());
        Assert.assertEquals(expected.getUsername(), actual.getUsername());
        Assert.assertEquals(expected.getUserType(), actual.getUserType());
        Assert.assertEquals(expected.getAmount().doubleValue(), actual.getAmount().doubleValue(), 0.01);
    }
}
