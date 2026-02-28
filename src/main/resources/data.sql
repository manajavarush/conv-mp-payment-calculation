-- Очистка таблиц (без проверки FK-порядка)
DELETE FROM tbl_client_payment;
DELETE FROM tbl_client_balance;
DELETE FROM tbl_client;

-- Сброс последовательностей (если используются)
-- Если последовательности не существуют — строки не повредят (Postgres выдаст ошибку),
-- в таком случае создать sequences перед запуском или закомментировать эти строки.
SELECT setval('balance_id_seq', 100, true);
SELECT setval('payment_id_seq', 1000, true);

---------------------------------------------------------------
-- Клиенты (3)
---------------------------------------------------------------
INSERT INTO tbl_client (id, inn, name, is_deleted, created_at)
VALUES (1, '770708389300', 'ООО Альфа', false, '2024-01-01 09:00:00'),
       (2, '012345678901', 'ООО Бета', false, '2024-01-10 10:30:00'),
       (3, '987654321098', 'ООО Гамма', false, '2024-02-01 11:15:00');

---------------------------------------------------------------
-- Балансы (2 пакета x 3 клиента = 6 строк)
-- id явные, чтобы проще ссылаться
---------------------------------------------------------------
INSERT INTO tbl_client_balance (id, client_id, dt, morning_balance, current_balance, created_at)
VALUES
    -- Клиент 1
    (100, 1, '2024-01-01 09:00:00', 100000.00, 95000.00, now()),
    (101, 1, '2024-02-01 09:00:00', 95000.00, 98000.00, now()),

    -- Клиент 2
    (102, 2, '2024-01-10 10:30:00', 50000.00, 60000.00, now()),
    (103, 2, '2024-03-01 10:30:00', 60000.00, 75000.00, now()),

    -- Клиент 3
    (104, 3, '2024-02-01 11:15:00', 200000.00, 195000.00, now()),
    (105, 3, '2024-08-01 10:15:00', 195000.00, 250000.00, now());

---------------------------------------------------------------
-- Платежи
-- Для каждого клиента: пакет D1 (dt совпадает с первой датой баланса) и пакет D2 (со второй)
-- Каждый пакет по 10 записей в строго заданном порядке (Вариант A)
---------------------------------------------------------------

-- Клиент 1 — D1 (dt = 2024-01-01 09:00:00)
INSERT INTO tbl_client_payment (id, client_id, dt, amount, status, direction, out_bank, corr_bank_name, description,
                                created_at)
VALUES (1000, 1, '2024-01-01 09:00:00', 100.00, false, false, true, 'Sberbank', 'External planned leave',
        now()),                                                                                        -- externalPlannedLeave
       (1001, 1, '2024-01-01 09:00:00', 200.00, false, false, false, NULL, 'Internal planned leave',
        now()),                                                                                        -- internalPlannedLeave
       (1002, 1, '2024-01-01 09:00:00', 300.00, true, false, true, 'VTB', 'External fact leave',
        now()),                                                                                        -- externalFactLeave
       (1003, 1, '2024-01-01 09:00:00', 400.00, true, false, false, NULL, 'Internal fact leave',
        now()),                                                                                        -- internalFactLeave
       (1004, 1, '2024-01-01 09:00:00', 500.00, true, true, true, 'Alfa', 'External fact income',
        now()),                                                                                        -- externalFactIncome
       (1005, 1, '2024-01-01 09:00:00', 600.00, true, true, false, NULL, 'Internal fact income',
        now()),                                                                                        -- internalFactIncome
       (1006, 1, '2024-01-01 09:00:00', 700.00, NULL, true, true, 'Gazprom', 'NULL status', now()),    -- NULL status
       (1007, 1, '2024-01-01 09:00:00', 800.00, true, NULL, true, 'Sberbank', 'NULL direction',
        now()),                                                                                        -- NULL direction
       (1008, 1, '2024-01-01 09:00:00', 900.00, true, true, NULL, 'Otkritie', 'NULL out_bank', now()), -- NULL out_bank
       (1009, 1, '2024-01-01 09:00:00', 1000.00, true, false, true, 'Tinkoff', 'Generic OK', now());
