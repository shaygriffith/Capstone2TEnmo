package com.techelevator.tenmo.dao;


import com.techelevator.tenmo.dao.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.dao.tenmo.model.Account;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.math.BigDecimal;

public class JdbcAccountDaoTest extends TenMoDaoTest {

    private static final Account ACCOUNT_1 = new Account(1L,1L, BigDecimal.valueOf(1000));
    private static final Account ACCOUNT_2 = new Account(2L,2L, BigDecimal.valueOf(1000));
    private static final Account ACCOUNT_3 = new Account(3L,3L, BigDecimal.valueOf(1000));
    private static final Account ACCOUNT_4 = new Account(4L,4L, BigDecimal.valueOf(1000));


    private JdbcAccountDao sut;
    private Account account;

    @Before
    public void setup() {
        sut = new JdbcAccountDao(new JdbcTemplate(dataSource));
    }

    @Test
    public void getByAccountId_return_correct_for_userId() {
        Account account = sut.getByAccountId(3L);
        assertAccountMatch(ACCOUNT_3, account);
    }

    @Test
    public void getByAccountId_return_null_for_noexistId() {
        Account account = sut.getByAccountId(9L);
        assertAccountMatchNull(account);
    }

    @Test
    public void getAccountBytransferId_return_correct_for_transferId() {
        Account account = sut.getAccountBytransferId(3L);
        assertAccountMatch(ACCOUNT_3, account);
    }

    @Test
    public void getAccountBytransferId_return_null_for_noexist_transferId() {
        Account account = sut.getAccountBytransferId(9L);
        assertAccountMatchNull(account);
        //Assert.assertNull(account);
    }

    @Test
    public void updateAccountById_return_correct_for_accountId() {
        Account updateAccount1 = sut.getByAccountId(1l);
        Account updateAccount2 = sut.getByAccountId(2l);

        sut.updateAccountById(updateAccount1.getAccount_id(), updateAccount2.getAccount_id(), new BigDecimal(100));

        Account account1 = ACCOUNT_1;
        account1.setBalance(BigDecimal.valueOf(900));
        Account account2 = ACCOUNT_2;
        account2.setBalance(BigDecimal.valueOf(1100));

        assertAccountMatch(account1, sut.getByAccountId(1l));
        assertAccountMatch(account2, sut.getByAccountId(2l));
    }

    @Test
    public void updateAccountByUserId_return_correct_for_userId() {
        Account updateAccount1 = sut.getByAccountId(1l);
        Account updateAccount2 = sut.getByAccountId(2l);

        sut.updateAccountById(updateAccount1.getUser_id(), updateAccount2.getUser_id(), new BigDecimal(200));

        Account account1 = ACCOUNT_1;
        account1.setBalance(BigDecimal.valueOf(800));
        Account account2 = ACCOUNT_2;
        account2.setBalance(BigDecimal.valueOf(1200));

        assertAccountMatch(account1, sut.getByAccountId(1l));
        assertAccountMatch(account2, sut.getByAccountId(2l));
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
