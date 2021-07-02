TRUNCATE accounts, users, transfers CASCADE;

INSERT INTO accounts (account_id, user_id)
VALUES (12345, 123)
        (56789, 456)
         (98765, 789);

INSERT INTO users (user_id, username)
VALUES (123, "ShayShay")
        (456, "Yuan")
        (789, "Formario");

INSERT INTO transfers (transfer_id, account_from, account_to)
VALUES (321, 12345, 56789 )
        (654, 12345, 98765)
        (987, 56789, 12345)
        (135, 56789, 98765);

ALTER SEQUENCE transfers_transfers_id_seq RESTART WITH 0;