-- Generic OK

-- Клиент 1 — D2 (dt = 2024-02-01 09:00:00)
INSERT INTO tbl_client_payment (id, client_id, dt, amount, status, direction, out_bank, corr_bank_name, description,
                                created_at)
VALUES (1010, 1, '2024-02-01 09:00:00', 1100.00, false, false, true, 'Sberbank', 'External planned leave', now()),
       (1011, 1, '2024-02-01 09:00:00', 1200.00, false, false, false, NULL, 'Internal planned leave', now()),
       (1012, 1, '2024-02-01 09:00:00', 1300.00, true, false, true, 'VTB', 'External fact leave', now()),
       (1013, 1, '2024-02-01 09:00:00', 1400.00, true, false, false, NULL, 'Internal fact leave', now()),
       (1014, 1, '2024-02-01 09:00:00', 1500.00, true, true, true, 'Alfa', 'External fact income', now()),
       (1015, 1, '2024-02-01 09:00:00', 1600.00, true, true, false, NULL, 'Internal fact income', now()),
       (1016, 1, '2024-02-01 09:00:00', 1700.00, NULL, true, true, 'Gazprom', 'NULL status', now()),
       (1017, 1, '2024-02-01 09:00:00', 1800.00, true, NULL, true, 'Sberbank', 'NULL direction', now()),
       (1018, 1, '2024-02-01 09:00:00', 1900.00, true, true, NULL, 'Otkritie', 'NULL out_bank', now()),
       (1019, 1, '2024-02-01 09:00:00', 2000.00, true, false, true, 'Tinkoff', 'Generic OK', now());

-- Клиент 2 — D1 (dt = 2024-01-10 10:30:00)
INSERT INTO tbl_client_payment (id, client_id, dt, amount, status, direction, out_bank, corr_bank_name, description,
                                created_at)
VALUES (1020, 2, '2024-01-10 10:30:00', 10.00, false, false, true, 'Sberbank', 'External planned leave', now()),
       (1021, 2, '2024-01-10 10:30:00', 20.00, false, false, false, NULL, 'Internal planned leave', now()),
       (1022, 2, '2024-01-10 10:30:00', 30.00, true, false, true, 'VTB', 'External fact leave', now()),
       (1023, 2, '2024-01-10 10:30:00', 40.00, true, false, false, NULL, 'Internal fact leave', now()),
       (1024, 2, '2024-01-10 10:30:00', 50.00, true, true, true, 'Tinkoff', 'External fact income', now()),
       (1025, 2, '2024-01-10 10:30:00', 60.00, true, true, false, NULL, 'Internal fact income', now()),
       (1026, 2, '2024-01-10 10:30:00', 70.00, NULL, true, true, 'Gazprom', 'NULL status', now()),
       (1027, 2, '2024-01-10 10:30:00', 80.00, true, NULL, true, 'Sberbank', 'NULL direction', now()),
       (1028, 2, '2024-01-10 10:30:00', 90.00, true, true, NULL, 'Otkritie', 'NULL out_bank', now()),
       (1029, 2, '2024-01-10 10:30:00', 100.00, true, false, true, 'Alfa', 'Generic OK', now());

-- Клиент 2 — D2 (dt = 2024-03-01 10:30:00)
INSERT INTO tbl_client_payment (id, client_id, dt, amount, status, direction, out_bank, corr_bank_name, description,
                                created_at)
