const express = require('express');
const path = require('path');
const Database = require('./database');

const app = express();
const PORT = process.env.PORT || 3000;

// Middleware
app.use(express.json());
app.use('/assets', express.static('public/assets'));
app.use(express.static('public'));

// Initialize database (only if DATABASE_URL exists)
if (process.env.DATABASE_URL || process.env.POSTGRES_URL) {
    Database.initTables().then(() => {
        Database.seedCars();
    }).catch(err => {
        console.log('Database not available, using fallback mode');
    });
}

// API Routes

// User registration
app.post('/api/register', async (req, res) => {
    try {
        const user = await Database.registerUser(req.body);
        res.json({ success: true, user });
    } catch (error) {
        res.status(400).json({ success: false, error: error.message });
    }
});

// User login
app.post('/api/login', async (req, res) => {
    try {
        const { email, password } = req.body;
        const user = await Database.loginUser(email, password);
        if (user) {
            res.json({ success: true, user });
        } else {
            res.status(401).json({ success: false, error: 'Invalid credentials' });
        }
    } catch (error) {
        res.status(500).json({ success: false, error: error.message });
    }
});

// Get all users (admin only)
app.get('/api/users', async (req, res) => {
    try {
        const users = await Database.getAllUsers();
        res.json(users);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// Get all cars
app.get('/api/cars', async (req, res) => {
    try {
        if (process.env.DATABASE_URL || process.env.POSTGRES_URL) {
            const cars = await Database.getAllCars();
            res.json(cars);
        } else {
            // Fallback data when no database
            const fallbackCars = [
                {id: 2, name: 'Maruti Swift', type: 'Hatchback', price: 1200, location: 'All Cities', image: '/assets/shift.jpg'},
                {id: 3, name: 'Toyota Innova', type: 'SUV', price: 2800, location: 'All Cities', image: '/assets/innova.jpg'},
                {id: 4, name: 'Hyundai Creta', type: 'SUV', price: 2500, location: 'All Cities', image: '/assets/hyundai.jpg'},
                {id: 6, name: 'Mahindra Thar', type: 'SUV', price: 3500, location: 'All Cities', image: '/assets/thar.jpg'},
                {id: 7, name: 'Mahindra XUV300', type: 'SUV', price: 2800, location: 'All Cities', image: '/assets/mahindra.jpg'},
                {id: 8, name: 'Kia Seltos', type: 'SUV', price: 3200, location: 'All Cities', image: '/assets/kia.jpg'}
            ];
            res.json(fallbackCars);
        }
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// Add new car (admin only)
app.post('/api/cars', async (req, res) => {
    try {
        const car = await Database.addCar(req.body);
        res.json({ success: true, car });
    } catch (error) {
        res.status(400).json({ success: false, error: error.message });
    }
});

// Create booking
app.post('/api/bookings', async (req, res) => {
    try {
        const booking = await Database.createBooking(req.body);
        res.json({ success: true, booking });
    } catch (error) {
        res.status(400).json({ success: false, error: error.message });
    }
});

// Get all bookings (admin only)
app.get('/api/bookings', async (req, res) => {
    try {
        const bookings = await Database.getAllBookings();
        res.json(bookings);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// Get user bookings
app.get('/api/bookings/:userId', async (req, res) => {
    try {
        const bookings = await Database.getUserBookings(req.params.userId);
        res.json(bookings);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
});

// Serve HTML file
app.get('/', (req, res) => {
    res.sendFile(path.join(__dirname, 'car-rental.html'));
});

if (process.env.NODE_ENV !== 'production') {
    app.listen(PORT, () => {
        console.log(`Server running on http://localhost:${PORT}`);
        console.log('Make sure PostgreSQL is running and database "car_rental" exists');
    });
}

module.exports = app;