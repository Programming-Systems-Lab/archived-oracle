/*
 * Title: SendWorklet
 * Description:  This class is used to send a Worklet to MP. This Worklet
 *               carries tag-processors with it.
 * Copyright (c) 2000: The Trustees of Columbia University and the City of New York.
 *                              All Rights Reserved.
 * @author Kanan Naik
 * @version 1.0
 */

package psl.oracle;

import psl.worklets.*;
import psl.codetransfer.*;

import java.util.*;

public class SendWorklet {

    static WVM wvm;

    public SendWorklet (String host, String name)
    {
	wvm = new WVM(this, host, name);
    }
  
    public static void send(String key, String rHost, String rName, String moduleName) {
    //WVM wvm = new WVM(this, host, name);
    {
      // this code block creates the Vector of classfile locations to send out
      Worklet wkl = new Worklet(null);

      Vector v = new Vector();
      v.add(moduleName);
      System.out.println("Creating CFRWJ");
      CFRWJ wj = new CFRWJ(key, v, rHost, rName);

      System.out.println("Adding CFRWJ to WKL");
      wkl.addJunction(wj);

      System.out.println("Deploying WKL w/ CFRWJ");
      wkl.deployWorklet(wvm);
    }
  }

}


