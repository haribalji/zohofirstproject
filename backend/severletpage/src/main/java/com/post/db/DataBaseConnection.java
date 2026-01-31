package com.post.db;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
public class DataBaseConnection {

    static    String username = System.getenv("DB_USERNAME");
    static   String url = System.getenv("DB_URL");
    static    String password = System.getenv("DB_PASSWORD");

//as this throws error when connection try to established that time a error can
// occur to handle  it we use try
//it is the method to call and  connect with the database

//  why stactic varibles then  only it can be accessed by the  static method
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
//        driver is used to connect with the database
    }
}

//

//
//        import java.sql.Connection;
//        import java.sql.DriverManager;
//public class dbconnection {
//
//    private static volatile Connection connection;
//
//    private static final String URL = "jdbc:postgresql://localhost:5432/posts";
//    private static final String USER = "postgres";
//    private static final String PASSWORD = "hari";
//
//    // private constructor =prevents object creation
//    private dbconnection() {}
//
//    public static  Connection getConnection() {
//        try {
//            if (connection == null || connection.isClosed()) {
//                synchronized (dbconnection.class) {
//                    if (connection == null || connection.isClosed()) {
//                        Class.forName("org.postgresql.Driver");//loading the driver class
//                        connection = DriverManager.getConnection(URL, USER, PASSWORD);
//                        System.out.println("DB Connected");
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return connection;
//    }
//}
