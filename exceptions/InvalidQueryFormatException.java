/**
 * Title: InvalidQueryFormatException
 * Description: Exception is thrown when a query received from Metaparser
 *              does not have a valid XML format. Valid XML format of query
 *              is: <FleXML:schemaQuery version="1.0" name="NAMESPACE:
 *              ELEMENT"><xPath>PATH</xPath></FleXML:schemaQuery>
 *              Here NAMESPACE and PATH are optional but at least one of
 *              them is required.
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
				+ "<schemaQuery version=\"1.0\" name="
                                + "\"NAMESPACE:ELEMENT\"><xpath>PATH</xpath>"
                                + "</schemaQuery>");
   }

  public InvalidQueryFormatException(String msg)
   {
    System.out.println("Query " + msg + " does not have proper format: "
                         	+ "<schemaQuery version=\"1.0\" name="
                                + "\"NAMESPACE:ELEMENT\"><xpath>PATH</xpath>"
                                + "</schemaQuery>");
   }
}