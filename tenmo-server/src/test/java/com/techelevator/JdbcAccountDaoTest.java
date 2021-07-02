package com.techelevator;

import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.model.Account;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;

public class JdbcAccountDaoTest extends AccountDaoTest{

    private static final Account ACCOUNT_1 = new Account(12345L, 123L, new BigDecimal(1000));
    private static final Account ACCOUNT_2 = new Account(56789L, 456L, new BigDecimal(500));


private JdbcAccountDao dao;
private Account testAccount;

@Before
    public void setup() {
    dao = new JdbcAccountDao(new JdbcTemplate(dataSource));
    testAccount = new Account(99L, 2L, new BigDecimal(200));

}

@Test
    public void getAccount_returns_correct_user_id(){
    Account account = dao.getByAccountId(1L);
    Assert.assertNotNull("getAccount returned null", account);
    assertIdsMatch("getAccount returned wrong or partial data", ACCOUNT_1, account);

    account = dao.getByAccountId(2L);
    Assert.assertNotNull("getAccount returned null", account);
    assertIdsMatch("getAccount returned wrong or partial data", ACCOUNT_2, account);
}

@Test
public void getAccount_returns_null_when_id_not_found(){
    Account account = dao.getByAccountId(9999L);
    Assert.assertNull("GetAccount failed to return null for id not in database", account);
}


private void assertIdsMatch(String message, Account expected, Account actual){
    Assert.assertEquals(message, expected.getAccount_id(), actual.getAccount_id());
    Assert.assertEquals(message, expected.getUser_id(), actual.getUser_id());
    Assert.assertEquals(message, expected.getBalance(), actual.getBalance());
}

}
