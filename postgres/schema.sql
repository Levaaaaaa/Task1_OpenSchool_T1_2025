CREATE TABLE clients (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    patronymic VARCHAR(255) NOT NULL,
    client_id UUID NOT NULL UNIQUE
);

CREATE TABLE accounts (
    id SERIAL PRIMARY KEY,
    client_id UUID NOT NULL,
    account_type VARCHAR(255) NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    CONSTRAINT fk_accounts_client_id FOREIGN KEY (client_id) REFERENCES clients (client_id)
);

-- Указание уникального или обычного индекса на client_id, если нужно ускорить JOIN-операции
CREATE INDEX idx_accounts_client_id ON accounts (client_id);

CREATE TABLE transactions (
    id SERIAL PRIMARY KEY,
    producer BIGINT NOT NULL,
    consumer BIGINT NOT NULL,
    amount NUMERIC(19, 2) NOT NULL CHECK (amount > 0),
    transaction_date TIMESTAMP NOT NULL,

    CONSTRAINT fk_transactions_producer FOREIGN KEY (producer) REFERENCES accounts (id),
    CONSTRAINT fk_transactions_consumer FOREIGN KEY (consumer) REFERENCES accounts (id)
);

-- Индексы для ускорения поиска по связям
CREATE INDEX idx_transactions_producer ON transactions (producer);
CREATE INDEX idx_transactions_consumer ON transactions (consumer);

CREATE TABLE datasource_error_logs (
    id SERIAL PRIMARY KEY,
    stack_trace TEXT NOT NULL,
    message VARCHAR(255) NOT NULL,
    method_signature VARCHAR(255) NOT NULL
);
