/**
* Title: OracleSienaInterface
* Description: This class provides an interface to the Siena. siena.jar
*			   must be included in the classpath.
* Copyright (c) 2000: The Trustees of Columbia University and the City of New York.
*							   All Rights Reserved.
* Company:		<p>
* @author Kanan Naik
* @version 1.0
*/

package psl.oracle.impl;

import java.io.*;
import java.net.*;
import java.lang.*;

import siena.*;
import psl.oracle.exceptions.*;
import psl.worklets2.wvm.*;
import psl.worklets2.worklets.*;

public class OracleSienaInterface implements Runnable, Notifiable
{
	static Siena si = null;
      static String hostname = null;

	/**
	* Constructor. Set the specified Siena object as the default.
	*/
	public OracleSienaInterface(Siena s)
	{
		this.si = s;
	}

	/**
	* Constructor. It sets a Siena master server running at localhost and
	* 4321 port.
	*/
	public OracleSienaInterface()
	{
		String master = "senp://localhost:4321";
		HierarchicalDispatcher hd = new HierarchicalDispatcher();
		try
		{
			hd.setMaster(master);
			System.out.println("Oracle master is " + master);
		}
		catch(siena.InvalidSenderException e)
		{
			e.printStackTrace();
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
		OracleSienaInterface osi = new OracleSienaInterface(hd);
		Thread t = new Thread(osi);
		t.start();
	}


	/**
	* Siena master server can be specified as command line argument.
	* If no argument is specified then it will start it at localhost,
	* port 4321.
	*/
	public static void main(String[] args)
	{
	      InetAddress addr = null;
		try
      	{
  	      	addr = InetAddress.getLocalHost();
		}
		catch(Exception e)
		{
            	System.out.println("Exception occurred: " + e);
		}
        	hostname = addr.toString();
            hostname = hostname.substring(hostname.indexOf('/')+1, hostname.length());
            //SendWorklet sw = new SendWorklet(hostname, "OracleRegistry");
 		String master = "senp://localhost:4321";
		if (args.length > 0)
		{
			master = args[0];
		}
		HierarchicalDispatcher hd = new HierarchicalDispatcher();
		try
		{
			hd.setMaster(master);
			System.out.println("Oracle master is " + master);
		}
		catch(siena.InvalidSenderException e)
		{
			e.printStackTrace();
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
		OracleSienaInterface osi = new OracleSienaInterface(hd);
		/*Filter f = new Filter();
		f.addConstraint("source", "psl.metaparser.Metaparser");
		f.addConstraint("type", "query");
		try
		{
			si.subscribe(f, this);
		}
		catch(siena.SienaException se)
		{
			se.printStackTrace();
		}
		System.out.println("Oracle subscribed to " + f);*/
	
		Thread t = new Thread(osi);
		t.start();
	}



	/**
	* Subscribes to an event generated by Metaparser.
	*/

	public void run()
	{
		Filter f = new Filter();
		f.addConstraint("source", "psl.metaparser.Metaparser");
		f.addConstraint("type", "query");
		try
		{
			si.subscribe(f, this);
		}
		catch(siena.SienaException se)
		{
			se.printStackTrace();
		}
		System.out.println("Oracle subscribed to " + f);
	}

	/**
	* This method is called when an event is received from Metaparser.
	* It will call getFragment(String query) method of Oracle. It will
	* generate a Siena event once a reply is received from Oracle.
	*/

	public void processMPQuery(String query, String MPHost, int MPSourceID, String MPRequestID)
	{
		SchemaFragment fragment = new SchemaFragment();
     		String moduleName = null;
		Oracle oracle = new Oracle();
		Notification  n = new Notification();
 		String msg = null;
		boolean result = false;
		long time = System.currentTimeMillis();
		n.putAttribute("hostname", hostname);
		n.putAttribute("source", "psl.oracle.impl.OracleSienaInterface");
		int responseID = this.hashCode();
		n.putAttribute("ResponseID", responseID);
 		n.putAttribute("MPSrcID", MPSourceID);
	  	n.putAttribute("MPRequestID", MPRequestID);
		n.putAttribute("type", "queryResult");
		try
		{
			fragment = oracle.getFragment(query);
		}
		catch (UnknownTagException ex)
		{
			msg = "Exception occured at Oracle: " + ex;
		}
		catch (InvalidQueryFormatException e)
		{
			msg = "Exception occured at Oracle: " + e;
		}
		catch (InvalidSchemaFormatException e)
		{
			msg = "Exception occured at Oracle: " + e;
		}
		catch (Exception e)
		{
			msg = "Exception occured at Oracle: " + e;
		}
		if(msg == null) //no exception occurred while retrieving a fragment
		{
			result = true;
    		      moduleName = fragment.getModuleName();
			SchemaFragmentToXML sfx = new SchemaFragmentToXML();
            	msg = sfx.toXML(fragment);
		}
		n.putAttribute("queryResult", result);
		n.putAttribute("value", msg);
		try
		{
			System.out.println("Oracle is sending back a reply: " + msg);
			si.publish(n);
		}
		catch(siena.SienaException se)
		{
			se.printStackTrace();
		}
		/*if(moduleName.length() > 0)
		{
			SendOracleReply sor = new SendOracleReply();
		      sor.sendReply( responseID, MPHost, moduleName);
		}*/

	}



	/**
	* This method will generate an event for TriKX. It should be
	* called when Oracle wants to send a notification to TriKX
	* about a portal update.
	*/

	public void generateTriKXEvent(String event)
	{
  		Notification  n1 = new Notification();
		long time = System.currentTimeMillis();
		n1.putAttribute("hostname", hostname);
		n1.putAttribute("source", "psl.oracle.impl.OracleSienaInterface");
		n1.putAttribute("timestamp", time);
 	      n1.putAttribute("type", "trikxEvent");
		n1.putAttribute("trikxEvent", event);
		System.out.println("Oracle is sending an update event to TriKX");
		try
		{
			si.publish(n1);
		}
		catch(siena.SienaException se)
		{
			se.printStackTrace();
		}
	}



	/**
	* This method will receive the events from the subscribed
	* components.
	*/
	public void notify(Notification n)
	{
		AttributeValue av = n.getAttribute("query");
		if (av != null)
		{
			String query = av.stringValue();
			System.out.println("Oracle got a query " + query);
			av = n.getAttribute("hostname");
			String MPHost = av.stringValue();
			av = n.getAttribute("source");
			String MPSource = av.stringValue();
			av = n.getAttribute("SrcID");
			int MPSourceID = av.intValue();
			av = n.getAttribute("RequestID");
			String MPRequestID = av.stringValue();
			processMPQuery(query, MPHost, MPSourceID, MPRequestID);
		}
		else
		{
			System.out.println("Oracle error: MP query is null.");
		}
	}



	public void notify(Notification[] no)
	{
	}

}
