/**

 * Title: SchemaFragmentToXML

 * Description: This class is used to convert object SchemaFragment

 *              in XML that will be sent to Metaparser.

 * Copyright (c) 2000: The Trustees of Columbia University and the City of New York.

 *                              All Rights Reserved.

 * Company:      <p>

 * @author Kanan Naik

 * @version 1.0

 */

package psl.oracle;

import java.util.*;
import java.lang.*;

public class SchemaFragmentToXML

{



  /**

   * Constructor

   */

  public SchemaFragmentToXML()

  {

  }



  /**

   * This method will be called to convert given SchemaFragment object into

   * XML. The format of resultant XML will be: <?xml version="1.0"><FleXML:

   * schemaFrag version="1.0" name="ELEMENT NAME"><schema>FRAGMENT</schema>

   * <module type="TYPE" persistent="true|false" instance="INSTANCE NAME">

   * MODULE NAME</module></FleXML:schemaFrag>

   */

  public String toXML(SchemaFragment sf)

  {

    String xmlForm = null;

    String initBuffer = "<schemaFrag version=\"1.0\" name=\"";

    String name = sf.getName();

    String fragment = sf.getDescription();

    Vector moduleName = sf.getModuleName();

    boolean persistent = sf.isPersistent();

    String instanceName = sf.getInstanceName();

    String buffer = null;

    String moduleString = "";
	for(Enumeration e = moduleName.elements();e.hasMoreElements();)
    {
       if(moduleString == "")
           moduleString = e.nextElement().toString();
       else
           moduleString = moduleString + "," + e.nextElement();
    }
    if(moduleString.length() > 0)

    {

      buffer = name + "\"> <subschema>" + fragment + "</subschema> <module type=\"java1.3\""

                    + " cacheable=\"" + persistent + "\" instance"

                    + "=\"" + instanceName + "\">" + moduleString + "</module> "

                    + "</schemaFrag>";

    }

    else

    {

      buffer = name + "\"> <schema>" + fragment + "</schema> <module type=\"\""

                    + " cacheable=\"\" instance"

                    + "=\"" + instanceName + "\">" + moduleString + "</module> "

                    + "</schemaFrag>";

    }

    xmlForm = initBuffer.concat(buffer);

    return xmlForm;

  }

}



