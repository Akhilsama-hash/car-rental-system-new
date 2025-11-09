import java.io.*;
import java.net.*;
import java.time.LocalDate;
import java.util.List;

// REST API Server
public class RestAPI {
    private static CarService carService = new CarService();
    private static UserService userService = new UserService();
    private static BookingService bookingService = new BookingService();
    
    public static void main(String[] args) throws IOException {
        initializeData();
        
        ServerSocket server = new ServerSocket(9090);
        System.out.println("Car Rental API Server started on http://localhost:9090");
        System.out.println("Available endpoints:");
        System.out.println("GET /api/cars - Get all cars");
        System.out.println("GET /api/cars/available - Get available cars");
        System.out.println("POST /api/auth/login - User login");
        System.out.println("POST /api/bookings - Create booking");
        System.out.println("GET /api/bookings - Get all bookings");
        
        while (true) {
            Socket client = server.accept();
            new Thread(() -> handleRequest(client)).start();
        }
    }
    
    private static void initializeData() {
        // Add cars
        carService.addCar(new Car(1, "Maruti Swift", "Hatchback", 1200, "All Cities", true));
        carService.addCar(new Car(2, "Honda City", "Sedan", 1800, "All Cities", true));
        carService.addCar(new Car(3, "Hyundai Creta", "SUV", 2500, "All Cities", true));
        carService.addCar(new Car(4, "Tata Nexon", "SUV", 2200, "All Cities", true));
        carService.addCar(new Car(5, "Mahindra XUV300", "SUV", 2800, "All Cities", true));
        carService.addCar(new Car(6, "Kia Seltos", "SUV", 3200, "All Cities", true));
        
        // Add users
        userService.addUser(new User(1, "admin", "admin", "ADMIN", "admin@drivenow.com"));
        userService.addUser(new User(2, "user", "user", "USER", "user@drivenow.com"));
    }
    
