/**
 * MySqlDataSource.java
 * Copyright (c) 2007 by Dr. Herong Yang. All rights reserved.
 */
import java.sql.*;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;


public class MySqlDataSource {
  public static void main(String [] args) {

    Connection con = null;
    try {
// Setting up the DataSource object
      MysqlDataSource ds = new MysqlDataSource();
      ds.setServerName("localhost");
      ds.setPortNumber(3306);
      ds.setDatabaseName("leo_test");
      ds.setUser("leonis");
      ds.setPassword("leoTest12");

// Getting a connection object
      con = ds.getConnection();
      
// Getting database info
      DatabaseMetaData meta = con.getMetaData();
      System.out.println("Server name: " 
        + meta.getDatabaseProductName());
      System.out.println("Server version: "
        + meta.getDatabaseProductVersion());

// Closing the connection
      con.close();
    } catch (Exception e) {
      System.err.println("Exception: "+e.getMessage());
    }
  }
}