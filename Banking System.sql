CREATE TABLE IF NOT EXISTS accounts (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT UNIQUE NOT NULL,
    balance REAL NOT NULL
);

CREATE TABLE IF NOT EXISTS transactions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    account_name TEXT NOT NULL,
    type TEXT CHECK(type IN ('DEPOSIT', 'WITHDRAW')) NOT NULL,
    amount REAL NOT NULL,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_name) REFERENCES accounts(name)
);

-- Insert a sample account
INSERT INTO accounts (name, balance) VALUES ('John Doe', 1000.00);

-- Select all accounts
SELECT * FROM accounts;

-- Deposit operation with transaction logging
UPDATE accounts SET balance = balance + 500 WHERE name = 'John Doe';
INSERT INTO transactions (account_name, type, amount) VALUES ('John Doe', 'DEPOSIT', 500);

-- Withdraw operation with transaction logging
UPDATE accounts SET balance = balance - 200 WHERE name = 'John Doe' AND balance >= 200;
INSERT INTO transactions (account_name, type, amount) VALUES ('John Doe', 'WITHDRAW', 200);

-- Check balance of a specific user
SELECT balance FROM accounts WHERE name = 'John Doe';

-- Retrieve transaction history for a user
SELECT * FROM transactions WHERE account_name = 'John Doe' ORDER BY timestamp DESC;

-- Delete an account (with cascading delete for transactions)
DELETE FROM accounts WHERE name = 'John Doe';
