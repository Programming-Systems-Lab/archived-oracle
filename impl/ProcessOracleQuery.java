package psl.oracle.impl;

import siena.*;
import psl.oracle.exceptions.*;

public class ProcessOracleQuery implements Notifiable
{
    public ProcessOracleQuery(OracleSienaInterface os)
    {
	
    }

    public void notify(Notification n)
    {
	String msg =  null;
	boolean result = false;
      AttributeValue av = n.getAttribute("oracleQuery");
      if (av != null)
      {
      	String query = av.stringValue();
	      System.out.println("Oracle got a query " + query);
		SchemaFragment fragment = new SchemaFragment();
		Oracle oracle = new Oracle();
		try
		{
	    		fragment = oracle.getFragment(query, "oracleQuery");
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
		OracleSienaInterface ors = new OracleSienaInterface();
		ors.processOracleQuery(query,result, msg);
 	   }
	}

    public void notify(Notification[] no)
    {
    }

	
}
