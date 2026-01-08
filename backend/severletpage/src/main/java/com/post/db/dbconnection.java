package com.post.db;

import java.sql.SQLException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class dbconnection {

     static   String url = "jdbc:postgresql://localhost:5432/posts";
    static    String username = "postgres";
     static    String password = "hari";
//as this throws error when connection try to established that time a error can occur to handle  it we use try

//    it is the method to call and  connect with the database

//  why   stactic varibles then  only it can be accessed by the  static method
    public static Connection getConnection() throws SQLException {
        System.out.println("it is called");
        return DriverManager.getConnection(url, username, password);
    }



}
