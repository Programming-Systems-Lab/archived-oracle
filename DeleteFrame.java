/**
 * Title: DeleteFrame
 * Description: This class provides a frame to perform a delete operation
 *              on Oracle database
 * Copyright (c) 2001: The Trustees of Columbia University and the City of New York.
  *                              All Rights Reserved.
 * Company:      <p>
 * @author Kanan Naik
 * @version 1.0
 */
package psl.oracle;

import java.awt.*;
import java.io.*;
import java.util.*;

public class DeleteFrame extends Frame
{
    String dbName = null;
    DBInterface db = null;

	public DeleteFrame()
	{
		setLayout(null);
		setBackground(new java.awt.Color(181,186,204));
		setSize(459,298);
		setVisible(false);
		modifyLabel1.setText("Enter an element name");
		add(modifyLabel1);
		modifyLabel1.setFont(new Font("Dialog", Font.PLAIN, 12));
		modifyLabel1.setBounds(24,60,144,24);
		deleteLabel2.setText("Enter the path(format: /../)");
		add(deleteLabel2);
		deleteLabel2.setFont(new Font("Dialog", Font.PLAIN, 12));
		deleteLabel2.setBounds(24,108,144,24);
		add(deleteText1);
		deleteText1.setBounds(180,60,216,24);
		add(deleteMessageLabel);
		deleteMessageLabel.setBounds(36,216,372,24);
		add(deleteText2);
		deleteText2.setBounds(180,108,216,24);
		submitButton2.setLabel("Submit");
		add(submitButton2);
		submitButton2.setBackground(new java.awt.Color(0,64,128));
		submitButton2.setForeground(java.awt.Color.white);
		submitButton2.setBounds(240,156,86,24);
		setTitle("Delete Frame");
		SymWindow aSymWindow = new SymWindow();
		this.addWindowListener(aSymWindow);
		SymMouse aSymMouse = new SymMouse();
		submitButton2.addMouseListener(aSymMouse);

	}

	public DeleteFrame(String title)
	{
		this();
		setTitle(title);
	}

    /**
     * Shows or hides the component depending on the boolean flag b.
     * @param b  if true, show the component; otherwise, hide the component.
     * @see java.awt.Component#isVisible
     */
    public void setVisible(boolean b)
	{
		if(b)
		{
			setLocation(50, 50);
		}
		super.setVisible(b);
	}

	static public void main(String args[])
	{
		(new DeleteFrame()).setVisible(true);
	}

	public void addNotify()
	{
	    Dimension d = getSize();

		super.addNotify();

		if (fComponentsAdjusted)
			return;

		// Adjust components according to the insets
		Insets insets = getInsets();
		setSize(insets.left + insets.right + d.width, insets.top + insets.bottom + d.height);
		Component components[] = getComponents();
		for (int i = 0; i < components.length; i++)
		{
			Point p = components[i].getLocation();
			p.translate(insets.left, insets.top);
			components[i].setLocation(p);
		}
		fComponentsAdjusted = true;
	}

    boolean fComponentsAdjusted = false;

	java.awt.Label modifyLabel1 = new java.awt.Label();
	java.awt.Label deleteLabel2 = new java.awt.Label();
	java.awt.TextField deleteText1 = new java.awt.TextField();
	java.awt.Label deleteMessageLabel = new java.awt.Label();
	java.awt.TextField deleteText2 = new java.awt.TextField();
	java.awt.Button submitButton2 = new java.awt.Button();

	class SymWindow extends java.awt.event.WindowAdapter
	{
		public void windowClosing(java.awt.event.WindowEvent event)
		{
			Object object = event.getSource();
			if (object == DeleteFrame.this)
				DeleteFrame_WindowClosing(event);
		}
	}

	void DeleteFrame_WindowClosing(java.awt.event.WindowEvent event)
	{
		setVisible(false);		 // hide the Frame
	}

	class SymMouse extends java.awt.event.MouseAdapter
	{
		public void mouseClicked(java.awt.event.MouseEvent event)
		{
			Object object = event.getSource();
			if (object == submitButton2)
				submitButton2_MouseClicked(event);
		}
	}

    String initializeParam()
	{
	    //code to read parameters from the property file
	    File file = new File("oracle.prop");
        if(file.exists() ==  false)
        {
            deleteMessageLabel.setText("File 'oracle.prop' must exist");
            return "error";
        }
        Properties property = new Properties();
        try
        {
            property.load(new FileInputStream(file));
        }
        catch(FileNotFoundException ffe)
        {
            deleteMessageLabel.setText("Exception: " + ffe);
        }
        catch(IOException ioe)
        {
            deleteMessageLabel.setText("Exception: "+ ioe);
        }
        dbName = property.getProperty("dbName");
        if(dbName == null || dbName.length() < 1)
        {
            deleteMessageLabel.setText("Parameter 'dbName' must be set in 'oracle.prop' file.");
            return "error";
        }
        try
        {
            db = new DBInterface(dbName);
        }
        catch(Exception e)
        {
            deleteMessageLabel.setText("Exception in database: " + e);
            shutdownDB();
        }
        return null;
   }


	void submitButton2_MouseClicked(java.awt.event.MouseEvent event)
	{
		String name = deleteText1.getText();
		String path = deleteText2.getText();
		name = name.trim();
        String msg = initializeParam();
        if(msg == null)
        {
            String key = "0." + name;
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

	void shutdownDB()
   {
       db.shutdown();
   }
}
