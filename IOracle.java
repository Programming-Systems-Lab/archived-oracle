/**
 * Title: IOracle
 * Description: Interface to Oracle. The only method which Oracle
 *              has to  implement is getFragment. This method throws
 *              UnknownTagException and InvalidQueryFormatException.
 * Copyright (c) 2000: The Trustees of Columbia University and the City of New York.
 *                              All Rights Reserved.
 * @author Kanan Naik
 * @version 1.0
 */



package psl.oracle;
import java.io.*;

/** This method returns a schemafragment and module information in XML format
 * when a proper formatted query is received using Siena. The proper query
 * format is : <FleXML:schemaQuery version="1.0" name="NAMESPACE:ELEMENT">
 * <xPath>PATH</xPath></FleXML:schemaQuery>
*/

public interface IOracle 
{
       public SchemaFragment getFragment(String query, String oraclePath) throws UnknownTagException,
                                                         InvalidQueryFormatException,
  						         InvalidSchemaFormatException;
}







































