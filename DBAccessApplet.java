/**
 * Title: DBAccessApplet
 * Description: A main frame to access Oracle database from an Applet. It provides user
 *              an option of adding or deleting the tags to the database.
 * Copyright (c) 2000: The Trustees of Columbia University and the City of New York.
  *                              All Rights Reserved.
 * Company:      <p>
 * @author Kanan Naik
 * @version 1.0
 */

package psl.oracle;

import java.awt.*;
import javax.swing.*;


public class DBAccessApplet extends JApplet
{
	public void init()
	{
		getRootPane().putClientProperty("defeatSystemEventQueueCheck", Boolean.TRUE);
		getContentPane().setLayout(null);
		getContentPane().setBackground(new java.awt.Color(204,207,201));
		getContentPane().setFont(new Font("Dialog", Font.PLAIN, 20));
		setSize(360,213);
		addJButton.setText("Add");
		addJButton.setActionCommand("Add");
		getContentPane().add(addJButton);
		addJButton.setBackground(new java.awt.Color(225,223,223));
		addJButton.setFont(new Font("Dialog", Font.BOLD, 15));
		addJButton.setBounds(216,48,84,24);
		deleteJButton.setText("Delete");
		deleteJButton.setActionCommand("Delete");
		getContentPane().add(deleteJButton);
		deleteJButton.setBackground(new java.awt.Color(225,223,223));
		deleteJButton.setFont(new Font("Dialog", Font.BOLD, 15));
		deleteJButton.setBounds(216,120,84,24);
		titleJLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		titleJLabel1.setText("FleXML-Oracle");
		getContentPane().add(titleJLabel1);
		titleJLabel1.setForeground(java.awt.Color.black);
		titleJLabel1.setFont(new Font("Dialog", Font.BOLD, 17));
		titleJLabel1.setBounds(48,72,144,36);
		titleJLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		titleJLabel2.setText("Database");
		getContentPane().add(titleJLabel2);
		titleJLabel2.setForeground(java.awt.Color.black);
		titleJLabel2.setFont(new Font("Dialog", Font.BOLD, 17));
		titleJLabel2.setBounds(60,120,132,36);
		SymMouse aSymMouse = new SymMouse();
		addJButton.addMouseListener(aSymMouse);
		deleteJButton.addMouseListener(aSymMouse);
	}

	javax.swing.JButton addJButton = new javax.swing.JButton();
	javax.swing.JButton deleteJButton = new javax.swing.JButton();
	javax.swing.JLabel titleJLabel1 = new javax.swing.JLabel();
	javax.swing.JLabel titleJLabel2 = new javax.swing.JLabel();

	class SymMouse extends java.awt.event.MouseAdapter
	{
		public void mouseClicked(java.awt.event.MouseEvent event)
		{
			Object object = event.getSource();
			if (object == addJButton)
				addJButton_mouseClicked(event);
			else if (object == deleteJButton)
				deleteJButton_mouseClicked(event);
		}
	}

   	void addJButton_mouseClicked(java.awt.event.MouseEvent event)
	{
		addJButton_mouseClicked_Interaction1(event);
	}

	void addJButton_mouseClicked_Interaction1(java.awt.event.MouseEvent event)
	{
		try {
			// AddJFrame Create and show the AddJFrame
			(new AddJFrame(".")).setVisible(true);
		} catch (java.lang.Exception e) {
		}
	}

	void deleteJButton_mouseClicked(java.awt.event.MouseEvent event)
	{
		deleteJButton_mouseClicked_Interaction1(event);
	}

	void deleteJButton_mouseClicked_Interaction1(java.awt.event.MouseEvent event)
	{
		try {
			// DeleteJFrame Create and show the DeleteJFrame
			(new DeleteJFrame(".")).setVisible(true);
		} catch (java.lang.Exception e) {
		}
	}
}
