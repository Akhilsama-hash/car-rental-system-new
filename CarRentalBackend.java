import java.util.*;
import java.time.LocalDate;

// Main Application Class
public class CarRentalBackend {
    private static CarService carService = new CarService();
    private static UserService userService = new UserService();
    private static BookingService bookingService = new BookingService();
    
    public static void main(String[] args) {
        // Initialize data
        initializeData();
        
        // Start REST API simulation
        System.out.println("Car Rental Backend Started");
        System.out.println("Available endpoints:");
        System.out.println("GET /api/cars - Get all cars");
        System.out.println("POST /api/bookings - Create booking");
        System.out.println("GET /api/users/{id} - Get user");
        
        // Demo API calls
        demoAPICalls();
    }
    
    private static void initializeData() {
        // Add cars
        carService.addCar(new Car(1, "Maruti Swift", "Hatchback", 1200, "All Cities", true));
        carService.addCar(new Car(2, "Honda City", "Sedan", 1800, "All Cities", true));
        carService.addCar(new Car(3, "Hyundai Creta", "SUV", 2500, "All Cities", true));
        carService.addCar(new Car(4, "Tata Nexon", "SUV", 2200, "All Cities", true));
        
        // Add users
        userService.addUser(new User(1, "admin", "admin", "ADMIN", "admin@drivenow.com"));
        userService.addUser(new User(2, "user", "user", "USER", "user@drivenow.com"));
    }
    
    private static void demoAPICalls() {
        System.out.println("\n=== Demo API Responses ===");
        
        // Get all cars
        System.out.println("\nGET /api/cars:");
        List<Car> cars = carService.getAllCars();
        cars.forEach(System.out::println);
        
        // Create booking
        System.out.println("\nPOST /api/bookings:");
        Booking booking = bookingService.createBooking(1, 1, LocalDate.now(), LocalDate.now().plusDays(3));
        System.out.println(booking);
        
        // Get user
        System.out.println("\nGET /api/users/1:");
        User user = userService.getUserById(1);
        System.out.println(user);
    }
}

// Models
class Car {
    private int id;
    private String model;
    private String type;
    private double rentPerDay;
    private String location;
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
    
    @Override
    public String toString() {
        return String.format("Car{id=%d, model='%s', type='%s', rent=%.0f, location='%s', available=%s}",
                id, model, type, rentPerDay, location, available);
    }
}

class User {
    private int id;
    private String username;
    private String password;
    private String role;
    private String email;
    
    public User(int id, String username, String password, String role, String email) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
    }
    
    // Getters and Setters
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
    private int id;
    private int userId;
    private int carId;
    private LocalDate startDate;
    private LocalDate endDate;
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
    
    // Getters and Setters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getCarId() { return carId; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public double getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    
    @Override
    public String toString() {
        return String.format("Booking{id=%d, userId=%d, carId=%d, startDate=%s, endDate=%s, amount=%.0f, status='%s'}",
                id, userId, carId, startDate, endDate, totalAmount, status);
    }
}

// Services
class CarService {
    private List<Car> cars = new ArrayList<>();
    private int nextId = 1;
    
    public List<Car> getAllCars() {
        return new ArrayList<>(cars);
    }
    
    public Car getCarById(int id) {
        return cars.stream().filter(car -> car.getId() == id).findFirst().orElse(null);
    }
    
    public Car addCar(Car car) {
        cars.add(car);
        return car;
    }
    
    public boolean removeCar(int id) {
        return cars.removeIf(car -> car.getId() == id);
    }
    
    public List<Car> getAvailableCars() {
        return cars.stream().filter(Car::isAvailable).toList();
    }
    
    public List<Car> filterCars(String type, String location, Double maxPrice) {
        return cars.stream()
                .filter(car -> type == null || car.getType().equalsIgnoreCase(type))
                .filter(car -> location == null || car.getLocation().contains(location))
                .filter(car -> maxPrice == null || car.getRentPerDay() <= maxPrice)
                .toList();
    }
}

class UserService {
    private List<User> users = new ArrayList<>();
    
    public User getUserById(int id) {
        return users.stream().filter(user -> user.getId() == id).findFirst().orElse(null);
    }
    
    public User getUserByUsername(String username) {
        return users.stream().filter(user -> user.getUsername().equals(username)).findFirst().orElse(null);
    }
    
    public User addUser(User user) {
        users.add(user);
        return user;
    }
    
    public User authenticate(String username, String password) {
        return users.stream()
                .filter(user -> user.getUsername().equals(username) && user.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }
}

class BookingService {
    private List<Booking> bookings = new ArrayList<>();
    private int nextId = 1;
    
    public Booking createBooking(int userId, int carId, LocalDate startDate, LocalDate endDate) {
        // Calculate total amount (simplified)
        Car car = new CarService().getCarById(carId);
        if (car == null || !car.isAvailable()) {
            throw new RuntimeException("Car not available");
        }
        
        long days = endDate.toEpochDay() - startDate.toEpochDay();
        double totalAmount = days * car.getRentPerDay();
        
        Booking booking = new Booking(nextId++, userId, carId, startDate, endDate, totalAmount);
        bookings.add(booking);
        
        // Mark car as unavailable
        car.setAvailable(false);
        
        return booking;
    }
    
    public List<Booking> getBookingsByUser(int userId) {
        return bookings.stream().filter(booking -> booking.getUserId() == userId).toList();
    }
    
    public List<Booking> getAllBookings() {
        return new ArrayList<>(bookings);
    }
    
    public boolean cancelBooking(int bookingId) {
        Booking booking = bookings.stream()
                .filter(b -> b.getId() == bookingId)
                .findFirst()
                .orElse(null);
        
        if (booking != null) {
            // Mark car as available again
            Car car = new CarService().getCarById(booking.getCarId());
            if (car != null) {
                car.setAvailable(true);
            }
            return bookings.removeIf(b -> b.getId() == bookingId);
        }
        return false;
    }
}

// API Response Classes
class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    
    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
    
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Success", data);
    }
    
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
    
    // Getters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public T getData() { return data; }
}