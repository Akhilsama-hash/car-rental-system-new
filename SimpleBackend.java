import java.util.*;
import java.time.LocalDate;
import java.io.*;

public class SimpleBackend {
    private static List<Car> cars = new ArrayList<>();
    private static List<User> users = new ArrayList<>();
    private static List<Booking> bookings = new ArrayList<>();
    
    public static void main(String[] args) {
        System.out.println("🚗 Car Rental Backend Started");
        
        // Initialize data
        initializeData();
        
        // Demo operations
        demoOperations();
        
        // Save data to files
        saveData();
    }
    
    private static void initializeData() {
        // Add cars
        cars.add(new Car(1, "Maruti Swift", "Hatchback", 1200, "All Cities", true));
        cars.add(new Car(2, "Honda City", "Sedan", 1800, "All Cities", true));
        cars.add(new Car(3, "Hyundai Creta", "SUV", 2500, "All Cities", true));
        cars.add(new Car(4, "Tata Nexon", "SUV", 2200, "All Cities", true));
        cars.add(new Car(5, "Mahindra XUV300", "SUV", 2800, "All Cities", true));
        cars.add(new Car(6, "Kia Seltos", "SUV", 3200, "All Cities", true));
        
        // Add users
        users.add(new User(1, "admin", "admin", "ADMIN", "admin@drivenow.com"));
        users.add(new User(2, "user", "user", "USER", "user@drivenow.com"));
        
        System.out.println("✅ Data initialized successfully");
    }
    
    private static void demoOperations() {
        System.out.println("\n=== Backend Operations Demo ===");
        
        // Display all cars
        System.out.println("\n📋 All Cars:");
        cars.forEach(System.out::println);
        
        // User authentication
        System.out.println("\n🔐 User Authentication:");
        User user = authenticate("user", "user");
        if (user != null) {
            System.out.println("✅ Login successful: " + user);
        }
        
        // Create booking
        System.out.println("\n📅 Creating Booking:");
        Booking booking = createBooking(2, 1, LocalDate.now(), LocalDate.now().plusDays(3));
        System.out.println("✅ Booking created: " + booking);
        
        // Display bookings
        System.out.println("\n📊 All Bookings:");
        bookings.forEach(System.out::println);
        
        // Filter cars
        System.out.println("\n🔍 SUV Cars:");
        getCarsByType("SUV").forEach(System.out::println);
    }
    
    // Car operations
    public static List<Car> getAllCars() {
        return new ArrayList<>(cars);
    }
    
    public static List<Car> getAvailableCars() {
        return cars.stream().filter(Car::isAvailable).toList();
    }
    
    public static List<Car> getCarsByType(String type) {
        return cars.stream().filter(car -> car.getType().equals(type)).toList();
    }
    
    public static Car getCarById(int id) {
        return cars.stream().filter(car -> car.getId() == id).findFirst().orElse(null);
    }
    
    public static boolean addCar(Car car) {
        return cars.add(car);
    }
    
    public static boolean removeCar(int id) {
        return cars.removeIf(car -> car.getId() == id);
    }
    
    // User operations
    public static User authenticate(String username, String password) {
        return users.stream()
                .filter(user -> user.getUsername().equals(username) && user.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }
    
    public static User getUserById(int id) {
        return users.stream().filter(user -> user.getId() == id).findFirst().orElse(null);
    }
    
    // Booking operations
    public static Booking createBooking(int userId, int carId, LocalDate startDate, LocalDate endDate) {
        Car car = getCarById(carId);
        if (car == null || !car.isAvailable()) {
            System.err.println("❌ Car not available for booking");
            return null;
        }
        
        long days = endDate.toEpochDay() - startDate.toEpochDay();
        double totalAmount = days * car.getRentPerDay();
        
        Booking booking = new Booking(bookings.size() + 1, userId, carId, startDate, endDate, totalAmount);
        bookings.add(booking);
        
        // Mark car as unavailable
        car.setAvailable(false);
        
        return booking;
    }
    
    public static List<Booking> getAllBookings() {
        return new ArrayList<>(bookings);
    }
    
    public static List<Booking> getBookingsByUser(int userId) {
        return bookings.stream().filter(booking -> booking.getUserId() == userId).toList();
    }
    
    // Data persistence
    private static void saveData() {
        try {
            // Save cars to file
            PrintWriter carWriter = new PrintWriter("cars_data.txt");
            cars.forEach(car -> carWriter.println(car.toFileString()));
            carWriter.close();
            
            // Save bookings to file
            PrintWriter bookingWriter = new PrintWriter("bookings_data.txt");
            bookings.forEach(booking -> bookingWriter.println(booking.toFileString()));
            bookingWriter.close();
            
            System.out.println("\n💾 Data saved to files successfully");
        } catch (IOException e) {
            System.err.println("❌ Error saving data: " + e.getMessage());
        }
    }
}

// Model Classes
class Car {
    private int id;
    private String model, type, location;
    private double rentPerDay;
    private boolean available;
    
    public Car(int id, String model, String type, double rentPerDay, String location, boolean available) {
        this.id = id;
        this.model = model;
        this.type = type;
        this.rentPerDay = rentPerDay;
        this.location = location;
        this.available = available;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public String getModel() { return model; }
    public String getType() { return type; }
    public double getRentPerDay() { return rentPerDay; }
    public String getLocation() { return location; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    
    public String toFileString() {
        return String.format("%d,%s,%s,%.0f,%s,%s", id, model, type, rentPerDay, location, available);
    }
    
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
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
    }
    
    // Getters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getEmail() { return email; }
    
    @Override
    public String toString() {
        return String.format("User{id=%d, username='%s', role='%s', email='%s'}",
                id, username, role, email);
    }
}

class Booking {
    private int id, userId, carId;
    private LocalDate startDate, endDate;
    private double totalAmount;
    private String status;
    
    public Booking(int id, int userId, int carId, LocalDate startDate, LocalDate endDate, double totalAmount) {
        this.id = id;
        this.userId = userId;
        this.carId = carId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalAmount = totalAmount;
        this.status = "CONFIRMED";
    }
    
    // Getters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getCarId() { return carId; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public double getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    
    public String toFileString() {
        return String.format("%d,%d,%d,%s,%s,%.0f,%s", 
                id, userId, carId, startDate, endDate, totalAmount, status);
    }
    
    @Override
    public String toString() {
        long days = endDate.toEpochDay() - startDate.toEpochDay();
        return String.format("Booking{id=%d, userId=%d, carId=%d, dates=%s to %s (%d days), amount=₹%.0f, status='%s'}",
                id, userId, carId, startDate, endDate, days, totalAmount, status);
    }
}