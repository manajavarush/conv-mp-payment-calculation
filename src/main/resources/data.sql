-- Очищаем данные в ПРАВИЛЬНОМ ПОРЯДКЕ (сначала дочерние таблицы)
-- 1. Сначала таблицы, которые ссылаются на другие
DELETE FROM tbl_client_payment;
DELETE FROM tbl_client_balance;

-- 2. Затем родительские таблицы
DELETE FROM tbl_client;


-- Клиенты (3 шт.)
INSERT INTO tbl_client (id, inn, name, is_deleted, created_at)
VALUES
  (1, 7707083893, 'ООО Альфа', false, '2024-01-01 09:00:00'),
  (2, 5001007321, 'ООО Бета',  false, '2024-01-10 10:30:00'),
  (3, 1234567890, 'ООО Гамма', false, '2024-02-01 11:15:00');

-- Остатки (2 даты × 3 клиента = 6 строк)
INSERT INTO tbl_client_balance (id, client_id, dt, morning_balance, current_balance, created_at)
VALUES
  (1, 1, '2024-01-01 09:00:00', 100000.00, 95000.00, now()),
  (2, 1, '2024-02-01 09:00:00', 95000.00, 98000.00, now()),
  (3, 2, '2024-01-10 10:30:00', 200000.00, 198000.00, now()),
  (4, 2, '2024-02-10 10:30:00', 198000.00, 205000.00, now()),
  (5, 3, '2024-02-01 11:15:00', 150000.00, 145000.00, now()),
  (6, 3, '2024-03-01 11:15:00', 145000.00, 160000.00, now());

