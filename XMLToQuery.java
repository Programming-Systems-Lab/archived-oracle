/**
 * Title: XMLToQuery
 * Description: This class is used to extract different elements (element
 *              name, path, namespace) from the query received in an XML
 *              format.
 * Copyright (c) 2000: The Trustees of Columbia University and the City of New York.
  *                              All Rights Reserved.
 * Company:      <p>
 * @author Kanan Naik
 * @version 1.0
 */

package psl.oracle;


public class XMLToQuery
{
  String query = null;

  /**
   * Constructor
   */
  public XMLToQuery()
  {
  }

  /**
   * Set the given string as a query. It should be called
   * before calling other methods of this class to retrieve
   * the element values.
   */
  public XMLToQuery(String query)
  {
    this.query = query;
  }

  /**
   * This method will return the namespace provided in the query.
   * It will return null if namespace is not present.
   */
  public String getNamespace() throws InvalidQueryFormatException
  {
    if(query == null)
      throw new InvalidQueryFormatException();
    int index1 = query.indexOf(" name=");
    if(index1 == -1)
    	throw new InvalidQueryFormatException();
    int index2 = query.indexOf('"', index1+7);
    if(index2 == -1)
    {
	index2 = query.indexOf("'",index1+7);
	if(index2 == -1)
		throw new InvalidQueryFormatException();
    }	
    String name = query.substring(index1+7, index2);
    int index3 = name.indexOf(':');
    if(index3 == -1)
      return null;
    else
    {
      String namespace = name.substring(0, index3);
      return namespace;
    }
  }

  /**
   * This method will return the element name provided in the query.
   * It will throw an InvalidQueryFormat exception if name is not
   * present.
   */
  public String getName() throws InvalidQueryFormatException
  {
    if(query == null)
      throw new InvalidQueryFormatException();
    int index1 = query.indexOf(" name=");
    if(index1 == -1)
      throw new InvalidQueryFormatException();
    int index2 = query.indexOf('"', index1+7);
    if(index2 == -1)
    {
	index2 = query.indexOf("'",index1+7);
	if(index2 == -1)
		throw new InvalidQueryFormatException();
    }	
    String temp = query.substring(index1+7, index2);
    int index3 = temp.indexOf(':');
    String name = null;
    if(index3 == -1)
    {
      name = temp;
    }
    else
    {
      name = temp.substring(index3+1, temp.length());
    }
    return name;
  }

  /**
   * This method will return the path provided in the query.
   * It will return null if path is not present.
   */
  public String getPath() throws InvalidQueryFormatException
  {
    String path = null;
    if(query == null)
      throw new InvalidQueryFormatException();
    int index1 = query.indexOf("<xpath>");
    int index2 = query.indexOf("</xpath>");
    if((index1 == -1) || (index2 == -1))
      throw new InvalidQueryFormatException(query);
    path = query.substring(index1+7, index2);
    if(path.length() < 1)
      return null;
    return path;
  }

  /**
   * This method will return the type provided in the query.
   * It will return null if type is not present.
   */
  public String getType() throws InvalidQueryFormatException
  {
    if(query == null)
      throw new InvalidQueryFormatException();
    int index1 = query.indexOf(" type=");
    int index2 = query.indexOf('"', index1+7);
    if(index2 == -1)
	index2 = query.indexOf("'", index1+7);		
    if((index1 == -1) || (index2 == -1))
      throw new InvalidQueryFormatException();
    String type = query.substring(index1+7, index2);
    if(type.length() < 1)
      return null;
    return type;
  }
}













