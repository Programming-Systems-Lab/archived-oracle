
/**
 * Title: DBInterface
 * Description: Creates an interface for Object Store database.
 * Copyright (c) 2000: The Trustees of Columbia University and the City of New York. 
  *                              All Rights Reserved.
 * @author Kanan Naik
 * @version 1.0
 */


package psl.oracle.impl;
import java.util.*;
import java.io.*;
import com.odi.*;
import com.odi.util.*;

/**
 * Intializes an interface with name "oracle.odb".
 */


public class DBInterface {
  private final static String TAGS   = "Tags";
  private final static String CLASSNAME  = "DBInterface";

   // Name of database
  private static String dbname = "oracle.odb";

  //Global session context shared by all threads
  private Session session               = null;
  private Database db                   = null;
  private Map Tags                   = null;
  private Transaction tr                = null;

   /**
   * Constructor
   * Initializes the UserManagerImpl database session
   * create a global session to be shared by all threads
   */

  public DBInterface() throws Exception
	{

       if(session == null)
      	{
	  	  session = Session.createGlobal(null, null);
		  session.join();
      	  Transaction.setDefaultAbortRetain(ObjectStore.RETAIN_HOLLOW);
      	}

    // Open the database or create a new one if necessary.

    	try
		{
		  db = Database.open(dbname, ObjectStore.OPEN_UPDATE);
	        System.out.println( "Opened database(" + dbname + ")");
            }
     catch (DatabaseNotFoundException e)
		{
		  db = Database.create(dbname, ObjectStore.ALL_READ | ObjectStore.ALL_WRITE);
	        System.out.println( " Created database(" + dbname + ")");
	      }


    // Find the allTags root or create it if it is not there.

     tr = Transaction.begin(ObjectStore.UPDATE);

     try
	      {
               Tags = (Map)db.getRoot(TAGS);
	         System.out.println(CLASSNAME + " Loaded root(" + TAGS + ")");
	 	}
     catch (DatabaseRootNotFoundException e)
		{
      /* Create a database root and associate it with a hashtable
       * that will contain the extent for all tags
       */

   		   Tags = new OSHashMap();
	         db.createRoot(TAGS, Tags);
	         System.out.println(CLASSNAME + " Created root(" + TAGS + ")");
    		}
	 finally
		{
		  // End the transaction
	         tr.commit(ObjectStore.RETAIN_HOLLOW);
	      }
  }




  public synchronized void shutdown()
	{
	    System.out.println(CLASSNAME + " Server Shutdown requested");
	    try {
		///Close the db and terminate the session
		      db.close();
		      session.leave();
		      session.terminate();
	        }
	    catch (Exception e)
		  {
		      e.printStackTrace();
	        }
      }


  /**
  * Get a key as a request and returns a corresponding object.
  */

  public Object get(Object queryTag)
   {
     tr = Transaction.begin(ObjectStore.UPDATE);
     Object temp = Tags.get(queryTag);
     //End the transaction
     tr.commit(ObjectStore.RETAIN_HOLLOW);
     return temp;
  }


  /**
  * This method receives a key and a data object and stores them
  * in database.
  */

  public void put(Object key, Object data)
    {
	    tr = Transaction.begin(ObjectStore.UPDATE);
    	    Tags.put(key,data);
	    //End the transaction
          tr.commit(ObjectStore.RETAIN_HOLLOW);
    }
}