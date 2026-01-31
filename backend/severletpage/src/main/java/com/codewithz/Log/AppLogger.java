package com.codewithz.Log;
import jakarta.servlet.http.HttpServlet;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class AppLogger extends HttpServlet {
    public static String LOG_FILE = "D:/java/severletpage/src/main/webapp/logs/app.log";
    private static final DateTimeFormatter FORMAT =DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    // Thread-safe logging
    public static synchronized void log(String action, Integer userId, String status, String message) {
        String time = LocalDateTime.now().format(FORMAT);//from this we will get the current time with our format
        String logLine = String.format("%s | user=%s | %s | %s | %s%n", time, userId == null ? "unknown" : userId, action, status, message);


        try (FileWriter fw = new FileWriter(LOG_FILE, true)) {

            fw.write(logLine);
        } catch (Exception e) {
          
            e.printStackTrace(); // fallback
        }
    }
}
