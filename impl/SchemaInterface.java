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
 * Copyright:    Copyright (c) <p>
 * Company:      <p>
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


/** Prompts user to add an information like module name, if
  * singleton or not and instance name. Instance name is only
  * stored if a module is singleton. A user can press enter tag to
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
                     System.out.println("No class exist with name: " + moduleName
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
  * fragments to the database. User is promted to enter an
  * information for every element name. .xsd file entered
  * by a user will be validated using SAX parser.
  */

   protected void addFragments() throws IOException,
                                  FileNotFoundException
    {
	System.out.println("Enter the name of a Schema document(.xsd format)");

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
	while(line.indexOf("<schema>") != -1)
	{	
		line = inLine.readLine();
		line = line.trim();
		if(line.indexOf("<?xml") != -1)
		{
			outLine.write(line, 0, line.length());
			outLine.write("\n");
		}
	}

	if(line.indexOf("<schema>") != -1)
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
		    db.shutdown();	
                System.exit(2);
	 }
        ElementInfo elementInfo = new ElementInfo();
        String path = "";
        String elementName = null;
	  inLine.close();
	  inLine = new BufferedReader(new FileReader(fileName));
        line = inLine.readLine();
        String fragment = null;
	  String nameSpace = null;
	  int index = -1;
        int index1 = -1;
        int index2 = -1;
        int i = -1;
	  
        String moduleInfo = "default,false,default";
        while(line != null)
          {
     		index = line.indexOf(":element ");	
            index1 =  line.indexOf("element ");
            if(index1 != -1)
              {
                  i=index1+8;
                  while (line.charAt(i) == ' ')
                    {
                      i++;
                    }
                  if((line.charAt(i) == 'n') && (line.charAt(i+1) == 'a') &&
                    (line.charAt(i+2) == 'm') && (line.charAt(i+3) == 'e'))
                      {
				if(index != -1)
				{
					int tempIndex = line.indexOf('<');
					nameSpace = line.substring(tempIndex+1, index);
				}
                        index1 = line.indexOf('"');
                        i = line.indexOf('"', index1+1);
                        elementName = line.substring(index1+1, i);
                        if(moduleInfo.equals("skip") == false)
                           {
                              System.out.println("Enter an information about an element "
                              + elementName +" in format: <modulename>, <issingleton>, "
                              +"<instancename>. Press an 'Enter' key for a default "
                              +"value: <defualt><false>. To skip all tags enter 'skip'");
                              moduleInfo = askModuleInfo(elementName);
                           }
                        path = path.concat("/" + elementName);
                        fragment = line;
                        index2 = line.indexOf("</element>");
                        while(index2 == -1)
                            {
                                line = inLine.readLine();
                                fragment = fragment.concat(line);
                                index2 = line.indexOf("</element>");
                            }
                        
                        String key = null;
				if(index != -1)
				{
					key = nameSpace + ":" + elementName;
				}
				else
				{
					key = elementName + "," + path;
				}
                        elementInfo.setFragment(fragment);
                        if(moduleInfo.equals("skip") == true)
                            elementInfo.setModuleInfo("default,false,default");
                        else
                            elementInfo.setModuleInfo(moduleInfo);
				System.out.println("Adding: " + key);
                        db.put(key, elementInfo.toString());
               }
          }

          line = inLine.readLine();
      }
      is.close();
      return;
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
					        db.shutdown();
						System.exit(1);
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



