package com.techelevator.tenmo.dao;


import com.techelevator.tenmo.dao.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.dao.tenmo.model.Account;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

public class JdbcAccountDaoTest extends TenMoDaoTest {

    private static final Account ACCOUNT_1 = new Account(1L,1L, BigDecimal.valueOf(1000));
    private static final Account ACCOUNT_2 = new Account(2L,2L, BigDecimal.valueOf(1000));

    private JdbcAccountDao sut;
    private Account account;

    @Before
    public void setup() {
        sut = new JdbcAccountDao(dataSource);
    }

    @Test
    public void getByAccountId_return_correct_for_userId() {
        Account account = sut.getByAccountId(1L);
        assertAccountMatch(ACCOUNT_1, account);
    }

    @Test
    public void getByAccountId_return_null_for_noexistId() {
        Account account = sut.getByAccountId(3L);
        assertAccountMatchNull(account);
    }

    @Test
    public void getAccountBytransferId_return_correct_for_transferId() {
        Account account = sut.getAccountBytransferId(1L);
        assertAccountMatch(ACCOUNT_2, account);
    }

    @Test
    public void getAccountBytransferId_return_null_for_noexist_transferId() {
        Account account = sut.getAccountBytransferId(3L);
        assertAccountMatchNull(account);
        //Assert.assertNull(account);
    }

    private void assertAccountMatch(Account expected, Account actual) {
        Assert.assertEquals(expected.getAccount_id(), actual.getAccount_id());
        Assert.assertEquals(expected.getUser_id(), actual.getUser_id());
        Assert.assertEquals(expected.getBalance().doubleValue(), actual.getBalance().doubleValue(), 0.01);
    }

    private void assertAccountMatchNull(Account actual) {
        Assert.assertEquals(null, actual.getAccount_id());
        Assert.assertEquals(null, actual.getUser_id());
        Assert.assertEquals(null, actual.getBalance());
    }
}
