/**
 * Title: SchemaFragment
 * Description: Defines an object which is used to send a schema
 *              definition to Metaparser when a query is received
 *              from Metaparser. It stores element name, schema
 *              definition(tag definition for an element), modulename
 *              associated with this tag, if a module is singleton or
 *              not and instance name if it is singleton.
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
    String moduleName;
    boolean isSingleton;
    String instanceName;

    public SchemaFragment()
    {
	name = null;
	description = null;
	moduleName = null;
	isSingleton = false;
	instanceName = null;
    }

    public String toString()
    {
        return name + "\n" + description + "\n" + moduleName + "\n" + isSingleton + "\n" + instanceName;
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

    public boolean getIsSingleton()
    {
	return isSingleton;
    }
    public void setIsSingleton(boolean singleton)
    {
	this.isSingleton = singleton;
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


