/**
 * Title: Oracle
 * Description: An implementation behind IOracle. Metaparser can send
 *              a query by calling getFragment(String query) method.
 *              If query format: <source id>,[<namespace>:]<element
 *              name>,<path> is not valid then InvalidQueryFormatException
 *              is thrown. If namespace is available then namespace +
 *              element name will be searched in database. If namespace
 *              is not avavilable then element name + path will be searched
 *              in database. If database does not have a matching schema
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



public class Oracle implements IOracle
{
   //Intializes database interface

    static HashtableDBInterface db = null;
    String dbName =  "oracleDB";
    public Oracle()
    {
    }


  /** This method gets a schema fragment for a given element.
   * All persistent objects must have an instance name associated with
   * them so that the XML MetaParser can store this XML Module
   * and other XML Modules can get a reference to this XML Module
   * for communication.
   * @exception UnknownTagException Thrown if there is no schema
   * for the given 'namespace, element name' or 'element name, path'.
   * @exception InvalidSchemaFormatException Thrown if the fragment stored
   * does not have a proper format.
   * @exception InvalidQueryFormatException Thrown if the format of a query
   * is not proper.
   */

public synchronized SchemaFragment getFragment(String query)
                                        throws UnknownTagException,
                                        InvalidQueryFormatException,
                                        InvalidSchemaFormatException
{
	ElementInfo elementInfo = null;
    int index = query.indexOf(',');
	int index1 = query.indexOf(':');
	int index2 = query.indexOf(',', index+1);
	int index3 = query.indexOf('=', index+1);
	String srcId = null;
	String nameSpace = null;
	String type = null;
	String name = null;
	String path = null;
	SchemaFragment fragment = new SchemaFragment();
	
	try
     {
	    db = new HashtableDBInterface(dbName);
	 }
     catch(Exception e)
	 {
	    System.out.println("Error while intializing the database: "+ e);
	    System.exit(1);
	 }

    if(index != -1)
    {
       srcId = query.substring(0, index-1);
       srcId = srcId.trim();
    }
    else
	{
	   throw new InvalidQueryFormatException(query);
	}

	if(index1 != -1)
	{
	    nameSpace = query.substring(index+1, index1);
		nameSpace = nameSpace.trim();
		
	}
	
	if(index3 != -1)
	{
	    if(index1 != -1)
	    {
	        type = query.substring(index1+1, index3);
	    }
	    else
	    {
	        type = query.substring(index+1, index3);
	    }
	}

	if( index2 != -1)
	{
		if(index1 == -1)
	    {
	        if(index3 == -1)
			    name = query.substring(index+1, index2);
			else
			    name = query.substring(index3+1, index2);
		}
		else
		{
		    if(index3 == -1)
			    name = query.substring(index1+1, index2);
			else
			    name = query.substring(index3+1, index2);    
		}
        path = query.substring(index2+1, query.length());
		path = path.trim();
    }
	else
	{
	    if(index1 == -1)
	    {
	        if(index3 == -1)
			    name = query.substring(index+1, query.length());
			else
			    name = query.substring(index3+1, query.length());
		}
		else
		{
		    if(index3 == -1)
			    name = query.substring(index1+1, query.length());
			else
			    name = query.substring(index3+1, query.length());    
		}
	}

    name = name.trim();
		
      //Initialize database

	 String nsName = null;
	 if(nameSpace != null)
	 {
	    if(type == null)
	        nsName = nameSpace+":"+name;
	    else
	        nsName = nameSpace+":"+type+"="+name;
	 }
	 
	 Object data = null;
	 if(nsName != null)
	    data = db.get(nsName);
     if(data == null)
     {
         String namePath = null;
         if(type == null)
         {
            if(path == null)
                namePath = name;
            else
	            namePath = name + "," + path;
	     }
	     else
	     {
	        if(path == null)
	            namePath = type+"="+name;
	        else
	            namePath = type+"="+name+","+path;
	     }
	     data = db.get(namePath);
         if(data == null)
            throw new UnknownTagException("There is no schema entry for the "
                                               + "tag " + name + " in the Oracle.");
     }

     elementInfo = ElementInfo.getElementInfo((String)data);
        //System.out.println(elementInfo.getPath());
     String schema = elementInfo.getFragment();

     String[] moduleInfo = new String[3];
     moduleInfo[0] = elementInfo.getModuleInfo(0);
     moduleInfo[1] = elementInfo.getModuleInfo(1);
     moduleInfo[2] = elementInfo.getModuleInfo(2);
     String className = null;
     boolean isPersistent = false;
     String instanceName = null;

     // parse it into each of its pieces
     for (int i=0;i<3;i++)
     {
	    StringTokenizer tk = new StringTokenizer(moduleInfo[i], ", ", false);
        className = null;

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
	    /*Class moduleClass = null;
	    try
	    {
              moduleClass = Class.forName(className[i]);
        }
        catch (ClassNotFoundException e)
         {
             throw new InvalidSchemaFormatException("There is no class named "+ className +
                                                       " for the XMLModule for the tag " +
                                                       name);
         }*/

        isPersistent = false;

	    try
	    {
	        // get whether this is a persistent or not
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
        fragment.setModuleName(className, i);
        fragment.setIsPersistent(isPersistent, i);
        fragment.setInstanceName(instanceName, i);
    }
    fragment.setName(name);
    fragment.setDescription(schema);
    db.shutdown();
    return fragment;
}


public static void main(String args[])
{
	if(args.length == 0 )
    {
		System.out.println("USAGE: java Oracle [<source id>,[<namespace>:][datatype=]element name[,path]");
		System.exit(1);
    }
	else
    {
		try
	    {
			String name = args[0];
			Oracle oracle = new Oracle();
			System.out.println(oracle.getFragment(name).toString());
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



















