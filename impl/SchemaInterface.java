/*
 * Title: SchemaInterface
 * Description:  Interface for a user to add schema fragments and files
 *               to oracle database. User can enter a file name(.xsd format).
 *               It's format willbe validated using XML parser and then
 *               user will be prompt to enter module information for each tag.
 *               (module name, issingleton and instance name). User can skip
 *               this information for individual tag or for all  tags to store
 *               the default values. The tags are stored in format of namespace:
 *               element name (if namespace is available and not default) and
 *               element name,path. These will become  keys for database.
 * Copyright (c) 2000: The Trustees of Columbia University and the City of New York. 
  *                              All Rights Reserved.
 * @author Kanan Naik
 * @version 1.0
 */

package psl.oracle.impl;

import java.util.*;
import java.io.*;
import java.lang.*;
import com.ibm.xml.parsers.*;
import org.xml.sax.*;
import psl.oracle.exceptions.*;


/**
 * Intializes database interface
 */

public class SchemaInterface
{
    String moduleInfo = "default,false,default";
    String defaultModuleInfo = "default,false,default";
    static DBInterface db = null;
    public SchemaInterface()
    {
        try
          {
            db = new DBInterface();
          }
        catch(Exception e)
          {
            System.out.println("Exception in database: " + e);
		db.shutdown();
            System.exit(1);
          }
    }


/** Prompts user to add information like module name, if
  * singleton or not and instance name. Instance name is only
  * stored if a module is singleton. A user can press "Enter" to
  * store a default value for current tag or he can type 'skip' to
  * store default values for all tags.
  */

protected String askModuleInfo(String name)
{
    String line = null;
    String moduleName = "psl/oracle/impl/default";
    String isSingleton = "false";
    String instanceName = "default";
    while(true)
    {
        line = getString();
        line = line.trim();
        if(line.equals("skip"))
        {
            return "skip";
        }
        if(line.equals("") == true)
        {
             return moduleName + "," + isSingleton +"," + instanceName;
         }
        StringTokenizer st = new StringTokenizer(line, ",");
        if(st.hasMoreElements())
            {
                moduleName = st.nextToken();
              Class moduleClass = null;
   	        try
                  {
                     moduleClass = Class.forName(moduleName);
                  }
                catch (ClassNotFoundException e)
                  {
                     System.out.println("No class exists with the name: " + moduleName
                                        +" Please enter again.");
                     moduleName="default";
      		     continue;
	          }
	        if(st.hasMoreElements())
            	   {
                      isSingleton = st.nextToken();
                   }
                else
                   {
                      System.out.println("Number of parameters is not valid");
                      continue;
                    }
                 isSingleton = isSingleton.trim();
                 if(!(isSingleton.equals("true") || isSingleton.equals("false")))
                    {
                        System.out.println("Value of isSingleton is niether true nor false");
                        isSingleton = "false";
                        continue;
                    }
                 if(st.hasMoreElements())
            	     {
                         instanceName = st.nextToken();
                     }
            	break;
                }
	      else
              {
	            System.out.println("Number of parameters is not valid");
	            continue;
	        }
       }

    return(moduleName + "," + isSingleton +"," + instanceName);
}

 /** This method is called when a user wants to add schema
  * fragments to the database. User is promted to enter 
  * data for every element name. .xsd file entered
  * by a user will be validated using SAX parser.
  */

