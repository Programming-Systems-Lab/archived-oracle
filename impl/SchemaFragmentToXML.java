/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) <p>
 * Company:      <p>
 * @author
 * @version 1.0
 */
package psl.oracle.impl;

public class SchemaFragmentToXML
{

  public SchemaFragmentToXML()
  {
  }

  public String toXML(SchemaFragment sf)
  {
    String xmlForm = null;
    String initBuffer = "<?xml version=\"1.0\" <FleXML:schemaFrag version=\"1.0\" name=\"";
    String name = sf.getName();
    String fragment = sf.getDescription();
    String moduleName = sf.getModuleName();
    boolean persistent = sf.isPersistent();
    String instanceName = sf.getInstanceName();
    String buffer = name + "\"> <schema>" + fragment + "</schema> <module type=\"java1.3\""
                    + " persistent=\"" + persistent + "\" instance"
                    + "=\"" + instanceName + "\">" + moduleName + "</module> "
                    + "</FleXML:schemaFrag>";
    xmlForm = initBuffer.concat(buffer);
    return xmlForm;
  }
}