/**
 * Title: SendTriKXSignal
 * Description: This class is used to generate a Siena event for TriKX.
 *              It should be used to when Oracle needs to change its
 *              TriKX portal. The modified portal file be taken from the
 *              predefined location.
 * Copyright (c) 2000: The Trustees of Columbia University and the City of New York.
 *                              All Rights Reserved.
 * Company:      <p>
 * @author Kanan Naik
 * @version 1.0
 */

package psl.oracle.impl;

import java.io.*;
import java.net.*;


public class SendTriKXSignal
{
  /**
   * This method will start a webserver that is used to download
   * a code by TriKX.  Then it will generate Siena event usign
   * OracleSienaInterface class.
   */
    public static void main(String[] args)
    {
        StartWebServer sws = new StartWebServer();
        String msg = sws.startServer();
        if (msg != null) //exception occured while starting a server
        {
            System.out.println("Exception: " + msg);
            System.exit(1);
        }

        OracleSienaInterface osi = new OracleSienaInterface();
        osi.generateTriKXEvent("oracle");
    }

}