/**
 * Title: InvalidSchemaFormatException
 * Description: Exception is thrown when a schema information format stored
 *              in the database is not valid. Proper format ise: <fragment>,
 *              <moduleInfo>
 *              ModuleInfo must contain module name, if persistent and instance name
 * Copyright (c) 2000: The Trustees of Columbia University and the City of New York.
  *                              All Rights Reserved.
 * @author Kanan Naik
 * @version 1.0
 */

package psl.oracle;

public class InvalidSchemaFormatException extends Throwable
 {

  public InvalidSchemaFormatException()
   {
      System.err.println("Schema  Information does not have proper format.");

   }

  public InvalidSchemaFormatException(String msg)
   {
    System.err.println("Schema  Information does not have proper format: " + msg);
   }
}
