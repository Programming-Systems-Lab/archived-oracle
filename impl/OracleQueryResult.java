package psl.oracle.impl;

import siena.*;
import psl.oracle.exceptions.*;

public class OracleQueryResult implements Notifiable
{
    public OracleQueryResult(OracleSienaInterface os)
    {
	
    }

    public void notify(Notification n)
    {
          AttributeValue av = n.getAttribute("oracleQueryResult");
          if (av != null)
	    {
	      boolean result = av.booleanValue();
	      av = n.getAttribute("value");
	      String reply = av.stringValue();
	      if(av != null)
	      {
		  if(result == false)
		  {
		      System.out.println("Exception occured at Oracle:" + reply);
		  }
		  else //Got reply - now download code here
		  {
		      av = n.getAttribute("oracleQuery");
		      String query = av.stringValue();
		      System.out.println("got reply: " + reply);
		      Oracle oracle = new Oracle();
		      oracle.processOracleReply(query, reply);
		  }
	       }
	       else 
	       {
		  System.out.println("Oracle error: Oracle reply is error. " + av.stringValue()); 
	       }
	  }
          else
	  {
	       System.out.println("Oracle error: Oracle reply is error. " + av.booleanValue()); 
	  }
    }

    public void notify(Notification[] no)
    {
    }

	
}
