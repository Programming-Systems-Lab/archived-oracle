/**
 * Title: SchemaFragment
 * Description: Defines an object which is used to store a schema
 *              definition and module information. It stores element name,
 *              schema fragment, modulename associated with an element, if
 *              a module is persistent or  not and instance name if it is
 *              persistent. (Metaparser will keep an instance if a module
 *              if it is persistent).
 * Copyright (c) 2000: The Trustees of Columbia University and the City of New York.
 *                              All Rights Reserved.
 * @author Kanan Naik
 * @version 1.0
 */

package psl.oracle;

import java.util.*;

public class SchemaFragment
{
    String name;
    String description;
    Vector moduleName = null;
    boolean isPersistent = false;
    String instanceName = null;

    public SchemaFragment()
    {
	name = null;
	description = null;
        moduleName = new Vector();
        isPersistent = false;
        instanceName = null;
    }

    public String toString()
    {
        String moduleString = "";
	    for(Enumeration e = moduleName.elements();e.hasMoreElements();)
        {
            if(moduleString == "")
                 moduleString = e.nextElement().toString();
            else
                 moduleString = moduleString + "," + e.nextElement();
        }
        return "\n" + description + "\n" + isPersistent + "," + instanceName
                    + "," + moduleString;
    }


    public String getName()
    {
	return name;
    }

    public void setName(String name)
    {
       	this.name = name;
    }



    public String getDescription()
    {
	return description;
    }

    public void setDescription(String description)
    {
	this.description = description;
    }



    public Vector getMoudleName()
    {
    	return moduleName;
    }


    public void setModuleName(Vector module)
    {
    if(module != null)
	    this.moduleName = module;
    }


    public boolean isPersistent()
    {
	return isPersistent;
    }

    public void setIsPersistent(boolean persistent)
    {
	this.isPersistent = persistent;
    }


    public String getInstanceName()
    {
	return instanceName;
    }

    public void setInstanceName(String instance)
    {
	this.instanceName = instance;
    }

}





