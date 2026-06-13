-- init.sql

-- Create App Users Table
CREATE TABLE app_users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) CHECK (role IN ('ADMIN', 'HELPER')) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Tickets Table
CREATE TABLE tickets (
    ticket_id VARCHAR(50) PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    seat_number VARCHAR(10) UNIQUE NOT NULL,
    is_scanned BOOLEAN DEFAULT FALSE,
    scanned_by INT REFERENCES app_users(id) ON DELETE SET NULL,
    scanned_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO app_users (username, password_hash, role) VALUES
('admin_user', '$2b$12$eXmplHshPass...', 'ADMIN'),
('helper_user', '$2b$12$eXmplHshPass...', 'HELPER');