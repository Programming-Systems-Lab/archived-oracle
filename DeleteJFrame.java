/**
 * Title: DeleteJFrame
 * Description: A frame to delete the tags to a database.
 * Copyright (c) 2000: The Trustees of Columbia University and the City of New York.
  *                              All Rights Reserved.
 * Company:      <p>
 * @author Kanan Naik
 * @version 1.0
 */

package psl.oracle;

import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.util.*;

public class DeleteJFrame extends javax.swing.JFrame
{
    DBInterfaceFrame db = null;
    String dbName = "oracleDB";
    String rootPath = null;
    String dbLoc = null;
	public DeleteJFrame(String path)
	{
	    rootPath = path;
		getContentPane().setLayout(null);
		getContentPane().setBackground(new java.awt.Color(204,207,201));
		setSize(387,289);
		setVisible(false);
		deleteJLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		deleteJLabel.setText("Delete Tag");
		getContentPane().add(deleteJLabel);
		deleteJLabel.setForeground(java.awt.Color.black);
		deleteJLabel.setFont(new Font("Dialog", Font.BOLD, 20));
		deleteJLabel.setBounds(144,24,120,24);
		deleteJLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		deleteJLabel1.setText("Enter a name of");
		getContentPane().add(deleteJLabel1);
		deleteJLabel1.setForeground(java.awt.Color.black);
		deleteJLabel1.setFont(new Font("Dialog", Font.BOLD, 12));
		deleteJLabel1.setBounds(36,72,120,24);
		deleteJLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		deleteJLabel2.setText("an element");
		getContentPane().add(deleteJLabel2);
		deleteJLabel2.setForeground(java.awt.Color.black);
		deleteJLabel2.setFont(new Font("Dialog", Font.BOLD, 12));
		deleteJLabel2.setBounds(72,96,84,24);
		deleteJTextField1.setToolTipText("format: [namespace:]name");
		getContentPane().add(deleteJTextField1);
		deleteJTextField1.setBounds(168,84,168,24);
		deleteJLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		deleteJLabel3.setText("Enter the xpath value");
		getContentPane().add(deleteJLabel3);
		deleteJLabel3.setForeground(java.awt.Color.black);
		deleteJLabel3.setFont(new Font("Dialog", Font.BOLD, 12));
		deleteJLabel3.setBounds(24,132,132,24);
		getContentPane().add(deleteMessageLabel);
		deleteMessageLabel.setForeground(java.awt.Color.black);
		deleteMessageLabel.setBounds(36,240,324,24);
		getContentPane().add(deleteJTextField2);
		deleteJTextField2.setBounds(168,132,168,24);
		deleteSubmitJButton.setText("Submit");
		deleteSubmitJButton.setActionCommand("Submit");
		getContentPane().add(deleteSubmitJButton);
		deleteSubmitJButton.setBackground(new java.awt.Color(225,223,223));
		deleteSubmitJButton.setFont(new Font("Dialog", Font.BOLD, 15));
		deleteSubmitJButton.setBounds(252,192,84,24);
		deleteMessageLabel.setAutoscrolls(true);

		closeJButton.setText("Close");
		closeJButton.setActionCommand("Close");
		getContentPane().add(closeJButton);
		closeJButton.setBackground(new java.awt.Color(225,223,223));
		closeJButton.setFont(new Font("Dialog", Font.BOLD, 15));
		closeJButton.setBounds(56,192,84,24);

		getContentPane().add(deleteMessageLabel);
		SymMouse aSymMouse = new SymMouse();
		deleteSubmitJButton.addMouseListener(aSymMouse);
        closeJButton.addMouseListener(aSymMouse);
	}

	public DeleteJFrame(String sTitle, String path)
	{
		this(path);
		setTitle(sTitle);
	}

	public void setVisible(boolean b)
	{
		if (b)
			setLocation(50, 50);
		super.setVisible(b);
	}


