
/**
 * Title: InvalidQueryFormatException
 * Description: Exception is thrown when a query format (<src id>,[<namespace>:],
 *		    <element name>,<path>) received from Metaparser is not valid.
 * Copyright (c) 2000: The Trustees of Columbia University and the City of New York. 
 *                     All Rights Reserved.
 * @author Kanan Naik
 * @version 1.0
 */

package psl.oracle.exceptions;

public class InvalidQueryFormatException extends Throwable
 {

  public InvalidQueryFormatException()
   {
      System.out.println("Query  does not have proper format: "
				+  "<src id>,[<namespace>:],<element name>,<path>");
   }

  public InvalidQueryFormatException(String msg)
   {
    System.out.println("Query " + msg + " does not have proper format: "
			    + "<src id>,[<namespace>:],<element name>,<path>");
   }
}