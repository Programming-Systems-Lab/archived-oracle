/*
 * Title: SchemaInterface
 * Description:  Interface for a user to add schema fragments and module information
 *               to oracle database (HSQL). User can enter a file name(.xsd format).
 *               Its format will be validated using IBM's SAX parser and then
 *               user will be prompt to enter module information for each tag.
 *               (module name, isPersistent and instance name). User can skip
 *               this information for individual tag or for all tags to store
 *               the default values or null values. The tags are stored in format
 *               of namespace:element name(if namespace is available) and
 *               element name,path. These are the keys for the database.
 * Copyright (c) 2000: The Trustees of Columbia University and the City of New York.
 *                              All Rights Reserved.
 * @author Kanan Naik
 * @version 1.0
 */

package psl.oracle;

import java.util.*;
import java.io.*;
import java.lang.*;

// import com.ibm.xml.parsers.*;

import org.apache.xerces.parsers.*;
import org.xml.sax.*;


/**
 * Intializes database interface
 */


public class SchemaInterface
{
    String moduleInfo = null;
    String defaultModuleInfo = null;
    String moduleDir = null;
    static DBInterface db = null;
    String dbName = null;
    PrintWriter log = null;

    public SchemaInterface()
    {
	//code to read parameters from the property file
	File file = new File("oracle.prop");
	Properties property = new Properties();
	try
	{
	    property.load(new FileInputStream(file));
	}
	catch(FileNotFoundException ffe)
	{
	    System.err.println("Exception: " + ffe);
	    System.exit(0);
	}
	catch(IOException ioe)
	{
	    System.err.println("Exception: "+ ioe);
            System.exit(1);
        }
	moduleInfo = property.getProperty("defaultModuleInfo");
	if(moduleInfo == null || moduleInfo.length() < 1)
	{
	    printLog("Parameter 'defaultModuleInfo' must be set in 'oracle.prop' file.");
	    System.exit(0);
	}
	defaultModuleInfo = moduleInfo;
	dbName = property.getProperty("dbName");
	if(dbName == null || dbName.length() < 1)
	{
	    printLog("Parameter 'dbName' must be set in 'oracle.prop' file.");
	    System.exit(2);
	}
	moduleDir = property.getProperty("moduleDir");
        if(moduleDir == null || moduleDir.length() < 1)
	{
	    printLog("Parameter 'moduleDir' must be set in 'oracle.prop' file.");
	    System.exit(2);
	}

	//property file code end here
     try
     {
         db = new DBInterface(dbName);
     }
     catch(Exception e)
     {
         System.err.println("Exception in database: " + e);
         db.shutdown();
         System.exit(1);
     }
   }




	/** Prompts user to add information like module name, if
  	* persistent or not and instance name. Instance name is only
  	* stored if a module is persistent. A user can press "Enter" to
  	* store a null value for current tag or he can enter "1" to
  	* store a default value for a module or he can enter "2" to
  	* store default values for rest of the tags in the file.
  	*/

