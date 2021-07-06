TRUNCATE users, accounts, transfers CASCADE;

-- Need a users so we can add users
INSERT INTO users (user_id, username, password_hash)
VALUES (1, 'test1', 'null'),
       (2, 'test2', 'null'),
       (3, 'test3', 'null'),
       (4, 'test4', 'null');

-- Need account so we can add account
INSERT INTO accounts (account_id, user_id, balance)
VALUES (1, 1, 1000),
       (2, 2, 1000),
       (3, 3, 1000),
       (4, 4, 1000);

-- Need transfers so we can add transfers
INSERT INTO transfers (transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount)
VALUES (1, 1, 1, 1, 2, 100),
       (2, 2, 2, 2, 1, 200),
       (3, 2, 2, 2, 3, 200),
       (4, 1, 1, 4, 3, 200);

--ALTER SEQUENCE transfers_transfer_id_sequence RESTART WITH 1000;