package psl.oracle.impl;



import java.io.*;

import java.net.*;

import java.lang.*;



import siena.*;

import psl.oracle.exceptions.*;



public class OracleSienaInterface implements Runnable, Notifiable

{

    static Siena si = null;

    public OracleSienaInterface(Siena s)
    {
        this.si = s;
    }

    public OracleSienaInterface()

    {
        String master = "senp://localhost:31337";
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
        /*f.clear();
        f.addConstraint("source", "psl.oracle.impl.OracleSienaInterface");
        f.addConstraint("type", "oracleQuery");
       try
        {
           //si.subscribe(f, this);
            si.subscribe(f, new ProcessOracleQuery(this));
        }
        catch(siena.SienaException se)
        {
            se.printStackTrace();
        }
        System.out.println("Oracle subscribed to " + f);
	  f.clear();
	  f.addConstraint("source", "psl.oracle.impl.OracleSienaInterface");
        f.addConstraint("type", "oracleQueryResult");
       try
        {
            //si.subscribe(f, this);
            si.subscribe(f, new OracleQueryResult(this));
        }
        catch(siena.SienaException se)
        {
            se.printStackTrace();
        }
        System.out.println("Oracle subscribed to " + f);*/
        //generateOracleEvent("asd,comment,/puschaseorder/comment");
    }



    public void processMPQuery(String query, String MPHost, String MPSource)

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

	    fragment = oracle.getFragment(query, "query");

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

            StartWebServer sws = new StartWebServer();

            msg = sws.startServer();

            if (msg != null) //exception occured while starting a server

            {

              result = false;

            }

            else //send fragment now

            {

	      msg = fragment.toString();

            }

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



    /*public void processOracleQuery(String query, boolean result, String msg)

    {

	Notification  n1 = new Notification();

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

	n1.putAttribute("hostname", hostname);

	n1.putAttribute("source", "psl.oracle.impl.OracleSienaInterface");

	n1.putAttribute("instance", "OracleSienaInteface1");

	n1.putAttribute("timestamp", time);

     	n1.putAttribute("OracleQuery", query);



	n1.putAttribute("type", "oracleQueryResult");

	n1.putAttribute("oracleQueryResult", result);

	n1.putAttribute("value", msg);

	try

	{

	    System.out.println("Oracle is sending back a reply");

	    si.publish(n1);

	}

	catch(siena.SienaException se)

	{

	    se.printStackTrace();

	}



    }



   public void generateOracleEvent(String query)

   {

	Notification  n = new Notification();

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

	hostname = hostname.substring(hostname.indexOf('/')+1, hostname.length());

	long time = System.currentTimeMillis();

	n.putAttribute("hostname", hostname);

	n.putAttribute("source", "psl.oracle.impl.OracleSienaInterface");

	n.putAttribute("timestamp", time);

      n.putAttribute("type", "oracleQuery");

      n.putAttribute("query", query);

	System.out.println("Oracle is sending a query to Oracle: " + query);

	try

	{

	    si.publish(n);

	}

 	catch(siena.SienaException se)

	{

	    se.printStackTrace();

	}



   }*/


    public void generateTriKXEvent(String event)

   {
        Notification  n1 = new Notification();
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
	    hostname = hostname.substring(hostname.indexOf('/')+1, hostname.length());
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

            processMPQuery(query, MPHost, MPSource);

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