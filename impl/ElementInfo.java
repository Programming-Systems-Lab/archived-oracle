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
	  public String path = "";
        public String fragment = "";
    	  public String moduleInfo = "";
        

  public ElementInfo() 
   {
	 key = "";
       fragment = "";
       moduleInfo = "";
	 
   }
  
  /**
  * This method must be used to store the elements of this class
  * in the database and it must be compatible with a method used to
  * retrieve an object from database.
  */ 

  public String toString()
  {
    return fragment + "[**]" + moduleInfo ;
  }
  

  /**
  * This method is used to convert a string object retrieved
  * from the database in the different elements of this class.
  */

  public static ElementInfo getElementInfo(String info)
  {
    int index1 = info.indexOf("[**]");
    ElementInfo ei = new ElementInfo();
    ei.fragment = info.substring(0, index1);
    ei.moduleInfo = info.substring(index1+4, info.length());
    return ei;
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