	protected String askModuleInfo(String name)
	{
    	String line = null;
	    int index1 = defaultModuleInfo.indexOf(',');
    	int index2 = defaultModuleInfo.indexOf(',', index1+1);
	    if(index1==-1)
    	{
			System.err.println("Format of 'defaultModuleInfo' in 'oracle.prop' is not proper: <module path>,<if persistent>,<instance>"        );
			System.exit(1) ;
    	}
    	String moduleName = defaultModuleInfo.substring(0, index1);
    	String isPersistent = null;
    	if(index2 == -1)
		isPersistent = defaultModuleInfo.substring(index1+1, defaultModuleInfo.length());
    	else
		isPersistent = defaultModuleInfo.substring(index1+1, index2);
    	if(!(isPersistent.equals("true") || isPersistent.equals("false")))
    	{
			printLog("Format of 'defaultModuleInfo' in 'oracle.prop' is not proper.");
			System.exit(1);
    	}
    	String instanceName = "null";
    	if(index2 == -1)
        	instanceName = null;
    	else
        	instanceName = defaultModuleInfo.substring(index2+1, defaultModuleInfo.length());
	    String defIsPersistent = isPersistent;

    	while(true)
    	{
        	line = getString();
        	line = line.trim();
        	if(line.equals("") == true)
        	{
            	moduleInfo = null;
            	return moduleInfo;
        	}
        	try
        	{
          		int input=Integer.valueOf(line).intValue();
          		if(input < 1 || input > 2)
          		{
            		System.out.println("Valid values are 1 or 2. Please try again!");
            		continue;
          		}
          		else
          		{
		            if(input == 1)
        		    {
              			return defaultModuleInfo;
            		}
            		if(input == 2)
            		{
		 	            moduleInfo = "skip";
	      				return moduleInfo;
            		}
          		}
        	}
        	catch(Exception ex)
        	{
          		String fileName = null;
		        File classFile = null;
        		boolean classExists = false;
		        StringTokenizer st = new StringTokenizer(line, ",");
        	    if(st.hasMoreElements())
          		{
		            moduleName = st.nextToken();
        		    fileName = moduleDir + File.separator + moduleName;
		            classFile = new File(fileName);
        		    classExists = classFile.exists();
		            if(classExists == false)
        		    {
		              System.out.println("No class exists with the name: " + moduleName
                                   +" Please enter again.");
        		      moduleName = defaultModuleInfo.substring(0, index1);
				      continue;
            		}
		            if(st.hasMoreElements())
        		    {
		              isPersistent = st.nextToken();
        		    }
		            else
        		    {
		              System.out.println("Number of parameters is not valid");
        		      continue;
		            }
        		    isPersistent = isPersistent.trim();
		            if(!(isPersistent.equals("true") || isPersistent.equals("false")))
        		    {
               			System.out.println("Value of isPersistent is niether true nor false OR "
                                   +"you have entered an instance name for a non "
                                   +"persistent module");
		                isPersistent = defIsPersistent;
        		        continue;
            		}
            		boolean instance = st.hasMoreElements();
		            if(instance && (isPersistent.equals("true")== true))
        		    {
               			instanceName = st.nextToken();
            		}
		            else if(!instance && (isPersistent.equals("true")== true))
        		    {
               			System.out.println("Instance name must be present if a module "
                                   + "is persistent");
		                continue;
        		    }
		            moduleInfo = moduleDir+File.separator+moduleName+","+isPersistent+","+instanceName;
        		    return moduleInfo;
          		}
      		}
    	}
	}


	/**
 	* This method is used to remove a record from the database. User has
 	* to specified a key value in order to delete an entry. The key format
 	* can be either namespace:element or element,path.
 	*/

	protected void removeFragment()throws UnknownTagException
	{
    	System.out.println("Enter the key value of a Schema fragment you want to "
                       + "remove. Format: <namespace>:<tag name> OR <tag "
                       + "name>,<path>");
	    String name = getString();
    	name = name.trim();
	    String key = "0." + name;
    	Object data = db.get(key);
	    if(data == null)
    	{
        	throw new UnknownTagException("There is no schema entry for the "
                                     + "tag " + name + " in the Oracle.");
	    }
    	ElementInfo e1 = ElementInfo.getElementInfo((String)data);
	    int version = -1;
    	String newVersion = String.valueOf(version);
	    newVersion = newVersion.trim();
    	int index = key.indexOf('.');
	    String newKey = newVersion + "." + key.substring(index+1, key.length());
    	db.remove(key);
	    e1.setKey(newKey);
    	System.out.println("Deleting " + name);
	    db.put(newKey, e1.toString());
	}



	/** This method is called when a user wants to add schema
  	* fragments to the database. User is promted to enter
  	* data for every element name. .xsd file entered by a user
  	* will be validated using IBM's SAX parser.
  	*/