-- Платежи (10 шт. × 2 даты × 3 клиента = 60 строк)
INSERT INTO tbl_client_payment (id, client_id, dt, amount, status, direction, out_bank, corr_bank_name, description, created_at)
VALUES
(1001, 1, '2024-07-05 09:37:00', 1000.00, false, false, true, 'Sberbank', 'Test payment 1001 - externalPlannedLeave', '2024-07-05 09:38:00'),
(1002, 1, '2024-07-05 09:37:00', 2000.00, false, false, false, NULL, 'Test payment 1002 - internalPlannedLeave', '2024-07-05 09:39:00'),
(1003, 1, '2024-07-05 09:37:00', 3000.00, true, false, true, 'Alfa', 'Test payment 1003 - externalFactLeave', '2024-07-05 09:40:00'),
(1004, 1, '2024-07-05 09:37:00', 4000.00, true, false, false, NULL, 'Test payment 1004 - internalFactLeave', '2024-07-05 09:42:00'),
(1005, 1, '2024-07-05 09:37:00', 5000.00, true, true, true, 'VTB', 'Test payment 1005 - externalFactIncome', '2024-07-05 09:43:00'),
(1006, 1, '2024-07-05 09:37:00', 6000.00, true, true, false, NULL, 'Test payment 1006 - internalFactIncome', '2024-07-05 09:45:00'),
(1007, 1, '2024-07-05 09:37:00', 1500.00, NULL, false, true, 'Sberbank', 'Test payment 1007 - NULL status', '2024-07-05 09:47:00'),
(1008, 1, '2024-07-05 09:37:00', 2500.00, true, true, NULL, NULL, 'Test payment 1008 - NULL outBank', '2024-07-05 09:49:00'),
(1009, 1, '2024-07-05 09:37:00', 3500.00, true, NULL, true, 'Alfa', 'Test payment 1009 - NULL direction', '2024-07-05 09:50:00'),
(1010, 1, '2024-07-05 09:37:00', 4500.00, true, true, true, 'Sberbank', 'Test payment 1010 - Duplicate case', '2024-07-05 09:52:00'),
(1011, 1, '2024-08-01 10:15:00', 1000.00, false, false, true, 'Sberbank', 'Test payment 1011 - externalPlannedLeave', '2024-08-01 10:16:00'),
(1012, 1, '2024-08-01 10:15:00', 2000.00, false, false, false, NULL, 'Test payment 1012 - internalPlannedLeave', '2024-08-01 10:17:00'),
(1013, 1, '2024-08-01 10:15:00', 3000.00, true, false, true, 'Alfa', 'Test payment 1013 - externalFactLeave', '2024-08-01 10:18:00'),
(1014, 1, '2024-08-01 10:15:00', 4000.00, true, false, false, NULL, 'Test payment 1014 - internalFactLeave', '2024-08-01 10:20:00'),
(1015, 1, '2024-08-01 10:15:00', 5000.00, true, true, true, 'VTB', 'Test payment 1015 - externalFactIncome', '2024-08-01 10:21:00'),
(1016, 1, '2024-08-01 10:15:00', 6000.00, true, true, false, NULL, 'Test payment 1016 - internalFactIncome', '2024-08-01 10:23:00'),
(1017, 1, '2024-08-01 10:15:00', 1500.00, NULL, false, true, 'Sberbank', 'Test payment 1017 - NULL status', '2024-08-01 10:25:00'),
(1018, 1, '2024-08-01 10:15:00', 2500.00, true, true, NULL, NULL, 'Test payment 1018 - NULL outBank', '2024-08-01 10:27:00'),
(1019, 1, '2024-08-01 10:15:00', 3500.00, true, NULL, true, 'Alfa', 'Test payment 1019 - NULL direction', '2024-08-01 10:28:00'),
(1020, 1, '2024-08-01 10:15:00', 4500.00, true, true, true, 'Sberbank', 'Test payment 1020 - Duplicate case', '2024-08-01 10:30:00'),
(1021, 2, '2024-07-05 09:37:00', 1000.00, false, false, true, 'Sberbank', 'Test payment 1021 - externalPlannedLeave', '2024-07-05 09:38:00'),
(1022, 2, '2024-07-05 09:37:00', 2000.00, false, false, false, NULL, 'Test payment 1022 - internalPlannedLeave', '2024-07-05 09:39:00'),
(1023, 2, '2024-07-05 09:37:00', 3000.00, true, false, true, 'Alfa', 'Test payment 1023 - externalFactLeave', '2024-07-05 09:40:00'),
(1024, 2, '2024-07-05 09:37:00', 4000.00, true, false, false, NULL, 'Test payment 1024 - internalFactLeave', '2024-07-05 09:42:00'),
(1025, 2, '2024-07-05 09:37:00', 5000.00, true, true, true, 'VTB', 'Test payment 1025 - externalFactIncome', '2024-07-05 09:43:00'),
(1026, 2, '2024-07-05 09:37:00', 6000.00, true, true, false, NULL, 'Test payment 1026 - internalFactIncome', '2024-07-05 09:45:00'),
(1027, 2, '2024-07-05 09:37:00', 1500.00, NULL, false, true, 'Sberbank', 'Test payment 1027 - NULL status', '2024-07-05 09:47:00'),
(1028, 2, '2024-07-05 09:37:00', 2500.00, true, true, NULL, NULL, 'Test payment 1028 - NULL outBank', '2024-07-05 09:49:00'),
(1029, 2, '2024-07-05 09:37:00', 3500.00, true, NULL, true, 'Alfa', 'Test payment 1029 - NULL direction', '2024-07-05 09:50:00'),
(1030, 2, '2024-07-05 09:37:00', 4500.00, true, true, true, 'Sberbank', 'Test payment 1030 - Duplicate case', '2024-07-05 09:52:00'),
(1031, 2, '2024-08-01 10:15:00', 1000.00, false, false, true, 'Sberbank', 'Test payment 1031 - externalPlannedLeave', '2024-08-01 10:16:00'),
(1032, 2, '2024-08-01 10:15:00', 2000.00, false, false, false, NULL, 'Test payment 1032 - internalPlannedLeave', '2024-08-01 10:17:00'),
(1033, 2, '2024-08-01 10:15:00', 3000.00, true, false, true, 'Alfa', 'Test payment 1033 - externalFactLeave', '2024-08-01 10:18:00'),
(1034, 2, '2024-08-01 10:15:00', 4000.00, true, false, false, NULL, 'Test payment 1034 - internalFactLeave', '2024-08-01 10:20:00'),
(1035, 2, '2024-08-01 10:15:00', 5000.00, true, true, true, 'VTB', 'Test payment 1035 - externalFactIncome', '2024-08-01 10:21:00'),
(1036, 2, '2024-08-01 10:15:00', 6000.00, true, true, false, NULL, 'Test payment 1036 - internalFactIncome', '2024-08-01 10:23:00'),
(1037, 2, '2024-08-01 10:15:00', 1500.00, NULL, false, true, 'Sberbank', 'Test payment 1037 - NULL status', '2024-08-01 10:25:00'),
(1038, 2, '2024-08-01 10:15:00', 2500.00, true, true, NULL, NULL, 'Test payment 1038 - NULL outBank', '2024-08-01 10:27:00'),
(1039, 2, '2024-08-01 10:15:00', 3500.00, true, NULL, true, 'Alfa', 'Test payment 1039 - NULL direction', '2024-08-01 10:28:00'),
(1040, 2, '2024-08-01 10:15:00', 4500.00, true, true, true, 'Sberbank', 'Test payment 1040 - Duplicate case', '2024-08-01 10:30:00'),
(1041, 3, '2024-07-05 09:37:00', 1000.00, false, false, true, 'Sberbank', 'Test payment 1041 - externalPlannedLeave', '2024-07-05 09:38:00'),
(1042, 3, '2024-07-05 09:37:00', 2000.00, false, false, false, NULL, 'Test payment 1042 - internalPlannedLeave', '2024-07-05 09:39:00'),
(1043, 3, '2024-07-05 09:37:00', 3000.00, true, false, true, 'Alfa', 'Test payment 1043 - externalFactLeave', '2024-07-05 09:40:00'),
(1044, 3, '2024-07-05 09:37:00', 4000.00, true, false, false, NULL, 'Test payment 1044 - internalFactLeave', '2024-07-05 09:42:00'),
(1045, 3, '2024-07-05 09:37:00', 5000.00, true, true, true, 'VTB', 'Test payment 1045 - externalFactIncome', '2024-07-05 09:43:00'),
(1046, 3, '2024-07-05 09:37:00', 6000.00, true, true, false, NULL, 'Test payment 1046 - internalFactIncome', '2024-07-05 09:45:00'),
(1047, 3, '2024-07-05 09:37:00', 1500.00, NULL, false, true, 'Sberbank', 'Test payment 1047 - NULL status', '2024-07-05 09:47:00'),
(1048, 3, '2024-07-05 09:37:00', 2500.00, true, true, NULL, NULL, 'Test payment 1048 - NULL outBank', '2024-07-05 09:49:00'),
(1049, 3, '2024-07-05 09:37:00', 3500.00, true, NULL, true, 'Alfa', 'Test payment 1049 - NULL direction', '2024-07-05 09:50:00'),
(1050, 3, '2024-07-05 09:37:00', 4500.00, true, true, true, 'Sberbank', 'Test payment 1050 - Duplicate case', '2024-07-05 09:52:00'),
(1051, 3, '2024-08-01 10:15:00', 1000.00, false, false, true, 'Sberbank', 'Test payment 1051 - externalPlannedLeave', '2024-08-01 10:16:00'),
(1052, 3, '2024-08-01 10:15:00', 2000.00, false, false, false, NULL, 'Test payment 1052 - internalPlannedLeave', '2024-08-01 10:17:00'),
(1053, 3, '2024-08-01 10:15:00', 3000.00, true, false, true, 'Alfa', 'Test payment 1053 - externalFactLeave', '2024-08-01 10:18:00'),
(1054, 3, '2024-08-01 10:15:00', 4000.00, true, false, false, NULL, 'Test payment 1054 - internalFactLeave', '2024-08-01 10:20:00'),
(1055, 3, '2024-08-01 10:15:00', 5000.00, true, true, true, 'VTB', 'Test payment 1055 - externalFactIncome', '2024-08-01 10:21:00'),
(1056, 3, '2024-08-01 10:15:00', 6000.00, true, true, false, NULL, 'Test payment 1056 - internalFactIncome', '2024-08-01 10:23:00'),
(1057, 3, '2024-08-01 10:15:00', 1500.00, NULL, false, true, 'Sberbank', 'Test payment 1057 - NULL status', '2024-08-01 10:25:00'),
(1058, 3, '2024-08-01 10:15:00', 2500.00, true, true, NULL, NULL, 'Test payment 1058 - NULL outBank', '2024-08-01 10:27:00'),
(1059, 3, '2024-08-01 10:15:00', 3500.00, true, NULL, true, 'Alfa', 'Test payment 1059 - NULL direction', '2024-08-01 10:28:00'),
(1060, 3, '2024-08-01 10:15:00', 4500.00, true, true, true, 'Sberbank', 'Test payment 1060 - Duplicate case', '2024-08-01 10:30:00');