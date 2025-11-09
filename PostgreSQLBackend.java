import java.sql.*;
import java.util.*;
import java.time.LocalDate;

public class PostgreSQLBackend {
    // PostgreSQL connection details
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/car_rental";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "postgres";
    
    public static void main(String[] args) {
        PostgreSQLManager dbManager = new PostgreSQLManager();
        
        // Test connection
        if (dbManager.testConnection()) {
            System.out.println("✅ PostgreSQL connection successful!");
            
            // Initialize database
            dbManager.initializeDatabase();
            
            // Demo operations
            demoOperations();
        } else {
            System.out.println("❌ PostgreSQL connection failed!");
            System.out.println("Make sure PostgreSQL is running and credentials are correct.");
        }
    }
    
    private static void demoOperations() {
        CarDAO carDAO = new CarDAO();
        UserDAO userDAO = new UserDAO();
        BookingDAO bookingDAO = new BookingDAO();
        
        System.out.println("\n=== PostgreSQL Database Operations ===");
        
        // Add sample cars
        carDAO.addCar(new Car(0, "Maruti Swift", "Hatchback", 1200, "All Cities", true));
        carDAO.addCar(new Car(0, "Honda City", "Sedan", 1800, "All Cities", true));
        carDAO.addCar(new Car(0, "Hyundai Creta", "SUV", 2500, "All Cities", true));
        carDAO.addCar(new Car(0, "Tata Nexon", "SUV", 2200, "All Cities", true));
        
        // Add admin user
        userDAO.addUser(new User(0, "carrental", "carrental@123", "ADMIN", "admin@carrental.com"));
        
        // Display all cars
        System.out.println("\n📋 All Cars from PostgreSQL:");
        carDAO.getAllCars().forEach(System.out::println);
        
        // Create booking
        System.out.println("\n📅 Creating Booking:");
        Booking booking = bookingDAO.createBooking(2, 1, LocalDate.now(), LocalDate.now().plusDays(3));
        if (booking != null) {
            System.out.println("✅ Booking created: " + booking);
        }
        
        // Display bookings
        System.out.println("\n📊 All Bookings:");
        bookingDAO.getAllBookings().forEach(System.out::println);
    }
}

// PostgreSQL Database Manager
class PostgreSQLManager {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/car_rental";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "postgres";
    
    public boolean testConnection() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }
    
    public void initializeDatabase() {
        try (Connection conn = getConnection()) {
            createTables(conn);
            System.out.println("✅ PostgreSQL database initialized successfully");
        } catch (SQLException e) {
            System.err.println("❌ Database initialization failed: " + e.getMessage());
        }
    }
    
    private void createTables(Connection conn) throws SQLException {
        // Cars table
        String createCarsTable = """
            CREATE TABLE IF NOT EXISTS cars (
                id SERIAL PRIMARY KEY,
                model VARCHAR(100) NOT NULL,
                type VARCHAR(50) NOT NULL,
                rent_per_day DECIMAL(10,2) NOT NULL,
                location VARCHAR(100) NOT NULL,
                available BOOLEAN DEFAULT TRUE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;
        
        // Users table
        String createUsersTable = """
            CREATE TABLE IF NOT EXISTS users (
                id SERIAL PRIMARY KEY,
                username VARCHAR(50) UNIQUE NOT NULL,
                password VARCHAR(255) NOT NULL,
                role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'USER')),
                email VARCHAR(100) NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;
        
        // Bookings table
        String createBookingsTable = """
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
            )
        """;
        
        // Create indexes
        String createIndexes = """
            CREATE INDEX IF NOT EXISTS idx_cars_type ON cars(type);
            CREATE INDEX IF NOT EXISTS idx_cars_available ON cars(available);
            CREATE INDEX IF NOT EXISTS idx_bookings_user ON bookings(user_id);
            CREATE INDEX IF NOT EXISTS idx_bookings_dates ON bookings(start_date, end_date);
        """;
        
        Statement stmt = conn.createStatement();
        stmt.execute(createCarsTable);
        stmt.execute(createUsersTable);
        stmt.execute(createBookingsTable);
        stmt.execute(createIndexes);
        stmt.close();
    }
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}

