package psl.oracle.impl;

import psl.oracle.exceptions.*;

import java.io.*;

public class SendOracleReply
{
  public SendOracleReply()
  {
  }

  public String sendReply(SchemaFragment fragment, String MPHost, String MPSource)
  {
    String msg = null;
    for(int i=0; i<3; i++)
    {
      String moduleName = fragment.getModuleName(i);
      System.out.println(moduleName);
      File classFile = new File(moduleName);
      boolean classExists = classFile.exists();
      if(classExists == false)
      {
        msg = "Exception occured at Oracle: Module name specified does not exists.";
        return msg;
      }

      System.out.println(MPHost);
      System.out.println(MPSource);
      //make an http connection tp MPHost at some random port
      //do some fileoutputstream - etc to create and dump the class file at
      //modules directory in payh given by MPSource
     }
         return null;
  }
}