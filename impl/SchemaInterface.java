/*
 * Title: SchemaInterface
 * Description:  Interface for a user to add schema fragments and files
 *               to oracle database. User can enter a file name(.xsd format).
 *               It's format willbe validated using XML parser and then
 *               user will be prompt to enter module information for each tag.
 *               (module name, isPersistent and instance name). User can skip
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
    String[] moduleInfo = {"default,false,null","default,false,null","default,false,null"};
    String defaultModuleInfo = "default,false,null";
    static HashtableDBInterface db = null;
    String dbName = "oracleDB";
    public SchemaInterface()
    {
        try
        {
            db = new HashtableDBInterface(dbName);
        }
        catch(Exception e)
        {
            System.out.println("Exception in database: " + e);
            db.shutdown();
            System.exit(1);
        }
    }

/** Prompts user to add information like module name, if
  * Persistent or not and instance name. Instance name is only
  * stored if a module is Persistent. A user can press "Enter" to
  * store a default value for current tag or he can type 'skip' to
  * store default values for all tags.
  */

protected String[] askModuleInfo(String name)
{
    String line = null;
    String moduleName = "psl/oracle/impl/default";
    String isPersistent = "false";
    String instanceName = "null";
    while(true)
    {
        line = getString();
        line = line.trim();
        int totalElements = 9;
        int actualElements = 0;
        if(line.equals("skip"))
        {
            moduleInfo[0] = "skip";
            return moduleInfo;
        }
        if(line.equals("") == true)
        {
            return moduleInfo;
        }

        StringTokenizer st = new StringTokenizer(line, ",");
        for (int i=0; i<3; i++)
        {
            if(st.hasMoreElements())
            {
                moduleName = st.nextToken();
                actualElements++;
                /*Class moduleClass = null;
                try
                {
                    moduleClass = Class.forName(moduleName);
                }
                catch (ClassNotFoundException e)
                {
                    System.out.println("No class exists with the name: " + moduleName
                                        +" Please enter again.");
                    moduleName ="default";
                    break;
                }*/
                if(st.hasMoreElements())
                {
                    isPersistent = st.nextToken();
                    actualElements++;
                }
                else
                {
                   System.out.println("Number of parameters is not valid");
                   break;
                }
                isPersistent = isPersistent.trim();
                if(!(isPersistent.equals("true") || isPersistent.equals("false")))
                {
                    System.out.println("Value of isPersistent is niether true nor false OR "
                                        +"you have entered an instance name for a non "
                                        +"persistent module");
                    isPersistent = "false";
                    break;
                }
                if(isPersistent.equals("false") == true)
                {
                    totalElements--;
                }
                if(st.hasMoreElements() && (isPersistent.equals("true")== true))
                {
                    instanceName = st.nextToken();
                    actualElements++;
                }
                //break;
                moduleInfo[i] = moduleName+","+isPersistent+","+instanceName;
                moduleName ="";
                isPersistent = "";
                instanceName = "";
                if (i==2)
                {
                    if(totalElements != actualElements)
                    {
                        System.out.println("No Instance name should present if a module "
                                           + "is not persistent");
                        break;
                    }
                    else
                    return moduleInfo;;
                }
              }
              else
              {
                    System.out.println("Number of parameters is not valid");
                    break;
              }
        }
        continue;

    }
}

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
  * data for every element name. .xsd file entered
  * by a user will be validated using SAX parser.
  */

protected void addFragments() throws IOException,
                                  FileNotFoundException
{
    System.out.println("Enter the name of a Schema document(.xsd format)");
    String[] moduleInfo = {"default,false,default", "default,false,default", "default,false,default"};

    //String fileName = getString();
    String fileName = "d:\\kanan\\research\\psl\\psl\\oracle\\data\\schema1.xsd";
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
    while(line != null)
    {
        outLine.write(line, 0, line.length());
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
                        //e = (ElementInfo) element.get(level);
                        if(index1 != -1)
                            addToDB(e);
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
                    index1 = line.indexOf("0.");
                    index2 = line.indexOf('"', index1+2);
                    if(index2 == -1)
                    {
                         index2 = line.indexOf("'", index1+2);
                    }
                    elementName = line.substring(index1+2, index2);
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
                        e.setFragment((e.getFragment()).concat(line));
                        element.set(i, e);
                    }
                }
              }  //name not found
              else if((line.indexOf("element>")) != -1)
              {
                for(i=0; i<=level; i++)
                {
                    ElementInfo e = (ElementInfo) element.get(i);
                    e.setFragment((e.getFragment()).concat(line));
                    element.set(i, e);
                }
                ElementInfo e = (ElementInfo) element.get(level);
                String key = e.getKey();
                addToDB(e);
                index = key.indexOf(':');
                index1 = key.indexOf("0.");
                if(index != -1) //namespace is not null
                {
                    elementName = key.substring(index1+2, key.length());
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
                  index1 = line.indexOf("0.");
                  index2 = line.indexOf('"', index1+2);
                  if(index2 == -1)
                  {
                      index2 = line.indexOf("'", index1+2);
                  }
                  typeName = line.substring(index1+2, index2);
                  level = level + 1;
                  element.add(level, new ElementInfo());
                  for(i=0; i<=level; i++)
                  {
                      ElementInfo e = (ElementInfo) element.get(i);
                      e.setFragment((e.getFragment()).concat(line));
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
                      e.setFragment((e.getFragment()).concat(line));
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
                e.setFragment((e.getFragment()).concat(line));
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
                e.setFragment((e.getFragment()).concat(line));
                element.set(i, e);
            }
        }
        line = inLine.readLine();
    }
    return;
}

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
* an element information in form of key and fragment. It stores
* an element in the database. At present this method doesn't allow
* a user to modify or overwrite an existing element.
*/

public void addToDB(ElementInfo element)
{
    String key = element.getKey();
    int index = key.indexOf("0.");
    String displayKey = key.substring(index+2, key.length());
    if(db.get(key) != null)
    {
        System.out.println("Object: " + displayKey + " already exists. Enter '0' to skip"
                           +" and '1' to modify this tag.");
        try
        {
            int input=Integer.valueOf(getString().trim()).intValue();
            if(input < 0 || input > 1)
                System.out.println("Valid values are 0 or 1. Please try again!");
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
            }
        }
        catch(NumberFormatException e)
        {
            System.out.println("Not an integer number. Please try again!");
        }

    }
    else
    {
      if(moduleInfo[0].equals("skip") == false)
      {
        System.out.println("Enter information about an element " + key
                            + " in format: <pre proc. modulename>, <isPersistent>,"
                            +"<instancename>, <inner proc. modulename>, <isPersistent>,"
                            +"<instancename>, <post proc. modulename>, <isPersistent>,"
                            +"<instancename>. Press an 'Enter' "
                            + "key for a default value: <default><false>. To skip all tags enter 'skip'");
        moduleInfo = askModuleInfo(key);
        if(moduleInfo[0].equals("skip") == false)
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
                        System.out.println("The following exception occurred: " + e);
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
                        System.out.println("The following exception occurred: " + e);
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
}
