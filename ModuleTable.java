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
import java.io.*;

public class ModuleTable
{
    public static void showTable(Object[][] mainT, int row, String rootPath)
    {
        ModuleTableFrame tableFrame = new ModuleTableFrame();
        tableFrame.showFrame(mainT, row, rootPath);
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
                cacheable = moduleInfo.substring(0, index);
                mainModule[i][1] = cacheable;
                index1 = moduleInfo.indexOf(',', index+1);
                boolean singleModule = false;
                if(cacheable.equals("true"))
                {
                    instanceName = moduleInfo.substring(index+1, index1);
                    mainModule[i][2] = instanceName;
                    index = index1;
                    index1 = moduleInfo.indexOf(',', index+1);
                }
                if (index1 == -1)
                {
                    mainModule[i][3] = moduleInfo.substring(index+1,
moduleInfo.length());
                    singleModule = true;
                }
                else
                    mainModule[i][3] = moduleInfo.substring(index+1, index1);
                if(!singleModule)
                {
                    for(int j=4; j<=col; j++)
                    {
                        index = index1;
                        index1 = moduleInfo.indexOf(',', index+1);
                        if(index1 == -1)
                        {
                            mainModule[i][j] = moduleInfo.substring(index+1,
moduleInfo.length());
                            break;
                        }
                        mainModule[i][j] = moduleInfo.substring(index+1, index1);
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
	   	            return "If Cacheable";
	   	        case 2:
	   	            return "Instance Name";
	   	        case 3:
	   	            return "Module Name";
	   	        default:
	   	        {
	   	            if(index <= col)
	   	                return "Module Name";
	   	            else
	   	                return " ";
	   	        }
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


    public void showFrame(Object[][] mainT, int row, String rootPath)
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

        //code to read parameters from the property file
	    File file = new File(rootPath + File.separator + "oracle.prop");
	    if(!file.exists())
	    {
		    file = new File(rootPath + File.separator + "psl" + File.separator + "oracle" + File.separator + "oracle.prop");
		    if(!file.exists())
		    {
		        ErrorJDialog ed = new ErrorJDialog();
	            ed.setMessage("File 'oracle.prop' does not exist");
			    return;
		    }
	    }
	    Properties property = new Properties();
	    try
	    {
		    property.load(new FileInputStream(file));
	    }
	    catch(FileNotFoundException ffe)
	    {
	        ErrorJDialog ed = new ErrorJDialog();
	        ed.setMessage("Exception: " + ffe);
		    return;
	    }
	    catch(IOException ioe)
	    {
	        ErrorJDialog ed = new ErrorJDialog();
	        ed.setMessage("Exception: "+ ioe);
		    return;
	    }
	    String maxModule = property.getProperty("maxModuleNumber");
	    if(maxModule == null || maxModule.length() < 1)
	    {
	        ErrorJDialog ed = new ErrorJDialog();
	        ed.setMessage("Parameter 'maxModuleNumber' must be set in 'oracle.prop' file.");
		    return;
	    }
	    int maxCol = Integer.valueOf(maxModule).intValue();
	    TableModel model = new ModuleTableModel(row, maxCol+3, mainT);
        JTable table = new JTable(model);
        TableColumnModel colModel = table.getColumnModel();
        JCheckBox cacheable = new JCheckBox();
        TableColumn cacheableCol = colModel.getColumn(1);
        cacheableCol.setCellEditor(new DefaultCellEditor(cacheable));

        JTextField instanceTextField = new JTextField();
        TableColumn instanceCol = colModel.getColumn(2);
        instanceCol.setCellEditor(new DefaultCellEditor(instanceTextField));

        JTextField nameTextField = new JTextField();

        TableColumn nameCol;

        for(int i=3; i< (maxCol+3); i++)
        {
            nameCol = colModel.getColumn(i);
            nameCol.setCellEditor(new DefaultCellEditor(nameTextField));
        }

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
		   if((model.getValueAt(row, 3) != null) &&
		        ((model.getValueAt(row, 2)== null) ||
                ((model.getValueAt(row, 2).toString()).length() <=0))&&
                (model.getValueAt(row, 1) != null)&&
                ((model.getValueAt(row, 1).toString()).equals("true")))
            {
                ErrorJDialog ed = new ErrorJDialog();
	            ed.setMessage("Value of instance name cannot be null for "
	                            +" a cacheable module");
	            return;
	        }
	    }
	    int col = model.getColumnCount();
	    for(int row=0; row<totalRow; row++)
	    {
	        String name = null;
	        String cacheable = null;
	        String instance = null;
	        if (model.getValueAt(row, 3) == null)
	            moduleInfo[row][1] = "";
	        else
	        {
	            name = model.getValueAt(row, 3).toString();
	            if (model.getValueAt(row, 1) == null)
	               moduleInfo[row][1] = "false,"+name;
	            else
	            {
	                cacheable = model.getValueAt(row, 1).toString();
	                if(cacheable.equals("false"))
	                    moduleInfo[row][1] = "false,"+ name;
	                else
	                {
	                    instance = model.getValueAt(row, 2).toString();
	                    moduleInfo[row][1] = "true,"+instance+","+name;
	                }
	            }
	            for(int i=4; i<=col; i++)
	            {
	                if((model.getValueAt(row, i) == null) ||
                        ((model.getValueAt(row, i).toString()).length() <=0))
                        break;
                    name = model.getValueAt(row, i).toString();
                    moduleInfo[row][1] = moduleInfo[row][1] + "," + name;
	            }
	        }
	    }
	    setVisible(false);
	    AddJFrame.addToDB(moduleInfo, totalRow);
	}
}