	protected void addFragments()
 					throws IOException, FileNotFoundException
	{
    	System.out.println("Enter the name of a Schema document(.xsd format)");
	    String moduleInfo = defaultModuleInfo;
    	String fileName = getString();
	    BufferedReader inLine = null;
    	BufferedWriter outLine = null;
	    try
    	{
			inLine = new BufferedReader(new FileReader(fileName));
			outLine = new BufferedWriter(new FileWriter("oracletemp.txt"));
    	}
	    catch (Exception e)
    	{
			System.err.println("Error on file read/write:" + e.getMessage());
			e.printStackTrace();
	    }
	    String line  = inLine.readLine();
    	String mainBuffer = "";
	    if(line != null)
    	    line = line.trim();
	    else
    	{
        	System.out.println("Format of .xsd file is not valid. Tag <schema> "
                    + "is expected.");
	        db.shutdown();
    	    System.exit(1);
    	}
    	while(line != null)
    	{
        	outLine.write(line, 0, line.length());
			mainBuffer = mainBuffer + line;
			outLine.write("\n");
        	line = inLine.readLine();
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
    	    System.err.println(e);
        	return;
	    }
    	System.out.println("Schema document validation is successfull.");
	    is.close();
    	processFile("oracletemp.txt", mainBuffer);
    	return;
	}


	/**
	* .xsd file is processed by this method. It will generate a proper
	* key value by computing a path for each element. It also parses
	* a file to store a schema fragment for each element.
	*/

