/**
 * Title: ElementInfo
 * Description: An object which will be stored in database as a string.
 *              It provides "toString" method, which must be used to convert
 *              the different elements in a proper format for database.
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
      public String moduleInfo = null;

/**
 * Constructor
 */
  public ElementInfo()
   {
     key = "";
     version = 0;
     fragment = "";
     moduleInfo = "";

  }

  /**
  * This method must be used to store the elements of this class
  * in the database and it must be compatible with the method used to
  * retrieve an object from the database.
  */

  public String toString()
  {
      return String.valueOf(version) + "[**]" + fragment + "[**]" + moduleInfo;
  }


  /**
  * This method is used to retrieve the elements of this class
  * from a string object.
  */

  public static ElementInfo getElementInfo(String info)
  {
    int index = info.indexOf("[**]");
    ElementInfo ei = new ElementInfo();
    String vers = info.substring(0,index);
    ei.version = Integer.parseInt(vers);
    int index1 = info.indexOf("[**]", index+3);
    ei.fragment = info.substring(index+4, index1);
    ei.moduleInfo = info.substring(index1+4, info.length());
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


  public String getModuleInfo()
  {
     return moduleInfo;
  }
  public void setModuleInfo(String description)
  {
    this.moduleInfo = description;
  }
}
