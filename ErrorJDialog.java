/**
 * Title: ErrorJDialog
 * Description: A dialog window to display an error message
 * Copyright (c) 2000: The Trustees of Columbia University and the City of New York.
  *                              All Rights Reserved.
 * Company:      <p>
 * @author Kanan Naik
 * @version 1.0
 */

package psl.oracle;

import java.awt.*;
import javax.swing.*;

public class ErrorJDialog extends javax.swing.JDialog
{
	public ErrorJDialog(Frame parent)
	{
		super(parent);
		getContentPane().setLayout(null);
		setSize(397,149);
		setVisible(false);
		msgJLabel.setDoubleBuffered(true);
		msgJLabel.setText("Message:");
		msgJLabel.setAutoscrolls(true);
		getContentPane().add(msgJLabel);
		msgJLabel.setBounds(36,24,348,24);
		errorJButton.setText("OK");
		getContentPane().add(errorJButton);
		errorJButton.setFont(new Font("Dialog", Font.BOLD, 15));
		errorJButton.setBounds(168,84,72,24);
		SymMouse aSymMouse = new SymMouse();
		errorJButton.addMouseListener(aSymMouse);

	}


	public ErrorJDialog()
	{
		this((Frame)null);
	}

	public ErrorJDialog(String sTitle)
	{
		this();
		setTitle(sTitle);
	}

	public void setVisible(boolean b)
	{
		if (b)
			setLocation(50, 50);
		super.setVisible(b);
	}

	static public void main(String args[])
	{
		(new ErrorJDialog()).setVisible(true);
	}

	public void addNotify()
	{
		// Record the size of the window prior to calling parents addNotify.
		Dimension size = getSize();

		super.addNotify();

		if (frameSizeAdjusted)
			return;
		frameSizeAdjusted = true;

		// Adjust size of frame according to the insets
		Insets insets = getInsets();
		setSize(insets.left + insets.right + size.width, insets.top + insets.bottom + size.height);
	}

	// Used by addNotify
	boolean frameSizeAdjusted = false;

	javax.swing.JLabel msgJLabel = new javax.swing.JLabel();
	javax.swing.JButton errorJButton = new javax.swing.JButton();


	class SymMouse extends java.awt.event.MouseAdapter
	{
		public void mouseClicked(java.awt.event.MouseEvent event)
		{
			Object object = event.getSource();
			if (object == errorJButton)
				errorJButton_mouseClicked(event);
		}
	}

	void errorJButton_mouseClicked(java.awt.event.MouseEvent event)
	{
		errorJButton_mouseClicked_Interaction1(event);
	}

	void errorJButton_mouseClicked_Interaction1(java.awt.event.MouseEvent event)
	{
	    this.setVisible(false);
    }

	public void setMessage( String msg)
	{
	    this.setVisible(true);
	    msgJLabel.setText(msg);
	}
}