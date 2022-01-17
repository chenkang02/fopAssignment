/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package assignmentq2groupg;

import java.sql.*;

/**
 *
 * @author KWT
 */
public class SQLConnector {
    // Get connection from java to sql
    public static Connection getSQLConnection() {
        try{
            String url = "jdbc:mysql://localhost:3306/maya2.0";
            String username = "root";
            String password = "CKlim@98305751";
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            return connection;
        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
