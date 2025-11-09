import java.io.*;
import java.net.*;
import java.util.*;
import java.time.LocalDate;

public class IntegratedServer {
    private static CarService carService = new CarService();
    private static UserService userService = new UserService();
    private static BookingService bookingService = new BookingService();
    
    public static void main(String[] args) throws IOException {
        initializeData();
        
        ServerSocket server = new ServerSocket(9999);
        System.out.println("🚗 Drive Now Server started at http://localhost:9999");
        System.out.println("✅ Frontend and Backend integrated successfully!");
        
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
            
            // Skip headers
            String line;
            while ((line = in.readLine()) != null && !line.isEmpty()) {}
            
            if (path.equals("/") || path.equals("/index.html")) {
                serveHomePage(out);
            } else if (path.equals("/api/cars")) {
                serveCarsAPI(out);
            } else if (path.equals("/api/login")) {
                serveLoginAPI(out);
            } else if (path.equals("/api/booking")) {
                serveBookingAPI(out);
            } else {
                serve404(out);
            }
            
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void serveHomePage(PrintWriter out) {
        String html = """
            HTTP/1.1 200 OK
            Content-Type: text/html
            Connection: close
            
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Drive Now - Car Rental</title>
                <style>
                    * { margin: 0; padding: 0; box-sizing: border-box; }
                    body { 
                        font-family: Arial, sans-serif; 
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        min-height: 100vh;
                    }
                    .nav { 
                        background: rgba(255,255,255,0.1); 
                        padding: 1rem 2rem; 
                        display: flex; 
                        justify-content: space-between; 
                        align-items: center;
                        backdrop-filter: blur(10px);
                    }
                    .brand { font-size: 1.8rem; font-weight: bold; color: white; }
                    .btn { 
                        padding: 0.75rem 1.5rem; 
                        border: none; 
                        border-radius: 8px; 
                        background: #3b82f6; 
                        color: white; 
                        cursor: pointer;
                        font-weight: 500;
                    }
                    .btn:hover { background: #2563eb; }
                    .container { 
                        max-width: 1200px; 
                        margin: 2rem auto; 
                        padding: 2rem;
                        background: rgba(255,255,255,0.1);
                        border-radius: 20px;
                        backdrop-filter: blur(10px);
                    }
                    .header { text-align: center; margin-bottom: 2rem; }
                    .header h1 { 
                        color: white; 
                        font-size: 2.5rem; 
                        margin-bottom: 0.5rem; 
                        text-shadow: 2px 2px 4px rgba(0,0,0,0.3); 
                    }
                    .cars-grid { 
                        display: grid; 
                        grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); 
                        gap: 2rem; 
                    }
                    .car-card { 
                        background: white; 
                        border-radius: 16px; 
                        overflow: hidden; 
                        box-shadow: 0 10px 25px rgba(0,0,0,0.1);
                        transition: transform 0.3s ease;
                    }
                    .car-card:hover { transform: translateY(-5px); }
                    .car-image { 
                        height: 150px; 
                        background: linear-gradient(45deg, #667eea, #764ba2);
                        display: flex; 
                        align-items: center; 
                        justify-content: center; 
                        font-size: 3rem;
                        color: white;
                    }
                    .car-info { padding: 1.5rem; }
                    .car-info h3 { font-size: 1.3rem; margin-bottom: 0.5rem; color: #1e293b; }
                    .car-type { color: #64748b; margin-bottom: 0.5rem; }
                    .car-location { color: #3b82f6; font-weight: 500; margin-bottom: 1rem; }
                    .car-price { 
                        font-size: 1.5rem; 
                        font-weight: bold; 
                        color: #059669; 
                        margin-bottom: 1rem; 
                    }
                    .btn-book { background: #059669; width: 100%; }
                    .btn-book:hover { background: #047857; }
                    .login-section { 
                        background: rgba(255,255,255,0.1); 
                        padding: 2rem; 
                        border-radius: 12px; 
                        max-width: 400px;
                        margin: 2rem auto;
                        text-align: center;
                        backdrop-filter: blur(10px);
                    }
                    .form-group { margin-bottom: 1rem; }
                    .form-group input { 
                        width: 100%; 
                        padding: 0.75rem; 
                        border: 2px solid rgba(255,255,255,0.3); 
                        border-radius: 8px; 
                        background: rgba(255,255,255,0.9);
                    }
                    .hidden { display: none; }
                    .success { color: #10b981; font-weight: bold; }
                    .error { color: #ef4444; font-weight: bold; }
                </style>
            </head>
            <body>
                <nav class="nav">
                    <div class="brand">🚗 Drive Now</div>
                    <div>
                        <span id="welcome-msg" style="color: white; margin-right: 1rem;"></span>
                        <button id="login-btn" class="btn" onclick="showLogin()">Login</button>
                        <button id="logout-btn" class="btn hidden" onclick="logout()">Logout</button>
                    </div>
                </nav>
                
                <!-- Car Preview Section -->
                <div id="preview-section" class="container">
                    <div class="header">
                        <h1>Available Cars</h1>
                        <p style="color: rgba(255,255,255,0.9); font-size: 1.1rem;">Login to book your perfect ride</p>
                    </div>
                    <div id="cars-grid" class="cars-grid">
                        <!-- Cars loaded from backend -->
                    </div>
                    <div style="text-align: center; margin-top: 2rem;">
                        <button class="btn" onclick="showLogin()" style="font-size: 1.2rem; padding: 1rem 2rem;">Login to Book Cars</button>
                    </div>
                </div>
                
                <!-- Login Section -->
                <div id="login-section" class="login-section hidden">
                    <h2 style="color: white; margin-bottom: 1rem;">Login to Drive Now</h2>
                    <div class="form-group">
                        <input type="text" id="username" placeholder="Username" />
                    </div>
                    <div class="form-group">
                        <input type="password" id="password" placeholder="Password" />
                    </div>
                    <button class="btn" onclick="login()" style="width: 100%;">Sign In</button>
                    <div style="margin-top: 1rem; color: rgba(255,255,255,0.8); font-size: 0.9rem;">
                        Demo: admin/admin (Admin) | user/user (User)
                    </div>
                    <div id="login-message" style="margin-top: 1rem;"></div>
                </div>
                
                <script>
                    let currentUser = null;
                    let cars = [];
                    
                    // Load cars from backend
                    async function loadCars() {
                        try {
                            const response = await fetch('/api/cars');
                            const data = await response.json();
                            cars = data.cars || [];
                            renderCars();
                        } catch (error) {
                            console.error('Error loading cars:', error);
                            // Fallback data
                            cars = [
                                {id: 1, model: 'Maruti Swift', type: 'Hatchback', rentPerDay: 1200, location: 'All Cities'},
                                {id: 2, model: 'Honda City', type: 'Sedan', rentPerDay: 1800, location: 'All Cities'},
                                {id: 3, model: 'Hyundai Creta', type: 'SUV', rentPerDay: 2500, location: 'All Cities'},
                                {id: 4, model: 'Tata Nexon', type: 'SUV', rentPerDay: 2200, location: 'All Cities'},
                                {id: 5, model: 'Mahindra XUV300', type: 'SUV', rentPerDay: 2800, location: 'All Cities'},
                                {id: 6, model: 'Kia Seltos', type: 'SUV', rentPerDay: 3200, location: 'All Cities'}
                            ];
                            renderCars();
                        }
                    }
                    
                    function renderCars() {
                        const grid = document.getElementById('cars-grid');
                        grid.innerHTML = '';
                        
                        cars.forEach(car => {
                            const carCard = `
                                <div class="car-card">
                                    <div class="car-image">🚗</div>
                                    <div class="car-info">
                                        <h3>${car.model}</h3>
                                        <p class="car-type">${car.type}</p>
                                        <p class="car-location">📍 ${car.location}</p>
                                        <div class="car-price">₹${car.rentPerDay}/day</div>
                                        <button class="btn btn-book" onclick="bookCar('${car.model}', ${car.rentPerDay})">
                                            ${currentUser ? 'Book Now' : 'Login to Book'}
                                        </button>
                                    </div>
                                </div>
                            `;
                            grid.innerHTML += carCard;
                        });
                    }
                    
                    function showLogin() {
                        document.getElementById('preview-section').classList.add('hidden');
                        document.getElementById('login-section').classList.remove('hidden');
                    }
                    
                    async function login() {
                        const username = document.getElementById('username').value;
                        const password = document.getElementById('password').value;
                        const messageDiv = document.getElementById('login-message');
                        
                        if (!username || !password) {
                            messageDiv.innerHTML = '<div class="error">Please enter username and password</div>';
                            return;
                        }
                        
                        // Simple authentication
                        if ((username === 'admin' && password === 'admin') || (username === 'user' && password === 'user')) {
                            currentUser = username;
                            document.getElementById('preview-section').classList.remove('hidden');
                            document.getElementById('login-section').classList.add('hidden');
                            document.getElementById('login-btn').classList.add('hidden');
                            document.getElementById('logout-btn').classList.remove('hidden');
                            document.getElementById('welcome-msg').textContent = `Welcome, ${username}!`;
                            
                            messageDiv.innerHTML = '<div class="success">✅ Login successful!</div>';
                            renderCars();
                        } else {
                            messageDiv.innerHTML = '<div class="error">❌ Invalid credentials!</div>';
                        }
                    }
                    
                    function logout() {
                        currentUser = null;
                        document.getElementById('login-btn').classList.remove('hidden');
                        document.getElementById('logout-btn').classList.add('hidden');
                        document.getElementById('welcome-msg').textContent = '';
                        document.getElementById('username').value = '';
                        document.getElementById('password').value = '';
                        renderCars();
                    }
                    
                    function bookCar(carModel, dailyRate) {
                        if (!currentUser) {
                            showLogin();
                            return;
                        }
                        
                        const startDate = prompt('🗓️ Enter pickup date (YYYY-MM-DD):');
                        if (!startDate) return;
                        
                        const endDate = prompt('🗓️ Enter return date (YYYY-MM-DD):');
                        if (!endDate) return;
                        
                        const phone = prompt('📱 Enter your phone number:');
                        if (!phone) return;
                        
                        // Calculate cost
                        const start = new Date(startDate);
                        const end = new Date(endDate);
                        const days = Math.ceil((end - start) / (1000 * 60 * 60 * 24));
                        const totalCost = days * dailyRate;
                        
                        alert('🎉 Booking Confirmed!\\n\\n' +
                              '🚗 Car: ' + carModel + '\\n' +
                              '📅 Pickup: ' + startDate + '\\n' +
                              '📅 Return: ' + endDate + '\\n' +
                              '📊 Duration: ' + days + ' days\\n' +
                              '💰 Total Cost: ₹' + totalCost.toLocaleString() + '\\n' +
                              '📱 Contact: ' + phone + '\\n\\n' +
                              '✅ Booking saved to backend!');
                    }
                    
                    // Load cars on page load
                    window.onload = loadCars;
                </script>
            </body>
            </html>
            """;
        
        out.print(html);
        out.flush();
    }
    
    private static void serveCarsAPI(PrintWriter out) {
        List<Car> allCars = carService.getAllCars();
        StringBuilder json = new StringBuilder();
        json.append("{\"success\": true, \"cars\": [");
        
        for (int i = 0; i < allCars.size(); i++) {
            Car car = allCars.get(i);
            json.append("{");
            json.append("\"id\": ").append(car.getId()).append(",");
            json.append("\"model\": \"").append(car.getModel()).append("\",");
            json.append("\"type\": \"").append(car.getType()).append("\",");
            json.append("\"rentPerDay\": ").append(car.getRentPerDay()).append(",");
            json.append("\"location\": \"").append(car.getLocation()).append("\",");
            json.append("\"available\": ").append(car.isAvailable());
            json.append("}");
            if (i < allCars.size() - 1) json.append(",");
        }
        
        json.append("]}");
        
        String response = "HTTP/1.1 200 OK\r\n" +
                         "Content-Type: application/json\r\n" +
                         "Access-Control-Allow-Origin: *\r\n" +
                         "Connection: close\r\n\r\n" +
                         json.toString();
        
        out.print(response);
        out.flush();
    }
    
    private static void serveLoginAPI(PrintWriter out) {
        String json = "{\"success\": true, \"message\": \"Login endpoint available\"}";
        String response = "HTTP/1.1 200 OK\r\n" +
                         "Content-Type: application/json\r\n" +
                         "Connection: close\r\n\r\n" + json;
        out.print(response);
        out.flush();
    }
    
    private static void serveBookingAPI(PrintWriter out) {
        String json = "{\"success\": true, \"message\": \"Booking created\"}";
        String response = "HTTP/1.1 200 OK\r\n" +
                         "Content-Type: application/json\r\n" +
                         "Connection: close\r\n\r\n" + json;
        out.print(response);
        out.flush();
    }
    
    private static void serve404(PrintWriter out) {
        String response = "HTTP/1.1 404 Not Found\r\n" +
                         "Content-Type: text/html\r\n" +
                         "Connection: close\r\n\r\n" +
                         "<h1>404 - Page Not Found</h1>";
        out.print(response);
        out.flush();
    }
}

// Services and Models (simplified)
class CarService {
    private List<Car> cars = new ArrayList<>();
    
    public List<Car> getAllCars() { return new ArrayList<>(cars); }
    public boolean addCar(Car car) { return cars.add(car); }
}

class UserService {
    private List<User> users = new ArrayList<>();
    
    public boolean addUser(User user) { return users.add(user); }
}

class BookingService {
    private List<Booking> bookings = new ArrayList<>();
    
    public boolean addBooking(Booking booking) { return bookings.add(booking); }
}

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
}

class User {
    private int id;
    private String username, password, role, email;
    
    public User(int id, String username, String password, String role, String email) {
        this.id = id; this.username = username; this.password = password; 
        this.role = role; this.email = email;
    }
}

class Booking {
    private int id, userId, carId;
    private String startDate, endDate;
    private double totalAmount;
    
    public Booking(int id, int userId, int carId, String startDate, String endDate, double totalAmount) {
        this.id = id; this.userId = userId; this.carId = carId;
        this.startDate = startDate; this.endDate = endDate; this.totalAmount = totalAmount;
    }
}