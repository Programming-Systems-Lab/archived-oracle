/**
 * Title: ModuleTable
 * Description: A frame to generate a table that contains the element
 *              names and accepts an input from a user for module
 *              information
 * Copyright (c) 2000: The Trustees of Columbia University and the City of New York.
  *                              All Rights Reserved.
 * Company:      <p>
 * @author Kanan Naik
 * @version 1.0
 */

 package psl.oracle;

import javax.swing.table.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ModuleTable
{
    public static void showTable(Object[][] mainT, int row)
    {
        ModuleTableFrame tableFrame = new ModuleTableFrame();
        tableFrame.showFrame(mainT, row);
     }
}

/**
* Computes the cell entries each time they are requested
*/

class ModuleTableModel extends AbstractTableModel
{
    static Object[][] mainModule = null;
    static private int row;
    static private int col;

    public ModuleTableModel (int r, int c, Object[][] mainT)
    {
        row = r;
        col = c;
        ElementInfo element = null;
        String key = null;
        mainModule = new Object[r][c];
        String moduleInfo = null;

        for(int i=0; i<row; i++)
        {
            element = (ElementInfo)mainT[i][0];
            key = element.getKey();
            key = key.substring(key.indexOf('.')+1, key.length());
            mainModule[i][0] = key;
            moduleInfo = element.getModuleInfo();
            int index = -1;
            int index1 = -1;
            String moduleName =null;
            String cacheable = null;
            String instanceName = null;
            if((moduleInfo != null) && (moduleInfo.length() > 0))
            {
                index = moduleInfo.indexOf(',');
                moduleName = moduleInfo.substring(0, index);
                mainModule[i][1] = moduleName;
                index1 = moduleInfo.indexOf(',', index+1);
                if(index1 == -1)
                {
                    mainModule[i][2] = "false";
                }
                else
                {
                    cacheable = moduleInfo.substring(index+1, index1);
                    mainModule[i][2] = cacheable;
                    if(cacheable.equals("true"))
                    {
                        instanceName = moduleInfo.substring(index1+1, moduleInfo.length());
                        mainModule[i][3] = instanceName;
                    }
                }

            }
        }
    }

    public ModuleTableModel ()
    {
    }
    public int getColumnCount()
	   	{
	   	    return col;
	   	}

	   	public String getColumnName(int index)
	   	{
	   	    switch (index)
	   	    {
	   	        case 0:
	   	            return "Element";
	   	        case 1:
	   	            return "Module Name";
	   	        case 2:
	   	            return "If Cacheable";
	   	        case 3:
	   	            return "Instance Name";
	   	       default:
	   	            return " ";
	   	    }
	   	}

	   	public int getRowCount()
	   	{
	   	    return row;
	   	}

	   	public Object getValueAt(int row, int col)
	   	{
	   	    return mainModule[row][col];
	   	}

	   	public void setValueAt(Object value, int row, int col)
	   	{
	   	    mainModule[row][col] = value;
	   	}

	   	public boolean isCellEditable(int row, int col)
	   	{
	   	    if(col == 0)
	   	      return false;
	   	    else
	   	      return true;
	   	}

}

class ModuleTableFrame extends JFrame
{
    javax.swing.JButton submitJButton = new javax.swing.JButton();
    int totalRow = -1;
    Object[][] moduleInfo = null;

    public ModuleTableFrame()
    {
    }


    public void showFrame(Object[][] mainT, int row)
    {
        //getContentPane().setLayout(null);
        moduleInfo = mainT;
        totalRow = row;
        setTitle("Module Information");
        setSize(600,300);
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                setVisible(false);
            }
        });
        TableModel model = new ModuleTableModel(row,4, mainT);
        JTable table = new JTable(model);

        JTextField nameTextField = new JTextField();
        TableColumnModel colModel = table.getColumnModel();
        TableColumn nameCol = colModel.getColumn(1);
        nameCol.setCellEditor(new DefaultCellEditor(nameTextField));

        JCheckBox cacheable = new JCheckBox();
        TableColumn cacheableCol = colModel.getColumn(2);
        cacheableCol.setCellEditor(new DefaultCellEditor(cacheable));

        JTextField instanceTextField = new JTextField();
        TableColumn instanceCol = colModel.getColumn(3);
        instanceCol.setCellEditor(new DefaultCellEditor(instanceTextField));

        getContentPane().add(new JScrollPane(table), "Center");
        submitJButton.setText("Submit");
		submitJButton.setActionCommand("Submit");
		getContentPane().add(submitJButton, "South");
		submitJButton.setBackground(new java.awt.Color(225,223,223));
		submitJButton.setFont(new Font("Dialog", Font.BOLD, 15));
		SymMouse aSymMouse = new SymMouse();
		submitJButton.addMouseListener(aSymMouse);
		setVisible(true);
    }

	class SymMouse extends java.awt.event.MouseAdapter
	{
		public void mouseClicked(java.awt.event.MouseEvent event)
		{
			Object object = event.getSource();
			if (object == submitJButton)
				submitJButton_mouseClicked(event);
		}
	}

	void submitJButton_mouseClicked(java.awt.event.MouseEvent event)
	{
		TableModel model = new ModuleTableModel();
		for(int row=0; row<totalRow; row++)
		{
		   if((model.getValueAt(row, 1) != null) &&
		        ((model.getValueAt(row, 3)== null) ||
                ((model.getValueAt(row, 3).toString()).length() <=0))&&
                (model.getValueAt(row, 2) != null)&&
                ((model.getValueAt(row, 2).toString()) == "true"))
            {
                ErrorJDialog ed = new ErrorJDialog();
	            ed.setMessage("Value of instance name cannot be null for "
	                            +" a cacheable module");
	            return;
	        }
	    }
	    for(int row=0; row<totalRow; row++)
	    {
	        String name = null;
	        String cacheable = null;
	        String instance = null;
	        if (model.getValueAt(row, 1) == null)
	            moduleInfo[row][1] = "";
	        else
	        {
	            name = model.getValueAt(row, 1).toString();
	            if (model.getValueAt(row, 2) == null)
	               moduleInfo[row][1] = name +",false";
	            else
	            {
	                cacheable = model.getValueAt(row, 2).toString();
	                if(cacheable == "false")
	                    moduleInfo[row][1] = name +",false";
	                else
	                {
	                    instance = model.getValueAt(row, 3).toString();
	                    moduleInfo[row][1] = name +",true,"+instance;
	                }
	            }
	        }
	    }
	    setVisible(false);
        AddFrame.addToDB(moduleInfo, totalRow);
	}
}