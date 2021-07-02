package com.techelevator;


import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.event.annotation.AfterTestClass;
import org.springframework.test.context.event.annotation.BeforeTestClass;

import java.io.IOException;
import java.sql.SQLException;

public class JdbcAccountDaoTests {

    public static SingleConnectionDataSource dataSource;

    @BeforeTestClass
    public static void setupDataSource() {
        dataSource = new SingleConnectionDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:8080/Accounts");
        dataSource.setUsername("postgres");
        dataSource.setPassword("postgres1");
        dataSource.setAutoCommit(false);

    }

    @Before
    public void loadTestData() throws IOException, SQLException {
        ScriptUtils.executeSqlScript(dataSource.getConnection(), new ClassPathResource("test-data.sql"));

    }

    @After
    public void rollback() throws SQLException {
        dataSource.getConnection().rollback();

    }

    @AfterTestClass
    public static void closeDataSource() {
        dataSource.destroy();
    }




}


