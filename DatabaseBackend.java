import java.sql.*;
import java.util.*;
import java.time.LocalDate;

public class DatabaseBackend {
    private static final String DB_URL = "jdbc:sqlite:car_rental.db";
    
    public static void main(String[] args) {
        DatabaseManager dbManager = new DatabaseManager();
        dbManager.initializeDatabase();
        
        CarDAO carDAO = new CarDAO();
        UserDAO userDAO = new UserDAO();
        BookingDAO bookingDAO = new BookingDAO();
        
        System.out.println("Car Rental Database Backend Started");
        
        // Demo operations
        demoOperations(carDAO, userDAO, bookingDAO);
    }
    
    private static void demoOperations(CarDAO carDAO, UserDAO userDAO, BookingDAO bookingDAO) {
        System.out.println("\n=== Database Operations Demo ===");
        
        // Add sample data
        carDAO.addCar(new Car(0, "Maruti Swift", "Hatchback", 1200, "All Cities", true));
        carDAO.addCar(new Car(0, "Honda City", "Sedan", 1800, "All Cities", true));
        carDAO.addCar(new Car(0, "Hyundai Creta", "SUV", 2500, "All Cities", true));
        
        userDAO.addUser(new User(0, "admin", "admin", "ADMIN", "admin@drivenow.com"));
        userDAO.addUser(new User(0, "user", "user", "USER", "user@drivenow.com"));
        
        // Display all cars
        System.out.println("\nAll Cars:");
        carDAO.getAllCars().forEach(System.out::println);
        
        // Create booking
        System.out.println("\nCreating booking...");
        Booking booking = new Booking(0, 2, 1, LocalDate.now(), LocalDate.now().plusDays(3), 3600);
        bookingDAO.addBooking(booking);
        
        // Display bookings
        System.out.println("\nAll Bookings:");
        bookingDAO.getAllBookings().forEach(System.out::println);
    }
}

// Database Manager
class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:car_rental.db";
    
    public void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            createTables(conn);
            System.out.println("Database initialized successfully");
        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
        }
    }
    
    private void createTables(Connection conn) throws SQLException {
        // Cars table
        String createCarsTable = """
            CREATE TABLE IF NOT EXISTS cars (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                model TEXT NOT NULL,
                type TEXT NOT NULL,
                rent_per_day REAL NOT NULL,
                location TEXT NOT NULL,
                available BOOLEAN DEFAULT TRUE
            )
        """;
        
        // Users table
        String createUsersTable = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL,
                role TEXT NOT NULL,
                email TEXT NOT NULL
            )
        """;
        
        // Bookings table
        String createBookingsTable = """
            CREATE TABLE IF NOT EXISTS bookings (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                car_id INTEGER NOT NULL,
                start_date DATE NOT NULL,
                end_date DATE NOT NULL,
                total_amount REAL NOT NULL,
                status TEXT DEFAULT 'CONFIRMED',
                FOREIGN KEY (user_id) REFERENCES users(id),
                FOREIGN KEY (car_id) REFERENCES cars(id)
            )
        """;
        
        conn.createStatement().execute(createCarsTable);
        conn.createStatement().execute(createUsersTable);
        conn.createStatement().execute(createBookingsTable);
    }
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}

// Data Access Objects (DAOs)
class CarDAO {
    public List<Car> getAllCars() {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT * FROM cars";
        
        try (Connection conn = DatabaseManager.getConnection();
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
        
        try (Connection conn = DatabaseManager.getConnection();
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
        String sql = "INSERT INTO cars (model, type, rent_per_day, location, available) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, car.getModel());
            stmt.setString(2, car.getType());
            stmt.setDouble(3, car.getRentPerDay());
            stmt.setString(4, car.getLocation());
            stmt.setBoolean(5, car.isAvailable());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding car: " + e.getMessage());
            return false;
        }
    }
    
    public boolean updateCarAvailability(int carId, boolean available) {
        String sql = "UPDATE cars SET available = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBoolean(1, available);
            stmt.setInt(2, carId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating car: " + e.getMessage());
            return false;
        }
    }
    
    public boolean deleteCar(int id) {
        String sql = "DELETE FROM cars WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting car: " + e.getMessage());
            return false;
        }
    }
}

class UserDAO {
    public boolean addUser(User user) {
        String sql = "INSERT INTO users (username, password, role, email) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole());
            stmt.setString(4, user.getEmail());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
            return false;
        }
    }
    
    public User authenticate(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
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
    public boolean addBooking(Booking booking) {
        String sql = "INSERT INTO bookings (user_id, car_id, start_date, end_date, total_amount) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, booking.getUserId());
            stmt.setInt(2, booking.getCarId());
            stmt.setDate(3, java.sql.Date.valueOf(booking.getStartDate()));
            stmt.setDate(4, java.sql.Date.valueOf(booking.getEndDate()));
            stmt.setDouble(5, booking.getTotalAmount());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding booking: " + e.getMessage());
            return false;
        }
    }
    
    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings";
        
        try (Connection conn = DatabaseManager.getConnection();
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

// Model Classes (same as before)
class Car {
    private int id;
    private String model, type, location;
    private double rentPerDay;
    private boolean available;
    
    public Car(int id, String model, String type, double rentPerDay, String location, boolean available) {
        this.id = id; this.model = model; this.type = type; 
        this.rentPerDay = rentPerDay; this.location = location; this.available = available;
    }
    
    // Getters
    public int getId() { return id; }
    public String getModel() { return model; }
    public String getType() { return type; }
    public double getRentPerDay() { return rentPerDay; }
    public String getLocation() { return location; }
    public boolean isAvailable() { return available; }
    
    @Override
    public String toString() {
        return String.format("Car{id=%d, model='%s', type='%s', rent=%.0f, location='%s', available=%s}",
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
    
    // Getters
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
    
    // Getters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getCarId() { return carId; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public double getTotalAmount() { return totalAmount; }
    
    @Override
    public String toString() {
        return String.format("Booking{id=%d, userId=%d, carId=%d, startDate=%s, endDate=%s, amount=%.0f}",
                id, userId, carId, startDate, endDate, totalAmount);
    }
}