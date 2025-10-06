-- =========================================================
-- Схема БД по ТЗ (CONV-1293 + CONV-4133)
-- "как есть", с учётом зачёркнутых пунктов
-- =========================================================

-- Таблица клиентов
CREATE TABLE IF NOT EXISTS tbl_client (
    id bigint PRIMARY KEY,                       -- PK, без автоинкремента
    inn bigint,                                  -- ИНН клиента (O)
    name varchar(255) NOT NULL,                  -- увеличено с 20 до 255 (правка из bd_2)
    is_deleted boolean NOT NULL DEFAULT false,
    created_at timestamp without time zone NOT NULL  -- новое поле (bd_2)
);

-- Таблица остатков клиентов
CREATE TABLE IF NOT EXISTS tbl_client_balance (
    id bigint PRIMARY KEY,
    client_id bigint NOT NULL REFERENCES tbl_client(id),
    dt timestamp without time zone NOT NULL,
    morning_balance double precision,     -- O
    current_balance double precision NOT NULL,
    created_at timestamp without time zone NOT NULL  -- новое поле (bd_2)
);

-- Таблица платежей клиентов
CREATE TABLE IF NOT EXISTS tbl_client_payment (
    id bigint PRIMARY KEY,
    client_id bigint NOT NULL REFERENCES tbl_client(id),
    dt timestamp without time zone NOT NULL,
    amount double precision,             -- O
    status boolean,                      -- O (true = исполнен)
    direction boolean,                   -- O (true = входящий)
    out_bank boolean,                    -- O (true = внешний)
    corr_bank_name varchar,              -- O
    description varchar,                 -- O
    created_at timestamp without time zone NOT NULL  -- новое поле (bd_2)
);
