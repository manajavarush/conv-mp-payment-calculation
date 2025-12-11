-- =========================================================
-- Схема БД по ТЗ (СКОРРЕКТИРОВАННАЯ)
-- =========================================================

-- 1. Создание последовательностей (Sequence) для автогенерации ID
CREATE SEQUENCE IF NOT EXISTS balance_id_seq START 100 INCREMENT 1;
CREATE SEQUENCE IF NOT EXISTS payment_id_seq START 1000 INCREMENT 1;

-- Таблица клиентов
CREATE TABLE IF NOT EXISTS tbl_client
(
    id         bigint PRIMARY KEY, -- PK, без автоинкремента (внешняя система)
    inn        varchar(12),        -- ИНН клиента (O) - ИСПРАВЛЕНО НА VARCHAR(12)
    name       varchar(255)                NOT NULL,
    is_deleted boolean                     NOT NULL DEFAULT false,
    created_at timestamp without time zone NOT NULL
);

-- Таблица остатков клиентов
CREATE TABLE IF NOT EXISTS tbl_client_balance
(
    id              bigint PRIMARY KEY DEFAULT nextval('balance_id_seq'), -- ID генерируется SEQUENCE
    client_id       bigint                      NOT NULL REFERENCES tbl_client (id),
    dt              timestamp without time zone NOT NULL,                 -- Дата/время получения данных
    morning_balance numeric(18, 2),                                       -- O - ИСПРАВЛЕНО НА NUMERIC(18, 2)
    current_balance numeric(18, 2)              NOT NULL,                 -- ИСПРАВЛЕНО НА NUMERIC(18, 2)
    created_at      timestamp without time zone NOT NULL
);

-- Таблица платежей клиентов
CREATE TABLE IF NOT EXISTS tbl_client_payment
(
    id             bigint PRIMARY KEY DEFAULT nextval('payment_id_seq'), -- ID генерируется SEQUENCE
    client_id      bigint                      NOT NULL REFERENCES tbl_client (id),
    dt             timestamp without time zone NOT NULL,                 -- Дата/время получения данных о платежах
    amount         numeric(18, 2),                                       -- O - ИСПРАВЛЕНО НА NUMERIC(18, 2)
    status         boolean,                                              -- O (true = исполнен)
    direction      boolean,                                              -- O (true = входящий)
    out_bank       boolean,                                              -- O (true = внешний)
    corr_bank_name varchar,                                              -- O
    description    varchar,                                              -- O
    created_at     timestamp without time zone NOT NULL
);
