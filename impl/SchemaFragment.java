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

package psl.oracle.impl;

public class SchemaFragment
{
    String name;
    String description;
    String moduleName = null;
    boolean isPersistent = false;
    String instanceName = null;

    public SchemaFragment()
    {
	name = null;
	description = null;
        moduleName = null;
        isPersistent = false;
        instanceName = null;
    }

    public String toString()
    {
        return "\n" + description + "\n" + moduleName + "," + isPersistent
               + "," + instanceName;
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

    public String getModuleName()
    {
	return moduleName;
    }
    public void setModuleName(String module)
    {
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


