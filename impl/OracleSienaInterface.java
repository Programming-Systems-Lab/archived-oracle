package psl.oracle.impl;

import java.io.*;
import java.net.*;
import java.lang.*;

import siena.*;
import psl.oracle.exceptions.*;

public class OracleSienaInterface implements Runnable, Notifiable
{
    Siena si = null;
    public OracleSienaInterface(Siena s)
    {
        this.si = s;
    }
    
    public static void main(String[] args)
    {
        String master = "senp://localhost:31337";
        
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
        catch(siena.InvalidHandlerException e)
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
    
    public void run()
    {
        Filter f = new Filter();
        f.addConstraint("source", "psl.metaparser.MPSienaInterface");
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
        //f.clear():
    }
    
    public void processMPQuery(String query)
    {
        SchemaFragment fragment = new SchemaFragment();
		Oracle oracle = new Oracle();
		Notification  n = new Notification();
		String msg = null;
		boolean result = false;
		InetAddress addr = null;
		try
		{
		    addr = InetAddress.getLocalHost();
		}
		catch(Exception e)
		{
		    System.out.println("Exception occurred: " + e);
		}
		String hostname = addr.toString();
		long time = System.currentTimeMillis();
		n.putAttribute("hostname", hostname);
		n.putAttribute("source", "psl.oracle.impl.OracleSienaInterface");
		n.putAttribute("instance", "OracleSienaInteface1");
		n.putAttribute("timestamp", time);
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
        if(msg == null)
		{
		    result = true;
		   // msg = fragment.toString();
		}
		n.putAttribute("queryResult", result);
		n.putAttribute("value", msg);
		try
		{
		    System.out.println("Oracle is sending back a reply");
		    si.publish(n);
		}
		catch(siena.SienaException se)
		{
		    se.printStackTrace();
		}
    }
    
    public void notify(Notification n)
    {
        AttributeValue av = n.getAttribute("query");
        if (av != null)
        {
            String query = av.stringValue();
            System.out.println("Oracle got a query " + query);
            processMPQuery(query);
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