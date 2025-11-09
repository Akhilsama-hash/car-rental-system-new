import java.io.*;
import java.net.*;

public class SimpleServer {
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(8000);
        System.out.println("Server running at http://localhost:8000");
        
        while (true) {
            Socket client = server.accept();
            handleRequest(client);
        }
    }
    
    static void handleRequest(Socket client) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream());
            
            String request = in.readLine();
            while (in.readLine() != null && !in.readLine().isEmpty()) {}
            
            String html = "HTTP/1.1 200 OK\r\n" +
                         "Content-Type: text/html\r\n" +
                         "Connection: close\r\n\r\n" +
                         "<!DOCTYPE html>" +
                         "<html><head><title>Drive Now</title>" +
                         "<style>body{font-family:Arial;background:linear-gradient(135deg,#667eea,#764ba2);margin:0;padding:20px}" +
                         ".container{max-width:800px;margin:0 auto;background:rgba(255,255,255,0.1);padding:20px;border-radius:10px}" +
                         ".car{background:white;margin:10px;padding:15px;border-radius:8px;box-shadow:0 2px 10px rgba(0,0,0,0.1)}" +
                         ".btn{background:#3b82f6;color:white;padding:10px 20px;border:none;border-radius:5px;cursor:pointer}" +
                         "h1{color:white;text-align:center}</style></head>" +
                         "<body><div class='container'>" +
                         "<h1>🚗 Drive Now Car Rental</h1>" +
                         "<div class='car'><h3>Maruti Swift</h3><p>Hatchback - ₹1,200/day</p><button class='btn'>Book Now</button></div>" +
                         "<div class='car'><h3>Honda City</h3><p>Sedan - ₹1,800/day</p><button class='btn'>Book Now</button></div>" +
                         "<div class='car'><h3>Hyundai Creta</h3><p>SUV - ₹2,500/day</p><button class='btn'>Book Now</button></div>" +
                         "<div class='car'><h3>Tata Nexon</h3><p>SUV - ₹2,200/day</p><button class='btn'>Book Now</button></div>" +
                         "</div></body></html>";
            
            out.print(html);
            out.flush();
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}