	public void processFile(String fileName, String mainBuffer)throws IOException,
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
    	String typeName = null;
	    String namespace = null;
    	while(line != null)
	    {
    	    index = line.indexOf("element");
        	if(index != -1) //element
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
             		    ElementInfo e11 = new ElementInfo();
           		    	e11.setFragment(mainBuffer);
	                    element.add(level, e11);
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
	                    if(index1 != -1)
    	                    index2 = line.indexOf('"', index1+1);
        	            else
            	        {
                	        index1 = line.indexOf("'");
                    	    index2 = line.indexOf("'", index1+1);
                   		}
	                    elementName = line.substring(index1+1, index2);
    			        for(i=0; i<=level; i++)
        	            {
            	            ElementInfo e = (ElementInfo) element.get(i);
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
                    	e1.setKey("0." + elementName + "," + e1.getPath());
	                    element.set(level, e1);
    	                index1 = line.indexOf("/>");
        	            ElementInfo e = (ElementInfo) element.get(level);
        			    if(index1 != -1)
                	    {
                    	   addToDB(e);
                    	}
	                    if(namespace != null)
    	                {
        	                e.setKey("0." + namespace + ":" + elementName);
            	            if(index1 != -1)
                	        {
                    	       addToDB(e);
                        	}
	                    }
    	                if(index1 != -1)
        	            {
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
                    	if (index1 == -1)
                    	{
	                      index1 = line.indexOf("'");
    	                }
        	            index2 = line.indexOf('"', index1+2);
            	        if(index2 == -1)
                	    {
                    	     index2 = line.indexOf("'", index1+2);
                    	}
	                    elementName = line.substring(index1+1, index2);
    	                int indexPath = mainPath.lastIndexOf('/');
        	            String parent =mainPath.substring(indexPath+1, mainPath.length());
            	        String path = (String)paths.get(parent);
                	    if( path != null)
                    	    path = path.concat("/" + elementName);
	                    else
    	                    path = mainPath.concat("/" + elementName);
        	            paths.put(elementName, path);
            	        for(i=0; i<=level; i++)
                	    {
                    	    ElementInfo e = (ElementInfo) element.get(i);
                        	element.set(i, e);
                    	}
	                }
    	          }  //name not found
        	      else if((line.indexOf("element>")) != -1)
            	  {
                	for(i=0; i<=level; i++)
               		{
                    	ElementInfo e = (ElementInfo) element.get(i);
	                    element.set(i, e);
    	            }
        	        ElementInfo e = (ElementInfo) element.get(level);
            	    String key = e.getKey();
                	addToDB(e);
	                index = key.indexOf(':');
    	            index1 = key.indexOf("0.");
        	        if(index != -1) //namespace is not null
            	    {
                	    elementName = key.substring(index+1, key.length());
                    	e.setKey("0." + elementName + "," + e.getPath());
	                    addToDB(e);
    	           }
        	       else
            	   {
                	    index = key.indexOf(',');
                    	elementName = key.substring(index1+2, index);
	                }
    	            int inNamespace = elementName.indexOf(':');
        	        String tagName = elementName.substring(inNamespace+1, elementName.length());
            	    int indexPath = mainPath.indexOf("/" + tagName);
                	mainPath = mainPath.substring(0, indexPath);
	                element.remove(level);
    	            level --;
        	    }
	          } //element found
    	      else if(line.indexOf(" name=") != -1) //check for type
        	  {
            	 String type = null;
	             if(line.indexOf("type name=") != -1)
    	         {
        	         index = line.indexOf("type name=");
            	     type = "type";
             	}
	             else if(line.indexOf("complexType name=") != -1)
    	         {
        	         index = line.indexOf("complexType name=");
            	     type = "complexType";
             	}
	            else if(line.indexOf("simpleType name=") != -1)
    	        {
        	         index = line.indexOf("simpleType name=");
            	     type = "simpleType";
             	}
	             if(type != null) //avoid "attribute name"
    	         {
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
        	         if (index1 == -1)
            	     {
                	     index1 = line.indexOf("'");
                  	 }
	                 index2 = line.indexOf('"', index1+2);
    	             if(index2 == -1)
        	         {
            	          index2 = line.indexOf("'", index1+2);
                	 }
                 	 typeName = line.substring(index1+1, index2);
                	 level = level + 1;
	                  element.add(level, new ElementInfo());
    	              for(i=0; i<=level; i++)
        	          {
            	          ElementInfo e = (ElementInfo) element.get(i);
	                      element.set(i, e);
    	              }
        	          ElementInfo e1 = (ElementInfo)element.get(level);
            	      String key = null;
                	  if(namespace != null)
                    	  key = "0." + namespace + ":" + type + "=" + typeName;
	                  else
    	                  key = "0." + type + "=" + typeName;
        	          e1.setKey(key);
            	      element.set(level, e1);
                	  index1 = line.indexOf("/>");
	                  if(index1 != -1)
    	              {
        	              ElementInfo e = (ElementInfo) element.get(level);
            	          addToDB(e);
                	  }
	              }
    	          else
        	      {
            	      for(i=0; i<=level; i++)
                	  {
                    	  ElementInfo e = (ElementInfo) element.get(i);
	                      element.set(i, e);
    	              }
        	      }
          	}
	        else if((line.indexOf("</type>") != -1) || (line.indexOf("simpleType>") != -1)
    	             || (line.indexOf("complexType>")!= -1) || (line.indexOf(":type>") != -1))
        	{
            	for(i=0; i<=level; i++)
            	{
                	ElementInfo e = (ElementInfo) element.get(i);
	                element.set(i, e);
    	        }
        	    ElementInfo e = (ElementInfo) element.get(level);
            	if(e.getKey().indexOf("ype=") != -1) //to avoid  <type> </type> kind of constructs
            	{
                	addToDB(e);
                	element.remove(level);
	                level --;
    	         }
        	}
	        else //element and type are not found
    	    {
        	    for(i=0; i<=level; i++)
            	{
                	ElementInfo e = (ElementInfo) element.get(i);
		            element.set(i, e);
        	    }
        	}
	        line = inLine.readLine();
    	}
	    return;
	}


	/**
  	* This method is used to increase the version value for the tags.
  	*/

	public String modifyKeyValue(String key, int version)
	{
		int index = key.indexOf('.');
		version++;
		String newVersion = String.valueOf(version);
		newVersion = newVersion.trim();
	    String newKey = newVersion + "." + key.substring(index+1, key.length());
	    return newKey;
	}



	/**
	* This method is called by processFile() method. It receives
	* an element information in form of a key and a fragment. It stores
	* an element in the database. This method also allows a user to
	* modify or overwrite an existing element. The previous entry will
	* be stored with a corresponding version number (depending upon the
	* number of times an entry was modified).
	*/

	public void addToDB(ElementInfo element)
	{
    	String key = element.getKey();
	    int index = key.indexOf("0.");
	    String displayKey = key.substring(index+2, key.length());
	    if(moduleInfo == null)
	      moduleInfo = "";
	    if(db.get(key) != null)
		{
			System.out.println("Object: " + displayKey + " already exists. Enter '0' to skip"
                           +" and '1' to modify this tag.");
	        while (true)
	        {
	          try
	          {
	            int input = -1;
	            input=Integer.valueOf(getString().trim()).intValue();
	            if(input < 0 || input > 1)
	            {
	                System.out.println("Valid values are 0 or 1. Please try again!");
	                continue;
		        }
	            else
	            {
	              if(input == 1)
	              {
	                Object data = db.get(key);
	                ElementInfo e1 = ElementInfo.getElementInfo((String)data);
		            int version = e1.getVersion();
	                String newKey = modifyKeyValue(key, version);
	                version++;
	                element.setVersion(version);
	                db.remove(key);
	                e1.setKey(newKey);
	                db.put(newKey, e1.toString());
	                addToDB(element);
	              }
    	          break;
	            }
	        }
	        catch(NumberFormatException e)
	        {
	            System.out.println("Not an integer number. Please try again!");
	            continue;
		    }
	      }
	    }
	    else
	    {
		    if(moduleInfo.equals("skip") == false)
		    {
		        System.out.println("Enter information about an element " + key
                            + " in format: <modulename>, <isPersistent>,"
                            +"<instancename>. Press an 'Enter' to store null "
                            + "value, enter  '1' for default value: <Default.class>,<false>. "
                            +"To skip all tags enter '2'");
		        moduleInfo = askModuleInfo(key);
		        if((moduleInfo == null) || (moduleInfo.equals("skip") == false))
		            element.setModuleInfo(moduleInfo);
		        else
		            element.setModuleInfo(defaultModuleInfo);
		    }
	        else
	        {
		        element.setModuleInfo(defaultModuleInfo);
		    }
		    System.out.println("Adding: " + key + "  " + element.toString()+ "\n\n");
	       db.put(key, element.toString());
	  }
	}



 /**
 *  Read an input from command line
 */

  public static String getString()
  {
     String result = null;
     try
     {
     	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
       	result = br.readLine();
     }
     catch (Exception e)
     {
        System.err.println("Exception in getString:" + e.getMessage());
     }
     return result;
  }


  /**
   * Provides user the options for adding or removing an entry in the
   * database.
   */

  public static void main(String[] args)
  {
      SchemaInterface schemaInterface1 = new SchemaInterface();
      int input=0;
      while(true)
      {
        System.out.println("Choose one from the following options:");
        System.out.println("1: Add schema fragments from .xsd file to the database");
        System.out.println("2: Remove a schema fragment from the database");
        System.out.println("3: Exit");
        try
        {
            input=Integer.valueOf(getString().trim()).intValue();
            if(input < 1 || input > 3)
                System.out.println("Valid values are [1-3]. Please try again!");
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
                        System.err.println("The following exception occurred: " + e);
                    }
                    break;
                    case 2:
                    try
                    {
                           schemaInterface1.removeFragment();
                    }
                    catch(UnknownTagException ex)
                    {
                    }
                    catch(Exception e)
                    {
                        System.err.println("The following exception occurred: " + e);
                    }
                    break;
                    case 3:
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

 /**
  * Writes messages to a log file
  */

  public void printLog(String msg)
  {
	log.println("SchemaInterface: " + msg);
  }

}