   protected void addFragments() throws IOException,
                                  FileNotFoundException
    {
	System.out.println("Enter the name of a Schema document(.xsd format)");
	moduleInfo = "default,false,default";
        String fileName = getString();
       	BufferedReader inLine = new BufferedReader(new FileReader(fileName));
	BufferedWriter outLine = new BufferedWriter(new FileWriter("oracletemp.txt"));
      String line  = inLine.readLine();
	if(line != null)
		line = line.trim();
	else
		{
				System.out.println("Format of .xsd file is not valid. Tag <schema> "
					+ "is expected.");
				db.shutdown();
				System.exit(1);
		}
	while((line.indexOf("<schema ") != -1) && (line.indexOf(":schema ") != -1))
	{	
		line = inLine.readLine();
		line = line.trim();
		if(line.indexOf("<?xml") != -1)
		{
			outLine.write(line, 0, line.length());
			outLine.write("\n");
		}
	}

	if((line.indexOf("<schema ") != -1) && (line.indexOf(":schema ") != -1))
	{	
		System.out.println("Format of .xsd file is not valid. Tag <schema> "
					+ "is expected.");
		db.shutdown();
		System.exit(1);
	}
	else
	{
		while(line != null)
			{
				outLine.write(line, 0, line.length());
				outLine.write("\n");
			      line = inLine.readLine();
			}
	}
	outLine.close();
      inLine.close();
	
	FileInputStream is = new FileInputStream("oracletemp.txt");
         //Verify the format of schema file.
        try
         {
        	SAXParser parser = new SAXParser();
    	      parser.parse(new InputSource(is));
         }
        catch(Exception e)
	 {
                System.out.println(e);
		    return;
	 }
	  System.out.println("Schema document validation is successfull.");
	  is.close();
        processFile("oracletemp.txt");
	  return;
    }
     
/**
* .xsd file is processed by this method. It will generate a proper 
* key value by computing a path for each element. It also parses
* a file to store a schema fragment with each element.
*/
  
public void processFile(String fileName)throws IOException,
							     FileNotFoundException	
{
        BufferedReader inLine = new BufferedReader(new FileReader(fileName));
        String line = inLine.readLine();
	  String mainPath = "";
	  int level = -1;
	  int index = -1;
	  int index1 = -1;
	  int index2 = -1;
	  int j = -1;
	  int i = -1;
	  Vector element = new Vector(10);
	  Hashtable paths = new Hashtable();
	  String elementName = null;
	  String namespace = null;

        while(line != null)
          {
		index = line.indexOf("element");	
		if(index != -1) //element found
		{
			j=index+8;
			if(line.length() > j + 4 )
			{
                  	while (line.charAt(j) == ' ')
                    	{
                      		j++;
	                  }
            	      if((line.charAt(j) == 'n') && (line.charAt(j+1) == 'a') &&   
	                    (line.charAt(j+2) == 'm') && (line.charAt(j+3) == 'e'))
                        {
					level = level + 1;
					element.add(level, new ElementInfo());
					index1 = line.indexOf("<");
					index2 = line.indexOf(":");

					if((index1 != -1) && (index2 != -1) && (index2 < index))
					{
						namespace = line.substring(index1+1, index2);
						namespace = namespace.trim();
					}
					else
					{
						namespace = null;
					}
					index1 = line.indexOf('"');
                        	index2 = line.indexOf('"', index1+1);
                        	elementName = line.substring(index1+1, index2);
					for(i=0; i<=level; i++)
					{
						ElementInfo e = (ElementInfo) element.get(i);
						e.setFragment((e.getFragment()).concat(line));
                  			element.set(i, e);
					}
				
					ElementInfo e1 = (ElementInfo)element.get(level);
					mainPath = mainPath.concat("/" + elementName);
					if(paths.get(elementName) == null)
					{
						e1.setPath(mainPath);
					}
					else
					{
						String temp = (String)paths.get(elementName);		
						e1.setPath(temp);
					}

					if(namespace != null)
					{
						e1.setKey(namespace + ":" + elementName);
						element.set(level, e1);
					}
					else
					{
						e1.setKey(elementName + "," + e1.getPath());
						element.set(level, e1);
					}

					index1 = line.indexOf("/>");
					if(index1 != -1)
					{
						ElementInfo e = (ElementInfo) element.get(level);
						addToDB(e);
						element.remove(level);
						int indexPath = mainPath.indexOf("/" + elementName);
						mainPath = mainPath.substring(0, indexPath);
						level --;
					}
            	    }
			else if((line.charAt(j) == 'r') && (line.charAt(j+1) == 'e') &&
                    (line.charAt(j+2) == 'f')) //if element ref found
			    {
					index1 = line.indexOf('"');
					index2 = line.indexOf('"', index1+1);
					elementName = line.substring(index1+1, index2);
					int indexPath = mainPath.lastIndexOf('/');
					String parent =mainPath.substring(indexPath+1, 							   mainPath.length());
					String path = (String)paths.get(parent);
					if( path != null)
						path = path.concat("/" + elementName);	
					else
						path = mainPath.concat("/" + elementName);
					paths.put(elementName, path);
					for(i=0; i<=level; i++)
					{
						ElementInfo e = (ElementInfo) element.get(i);
						e.setFragment((e.getFragment()).concat(line));
						element.set(i, e);
					}
					
			    }
			}
			else  if((line.indexOf("element>")) != -1)
			{
				for(i=0; i<=level; i++)
				{
					ElementInfo e = (ElementInfo) element.get(i);
					e.setFragment((e.getFragment()).concat(line));
					element.set(i, e);	
				}
				ElementInfo e = (ElementInfo) element.get(level);
				addToDB(e);
				element.remove(level);
				String key = e.getKey();
				index = key.indexOf(':');
				if(index == -1)
				{
					index1 = key.indexOf(',');
					elementName = key.substring(0, index1);
				}
				else
				{
					elementName = key.substring(index+1, key.length());
				}
				int indexPath = mainPath.indexOf("/" + elementName);
				mainPath = mainPath.substring(0, indexPath);
				level --;
			}
       	}
		else //element not found
		{
			for(i=0; i<=level; i++)
			{
				ElementInfo e = (ElementInfo) element.get(i);
				e.setFragment((e.getFragment()).concat(line));
				element.set(i, e);	
			}			
		}
		line = inLine.readLine();
	}
     return;
}


/**
* This method is called by processFile() method. It receives
* an element information in form of key and fragment. It stores
* an element in the database. At present this method doesn't allow
* a user to modify or overwrite an existing element.
*/

public void addToDB(ElementInfo element)
{
    String key = element.getKey();
    if(db.get(key) != null)
    {
	 System.out.println("Object: " + key + " already exists.");
    }
    else
    {
	  if(moduleInfo.equals("skip") == false)
        {
              System.out.println("Enter information about an element " + key 
              + " in format: <modulename>, <issingleton>, <instancename>. Press an 'Enter' "
              + "key for a default value: <defualt><false>. To skip all tags enter 'skip'");
                  	      
		  moduleInfo = askModuleInfo(key);
		  if(moduleInfo.equals("skip") == false)
			element.setModuleInfo(moduleInfo);
		  else
			element.setModuleInfo(defaultModuleInfo);
	  }
	  else
	  {
	        element.setModuleInfo(defaultModuleInfo);
	  }
        System.out.println("Adding: " + key + "\n\n");
	  db.put(key, element.toString());
     }
}


