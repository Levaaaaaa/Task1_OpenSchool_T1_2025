            INSERT INTO clients (first_name, last_name, patronymic, client_id) VALUES
            ('Иван', 'Иванов', 'Иванович', '11111111-1111-1111-1111-111111111111'),
            ('Петр', 'Петров', 'Петрович', '22222222-2222-2222-2222-222222222222'),
            ('Сидор', 'Сидоров', 'Сидорович', '33333333-3333-3333-3333-333333333333');

            -- Вставка счетов (предполагаем, что они получат id = 1, 2, 3)
            INSERT INTO accounts (client_id, account_type, amount) VALUES
            ('11111111-1111-1111-1111-111111111111', 'DEBIT', 1000.00),
            ('11111111-1111-1111-1111-111111111111', 'CREDIT', 500.00),
            ('22222222-2222-2222-2222-222222222222', 'DEBIT', 300.00);

            INSERT INTO transactions (producer, consumer, amount, transaction_date) VALUES
            (1, 3, 200.00, CURRENT_TIMESTAMP),
            (2, 3, 100.00, CURRENT_TIMESTAMP);

            INSERT INTO datasource_error_logs (stack_trace, message, method_signature) VALUES
            ('java.lang.NullPointerException at com.example.Service.methodA(Service.java:10)', 'Null reference encountered', 'com.example.Service.methodA(String)'),
            ('org.postgresql.util.PSQLException: ERROR: relation "some_table" does not exist', 'Table not found in DB', 'com.example.Repository.findData()');