
/**
 * Title: InvalidQueryFormatException
 * Description: Exception is thrown when a query format (<src id>,[<namespace>:],
 *		    <element name>,<path>) received from Metaparser is not valid.
 * Copyright:    Copyright (c) <p>
 * Company:      <p>
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