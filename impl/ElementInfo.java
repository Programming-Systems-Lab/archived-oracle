/**
 * Title: ElementInfo
 * Description: An object which will be stored in database as a string.
 *              It provides "toString" method, which must be used to convert
 *              the different elements of class in a proper format for database.
 * Copyright (c) 2000: The Trustees of Columbia University and the City of New York.
  *                              All Rights Reserved.
 * Company:      <p>
 * @author Kanan Naik
 * @version 1.0
 */


package psl.oracle.impl;

import psl.*;
import java.util.*;

public class ElementInfo
{
      public String key = "";
      public int version = 0;
      public String path = "";
      public String fragment = "";
      public String[] moduleInfo = new String[3];


  public ElementInfo()
   {
     key = "";
     version = 0;
     fragment = "";
     for(int i=0; i<3; i++)
        moduleInfo[i] = "";

   }

  /**
  * This method must be used to store the elements of this class
  * in the database and it must be compatible with a method used to
  * retrieve an object from database.
  */

  public String toString()
  {
    return String.valueOf(version) + "[**]" + fragment + "[**]" + moduleInfo[0]+ "[**]" + moduleInfo[1]+ "[**]" + moduleInfo[2];
  }


  /**
  * This method is used to convert a string object retrieved
  * from the database in the different elements of this class.
  */

  public static ElementInfo getElementInfo(String info)
  {
    int index = info.indexOf("[**]");
    ElementInfo ei = new ElementInfo();
    String vers = info.substring(0,index);
    ei.version = Integer.parseInt(vers);
    int index1 = info.indexOf("[**]", index+3);
    ei.fragment = info.substring(index+4, index1);
    int index2 = info.indexOf("[**]", index1+1);
    ei.moduleInfo[0] = info.substring(index1+4, index2);
    index1 = info.indexOf("[**]", index2+1);
    ei.moduleInfo[1] = info.substring(index2+4, index1);
    ei.moduleInfo[2] = info.substring(index1+4, info.length());
    return ei;
  }

  public int getVersion()
  {
	return version;
  }
  public void setVersion(int version)
  {
	this.version = version;
  }

  public String getKey()
  {
	return key;
  }
  public void setKey(String key)
  {
	this.key = key;
  }

  public String getPath()
  {
	return path;
  }
  public void setPath(String path)
  {
	this.path = path;
  }

  public String getFragment()
  {
    return fragment;
  }
  public void setFragment(String fragment)
  {
    this.fragment = fragment;
  }

  public String getModuleInfo(int i)
    {
	return moduleInfo[i];
    }
  public void setModuleInfo(String[] description)
    {
        this.moduleInfo[0] = description[0];
	this.moduleInfo[1] = description[1];
	this.moduleInfo[2] = description[2];
    }

  public void setModuleInfo(String description)
    {
	this.moduleInfo[0] = description;
	this.moduleInfo[1] = description;
	this.moduleInfo[2] = description;
    }

}
