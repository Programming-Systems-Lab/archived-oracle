/**
 * Title: Oracle
 * Description: An implementation behind IOracle. Metaparser sends
 *              a query using Siena. Siena calls getFragment(String query) method.
 *              If query format is not valid then InvalidQueryFormatException
 *              is thrown. If namespace is available then namespace +
 *              element name will be searched in the database. If namespace
 *              is not avavilable then element name + path will be searched
 *              in the database. If the database does not have a matching schema
 *              definition then UnknownTagException is thrown.
 * Copyright (c) 2000: The Trustees of Columbia University and the City of New York.
 *                              All Rights Reserved.
 * @author Kanan Naik
 * @version 1.0
 */


package psl.oracle.impl;

import psl.groupspace.impl.*;
import psl.oracle.exceptions.*;

import java.io.*;
import java.util.*;
import java.net.*;

public class Oracle implements IOracle
{
   //Intializes database interface
    Hashtable oracleQuery = new Hashtable();
    static DBInterface db = null;
    String dbName =  "oracleDB";
    public Oracle()
    {
    }

  /**
   * This method is used to get a partial match of the
   * the specified path of an element.
   */
  public static String getPartialMatch(String name)
  {
    if(name == null)
        return null;
    int index1 = name.indexOf('/');
    int index2 = name.indexOf('/',index1+1);
    if(index2 == -1)
        return null;
    else
    {
        int index3 = name.indexOf(',');
        String result = name.substring(0,index3+1);
        result = result + name.substring(index2, name.length());
        return result;
    }
  }


  /** This method gets a schema fragment for a given element.
   * This method returns a String that represents object Schemafragment
   * in XML format. The valid query format is : <FleXML:schemaQuery
   * version="1.0" name="NAMESPACE:ELEMENT"><xPath>PATH</xPath>
   * </FleXML:schemaQuery>
   *
   * @exception UnknownTagException Thrown if there is no schema
   * for the given namespace and element name or element name and path.
   * @exception InvalidSchemaFormatException Thrown if the fragment stored
   * does not have a proper format.
   * @exception InvalidQueryFormatException Thrown if the format of a query
   * is not valid.
   */

  public synchronized String getFragment(String queryXML)
                                        throws UnknownTagException,
                                        InvalidQueryFormatException,
                                        InvalidSchemaFormatException
  {
    ElementInfo elementInfo = null;
    XMLToQuery xtq = new XMLToQuery(queryXML);
    String namespace = xtq.getNamespace();
    String name = xtq.getName();
    String path = null;
    path = xtq.getPath();
   // System.out.println("name "+name);
   // System.out.println("namespace "+namespace);
   // System.out.println("path "+path);
   // System.out.println("type "+type);

    SchemaFragment fragment = new SchemaFragment();
    try
    {
        db = new DBInterface(dbName);
    }
     catch(Exception e)
    {
        System.out.println("Error while intializing the database: "+ e);
        System.exit(1);
    }

      //Initialize database
    String nsName = null;
    if(namespace != null)
    {
      nsName = namespace+":"+name;
    }
    Object data = null;
    if(nsName != null)
      data = db.get("0." + nsName);
    if(data == null)
    {
        String namePath = null;
        if(path == null)
             namePath = name;
        else
             namePath = name + "," + path;
         data = db.get("0." + namePath);
         String modifiedPath = null;
         if(data == null) //modified for partial matching of path(suffix)
         {
            modifiedPath = getPartialMatch(namePath);
            while(modifiedPath != null)
            {
                data = db.get(modifiedPath);
                if(data != null)
                    break;
                modifiedPath = getPartialMatch(modifiedPath);
            }
            if(data == null)
                throw new UnknownTagException("There is no schema entry for the "
                                               + "tag " + name + " in the Oracle.");
         }
     }
     elementInfo = ElementInfo.getElementInfo((String)data);
     //System.out.println(elementInfo.getPath());
     String schema = elementInfo.getFragment();
     String moduleInfo = elementInfo.getModuleInfo();
     String className = null;
     String fileName = null;
     File classFile = null;
     boolean classExists = false;
     boolean isPersistent = false;
     String instanceName = null;
     System.out.println("info "+ moduleInfo);
     if(moduleInfo.length() > 0)
     {
      StringTokenizer tk = new StringTokenizer(moduleInfo, ", ", false);
      try
      {
        // get the className
        className = tk.nextToken();
      }
      catch (Exception e)
      {
        throw new InvalidSchemaFormatException("The database for the Oracle "
                        	                    + "is incorrectly formatted for the "
                                                    + "element " + name + ". The line for"
                                                    + " this tag is: " + moduleInfo + " and"
                                                    + " the error was: " + e);
      }

      // try to get the class for this className
        fileName = "psl/oracle/modules/" + className;
        classFile = new File(fileName);
        classExists = classFile.exists();
        if(classExists == false)
        {
           throw new InvalidSchemaFormatException("There is no class named "+ className +
                                                     " for the XMLModule for the tag " +
                                                     name);
        }
        isPersistent = false;
        try
        {
          // get if this is persistent or not
          isPersistent = Boolean.valueOf(tk.nextToken()).booleanValue();
        }
        catch (Exception e)
        {
          throw new InvalidSchemaFormatException("This schema definition for tag "
	            	                         + name + " is incorrectly formatted."
                                                 +"The line for this tag is: "
                                                 + moduleInfo + ", and the error was "
						                             + e);
        }
        instanceName = " ";
        if (isPersistent == true)
        {
          try
	  {
            instanceName = tk.nextToken();
	  }
	  catch (Exception e)
	  {
            throw new InvalidSchemaFormatException("The schema definition for "
	      			                        + "the tag " + name
                                                        + "is incorrectly formatted. "
                 					+ "The line for this tag is: "
						        + moduleInfo + ", and the error"
                                                        +" was "+ e);
          }

        }
        fragment.setModuleName(className);
        fragment.setIsPersistent(isPersistent);
        fragment.setInstanceName(instanceName);
      }
      else
      {
        fragment.setModuleName("");
        fragment.setIsPersistent(false);
        fragment.setInstanceName("");
      }

      fragment.setName(name);
      fragment.setDescription(schema);
      db.shutdown();
      SchemaFragmentToXML sfx = new SchemaFragmentToXML();
      String result = sfx.toXML(fragment);
      return result;
  }

  /**
   * Oracle can receive a query from command line but a valid
   * query must be stored a file.
   */

  public static void main(String args[])
  {
    if(args.length == 0 )
    {
      System.out.println("USAGE: java Oracle <file name>");
      System.exit(1);
    }
    else
    {
      try
      {
	String file = args[0];
        File fileName = new File(file);
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String name = br.readLine() ;
	Oracle oracle = new Oracle();
	System.out.println(oracle.getFragment(name));
      }
      catch (UnknownTagException ex)
      {
        db.shutdown();
	System.exit(1);
      }
      catch (InvalidQueryFormatException e)
      {
        db.shutdown();
	System.exit(2);
      }
      catch (InvalidSchemaFormatException e)
      {
        db.shutdown();
        System.exit(2);
      }
      catch (Exception e)
      {
        System.out.println("The following exception occurred while "
                           + "trying to use getFragment " + e);
        db.shutdown();
        System.exit(1);
      }
    }
  }
}







































