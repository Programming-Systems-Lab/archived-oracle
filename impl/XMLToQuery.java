
/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) <p>
 * Company:      <p>
 * @author
 * @version 1.0
 */
package psl.oracle.impl;

import psl.oracle.exceptions.*;

public class XMLToQuery
{
  String query = null;

  public XMLToQuery()
  {
  }

  public XMLToQuery(String query)
  {
    this.query = query;
  }

  public String getNamespace() throws InvalidQueryFormatException
  {
    if(query == null)
      throw new InvalidQueryFormatException();
    int index1 = query.indexOf(" name=");
    if(index1 == -1)
      throw new InvalidQueryFormatException();
    int index2 = query.indexOf('"',index1+7);
    if(index2 == -1)
      throw new InvalidQueryFormatException();
    String name = query.substring(index1+7, index2);
    int index3 = name.indexOf(':');
    if(index3 == -1)
      return null;
    else
    {
      String namespace = name.substring(index1+7, index3);
      return namespace;
    }
  }

  public String getName() throws InvalidQueryFormatException
  {
    if(query == null)
      throw new InvalidQueryFormatException();
    int index1 = query.indexOf(" name=");
    if(index1 == -1)
      throw new InvalidQueryFormatException();
    int index2 = query.indexOf('"',index1+7);
    if(index2 == -1)
      throw new InvalidQueryFormatException();
    String temp = query.substring(index1+7, index2);
    int index3 = temp.indexOf(':');
    String name = null;
    if(index3 == -1)
    {
      name = temp;
    }
    else
    {
      name = name.substring(index3+1, temp.length());
    }
    return name;
  }

  public String getPath() throws InvalidQueryFormatException
  {
    if(query == null)
      throw new InvalidQueryFormatException();
    int index1 = query.indexOf("<xPath>");
    int index2 = query.indexOf("</xPath>");
    if((index1 == -1) || (index2 == -1))
      throw new InvalidQueryFormatException();
    String path = query.substring(index1+7, index2);
    return path;
  }

  public String getType() throws InvalidQueryFormatException
  {
    if(query == null)
      throw new InvalidQueryFormatException();
    int index1 = query.indexOf(" type=");
    int index2 = query.indexOf('"', index1+7);
    if((index1 == -1) || (index2 == -1))
      throw new InvalidQueryFormatException();
    String type = query.substring(index1+7, index2);
    if(type.length() < 1)
      return null;
    return type;
  }
}