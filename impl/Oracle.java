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
 * Copyright:    Copyright (c) <p>
 * Company:      <p>
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

    static DBInterface db = null;
    public Oracle()
    {
      
    }


  /** This method gets a schema fragment for a given element.
   *  All singleton's must have an instance name associated with
   * them so that the XML MetaParser can store this XML Module,
   * and so that other XML Modules can get a reference to this
   * XML Module for communication.
   * @exception UnknownTagException Thrown if there is no schema
   * for the given 'namespace, element name' or 'element name, path'.
   * @exception InvalidSchemaFormat Thrown if the fragment stored
   * does not have a proper format.
   * @exception InvalidQueryFormat Thrown if the format of a query
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
      String srcId = null;
	String nameSpace = null;
	String name = null;
	String path = null;
        if(index != -1)
        {
          srcId = query.substring(0, index-1);
          srcId = srcId.trim();
        }
        else
	  {
		throw new InvalidQueryFormatException("1"+query);
	  }

	if(index1 != -1)
	{
		nameSpace = query.substring(index+1, index1);
		nameSpace = nameSpace.trim();
	}

	if( index2 != -1)
	{
		if(index1 == -1)
			{
				name = query.substring(index+1, index2);
			}
		else
			{
				name = query.substring(index1+1, index2);
				name = name.trim();
			}

		path = query.substring(index2+1, query.length());
		path = path.trim();
        }
	else
	{
		throw new InvalidQueryFormatException("2"+query);
	}

      //Initialize database

	try
       {
	    db = new DBInterface();
	 }
      catch(Exception e)
	 {
	    System.out.println("Error while intializing the database: "+ e);
	    System.exit(1);
	 }


	String nsName = nameSpace+":"+name;
      Object data = db.get(nsName);
      if(data == null)
          {
              String namePath = name + "," + path;
              data = db.get(namePath);
              if(data == null)
		throw new UnknownTagException("There is no schema entry for the "
                                               + "tag " + name + " in the Oracle.");
          }

         elementInfo = ElementInfo.getElementInfo((String)data);
        //System.out.println(elementInfo.getPath());
	String schema = elementInfo.getFragment();

        String moduleInfo = elementInfo.getModuleInfo();
	// parse it into each of its pieces
	StringTokenizer tk = new StringTokenizer(moduleInfo, ", ", false);
      String className = null;

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
          moduleClass = Class.forName(className);
        }
        catch (ClassNotFoundException e)
         {
             throw new InvalidSchemaFormatException("There is no class named "+ className +
                                                       " for the XMLModule for the tag " +
                                                       name);
         }*/

	boolean isSingleton = false;

	try
	    {
		// get whether this is a singleton or not
		isSingleton = Boolean.valueOf(tk.nextToken()).booleanValue();
	    }

	catch (Exception e)
	    {
		throw new InvalidSchemaFormatException("This schema definition for tag "
				                         + name + " is incorrectly formatted."
                                                         +"The line for this tag is: "
                                                         + moduleInfo + ", and the error was "
						         + e);
	    }


	String instanceName = " ";
	if (isSingleton == true)
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
	SchemaFragment fragment = new SchemaFragment();
	fragment.setName(name);
	fragment.setDescription(schema);
	fragment.setModuleName(className);
	fragment.setIsSingleton(isSingleton);
	fragment.setInstanceName(instanceName);
	db.shutdown();
	return fragment;
    }


public static void main(String args[])
    {
	if(args.length == 0 )
	    {
		System.out.println("USAGE: java Oracle [<source id>,[<namespace>:]element name,path");
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



















