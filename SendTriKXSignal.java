/**
 * Title: SendTriKXSignal
 * Description: This class is used to generate a Siena event for TriKX.
 *              It should be used when Oracle needs to change its
 *              TriKX portal. The modified portal file is taken from the
 *              predefined location.
 * Copyright (c) 2000: The Trustees of Columbia University and the City of New York.
 *                              All Rights Reserved.
 * Company:      <p>
 * @author Kanan Naik
 * @version 1.0
 */

package psl.oracle;

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
        OracleSienaInterface osi = new OracleSienaInterface();
        osi.generateTriKXEvent("Oracle");
    }

}
