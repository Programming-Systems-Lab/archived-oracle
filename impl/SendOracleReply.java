/**

 * Title: SendOracleReply

 * Description: This class will be used to implement a mechanism to

 *              send the modules or module URI to Metaparser. At present

 *              it is not used but it will be modify to incorporate

 *              Worklets functionality or other mechanism used to transfer

 *              the data to Metaparser.

 * Copyright (c) 2000: The Trustees of Columbia University and the City of New York.

  *                              All Rights Reserved.

 * Company:      <p>

 * @author Kanan Naik

 * @version 1.0

 */



package psl.oracle.impl;



import psl.oracle.exceptions.*;
import psl.worklets2.wvm.*;
import psl.worklets2.worklets.*;



import java.io.*;



public class SendOracleReply

{



  /**

   * Constructor

   */

  public SendOracleReply()

  {

  }



  /**

   * This method must be called in order to send back the Schemafragment

   * object.

   */

  public String sendReply(WVM wvm, String MPHost, String moduleName)

  {

    String msg = null;
    String fileName = "psl/oracle/modules/" + moduleName;
    File classFile = new File(fileName);
    boolean classExists = classFile.exists();
    if(classExists == false)

    {

      msg = "Exception occured at Oracle: Module name specified does not exists.";

      return msg;

    }
    SendWorklet sw = new SendWorklet (wvm, MPHost, "MPRegistry", fileName);

    return null;

  }

}
