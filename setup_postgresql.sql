-- PostgreSQL Setup for Car Rental System
-- Run these commands in PostgreSQL (pgAdmin or psql)

-- 1. Create database
CREATE DATABASE car_rental;

-- 2. Connect to the database
\c car_rental;

-- 3. Create tables
CREATE TABLE IF NOT EXISTS cars (
    id SERIAL PRIMARY KEY,
    model VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    rent_per_day DECIMAL(10,2) NOT NULL,
    location VARCHAR(100) NOT NULL,
    available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'USER')),
    email VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS bookings (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    car_id INTEGER NOT NULL REFERENCES cars(id) ON DELETE CASCADE,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'CONFIRMED' CHECK (status IN ('CONFIRMED', 'CANCELLED', 'COMPLETED')),
    phone VARCHAR(15),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_cars_type ON cars(type);
CREATE INDEX IF NOT EXISTS idx_cars_available ON cars(available);
CREATE INDEX IF NOT EXISTS idx_bookings_user ON bookings(user_id);
CREATE INDEX IF NOT EXISTS idx_bookings_car ON bookings(car_id);
CREATE INDEX IF NOT EXISTS idx_bookings_dates ON bookings(start_date, end_date);

-- 5. Insert sample data
INSERT INTO cars (model, type, rent_per_day, location) VALUES
('Maruti Swift', 'Hatchback', 1200.00, 'All Cities'),
('Honda City', 'Sedan', 1800.00, 'All Cities'),
('Hyundai Creta', 'SUV', 2500.00, 'All Cities'),
('Tata Nexon', 'SUV', 2200.00, 'All Cities'),
('Mahindra XUV300', 'SUV', 2800.00, 'All Cities'),
('Kia Seltos', 'SUV', 3200.00, 'All Cities');

INSERT INTO users (username, password, role, email) VALUES
('admin', 'admin', 'ADMIN', 'admin@drivenow.com'),
('user', 'user', 'USER', 'user@drivenow.com');

-- 6. Verify data
SELECT 'Cars' as table_name, COUNT(*) as count FROM cars
UNION ALL
SELECT 'Users' as table_name, COUNT(*) as count FROM users;

-- 7. Show all cars
SELECT * FROM cars;

-- 8. Show all users
SELECT id, username, role, email FROM users;