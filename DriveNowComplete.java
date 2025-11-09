import java.io.*;
import java.net.*;
import java.util.*;
import java.time.LocalDate;

public class DriveNowComplete {
    private static Map<String, User> users = new HashMap<>();
    private static List<Car> cars = new ArrayList<>();
    private static List<Booking> bookings = new ArrayList<>();
    
    static {
        // Initialize users
        users.put("admin", new User("admin", "admin", "ADMIN", "admin@drivenow.com"));
        users.put("user", new User("user", "user", "USER", "user@drivenow.com"));
        
        // Initialize cars
        cars.add(new Car(2, "Maruti Swift", "Hatchback", 1200, "All Cities", true));
        cars.add(new Car(3, "Honda City", "Sedan", 1800, "All Cities", true));
        cars.add(new Car(4, "Hyundai Creta", "SUV", 2500, "All Cities", true));
        cars.add(new Car(6, "Tata Nexon", "SUV", 2200, "All Cities", true));
        cars.add(new Car(7, "Mahindra XUV300", "SUV", 2800, "All Cities", true));
        cars.add(new Car(8, "Kia Seltos", "SUV", 3200, "All Cities", true));
    }
    
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(5000);
        System.out.println("Drive Now Server started on http://localhost:5000");
        
        while (true) {
            Socket client = server.accept();
            new Thread(() -> handleClient(client)).start();
        }
    }
    
    private static void handleClient(Socket client) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream());
            
            String request = in.readLine();
            if (request != null) {
                String[] parts = request.split(" ");
                String method = parts[0];
                String path = parts[1];
                
                // Read headers
                String line;
                while ((line = in.readLine()) != null && !line.isEmpty()) {
                    // Skip headers
                }
                
                if (path.equals("/")) {
                    sendHomePage(out);
                } else if (path.equals("/login")) {
                    sendLoginPage(out);
                } else if (path.equals("/dashboard")) {
                    sendDashboard(out);
                } else if (path.equals("/admin")) {
                    sendAdminPage(out);
                } else {
                    send404(out);
                }
            }
            
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void sendHomePage(PrintWriter out) {
        StringBuilder carsHtml = new StringBuilder();
        String[] carEmojis = {"🚗", "🚙", "🚕", "🏎️", "🚐", "🚑"};
        
        for (int i = 0; i < cars.size(); i++) {
            Car car = cars.get(i);
            String emoji = carEmojis[i % carEmojis.length];
            carsHtml.append(String.format("""
                <div class="car-card">
                    <div class="car-image">%s</div>
                    <div class="car-info">
                        <h3>%s</h3>
                        <p class="car-type">%s</p>
                        <p class="car-location">📍 %s</p>
                        <div class="car-price">₹%,.0f/day</div>
                        <button class="btn btn-book" onclick="showLogin()">Login to Book</button>
                    </div>
                </div>
                """, emoji, car.model, car.type, car.location, car.rentPerDay));
        }
        
        String html = """
            HTTP/1.1 200 OK
            Content-Type: text/html
            
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Drive Now</title>
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
                    .brand { font-size: 1.8rem; font-weight: bold; color: #3b82f6; }
                    .btn { 
                        padding: 0.75rem 1.5rem; 
                        border: none; 
                        border-radius: 8px; 
                        background: #3b82f6; 
                        color: white; 
                        cursor: pointer;
                        text-decoration: none;
                        display: inline-block;
                        font-weight: 500;
                    }
                    .btn:hover { background: #2563eb; }
                    .container { 
                        max-width: 1200px; 
                        margin: 0 auto; 
                        padding: 2rem;
                        background: rgba(255,255,255,0.1);
                        border-radius: 20px;
                        backdrop-filter: blur(10px);
                    }
                    .dashboard-header { margin-bottom: 2rem; text-align: center; }
                    .dashboard-header h1 { 
                        color: white; 
                        font-size: 2.5rem; 
                        margin-bottom: 0.5rem; 
                        text-shadow: 2px 2px 4px rgba(0,0,0,0.3); 
                    }
                    .cars-grid { 
                        display: grid; 
                        grid-template-columns: repeat(auto-fill, minmax(350px, 1fr)); 
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
                        height: 200px; 
                        background: linear-gradient(45deg, rgba(102,126,234,0.2), rgba(118,75,162,0.2));
                        display: flex; 
                        align-items: center; 
                        justify-content: center; 
                        font-size: 4rem;
                    }
                    .car-info { padding: 1.5rem; }
                    .car-info h3 { font-size: 1.5rem; margin-bottom: 0.5rem; color: #1e293b; }
                    .car-type { color: #64748b; margin-bottom: 0.5rem; }
                    .car-location { color: #3b82f6; font-weight: 500; margin-bottom: 1rem; }
                    .car-price { 
                        font-size: 1.8rem; 
                        font-weight: bold; 
                        color: #059669; 
                        margin-bottom: 1rem; 
                    }
                    .btn-book { background: #059669; width: 100%; }
                    .btn-book:hover { background: #047857; }
                </style>
            </head>
            <body>
                <nav class="nav">
                    <div class="brand">Drive Now</div>
                    <div>
                        <a href="/login" class="btn">Login</a>
                    </div>
                </nav>
                
                <div class="container">
                    <div class="dashboard-header">
                        <h1>🚗 Available Cars</h1>
                        <p style="color: rgba(255,255,255,0.9); font-size: 1.1rem;">Login to book your perfect ride</p>
                    </div>
                    
                    <div class="cars-grid">
                        %s
                    </div>
                    
                    <div style="text-align: center; margin-top: 2rem;">
                        <a href="/login" class="btn" style="font-size: 1.2rem; padding: 1rem 2rem;">Login to Book Cars</a>
                    </div>
                </div>
                
                <script>
                    function showLogin() {
                        window.location.href = '/login';
                    }
                </script>
            </body>
            </html>
            """.formatted(carsHtml.toString());
        
        out.print(html);
        out.flush();
    }
    
    private static void sendLoginPage(PrintWriter out) {
        String html = """
            HTTP/1.1 200 OK
            Content-Type: text/html
            
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Login - Drive Now</title>
                <style>
                    * { margin: 0; padding: 0; box-sizing: border-box; }
                    body { 
                        font-family: Arial, sans-serif; 
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        min-height: 100vh;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                    }
                    .auth-card { 
                        background: white; 
                        padding: 2.5rem; 
                        border-radius: 16px; 
                        box-shadow: 0 20px 25px rgba(0,0,0,0.1);
                        text-align: center;
                        width: 100%;
                        max-width: 400px;
                    }
                    .brand { font-size: 2rem; font-weight: bold; color: #3b82f6; margin-bottom: 1.5rem; }
                    .form-group { margin-bottom: 1rem; text-align: left; }
                    input { 
                        width: 100%; 
                        padding: 0.875rem; 
                        border: 2px solid #e2e8f0; 
                        border-radius: 8px; 
                        font-size: 1rem;
                    }
                    .btn { 
                        width: 100%;
                        padding: 0.875rem; 
                        border: none; 
                        border-radius: 8px; 
                        background: #3b82f6; 
                        color: white; 
                        font-weight: 500; 
                        cursor: pointer; 
                        font-size: 1rem;
                    }
                    .btn:hover { background: #2563eb; }
                    .demo { 
                        margin-top: 1rem; 
                        padding: 0.75rem; 
                        background: #f1f5f9; 
                        border-radius: 6px; 
                        font-size: 0.875rem;
                    }
                </style>
            </head>
            <body>
                <div class="auth-card">
                    <div class="brand">Drive Now</div>
                    <h2>Welcome Back</h2>
                    <p style="color: #64748b; margin-bottom: 2rem;">Sign in to your account</p>
                    
                    <div>
                        <div class="form-group">
                            <input type="text" id="username" placeholder="Username" required>
                        </div>
                        <div class="form-group">
                            <input type="password" id="password" placeholder="Password" required>
                        </div>
                        <button onclick="handleLogin()" class="btn">Sign In</button>
                        <div style="margin-top: 1rem;">
                            <button onclick="window.location.href='/dashboard'" class="btn" style="background: #059669; margin-right: 0.5rem;">User Dashboard</button>
                            <button onclick="window.location.href='/admin'" class="btn" style="background: #dc2626;">Admin Panel</button>
                        </div>
                    </div>
                    
                    <div class="demo">
                        <strong>Demo Accounts:</strong><br>
                        Admin: admin/admin<br>
                        User: user/user
                    </div>
                </div>
                
                <script>
                    function handleLogin() {
                        const username = document.getElementById('username').value;
                        const password = document.getElementById('password').value;
                        
                        if (username === 'admin' && password === 'admin') {
                            alert('✅ Admin login successful!');
                            window.location.href = '/admin';
                        } else if (username === 'user' && password === 'user') {
                            alert('✅ User login successful!');
                            window.location.href = '/dashboard';
                        } else if (username && password) {
                            alert('❌ Invalid credentials! Use demo accounts below.');
                        } else {
                            alert('⚠️ Please enter username and password');
                        }
                    }
                </script>
            </body>
            </html>
            """;
        out.print(html);
        out.flush();
    }
    
    private static void sendDashboard(PrintWriter out) {
        StringBuilder carsHtml = new StringBuilder();
        String[] carImages = {
            "https://images.unsplash.com/photo-1552519507-da3b142c6e3d?w=400",
            "https://images.unsplash.com/photo-1563720223185-11003d516935?w=400", 
            "https://images.unsplash.com/photo-1544636331-e26879cd4d9b?w=400",
            "https://images.unsplash.com/photo-1583121274602-3e2820c69888?w=400",
            "https://images.unsplash.com/photo-1605559424843-9e4c228bf1c2?w=400",
            "https://images.unsplash.com/photo-1549317661-bd32c8ce0db2?w=400"
        };
        
        for (int i = 0; i < cars.size(); i++) {
            Car car = cars.get(i);
            String image = carImages[i % carImages.length];
            carsHtml.append(String.format("""
                <div class="car-card">
                    <div class="car-image" style="background-image: url('%s'); background-size: cover; background-position: center;"></div>
                    <div class="car-info">
                        <h3>%s</h3>
                        <p class="car-type">%s</p>
                        <p class="car-location">📍 %s</p>
                        <div class="car-price">₹%,.0f/day</div>
                        <button class="btn btn-book" onclick="bookCar('%s', '%s', %,.0f)">Book Now</button>
                    </div>
                </div>
                """, image, car.model, car.type, car.location, car.rentPerDay, car.model, car.location, car.rentPerDay));
        }
        
        String html = """
            HTTP/1.1 200 OK
            Content-Type: text/html
            
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Dashboard - Drive Now</title>
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
                    .brand { font-size: 1.8rem; font-weight: bold; color: #3b82f6; }
                    .btn { 
                        padding: 0.5rem 1rem; 
                        border: none; 
                        border-radius: 6px; 
                        text-decoration: none; 
                        font-weight: 500; 
                        cursor: pointer;
                        background: #ef4444;
                        color: white;
                    }
                    .container { 
                        max-width: 1200px; 
                        margin: 0 auto; 
                        padding: 2rem;
                        background: rgba(255,255,255,0.1);
                        border-radius: 20px;
                        backdrop-filter: blur(10px);
                    }
                    .dashboard-header { margin-bottom: 2rem; text-align: center; }
                    .dashboard-header h1 { 
                        color: white; 
                        font-size: 2.5rem; 
                        margin-bottom: 0.5rem; 
                        text-shadow: 2px 2px 4px rgba(0,0,0,0.3); 
                    }
                    .filters-section {
                        display: flex;
                        gap: 2rem;
                        justify-content: center;
                        margin: 2rem 0;
                        flex-wrap: wrap;
                    }
                    .filter-group {
                        display: flex;
                        flex-direction: column;
                        align-items: center;
                        gap: 0.5rem;
                    }
                    .filter-group label {
                        font-weight: 600;
                        color: white;
                        text-shadow: 1px 1px 2px rgba(0,0,0,0.5);
                    }
                    .filter-group select {
                        padding: 0.75rem;
                        border-radius: 8px;
                        border: 2px solid rgba(255,255,255,0.3);
                        background: rgba(255,255,255,0.9);
                        min-width: 150px;
                    }
                    .cars-grid { 
                        display: grid; 
                        grid-template-columns: repeat(auto-fill, minmax(350px, 1fr)); 
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
                        height: 200px; 
                        position: relative;
                        overflow: hidden;
                    }
                    .car-info { padding: 1.5rem; }
                    .car-info h3 { font-size: 1.5rem; margin-bottom: 0.5rem; color: #1e293b; }
                    .car-type { color: #64748b; margin-bottom: 0.5rem; }
                    .car-location { color: #3b82f6; font-weight: 500; margin-bottom: 1rem; }
                    .car-price { 
                        font-size: 1.8rem; 
                        font-weight: bold; 
                        color: #059669; 
                        margin-bottom: 1rem; 
                    }
                    .btn-book { background: #3b82f6; color: white; width: 100%; padding: 0.75rem; }
                    .btn-book:hover { background: #2563eb; }
                </style>
            </head>
            <body>
                <nav class="nav">
                    <div class="brand">Drive Now</div>
                    <div>
                        <span style="margin-right: 1rem; color: white;">Welcome, User</span>
                        <a href="/" class="btn">Logout</a>
                    </div>
                </nav>
                
                <div class="container">
                    <div class="dashboard-header">
                        <h1>🚗 Drive Now - Available Cars</h1>
                        <p style="color: rgba(255,255,255,0.9); font-size: 1.1rem;">Choose from our premium fleet - Available in all major cities</p>
                    </div>
                    
                    <div class="filters-section">
                        <div class="filter-group">
                            <label>📍 Location:</label>
                            <select>
                                <option value="">All Locations</option>
                                <option value="Mumbai">Mumbai</option>
                                <option value="Delhi">Delhi</option>
                                <option value="Bangalore">Bangalore</option>
                                <option value="Chennai">Chennai</option>
                                <option value="Pune">Pune</option>
                                <option value="Hyderabad">Hyderabad</option>
                                <option value="Kolkata">Kolkata</option>
                            </select>
                        </div>
                        <div class="filter-group">
                            <label>🚙 Car Type:</label>
                            <select>
                                <option value="">All Types</option>
                                <option value="Hatchback">Hatchback</option>
                                <option value="Sedan">Sedan</option>
                                <option value="SUV">SUV</option>
                            </select>
                        </div>
                        <div class="filter-group">
                            <label>💰 Price Range:</label>
                            <select>
                                <option value="">All Prices</option>
                                <option value="0-1500">Under ₹1,500</option>
                                <option value="1500-2500">₹1,500 - ₹2,500</option>
                                <option value="2500-5000">₹2,500+</option>
                            </select>
                        </div>
                    </div>
                    
                    <div class="cars-grid">
                        %s
                    </div>
                </div>
                
                <script>
                    function bookCar(carModel, location, dailyRate) {
                        const startDate = prompt('🗓️ Enter pickup date (YYYY-MM-DD):');
                        if (!startDate) return;
                        
                        const endDate = prompt('🗓️ Enter return date (YYYY-MM-DD):');
                        if (!endDate) return;
                        
                        const phone = prompt('📱 Enter your phone number:');
                        if (!phone) return;
                        
                        // Calculate days and total cost
                        const start = new Date(startDate);
                        const end = new Date(endDate);
                        const days = Math.ceil((end - start) / (1000 * 60 * 60 * 24));
                        const totalCost = days * dailyRate;
                        
                        alert('🎉 Booking Confirmed!\\n\\n' +
                              '🚗 Car: ' + carModel + '\\n' +
                              '📍 Location: ' + location + '\\n' +
                              '📅 Pickup: ' + startDate + '\\n' +
                              '📅 Return: ' + endDate + ' by 9:00 PM\\n' +
                              '📊 Duration: ' + days + ' days\\n' +
                              '💰 Total Cost: ₹' + totalCost.toLocaleString() + '\\n' +
                              '📱 Contact: ' + phone + '\\n\\n' +
                              '⚠️ IMPORTANT: Please return the car by 9:00 PM on ' + endDate + '\\n' +
                              '✅ Confirmation details will be sent to you!');
                    }
                </script>
            </body>
            </html>
            """.formatted(carsHtml.toString());
        
        out.print(html);
        out.flush();
    }
    
    private static void sendAdminPage(PrintWriter out) {
        StringBuilder carsTable = new StringBuilder();
        for (Car car : cars) {
            carsTable.append(String.format("""
                <tr>
                    <td>%d</td>
                    <td>%s</td>
                    <td>%s</td>
                    <td>%s</td>
                    <td>₹%,.0f</td>
                    <td><span style="color: %s;">%s</span></td>
                </tr>
                """, car.id, car.model, car.type, car.location, car.rentPerDay,
                car.available ? "#059669" : "#dc2626",
                car.available ? "Available" : "Booked"));
        }
        
        String html = """
            HTTP/1.1 200 OK
            Content-Type: text/html
            
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Admin Dashboard - Drive Now</title>
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
                    .brand { font-size: 1.8rem; font-weight: bold; color: #3b82f6; }
                    .btn { 
                        padding: 0.5rem 1rem; 
                        border: none; 
                        border-radius: 6px; 
                        text-decoration: none; 
                        font-weight: 500; 
                        cursor: pointer;
                        background: #ef4444;
                        color: white;
                    }
                    .container { 
                        max-width: 1200px; 
                        margin: 0 auto; 
                        padding: 2rem;
                    }
                    .admin-section { 
                        background: rgba(255,255,255,0.1); 
                        padding: 2rem; 
                        margin-bottom: 2rem; 
                        border-radius: 12px; 
                        backdrop-filter: blur(10px);
                    }
                    .admin-section h2 { color: white; margin-bottom: 1rem; }
                    .form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; margin-bottom: 1rem; }
                    .form-group { margin-bottom: 1rem; }
                    input, select { 
                        width: 100%; 
                        padding: 0.75rem; 
                        border: 2px solid rgba(255,255,255,0.3); 
                        border-radius: 8px; 
                        background: rgba(255,255,255,0.9);
                    }
                    .btn-primary { background: #3b82f6; color: white; padding: 0.75rem 1.5rem; }
                    table { width: 100%; border-collapse: collapse; margin-top: 1rem; background: rgba(255,255,255,0.9); border-radius: 8px; overflow: hidden; }
                    th, td { padding: 0.75rem; text-align: left; border-bottom: 1px solid #e2e8f0; }
                    th { background: #f8fafc; font-weight: 600; }
                </style>
            </head>
            <body>
                <nav class="nav">
                    <div class="brand">Drive Now Admin</div>
                    <div>
                        <span style="margin-right: 1rem; color: white;">Admin Panel</span>
                        <a href="/" class="btn">Logout</a>
                    </div>
                </nav>
                
                <div class="container">
                    <div class="admin-section">
                        <h2>Add New Car</h2>
                        <form onsubmit="addCar(event)">
                            <div class="form-row">
                                <div class="form-group">
                                    <input type="text" id="model" placeholder="Model (e.g., Toyota Innova)" required>
                                </div>
                                <div class="form-group">
                                    <select id="type" required>
                                        <option value="">Select Car Type</option>
                                        <option value="Hatchback">Hatchback</option>
                                        <option value="Sedan">Sedan</option>
                                        <option value="SUV">SUV</option>
                                        <option value="Luxury">Luxury</option>
                                    </select>
                                </div>
                            </div>
                            <div class="form-row">
                                <div class="form-group">
                                    <input type="number" id="rent" placeholder="Rent per day" required>
                                </div>
                                <div class="form-group">
                                    <select id="location" required>
                                        <option value="">Select Location</option>
                                        <option value="All Cities">All Cities</option>
                                        <option value="Mumbai">Mumbai</option>
                                        <option value="Delhi">Delhi</option>
                                        <option value="Bangalore">Bangalore</option>
                                        <option value="Chennai">Chennai</option>
                                        <option value="Pune">Pune</option>
                                        <option value="Hyderabad">Hyderabad</option>
                                        <option value="Kolkata">Kolkata</option>
                                    </select>
                                </div>
                            </div>
                            <button type="submit" class="btn btn-primary">Add Car</button>
                        </form>
                    </div>
                    
                    <div class="admin-section">
                        <h2>All Cars</h2>
                        <table>
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Model</th>
                                    <th>Type</th>
                                    <th>Location</th>
                                    <th>Rent/Day</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                %s
                            </tbody>
                        </table>
                    </div>
                </div>
                
                <script>
                    function addCar(event) {
                        event.preventDefault();
                        const model = document.getElementById('model').value;
                        const type = document.getElementById('type').value;
                        const rent = document.getElementById('rent').value;
                        const location = document.getElementById('location').value;
                        
                        alert('Car "' + model + '" added successfully!');
                        document.querySelector('form').reset();
                    }
                </script>
            </body>
            </html>
            """.formatted(carsTable.toString());
        
        out.print(html);
        out.flush();
    }
    
    private static void send404(PrintWriter out) {
        String html = """
            HTTP/1.1 404 Not Found
            Content-Type: text/html
            
            <h1>404 - Page Not Found</h1>
            <a href="/">Go Home</a>
            """;
        out.print(html);
        out.flush();
    }
    
    // Model Classes
    static class User {
        String username, password, role, email;
        User(String username, String password, String role, String email) {
            this.username = username; this.password = password; this.role = role; this.email = email;
        }
    }
    
    static class Car {
        int id; String model, type, location; double rentPerDay; boolean available;
        Car(int id, String model, String type, double rentPerDay, String location, boolean available) {
            this.id = id; this.model = model; this.type = type; this.rentPerDay = rentPerDay; 
            this.location = location; this.available = available;
        }
    }
    
    static class Booking {
        int id; User user; Car car; LocalDate startDate, endDate; double totalAmount;
        Booking(int id, User user, Car car, LocalDate startDate, LocalDate endDate, double totalAmount) {
            this.id = id; this.user = user; this.car = car; this.startDate = startDate; 
            this.endDate = endDate; this.totalAmount = totalAmount;
        }
    }
}