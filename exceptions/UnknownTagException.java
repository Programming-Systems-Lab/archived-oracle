
/**
 * Title: UnknownTagException
 * Description: Exception is thrown when a tag requested by Metaparser is not
 *              known to Oracle. There is no entry for a given tag and its
 *              path in database.
 * Copyright (c) 2000: The Trustees of Columbia University and the City of New York. 
  *                              All Rights Reserved.
 * @author Kanan Naik
 * @version 1.0
 */


package psl.oracle.exceptions;


public class UnknownTagException extends Throwable {
  public UnknownTagException ()
  {
	System.out.println("Unknown tag exception. There is no entry for a given "
				   + "tag and its path.");
  }

  public UnknownTagException (String msg)
  {
	  System.out.println("Unknown tag exception error: "+ msg);
  }

}