 /**
 *  Read an input from command line
 */


  public static String getString()
    {
        int i;
	String input = "";
	boolean finish = false;
	while (!finish)
	    {
               try
		{
                   i = System.in.read();
      		   if (i == '\n')
        	       finish = true;
                   else
         	       input = input + (char) i;
		}
 	       catch(java.io.IOException e)
		{
                   finish = true;
		}
	    }
	return input;
    }


    public static void main(String[] args)
    {
	SchemaInterface schemaInterface1 = new SchemaInterface();
	int input=0;
	while(true)
	    {
		System.out.println("Choose from one of the following options:");
		System.out.println("1: Add schema fragments from .xsd file to the database");
		System.out.println("2: Exit");
		try
		    {
			input=Integer.valueOf(getString().trim()).intValue();
			if(input < 1 || input > 2)
			    System.out.println("Valid values are [1-2]. Please try again!");
			else
			    {
				switch(input)
				    {
				    case 1:
					try
					    {
					       schemaInterface1.addFragments();
  					    }
					catch(Exception e)
					    {
						System.out.println("The following exception occurred: " + e);
					    }
					break;
				    case 2:
	    			        {
	    			          db.shutdown();
					    System.exit(1);
				        }
			            }
		             }
	          }
		  catch(NumberFormatException e)
		        {
			        System.out.println("Not an integer number. Please try again!");
		        }
	    }


    }
}



