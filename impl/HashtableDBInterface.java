package psl.oracle.impl;

import java.util.*;
import java.io.*;

public class HashtableDBInterface
{
    private String TABLENAME;
    private final String CLASSNAME   = "HashtableDBInterface";

    /* Name of database */
    private String dbname;

    /* The database */
    private Hashtable h;
    FileInputStream fis;

    /**
     * Constructor
     *
     */
    public HashtableDBInterface(String userTableName) throws Exception
    {
      TABLENAME = userTableName;
      dbname = userTableName+".dat";
      try
      {
        FileInputStream fis = new FileInputStream(dbname);
        ObjectInputStream ois = new ObjectInputStream(fis);
        h = (Hashtable)ois.readObject();
        ois.close();
        fis.close();
      }
      catch(FileNotFoundException e) { /* Create new hashtable */
        h = new Hashtable();
      }
      catch(Exception e)
      { /* Something else is wrong */
          e.printStackTrace();
      }
  }


 public synchronized void shutdown()
 {
	try
      {
	    /* Close the db and terminate the session */
	    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dbname));
	    oos.writeObject(h);
	    oos.close();
	}
      catch(Exception e)
      {
          e.printStackTrace();
      }
  }

   public synchronized Object get(Object queryTag)
   {
	return h.get(queryTag);
   }

   public synchronized void put(Object key, Object data)
   {
	h.put(key,data);
    }

   public synchronized void remove(Object key)
   {
	h.remove(key);
   }
}


