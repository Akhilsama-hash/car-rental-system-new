const { Pool } = require('pg');

// PostgreSQL connection configuration
const pool = new Pool({
    connectionString: process.env.DATABASE_URL || process.env.POSTGRES_URL,
    ssl: process.env.NODE_ENV === 'production' ? { rejectUnauthorized: false } : false
});

// Database connection functions
class Database {
    // Initialize database tables
    static async initTables() {
        const client = await pool.connect();
        try {
            // Create users table
            await client.query(`
                CREATE TABLE IF NOT EXISTS users (
                    id SERIAL PRIMARY KEY,
                    name VARCHAR(255) NOT NULL,
                    email VARCHAR(255) UNIQUE NOT NULL,
                    password VARCHAR(255) NOT NULL,
                    phone VARCHAR(20),
                    dob DATE,
                    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            `);

            // Create cars table
            await client.query(`
                CREATE TABLE IF NOT EXISTS cars (
                    id SERIAL PRIMARY KEY,
                    name VARCHAR(255) NOT NULL,
                    type VARCHAR(100) NOT NULL,
                    price INTEGER NOT NULL,
                    location VARCHAR(255) NOT NULL,
                    image VARCHAR(500),
                    available BOOLEAN DEFAULT true
                )
            `);

            // Create bookings table
            await client.query(`
                CREATE TABLE IF NOT EXISTS bookings (
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
                )
            `);

            console.log('Database tables initialized successfully');
        } catch (err) {
            console.error('Error initializing tables:', err);
        } finally {
            client.release();
        }
    }

    // Seed initial car data
    static async seedCars() {
        const client = await pool.connect();
        try {
            const existingCars = await client.query('SELECT COUNT(*) FROM cars');
            if (parseInt(existingCars.rows[0].count) === 0) {
                await client.query(`
                    INSERT INTO cars (name, type, price, location, image) VALUES
                    ('Maruti Swift', 'Hatchback', 1200, 'All Cities', '/assets/shift.jpg'),
                    ('Toyota Innova', 'SUV', 2800, 'All Cities', '/assets/innova.jpg'),
                    ('Hyundai Creta', 'SUV', 2500, 'All Cities', '/assets/hyundai.jpg'),
                    ('Mahindra Thar', 'SUV', 3500, 'All Cities', '/assets/thar.jpg'),
                    ('Mahindra XUV300', 'SUV', 2800, 'All Cities', '/assets/mahindra.jpg'),
                    ('Kia Seltos', 'SUV', 3200, 'All Cities', '/assets/kia.jpg')
                `);
                console.log('Initial car data seeded');
            }
        } catch (err) {
            console.error('Error seeding cars:', err);
        } finally {
            client.release();
        }
    }

    // User operations
    static async registerUser(userData) {
        const client = await pool.connect();
        try {
            const result = await client.query(
                'INSERT INTO users (name, email, password, phone, dob) VALUES ($1, $2, $3, $4, $5) RETURNING *',
                [userData.name, userData.email, userData.password, userData.phone, userData.dob]
            );
            return result.rows[0];
        } catch (err) {
            throw err;
        } finally {
            client.release();
        }
    }

    static async loginUser(email, password) {
        const client = await pool.connect();
        try {
            const result = await client.query(
                'SELECT * FROM users WHERE email = $1 AND password = $2',
                [email, password]
            );
            return result.rows[0];
        } catch (err) {
            throw err;
        } finally {
            client.release();
        }
    }

    static async getAllUsers() {
        const client = await pool.connect();
        try {
            const result = await client.query('SELECT * FROM users ORDER BY registered_at DESC');
            return result.rows;
        } catch (err) {
            throw err;
        } finally {
            client.release();
        }
    }

    // Car operations
    static async getAllCars() {
        const client = await pool.connect();
        try {
            const result = await client.query('SELECT * FROM cars ORDER BY id');
            return result.rows;
        } catch (err) {
            throw err;
        } finally {
            client.release();
        }
    }

    static async addCar(carData) {
        const client = await pool.connect();
        try {
            const result = await client.query(
                'INSERT INTO cars (name, type, price, location, image) VALUES ($1, $2, $3, $4, $5) RETURNING *',
                [carData.name, carData.type, carData.price, carData.location, carData.image]
            );
            return result.rows[0];
        } catch (err) {
            throw err;
        } finally {
            client.release();
        }
    }

    // Booking operations
    static async createBooking(bookingData) {
        const client = await pool.connect();
        try {
            const result = await client.query(
                'INSERT INTO bookings (user_id, user_name, car_model, location, pickup_date, return_date, days, total_cost, phone) VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9) RETURNING *',
                [bookingData.userId, bookingData.userName, bookingData.carModel, bookingData.location, 
                 bookingData.pickupDate, bookingData.returnDate, bookingData.days, bookingData.totalCost, bookingData.phone]
            );
            return result.rows[0];
        } catch (err) {
            throw err;
        } finally {
            client.release();
        }
    }

    static async getAllBookings() {
        const client = await pool.connect();
        try {
            const result = await client.query('SELECT * FROM bookings ORDER BY booked_at DESC');
            return result.rows;
        } catch (err) {
            throw err;
        } finally {
            client.release();
        }
    }

    static async getUserBookings(userId) {
        const client = await pool.connect();
        try {
            const result = await client.query('SELECT * FROM bookings WHERE user_id = $1 ORDER BY booked_at DESC', [userId]);
            return result.rows;
        } catch (err) {
            throw err;
        } finally {
            client.release();
        }
    }
}

module.exports = Database;