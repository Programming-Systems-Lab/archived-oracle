
/**
 * Title: InvalidSchemaFormatException
 * Description: Exception is thrown when a schema information format stored
 *              in database is not valid. Proper format should be: <fragment>, 
 *              <moduleInfo>
 *              ModuleInfo must contain module name, if singleton and instance name
 * Copyright:    Copyright (c) <p>
 * Company:      <p>
 * @author Kanan Naik
 * @version 1.0
 */

package psl.oracle.exceptions;

public class InvalidSchemaFormatException extends Throwable
 {

  public InvalidSchemaFormatException()
   {
      System.out.println("Schema  Information does not have proper format.");
			
   }

  public InvalidSchemaFormatException(String msg)
   {
    System.out.println("Schema  Information does not have proper format: " + msg);
   }
}