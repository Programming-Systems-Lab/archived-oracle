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
    String[] moduleName = new String[3];
    boolean[] isPersistent = new boolean[3];
    String[] instanceName = new String[3];

    public SchemaFragment()
    {
	name = null;
	description = null;
	int i;
	for(i=0; i<3; i++)
	    moduleName[i] = null;
	for(i=0; i<3; i++)
	    isPersistent[i] = false;
	for(i=0; i<3; i++)
	    instanceName[i] = null;
    }

    public String toString()
    {
        return "\n" + description + "\n" + moduleName[0] + "," + isPersistent[0]
               + "," + instanceName[0]+ "\n" + moduleName[1] + "," + isPersistent[1]
               + "," + instanceName[1]+ "\n" + moduleName[2] + "," + isPersistent[2]
               + "," + instanceName[2];
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

    public String getModuleName(int i)
    {
	return moduleName[i];
    }
    public void setModuleName(String module, int i)
    {
	this.moduleName[i] = module;
    }

    public boolean getIsPersistent(int i)
    {
	return isPersistent[i];
    }
    public void setIsPersistent(boolean persistent, int i)
    {
	this.isPersistent[i] = persistent;
    }

    public String getInstanceName(int i)
    {
	return instanceName[i];
    }
    public void setInstanceName(String instance, int i)
    {
	this.instanceName[i] = instance;
    }

}


