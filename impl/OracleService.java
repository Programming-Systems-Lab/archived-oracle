/**
 * Title: OracleService
 * Description: The service provided for communication of the Oracle
 *              with the Metaparser and WGC. It subscribes to Metaparser
 *              Service to receive a query and generates an event when it
 *              finds a reply. It pushes and pulls schema Fragment definitions
 *              to WGC.
 * Copyright:    Copyright (c) <p>
 * Company:      <p>
 * @author Kanan Naik
 * @version 1.0
 */

package psl.oracle.impl;

import psl.groupspace.*;
import psl.oracle.roles.*;
import psl.wgcache.*;
import psl.wgcache.impl.*;
import psl.oracle.exceptions.*;
import psl.oracle.impl.*;

import java.util.*;
import java.beans.*;


public class OracleService implements GroupspaceService,
				      GroupspaceCallback,
				      OracleRole
{
    private GroupspaceController gc;
    private static String roleName = "OracleRole";
    private static final String subscribeMPClassName = "psl.oracle.impl.SchemaFragment";
    private static final String subscribeWGCClassName = "psl.oracle.impl.Cacheable";
    //private static final String subscribeWGCMissClassName = "psl.groupspace.impl.WGC_Oracle_Miss";

    public OracleService()
     {
     }

    /**
     * Subscribes to Metaparser and WGC (hit ans miss) events
     */

    public boolean gsInit(GroupspaceController gc)
    {
	this.gc = gc;
	gc.registerRole(roleName, this);
	gc.subscribeEvent(this, "MetaparserEvent");
	gc.subscribeEvent(this, "WGCache_Oracle_Hit");
	gc.subscribeEvent(this, "WGCache_Oracle_Miss");
	return true;
    }

    public void gsUnload()
    {
	gc.unsubscribeAllEvents(this);
	gc = null;
    }

    /**
     * Generates WGC event when Oracle needs to push a schemafragment
     * in cache.
     */

    public void run()
      {
	  Cacheable wgcObject = new Cacheable();
	  wgcObject.key = "mytag1";
	  wgcObject.data = "data";
	  wgcObject.size = 4;
	  try
       	  {
		GroupspaceEvent gPut = new GroupspaceEvent ( wgcObject,	 "Oracle_WGCache_Put",  null, wgcObject, false );
		System.err.println ( "Send WGC Put event" );
		gc.groupspaceEvent ( gPut );
		/*GroupspaceEvent gGet = new GroupspaceEvent ( wgcObject,	 "Oracle_WGCache_Get",  null, wgcObject, false );
		System.err.println ( "Send WGC  Get event" );
		gc.groupspaceEvent ( gGet );*/
	   }
	catch ( PropertyVetoException pve )
           {
           	System.err.println ( "New Query Vetoed." );
           }
      }

    public int callback(GroupspaceEvent ge)
    {
	oracleEvent(ge);
 	return GroupspaceCallback.CONTINUE;
    }

    public String roleName()
    {
	return roleName;
    }


  /**
   * Receives and event from Metaparser and WGC. If an event occures from
   * Metaparser, it will call getFragment method and get proper schema definition.
   * It may throw UnknownTag, InvalidQueryFragment and InvalidSchemaFragment
   * exceptions.
   */



    public void oracleEvent(GroupspaceEvent ge)
    {
	String eventDescription = ge.getEventDescription();
	Object eventObject = ge.getDbo();
	System.out.println("Getting an Event\n" + "Event Type: "
			    + eventDescription + "\n" + "Event Data: " + eventObject);
	if(eventDescription.equals("MetaparserEvent") == true)
	  {
		try
		  {
			String query = (String)eventObject;
			System.out.println("Got tag " + query);
			SchemaFragment fragment = new SchemaFragment();
			Oracle oracle = new Oracle();
			try
			  {
				fragment = oracle.getFragment(query);
			  }
			catch (UnknownTagException ex)
                 	  {
	                        return;
                	  }
			catch (InvalidQueryFormatException e)
			  {
			        return;
			  }
                  catch (InvalidSchemaFormatException e)
			  {
			        return;
			  }
			catch (Exception e)
			  {
				System.out.println("The following exception occurred while " + "trying to use getDTDTag " + e);
				return;
			  }
     			GroupspaceEvent go = new GroupspaceEvent(fragment, "OracleEvent", null, fragment, true);
								gc.groupspaceEvent(go);
		        }
		catch(Exception e)
			{
			  System.out.println("Exception occured: " + e);
     			}
	      }
	else if(eventDescription.equals("WGCache_Oracle_Hit") == true)
	      {
		  System.out.println("Receiving WGC event - Hit");
                  Cacheable cached = new Cacheable();
                  cached  = (Cacheable)eventObject;
	      }
	else if(eventDescription.equals("WGCache_Oracle_Miss") == true)
	      {
		  System.out.println("Receiving WGC event - Miss");
	      }
      }
}
























































