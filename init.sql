-- init.sql  –  Runs automatically when MySQL container starts for the first time.

CREATE DATABASE IF NOT EXISTS calculator;
USE calculator;

CREATE TABLE IF NOT EXISTS history (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    operand1    DOUBLE       NOT NULL,
    operator    VARCHAR(20)  NOT NULL,
    operand2    DOUBLE       NOT NULL DEFAULT 0,
    result      DOUBLE       NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
