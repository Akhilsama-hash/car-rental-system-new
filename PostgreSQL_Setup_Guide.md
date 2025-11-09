# PostgreSQL Setup Guide for Car Rental System

## Step 1: Install PostgreSQL

### Download and Install:
1. Go to https://www.postgresql.org/download/
2. Download PostgreSQL for Windows
3. Run installer and follow setup wizard
4. **Remember your password** for 'postgres' user
5. Default port: 5432

## Step 2: Setup Database

### Option A: Using pgAdmin (GUI)
1. Open pgAdmin (installed with PostgreSQL)
2. Connect to PostgreSQL server
3. Right-click "Databases" → Create → Database
4. Name: `car_rental`
5. Open Query Tool and run `setup_postgresql.sql`

### Option B: Using Command Line (psql)
```bash
# Open Command Prompt as Administrator
psql -U postgres

# Create database
CREATE DATABASE car_rental;

# Connect to database
\c car_rental

# Run the setup script
\i C:/Users/krish/Desktop/car-rental-frontend/setup_postgresql.sql
```

## Step 3: Update Java Code

### Edit PostgreSQLBackend.java:
```java
// Line 8: Update your password
private static final String DB_PASSWORD = "your_actual_password";
```

## Step 4: Download PostgreSQL JDBC Driver

### Download:
1. Go to https://jdbc.postgresql.org/download.html
2. Download `postgresql-42.7.0.jar` (or latest version)
3. Place in your project folder

## Step 5: Compile and Run

### With JDBC Driver:
```bash
# Compile
javac -cp postgresql-42.7.0.jar PostgreSQLBackend.java

# Run
java -cp .;postgresql-42.7.0.jar PostgreSQLBackend
```

### Without Driver (if using newer Java):
```bash
javac PostgreSQLBackend.java
java PostgreSQLBackend
```

## Step 6: Verify Connection

### Expected Output:
```
✅ PostgreSQL connection successful!
✅ PostgreSQL database initialized successfully

=== PostgreSQL Database Operations ===
✅ Car added with ID: 1
✅ Car added with ID: 2
...
📋 All Cars from PostgreSQL:
Car{id=1, model='Maruti Swift', type='Hatchback', rent=₹1200/day, location='All Cities', available=true}
...
```

## Troubleshooting

### Connection Failed?
1. **Check PostgreSQL is running:**
   - Windows: Services → PostgreSQL
   - Or restart from pgAdmin

2. **Check credentials:**
   - Username: `postgres`
   - Password: (what you set during installation)
   - Port: `5432`

3. **Check database exists:**
   ```sql
   SELECT datname FROM pg_database WHERE datname = 'car_rental';
   ```

### JDBC Driver Issues?
1. Download correct driver version
2. Place in same folder as Java files
3. Use correct classpath in compile/run commands

## Database Schema

### Tables Created:
- **cars**: id, model, type, rent_per_day, location, available
- **users**: id, username, password, role, email
- **bookings**: id, user_id, car_id, start_date, end_date, total_amount, status

### Sample Queries:
```sql
-- View all cars
SELECT * FROM cars;

-- View available cars
SELECT * FROM cars WHERE available = true;

-- View bookings with car details
SELECT b.*, c.model, u.username 
FROM bookings b 
JOIN cars c ON b.car_id = c.id 
JOIN users u ON b.user_id = u.id;
```

## Next Steps

1. ✅ Setup PostgreSQL
2. ✅ Run PostgreSQLBackend.java
3. 🔄 Integrate with web frontend
4. 🚀 Deploy to cloud (Heroku, AWS, etc.)

Your car rental system is now connected to a production-ready PostgreSQL database! 🎉