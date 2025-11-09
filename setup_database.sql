-- Create database
CREATE DATABASE car_rental;

-- Connect to the database
\c car_rental;

-- Create users table
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    dob DATE,
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create cars table
CREATE TABLE cars (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(100) NOT NULL,
    price INTEGER NOT NULL,
    location VARCHAR(255) NOT NULL,
    image VARCHAR(500),
    available BOOLEAN DEFAULT true
);

-- Create bookings table
CREATE TABLE bookings (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id),
    user_name VARCHAR(255),
    car_model VARCHAR(255) NOT NULL,
    location VARCHAR(255) NOT NULL,
    pickup_date DATE NOT NULL,
    return_date DATE NOT NULL,
    days INTEGER NOT NULL,
    total_cost INTEGER NOT NULL,
    phone VARCHAR(20),
    booked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'Pending',
    returned BOOLEAN DEFAULT false,
    returned_at TIMESTAMP
);

-- Insert sample cars
INSERT INTO cars (name, type, price, location, image) VALUES
('Maruti Swift', 'Hatchback', 1200, 'All Cities', '/assets/shift.jpg'),
('Toyota Innova', 'SUV', 2800, 'All Cities', '/assets/innova.jpg'),
('Hyundai Creta', 'SUV', 2500, 'All Cities', '/assets/hyundai.jpg'),
('Mahindra Thar', 'SUV', 3500, 'All Cities', '/assets/thar.jpg'),
('Mahindra XUV300', 'SUV', 2800, 'All Cities', '/assets/mahindra.jpg'),
('Kia Seltos', 'SUV', 3200, 'All Cities', '/assets/kia.jpg');