/**
 * Title: GetModuleInfoFrame
 * Description: This class provides a frame to ask user about information
 *              of a module
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

import org.apache.xerces.parsers.*;
import org.xml.sax.*;



public class GetModuleInfoFrame extends Frame implements Runnable
{
    String moduleInfo = null;

    public GetModuleInfoFrame(String value)
	{
		setLayout(null);
		setBackground(new java.awt.Color(181,186,204));
		setSize(441,236);
		add(addText1);
		addText1.setBounds(84,96,216,24);
		add(addMessageLabel);
		addMessageLabel.setBounds(86,36,324,24);
		addEnterButton.setLabel("Enter");
		add(addEnterButton);
		addEnterButton.setBackground(new java.awt.Color(0,64,128));
		addEnterButton.setForeground(java.awt.Color.white);
		addEnterButton.setBounds(324,96,86,24);
		addDefaultButton.setLabel("Default");
		add(addDefaultButton);
		addDefaultButton.setBackground(new java.awt.Color(0,64,128));
		addDefaultButton.setForeground(java.awt.Color.white);
		addDefaultButton.setBounds(84,156,86,24);
		addSkipButton.setLabel("Skip");
		add(addSkipButton);
		addSkipButton.setBackground(new java.awt.Color(0,64,128));
		addSkipButton.setForeground(java.awt.Color.white);
		addSkipButton.setBounds(214,156,86,24);
		setTitle("Module Information");
		SymWindow aSymWindow = new SymWindow();
		this.addWindowListener(aSymWindow);
		SymMouse aSymMouse = new SymMouse();
		addEnterButton.addMouseListener(aSymMouse);
		addDefaultButton.addMouseListener(aSymMouse);
		addSkipButton.addMouseListener(aSymMouse);
		addMessageLabel.setText("Enter module info. for "+value);
		setVisible(true);
		new Thread(this).start();
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

    public void destroyFrame()
	{
		//super.disable();
	}


	public void addNotify()
	{
	    // Record the size of the window prior to calling parents addNotify.
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

    // Used for addNotify check.
	boolean fComponentsAdjusted = false;

	java.awt.TextField addText1 = new java.awt.TextField();
	java.awt.Label addMessageLabel = new java.awt.Label();
	java.awt.Button addEnterButton = new java.awt.Button();
	java.awt.Button addDefaultButton = new java.awt.Button();
	java.awt.Button addSkipButton = new java.awt.Button();

	class SymWindow extends java.awt.event.WindowAdapter
	{
		public void windowClosing(java.awt.event.WindowEvent event)
		{
			Object object = event.getSource();
			if (object == GetModuleInfoFrame.this)
				GetModuleInfoFrame_WindowClosing(event);
		}
	}

	void GetModuleInfoFrame_WindowClosing(java.awt.event.WindowEvent event)
	{
		setVisible(false);		 // hide the Frame
	}

	class SymMouse extends java.awt.event.MouseAdapter
	{
		public void mouseClicked(java.awt.event.MouseEvent event)
		{
			Object object = event.getSource();
			if (object == addEnterButton)
				addEnterButton_MouseClicked(event);
			else if (object == addDefaultButton)
				addDefaultButton_MouseClicked(event);
			else if (object == addSkipButton)
				addSkipButton_MouseClicked(event);
		}
	}

	void addEnterButton_MouseClicked(java.awt.event.MouseEvent event)
	{
		String info = addText1.getText();
		if(!(info.length() > 0))
		{
		    moduleInfo = "";
		}
		else //check format of input and set the variable
		{
		    moduleInfo = info;
		}
		setVisible(false);
		notifyAll();
		//destroyFrame();
	    return;
	}

	void addDefaultButton_MouseClicked(java.awt.event.MouseEvent event)
	{
		// to do: code goes here.
		moduleInfo = "default";
		setVisible(false);
		notifyAll();
		//destroyFrame();
		return;
    }


	void addSkipButton_MouseClicked(java.awt.event.MouseEvent event)
	{
		moduleInfo = "skip";
		setVisible(false);
		notifyAll();
		//destroyFrame();
		return;
	}

	public void run()
	{
	    try{
	     wait();}
	     catch(Exception e){}
	     //AddFrame.setModuleInfo(moduleInfo);
	}
    public void getInfo()
    {

    }

}