// Data Access Objects for PostgreSQL
class CarDAO {
    public List<Car> getAllCars() {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT * FROM cars ORDER BY id";
        
        try (Connection conn = PostgreSQLManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                cars.add(new Car(
                    rs.getInt("id"),
                    rs.getString("model"),
                    rs.getString("type"),
                    rs.getDouble("rent_per_day"),
                    rs.getString("location"),
                    rs.getBoolean("available")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting cars: " + e.getMessage());
        }
        return cars;
    }
    
    public Car getCarById(int id) {
        String sql = "SELECT * FROM cars WHERE id = ?";
        
        try (Connection conn = PostgreSQLManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Car(
                    rs.getInt("id"),
                    rs.getString("model"),
                    rs.getString("type"),
                    rs.getDouble("rent_per_day"),
                    rs.getString("location"),
                    rs.getBoolean("available")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting car: " + e.getMessage());
        }
        return null;
    }
    
    public boolean addCar(Car car) {
        String sql = "INSERT INTO cars (model, type, rent_per_day, location, available) VALUES (?, ?, ?, ?, ?) RETURNING id";
        
        try (Connection conn = PostgreSQLManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, car.getModel());
            stmt.setString(2, car.getType());
            stmt.setDouble(3, car.getRentPerDay());
            stmt.setString(4, car.getLocation());
            stmt.setBoolean(5, car.isAvailable());
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println("✅ Car added with ID: " + rs.getInt("id"));
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding car: " + e.getMessage());
        }
        return false;
    }
    
    public boolean updateCarAvailability(int carId, boolean available) {
        String sql = "UPDATE cars SET available = ? WHERE id = ?";
        
        try (Connection conn = PostgreSQLManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBoolean(1, available);
            stmt.setInt(2, carId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating car: " + e.getMessage());
        }
        return false;
    }
    
    public List<Car> getCarsByType(String type) {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT * FROM cars WHERE type = ? AND available = true ORDER BY rent_per_day";
        
        try (Connection conn = PostgreSQLManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, type);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                cars.add(new Car(
                    rs.getInt("id"),
                    rs.getString("model"),
                    rs.getString("type"),
                    rs.getDouble("rent_per_day"),
                    rs.getString("location"),
                    rs.getBoolean("available")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting cars by type: " + e.getMessage());
        }
        return cars;
    }
}

class UserDAO {
    public boolean addUser(User user) {
        String sql = "INSERT INTO users (username, password, role, email) VALUES (?, ?, ?, ?) ON CONFLICT (username) DO NOTHING RETURNING id";
        
        try (Connection conn = PostgreSQLManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole());
            stmt.setString(4, user.getEmail());
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println("✅ User added with ID: " + rs.getInt("id"));
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
        }
        return false;
    }
    
    public User authenticate(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        
        try (Connection conn = PostgreSQLManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("role"),
                    rs.getString("email")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
        }
        return null;
    }
}

class BookingDAO {
    public Booking createBooking(int userId, int carId, LocalDate startDate, LocalDate endDate) {
        // First check if car is available
        CarDAO carDAO = new CarDAO();
        Car car = carDAO.getCarById(carId);
        if (car == null || !car.isAvailable()) {
            System.err.println("❌ Car not available for booking");
            return null;
        }
        
        // Calculate total amount
        long days = endDate.toEpochDay() - startDate.toEpochDay();
        double totalAmount = days * car.getRentPerDay();
        
        String sql = "INSERT INTO bookings (user_id, car_id, start_date, end_date, total_amount) VALUES (?, ?, ?, ?, ?) RETURNING id";
        
        try (Connection conn = PostgreSQLManager.getConnection()) {
            conn.setAutoCommit(false); // Start transaction
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, carId);
                stmt.setDate(3, java.sql.Date.valueOf(startDate));
                stmt.setDate(4, java.sql.Date.valueOf(endDate));
                stmt.setDouble(5, totalAmount);
                
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    int bookingId = rs.getInt("id");
                    
                    // Mark car as unavailable
                    carDAO.updateCarAvailability(carId, false);
                    
                    conn.commit(); // Commit transaction
                    
                    return new Booking(bookingId, userId, carId, startDate, endDate, totalAmount);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating booking: " + e.getMessage());
        }
        return null;
    }
    
    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings ORDER BY created_at DESC";
        
        try (Connection conn = PostgreSQLManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                bookings.add(new Booking(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getInt("car_id"),
                    rs.getDate("start_date").toLocalDate(),
                    rs.getDate("end_date").toLocalDate(),
                    rs.getDouble("total_amount")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting bookings: " + e.getMessage());
        }
        return bookings;
    }
}

// Model Classes
class Car {
    private int id;
    private String model, type, location;
    private double rentPerDay;
    private boolean available;
    
    public Car(int id, String model, String type, double rentPerDay, String location, boolean available) {
        this.id = id; this.model = model; this.type = type; 
        this.rentPerDay = rentPerDay; this.location = location; this.available = available;
    }
    
    public int getId() { return id; }
    public String getModel() { return model; }
    public String getType() { return type; }
    public double getRentPerDay() { return rentPerDay; }
    public String getLocation() { return location; }
    public boolean isAvailable() { return available; }
    
    @Override
    public String toString() {
        return String.format("Car{id=%d, model='%s', type='%s', rent=₹%.0f/day, location='%s', available=%s}",
                id, model, type, rentPerDay, location, available);
    }
}

class User {
    private int id;
    private String username, password, role, email;
    
    public User(int id, String username, String password, String role, String email) {
        this.id = id; this.username = username; this.password = password; 
        this.role = role; this.email = email;
    }
    
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getEmail() { return email; }
}

class Booking {
    private int id, userId, carId;
    private LocalDate startDate, endDate;
    private double totalAmount;
    
    public Booking(int id, int userId, int carId, LocalDate startDate, LocalDate endDate, double totalAmount) {
        this.id = id; this.userId = userId; this.carId = carId;
        this.startDate = startDate; this.endDate = endDate; this.totalAmount = totalAmount;
    }
    
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getCarId() { return carId; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public double getTotalAmount() { return totalAmount; }
    
    @Override
    public String toString() {
        long days = endDate.toEpochDay() - startDate.toEpochDay();
        return String.format("Booking{id=%d, userId=%d, carId=%d, dates=%s to %s (%d days), amount=₹%.0f}",
                id, userId, carId, startDate, endDate, days, totalAmount);
    }
}