    private static void handleRequest(Socket client) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream());
            
            String requestLine = in.readLine();
            if (requestLine == null) return;
            
            String[] parts = requestLine.split(" ");
            String method = parts[0];
            String path = parts[1];
            
            // Read headers
            String line;
            while ((line = in.readLine()) != null && !line.isEmpty()) {
                // Skip headers for now
            }
            
            String response = "";
            
            // Route handling
            if (method.equals("GET") && path.equals("/api/cars")) {
                response = getCarsResponse();
            } else if (method.equals("GET") && path.equals("/api/cars/available")) {
                response = getAvailableCarsResponse();
            } else if (method.equals("GET") && path.equals("/api/bookings")) {
                response = getBookingsResponse();
            } else if (method.equals("POST") && path.equals("/api/auth/login")) {
                response = loginResponse();
            } else if (method.equals("POST") && path.equals("/api/bookings")) {
                response = createBookingResponse();
            } else if (path.equals("/")) {
                response = getHomeResponse();
            } else {
                response = get404Response();
            }
            
            out.print(response);
            out.flush();
            client.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static String getCarsResponse() {
        List<Car> cars = carService.getAllCars();
        StringBuilder json = new StringBuilder();
        json.append("{\n  \"success\": true,\n  \"data\": [\n");
        
        for (int i = 0; i < cars.size(); i++) {
            Car car = cars.get(i);
            json.append("    {\n");
            json.append("      \"id\": ").append(car.getId()).append(",\n");
            json.append("      \"model\": \"").append(car.getModel()).append("\",\n");
            json.append("      \"type\": \"").append(car.getType()).append("\",\n");
            json.append("      \"rentPerDay\": ").append(car.getRentPerDay()).append(",\n");
            json.append("      \"location\": \"").append(car.getLocation()).append("\",\n");
            json.append("      \"available\": ").append(car.isAvailable()).append("\n");
            json.append("    }");
            if (i < cars.size() - 1) json.append(",");
            json.append("\n");
        }
        
        json.append("  ]\n}");
        
        return "HTTP/1.1 200 OK\r\n" +
               "Content-Type: application/json\r\n" +
               "Access-Control-Allow-Origin: *\r\n" +
               "Connection: close\r\n\r\n" +
               json.toString();
    }
    
    private static String getAvailableCarsResponse() {
        List<Car> cars = carService.getAvailableCars();
        StringBuilder json = new StringBuilder();
        json.append("{\n  \"success\": true,\n  \"message\": \"Available cars retrieved\",\n  \"data\": [\n");
        
        for (int i = 0; i < cars.size(); i++) {
            Car car = cars.get(i);
            json.append("    {\n");
            json.append("      \"id\": ").append(car.getId()).append(",\n");
            json.append("      \"model\": \"").append(car.getModel()).append("\",\n");
            json.append("      \"type\": \"").append(car.getType()).append("\",\n");
            json.append("      \"rentPerDay\": ").append(car.getRentPerDay()).append(",\n");
            json.append("      \"location\": \"").append(car.getLocation()).append("\"\n");
            json.append("    }");
            if (i < cars.size() - 1) json.append(",");
            json.append("\n");
        }
        
        json.append("  ]\n}");
        
        return "HTTP/1.1 200 OK\r\n" +
               "Content-Type: application/json\r\n" +
               "Access-Control-Allow-Origin: *\r\n" +
               "Connection: close\r\n\r\n" +
               json.toString();
    }
    
    private static String getBookingsResponse() {
        List<Booking> bookings = bookingService.getAllBookings();
        StringBuilder json = new StringBuilder();
        json.append("{\n  \"success\": true,\n  \"data\": [\n");
        
        for (int i = 0; i < bookings.size(); i++) {
            Booking booking = bookings.get(i);
            json.append("    {\n");
            json.append("      \"id\": ").append(booking.getId()).append(",\n");
            json.append("      \"userId\": ").append(booking.getUserId()).append(",\n");
            json.append("      \"carId\": ").append(booking.getCarId()).append(",\n");
            json.append("      \"startDate\": \"").append(booking.getStartDate()).append("\",\n");
            json.append("      \"endDate\": \"").append(booking.getEndDate()).append("\",\n");
            json.append("      \"totalAmount\": ").append(booking.getTotalAmount()).append(",\n");
            json.append("      \"status\": \"").append(booking.getStatus()).append("\"\n");
            json.append("    }");
            if (i < bookings.size() - 1) json.append(",");
            json.append("\n");
        }
        
        json.append("  ]\n}");
        
        return "HTTP/1.1 200 OK\r\n" +
               "Content-Type: application/json\r\n" +
               "Access-Control-Allow-Origin: *\r\n" +
               "Connection: close\r\n\r\n" +
               json.toString();
    }
    
    private static String loginResponse() {
        // Simplified login response
        String json = "{\n" +
                     "  \"success\": true,\n" +
                     "  \"message\": \"Login successful\",\n" +
                     "  \"data\": {\n" +
                     "    \"userId\": 1,\n" +
                     "    \"username\": \"user\",\n" +
                     "    \"role\": \"USER\",\n" +
                     "    \"token\": \"jwt-token-here\"\n" +
                     "  }\n" +
                     "}";
        
        return "HTTP/1.1 200 OK\r\n" +
               "Content-Type: application/json\r\n" +
               "Access-Control-Allow-Origin: *\r\n" +
               "Connection: close\r\n\r\n" +
               json;
    }
    
    private static String createBookingResponse() {
        // Simplified booking creation
        Booking booking = bookingService.createBooking(1, 1, LocalDate.now(), LocalDate.now().plusDays(3));
        
        String json = "{\n" +
                     "  \"success\": true,\n" +
                     "  \"message\": \"Booking created successfully\",\n" +
                     "  \"data\": {\n" +
                     "    \"id\": " + booking.getId() + ",\n" +
                     "    \"userId\": " + booking.getUserId() + ",\n" +
                     "    \"carId\": " + booking.getCarId() + ",\n" +
                     "    \"startDate\": \"" + booking.getStartDate() + "\",\n" +
                     "    \"endDate\": \"" + booking.getEndDate() + "\",\n" +
                     "    \"totalAmount\": " + booking.getTotalAmount() + ",\n" +
                     "    \"status\": \"" + booking.getStatus() + "\"\n" +
                     "  }\n" +
                     "}";
        
        return "HTTP/1.1 200 OK\r\n" +
               "Content-Type: application/json\r\n" +
               "Access-Control-Allow-Origin: *\r\n" +
               "Connection: close\r\n\r\n" +
               json;
    }
    
    private static String getHomeResponse() {
        String html = "<!DOCTYPE html>" +
                     "<html><head><title>Car Rental API</title></head>" +
                     "<body style='font-family:Arial;padding:20px;background:#f5f5f5'>" +
                     "<h1>🚗 Car Rental API Server</h1>" +
                     "<h2>Available Endpoints:</h2>" +
                     "<ul>" +
                     "<li><a href='/api/cars'>GET /api/cars</a> - Get all cars</li>" +
                     "<li><a href='/api/cars/available'>GET /api/cars/available</a> - Get available cars</li>" +
                     "<li><a href='/api/bookings'>GET /api/bookings</a> - Get all bookings</li>" +
                     "<li>POST /api/auth/login - User login</li>" +
                     "<li>POST /api/bookings - Create booking</li>" +
                     "</ul>" +
                     "<p>Server running on port 8080</p>" +
                     "</body></html>";
        
        return "HTTP/1.1 200 OK\r\n" +
               "Content-Type: text/html\r\n" +
               "Connection: close\r\n\r\n" +
               html;
    }
    
    private static String get404Response() {
        String json = "{\n" +
                     "  \"success\": false,\n" +
                     "  \"message\": \"Endpoint not found\"\n" +
                     "}";
        
        return "HTTP/1.1 404 Not Found\r\n" +
               "Content-Type: application/json\r\n" +
               "Access-Control-Allow-Origin: *\r\n" +
               "Connection: close\r\n\r\n" +
               json;
    }
}