package psl.oracle.impl;

import java.util.*;
import java.io.*;
import java.sql.*;
import org.hsql.*;

public class HashtableDBInterface
{
    
    /* Name of database */
    private String dbname;
    Connection conn = null;
    private String tableName = null;
    
    /**
     * Constructor
     *
     */
    public HashtableDBInterface(String userTableName) 
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


