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







package psl.oracle;



import java.io.*;
import java.util.*;


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



  public String sendReply(String key, String MPHost, String name, Vector moduleName)

  {

    String msg = null;

    SendWorklet.send(key, MPHost, name, moduleName);

    return null;

  }

}