	public void addNotify()
	{
		// Record the size of the window prior to calling parents addNotify.
		Dimension size = getSize();

		super.addNotify();

		if (frameSizeAdjusted)
			return;
		frameSizeAdjusted = true;

		// Adjust size of frame according to the insets and menu bar
		Insets insets = getInsets();
		javax.swing.JMenuBar menuBar = getRootPane().getJMenuBar();
		int menuBarHeight = 0;
		if (menuBar != null)
			menuBarHeight = menuBar.getPreferredSize().height;
		setSize(insets.left + insets.right + size.width, insets.top + insets.bottom + size.height + menuBarHeight);
	}

	// Used by addNotify
	boolean frameSizeAdjusted = false;


	javax.swing.JLabel deleteJLabel = new javax.swing.JLabel();
	javax.swing.JLabel deleteJLabel1 = new javax.swing.JLabel();
	javax.swing.JLabel deleteJLabel2 = new javax.swing.JLabel();
	javax.swing.JTextField deleteJTextField1 = new javax.swing.JTextField();
	javax.swing.JLabel deleteJLabel3 = new javax.swing.JLabel();
	javax.swing.JTextField deleteJTextField2 = new javax.swing.JTextField();
	javax.swing.JButton deleteSubmitJButton = new javax.swing.JButton();
	javax.swing.JLabel deleteMessageLabel = new javax.swing.JLabel();
    javax.swing.JButton closeJButton = new javax.swing.JButton();

    void shutdownDB()
   {
       db.shutdown();
   }


    String initializeParam()
    {
	String msg = null;
	//code to read parameters from the property file
	File file = new File(rootPath + File.separator + "oracle.prop");
	if(!file.exists())
	    {
		file = new File(rootPath + File.separator + "psl" + File.separator + "oracle" + File.separator + "oracle.prop");
		if(!file.exists())
		    {
			printError("File 'oracle.prop' does not exist");
			return "error";
		    }
	    }
	Properties property = new Properties();
	try
	    {
		property.load(new FileInputStream(file));
	    }
	catch(FileNotFoundException ffe)
	    {
		printError("Exception: " + ffe);
		return "error";
	    }
	catch(IOException ioe)
	    {
		printError("Exception: "+ ioe);
		return "error";
	    }
	dbLoc = property.getProperty("dbLocation");
	if(dbLoc == null || dbLoc.length() < 1)
	    {
		printError("Parameter 'dbLocation' must be set in 'oracle.prop' file.");
		return "error";
	    }
	//property file code end here
	try
	    {
		//dbName = dbLoc + File.separator + dbName;
		db = new DBInterfaceFrame(dbName);
	    }
	catch(Exception e)
	    {
		printError("Exception in database: " + e);
		msg = db.shutdown();
		return "error";
	    }
	return null;
    }


	class SymMouse extends java.awt.event.MouseAdapter
	{
		public void mouseClicked(java.awt.event.MouseEvent event)
		{
			Object object = event.getSource();
			if (object == deleteSubmitJButton)
				deleteSubmitJButton_mouseClicked(event);
		    if (object == closeJButton)
				closeJButton_mouseClicked(event);
		}
	}

    void closeJButton_mouseClicked(java.awt.event.MouseEvent event)
    {
        dispose();
        setVisible(false);
	}

	void deleteSubmitJButton_mouseClicked(java.awt.event.MouseEvent event)
	{
		String name = deleteJTextField1.getText();
		String path = deleteJTextField2.getText();
		name = name.trim();
        String msg = initializeParam();
        if(msg == null)
        {
            String key = "0." + name;
            if((path != null) && (path.length() > 0))
                key = key+","+path;
    	    Object data = db.get(key);
    	    if(data == null)
    	    {
        	    deleteMessageLabel.setText("No schema entry for "
                                     + name );
	        }
	        else
	        {
	            ElementInfo e1 = ElementInfo.getElementInfo((String)data);
	            int version = -1;
	            String newVersion = String.valueOf(version);
	            newVersion = newVersion.trim();
    	        int index = key.indexOf('.');
	            String newKey = newVersion + "." + key.substring(index+1, key.length());
	            db.remove(key);
	            e1.setKey(newKey);
	            deleteMessageLabel.setText("Deleting " + name);
	            db.put(newKey, e1.toString());
	            shutdownDB();
	        }
	    }
	}

     public static void printError(String msg)
    {
	ErrorJDialog ed = new ErrorJDialog();
	ed.setMessage(msg);
    }
}
