package psl.oracle.impl;

import java.io.*;
import java.net.*;


public class SendTriKXSignal
{
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