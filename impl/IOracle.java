/**
 * Title: IOracle
 * Description: Interface for Oracle. The only method which Oracle
 *              has to  implement is getFragment. This method throws
 *              UnknownTagException and InvalidQueryFormatException.
 * Copyright (c) 2000: The Trustees of Columbia University and the City of New York. 
  *                              All Rights Reserved.
 * @author Kanan Naik
 * @version 1.0
 */

package psl.oracle.impl;

import psl.groupspace.impl.*;
import psl.oracle.exceptions.*;
import java.io.*;

/** Can return SchemaFragment when given a query in a proper format.
 * <p>When an unknown XML tag is encountered, this tag is sent to the
 * Oracle by invoking the method getFragment(String query), which
 * returns a SchemaFragment.</p>
 */

public interface IOracle {
       public SchemaFragment getFragment(String rootName, String source) throws UnknownTagException,
                                                          InvalidQueryFormatException,
									    InvalidSchemaFormatException;
  }