VALUES (1030, 2, '2024-03-01 10:30:00', 110.00, false, false, true, 'Sberbank', 'External planned leave', now()),
       (1031, 2, '2024-03-01 10:30:00', 120.00, false, false, false, NULL, 'Internal planned leave', now()),
       (1032, 2, '2024-03-01 10:30:00', 130.00, true, false, true, 'VTB', 'External fact leave', now()),
       (1033, 2, '2024-03-01 10:30:00', 140.00, true, false, false, NULL, 'Internal fact leave', now()),
       (1034, 2, '2024-03-01 10:30:00', 150.00, true, true, true, 'Tinkoff', 'External fact income', now()),
       (1035, 2, '2024-03-01 10:30:00', 160.00, true, true, false, NULL, 'Internal fact income', now()),
       (1036, 2, '2024-03-01 10:30:00', 170.00, NULL, true, true, 'Gazprom', 'NULL status', now()),
       (1037, 2, '2024-03-01 10:30:00', 180.00, true, NULL, true, 'Sberbank', 'NULL direction', now()),
       (1038, 2, '2024-03-01 10:30:00', 190.00, true, true, NULL, 'Otkritie', 'NULL out_bank', now()),
       (1039, 2, '2024-03-01 10:30:00', 200.00, true, false, true, 'Alfa', 'Generic OK', now());

-- Клиент 3 — D1 (dt = 2024-02-01 11:15:00)
INSERT INTO tbl_client_payment (id, client_id, dt, amount, status, direction, out_bank, corr_bank_name, description,
                                created_at)
VALUES (1040, 3, '2024-02-01 11:15:00', 1000.00, false, false, true, 'Sberbank', 'External planned leave', now()),
       (1041, 3, '2024-02-01 11:15:00', 2000.00, false, false, false, NULL, 'Internal planned leave', now()),
       (1042, 3, '2024-02-01 11:15:00', 3000.00, true, false, true, 'VTB', 'External fact leave', now()),
       (1043, 3, '2024-02-01 11:15:00', 4000.00, true, false, false, NULL, 'Internal fact leave', now()),
       (1044, 3, '2024-02-01 11:15:00', 5000.00, true, true, true, 'Tinkoff', 'External fact income', now()),
       (1045, 3, '2024-02-01 11:15:00', 6000.00, true, true, false, NULL, 'Internal fact income', now()),
       (1046, 3, '2024-02-01 11:15:00', 7000.00, NULL, true, true, 'Gazprom', 'NULL status', now()),
       (1047, 3, '2024-02-01 11:15:00', 8000.00, true, NULL, true, 'Sberbank', 'NULL direction', now()),
       (1048, 3, '2024-02-01 11:15:00', 9000.00, true, true, NULL, 'Otkritie', 'NULL out_bank', now()),
       (1049, 3, '2024-02-01 11:15:00', 10000.00, true, false, true, 'Alfa', 'Generic OK', now());

-- Клиент 3 — D2 (dt = 2024-08-01 10:15:00)
INSERT INTO tbl_client_payment (id, client_id, dt, amount, status, direction, out_bank, corr_bank_name, description,
                                created_at)
VALUES (1050, 3, '2024-08-01 10:15:00', 11000.00, false, false, true, 'Sberbank', 'External planned leave', now()),
       (1051, 3, '2024-08-01 10:15:00', 12000.00, false, false, false, NULL, 'Internal planned leave', now()),
       (1052, 3, '2024-08-01 10:15:00', 13000.00, true, false, true, 'VTB', 'External fact leave', now()),
       (1053, 3, '2024-08-01 10:15:00', 14000.00, true, false, false, NULL, 'Internal fact leave', now()),
       (1054, 3, '2024-08-01 10:15:00', 15000.00, true, true, true, 'Tinkoff', 'External fact income', now()),
       (1055, 3, '2024-08-01 10:15:00', 16000.00, true, true, false, NULL, 'Internal fact income', now()),
       (1056, 3, '2024-08-01 10:15:00', 17000.00, NULL, true, true, 'Gazprom', 'NULL status', now()),
       (1057, 3, '2024-08-01 10:15:00', 18000.00, true, NULL, true, 'Sberbank', 'NULL direction', now()),
       (1058, 3, '2024-08-01 10:15:00', 19000.00, true, true, NULL, 'Otkritie', 'NULL out_bank', now()),
       (1059, 3, '2024-08-01 10:15:00', 20000.00, true, false, true, 'Alfa', 'Generic OK', now());
