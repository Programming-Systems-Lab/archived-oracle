/**
 * Title: OracleDBAccess
 * Description: This class provides an GUI interface to the Oracle database.
 *              It provides essential methods like Add and Delete
 * Copyright (c) 2001: The Trustees of Columbia University and the City of New York.
  *                              All Rights Reserved.
 * Company:      <p>
 * @author Kanan Naik
 * @version 1.0
 */
package psl.oracle;

import java.awt.*;
import java.applet.*;

public class OracleDBAccess extends Applet
{
	public void init()
	{
		setLayout(null);
		setBackground(new java.awt.Color(188,187,198));
		setForeground(java.awt.Color.white);
		setSize(489,236);
		addButton.setLabel("ADD");
		add(addButton);
		addButton.setBackground(new java.awt.Color(0,64,128));
		addButton.setBounds(324,48,96,34);
		deleteButton.setLabel("Delete");
		add(deleteButton);
		deleteButton.setBackground(new java.awt.Color(0,64,128));
		deleteButton.setBounds(324,145,96,34);
		mainLabel.setText("Oracle Database Access");
		mainLabel.setAlignment(java.awt.Label.CENTER);
		add(mainLabel);
		mainLabel.setForeground(java.awt.Color.black);
		mainLabel.setFont(new Font("Dialog", Font.PLAIN, 15));
		mainLabel.setBounds(48,48,180,40);
		SymMouse aSymMouse = new SymMouse();
		addButton.addMouseListener(aSymMouse);
		deleteButton.addMouseListener(aSymMouse);
		SymAction lSymAction = new SymAction();

	}

	java.awt.Button addButton = new java.awt.Button();
	java.awt.Button deleteButton = new java.awt.Button();
	java.awt.Label mainLabel = new java.awt.Label();

	class SymMouse extends java.awt.event.MouseAdapter
	{
		public void mouseClicked(java.awt.event.MouseEvent event)
		{
			Object object = event.getSource();
			if (object == addButton)
				addButton_MouseClicked(event);
			else if (object == deleteButton)
				deleteButton_MouseClicked(event);


		}
	}

	void addButton_MouseClicked(java.awt.event.MouseEvent event)
	{
		addButton_MouseClicked_Interaction2(event);
	}

	void deleteButton_MouseClicked(java.awt.event.MouseEvent event)
	{
		deleteButton_MouseClicked_Interaction1(event);
	}

	void addButton_MouseClicked_Interaction2(java.awt.event.MouseEvent event)
	{
		try {
			(new AddFrame()).setVisible(true);
		} catch (java.lang.Exception e) {
		}
	}

	void deleteButton_MouseClicked_Interaction1(java.awt.event.MouseEvent event)
	{
		try {
			(new DeleteFrame()).setVisible(true);
		} catch (java.lang.Exception e) {
		}
	}

	class SymAction implements java.awt.event.ActionListener
	{
		public void actionPerformed(java.awt.event.ActionEvent event)
		{
		}
	}
}
