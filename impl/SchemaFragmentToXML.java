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

package psl.oracle.impl;

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
    String initBuffer = "<?xml version=\"1.0\" <FleXML:schemaFrag version=\"1.0\" name=\"";
    String name = sf.getName();
    String fragment = sf.getDescription();
    String moduleName = sf.getModuleName();
    boolean persistent = sf.isPersistent();
    String instanceName = sf.getInstanceName();
    String buffer = null;
    if(moduleName.length() > 0)
    {
      buffer = name + "\"> <schema>" + fragment + "</schema> <module type=\"java1.3\""
                    + " persistent=\"" + persistent + "\" instance"
                    + "=\"" + instanceName + "\">" + moduleName + "</module> "
                    + "</FleXML:schemaFrag>";
    }
    else
    {
      buffer = name + "\"> <schema>" + fragment + "</schema> <module type=\"\""
                    + " persistent=\"\" instance"
                    + "=\"" + instanceName + "\">" + moduleName + "</module> "
                    + "</FleXML:schemaFrag>";
    }
    xmlForm = initBuffer.concat(buffer);
    return xmlForm;
  }
}