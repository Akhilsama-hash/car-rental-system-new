# Car Rental Application with PostgreSQL

## Setup Instructions

### 1. PostgreSQL Setup in pgAdmin4

1. **Open pgAdmin4**
2. **Create Database:**
   - Right-click on "Databases"
   - Select "Create" > "Database"
   - Name: `car_rental`
   - Click "Save"

3. **Run SQL Script:**
   - Right-click on `car_rental` database
   - Select "Query Tool"
   - Copy and paste the content from `setup_database.sql`
   - Click "Execute" (F5)

### 2. Update Database Configuration

1. **Edit `database.js`:**
   - Update the password in the connection config:
   ```javascript
   const pool = new Pool({
       user: 'postgres',
       host: 'localhost',
       database: 'car_rental',
       password: 'YOUR_POSTGRESQL_PASSWORD', // Replace this
       port: 5432,
   });
   ```

### 3. Install Dependencies

Open Command Prompt in your project folder and run:
```bash
npm install
```

### 4. Start the Application

```bash
npm start
```

The application will be available at: `http://localhost:3000`

## Features

- **User Registration/Login** with PostgreSQL storage
- **Admin Panel** (Login: carrental / carrental@123)
- **Car Management** with database persistence
- **Booking System** with PostgreSQL storage
- **Real-time Data** from database

## Database Tables

- **users**: User registration data
- **cars**: Car inventory
- **bookings**: Booking records

## API Endpoints

- `POST /api/register` - User registration
- `POST /api/login` - User login
- `GET /api/cars` - Get all cars
- `POST /api/cars` - Add new car (admin)
- `GET /api/bookings` - Get all bookings (admin)
- `POST /api/bookings` - Create booking

## Troubleshooting

1. **Database Connection Error:**
   - Ensure PostgreSQL is running
   - Check database credentials in `database.js`
   - Verify database `car_rental` exists

2. **Port Already in Use:**
   - Change port in `server.js` if 3000 is occupied

3. **Module Not Found:**
   - Run `npm install` to install dependencies