/**
 * Title: DBInterface
 * Description: This class provides an interface to the HSQL database.
 *              It provides essential methods like put(key, data), get(key),
 *              remove(key) and shutdown().
 * Copyright (c) 2000: The Trustees of Columbia University and the City of New York.
  *                              All Rights Reserved.
 * Company:      <p>
 * @author Kanan Naik
 * @version 1.0
 */

 package psl.oracle.impl;

import java.util.*;
import java.io.*;
import java.sql.*;
import org.hsql.*;

public class DBInterface
{

    /* Name of database */
    private String dbname;
    Connection conn = null;
    private String tableName = null;

    /**
     * Constructor
     * Intializes the HSQL database.
     */
    public DBInterface(String userTableName)
    {
      tableName = userTableName;
      dbname = userTableName+".dat";

      try
      {
        Class.forName("org.hsql.jdbcDriver");
        conn=DriverManager.getConnection("jdbc:HypersonicSQL:dbname","sa","");
        Statement stat=conn.createStatement();
        stat.execute("CREATE TABLE " + userTableName + "(key varchar(2000),element varchar(20000))");
      }
      catch(SQLException ex)//Throw an exception if table already exists
      {
      }

      catch(Exception e)
      {
        e.printStackTrace();
      }

  }


  /**
   * This method must be called before exiting an application that
   * uses HSQL database.
   */
 public synchronized void shutdown()
 {
	try
      {
	    /* Close the db and terminate the session */
        conn.close();
  	  }
      catch(Exception e)
      {
          e.printStackTrace();
      }
 }

 /**
 * This method uses JDBC to make a connection to the database
 * and to retrieve a record from the database.
 */

 public synchronized Object get(Object queryTag)
 {
    // Create a statement object
    ResultSet result = null;
    Object result1 = null;
    try
    {
        Statement stat=conn.createStatement();
        result =stat.executeQuery("SELECT element FROM " + tableName
                            +  " WHERE key = '"+ queryTag +"'");
        if(result.next()) //if object is found
            result1 = (Object)result.getString(1);
        else
            result1 = null;
    }
    catch(SQLException ex)
    {
        System.out.println("SQL exception: " + ex);
    }
    return result1;
 }


/**
 * Uses JDBC connection to store a record in the database.
 */
 public synchronized void put(Object key, Object data)
 {
    try
    {
       //use PreparedStatement as data may contain "'"
       PreparedStatement prep=conn.prepareCall("INSERT INTO " + tableName + " (key,element) VALUES (?,?)");
       prep.clearParameters();
       prep.setString(1,key.toString());
       prep.setString(2,data.toString());
       prep.execute();
       prep.close();
    }
    catch(SQLException ex)
    {
       System.out.println("SQL exception: " + ex);
    }
 }

 /**
  * This method can be used to remove a record from the
  * HSQL database.
  */
 public synchronized void remove(Object key)
 {
    try
    {
        Statement stat=conn.createStatement();
        ResultSet result=stat.executeQuery("DELETE FROM " + tableName
                                            + " WHERE key='"+ key + "'" );
    }
    catch(SQLException ex)
    {
        System.out.println("SQL exception: " + ex);
    }
 }

}


