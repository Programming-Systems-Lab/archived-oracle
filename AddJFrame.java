/**
 * Title: AddJFrame
 * Description: A frame to add the tags to a database. It generates
 *              a table for all elements to get the module information
 *              from a user.
 * Copyright (c) 2000: The Trustees of Columbia University and the City of New York.
  *                              All Rights Reserved.
 * Company:      <p>
 * @author Kanan Naik
 * @version 1.0
 */
package psl.oracle;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.io.*;
import java.util.*;
import org.apache.xerces.parsers.*;
import org.xml.sax.*;


public class AddJFrame extends javax.swing.JFrame
{
    int numRow = -1;
	public AddJFrame()
	{
		getContentPane().setLayout(null);
		getContentPane().setBackground(new java.awt.Color(204,207,201));
		setSize(399,222);
		setVisible(false);
		addJLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		addJLabel.setText("Add Tags");
		getContentPane().add(addJLabel);
		addJLabel.setForeground(java.awt.Color.black);
		addJLabel.setFont(new Font("Dialog", Font.BOLD, 20));
		addJLabel.setBounds(144,12,108,36);
		addJLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		addJLabel1.setText("Select a name");
		getContentPane().add(addJLabel1);
		addJLabel1.setForeground(java.awt.Color.black);
		addJLabel1.setFont(new Font("Dialog", Font.BOLD, 12));
		addJLabel1.setBounds(24,72,108,24);
		addJLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		addJLabel2.setText("of the .xsd file");
		getContentPane().add(addJLabel2);
		addJLabel2.setForeground(java.awt.Color.black);
		addJLabel2.setFont(new Font("Dialog", Font.BOLD, 12));
		addJLabel2.setBounds(24,96,108,24);
		getContentPane().add(addJTextField1);
		addJTextField1.setBounds(144,84,225,24);
		addJButton1.setText("Parse");
		addJButton1.setActionCommand("Parse");
		getContentPane().add(addJButton1);
		addJButton1.setBackground(new java.awt.Color(225,223,223));
		addJButton1.setFont(new Font("Dialog", Font.BOLD, 15));
		addJButton1.setBounds(192,120,84,24);
		messageJLabel.setAutoscrolls(true);
		getContentPane().add(messageJLabel);
		messageJLabel.setForeground(java.awt.Color.black);
		messageJLabel.setBounds(24,168,360,24);
		SymMouse aSymMouse = new SymMouse();
		addJButton1.addMouseListener(aSymMouse);
	}

	public AddJFrame(String sTitle)
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
		(new AddJFrame()).setVisible(true);
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
	static String dbName = null;
	static String moduleDir = null;
	static String oraclePath = null;
	static DBInterfaceFrame db = null;

	javax.swing.JLabel addJLabel = new javax.swing.JLabel();
	javax.swing.JLabel addJLabel1 = new javax.swing.JLabel();
	javax.swing.JLabel addJLabel2 = new javax.swing.JLabel();
	javax.swing.JTextField addJTextField1 = new javax.swing.JTextField();
	javax.swing.JButton addJButton1 = new javax.swing.JButton();
	static javax.swing.JLabel messageJLabel = new javax.swing.JLabel();


	class SymMouse extends java.awt.event.MouseAdapter
	{
		public void mouseClicked(java.awt.event.MouseEvent event)
		{
			Object object = event.getSource();
			if (object == addJButton1)
				addJButton1_mouseClicked(event);
		}
	}

	void addJButton1_mouseClicked(java.awt.event.MouseEvent event)
	{
		String msg = initialize();
		messageJLabel.setText("Processing started ...");
		if(msg != null)
		    return;
		String fileName = addJTextField1.getText();
		File file = new File(fileName);
	    if(!file.exists())
		{
		    printError("File " + fileName + " does not exist");
		    return;
		}
		BufferedReader inLine = null;
    	BufferedWriter outLine = null;
    	String line = null;
	    try
	    {
		    inLine = new BufferedReader(new FileReader(fileName));
		    outLine = new BufferedWriter(new FileWriter("oracletemp.txt"));
		    line  = inLine.readLine();
	    }
	    catch (Exception e)
	    {
		    printError("Error on file read/write:" +
e.getMessage());
            return;
	    }
	    String mainBuffer = "";
	    if(line != null)
    	    line = line.trim();
	    else
	    {
        	printError("Format of .xsd file is not valid. Tag <schema> "
				   + "is expected.");
		    return;
		}
		try
		{
    	    while(line != null)
	        {
        	    outLine.write(line, 0, line.length());
		        mainBuffer = mainBuffer + line;
		        outLine.write("\n");
        	    line = inLine.readLine();
	        }
	        outLine.close();
    	    inLine.close();
	        FileInputStream is = new FileInputStream("oracletemp.txt");
            //Verify the format of schema file.
        	SAXParser parser = new SAXParser();
	        parser.parse(new InputSource(is));
	        messageJLabel.setText("Schema document validation is successfull.");
	        is.close();
	    }
    	catch(Exception e)
	    {
		    printError(""+e);
		    return;
        }
    	msg = processFile("oracletemp.txt", mainBuffer);
    	if(msg != null)
    	    return;
    }

	private String processFile(String fileName, String mainBuffer)
	{
	    BufferedReader inLine = null;
	    String line = null;
	    try
	        {
	        inLine = new BufferedReader(new FileReader(fileName));
	        line = inLine.readLine();
	        }
	    catch(Exception e)
	        {
	            printError(""+e);
	            return "error";
	        }
    	String mainPath = "";
	    int level = -1;
    	int index = -1;
	    int index1 = -1;
    	int index2 = -1;
	    int j = -1;
    	int i = -1;
	    Vector element = new Vector(10);
    	Hashtable paths = new Hashtable();
    	Hashtable moduleName = new Hashtable();
    	String elementName = null;
    	String typeName = null;
	    String namespace = null;
    	while(line != null)
	    {
		    index = line.indexOf("element");
        	if(index != -1) //element
		    {
			j=index+8;
			if(line.length() > j + 4 )
			    {
				while (line.charAt(j) == ' ')
				    {
					j++;
				    }
				if((line.charAt(j) == 'n') && (line.charAt(j+1) == 'a') &&
				   (line.charAt(j+2) == 'm') && (line.charAt(j+3) == 'e'))
				    {
					level = level + 1;
					ElementInfo e11 = new ElementInfo();
					e11.setFragment(mainBuffer);
					element.add(level, e11);
					index1 = line.indexOf("<");
					index2 = line.indexOf(":");
					if((index1 != -1) && (index2 != -1) && (index2 < index))
					    {
						namespace = line.substring(index1+1, index2);
						namespace = namespace.trim();
					    }
					else
					    {
						namespace = null;
					    }
					index1 = line.indexOf('"');
					if(index1 != -1)
					    index2 = line.indexOf('"', index1+1);
					else
					    {
						index1 = line.indexOf("'");
						index2 = line.indexOf("'", index1+1);
					    }
					elementName = line.substring(index1+1, index2);
					for(i=0; i<=level; i++)
					    {
						ElementInfo e = (ElementInfo) element.get(i);
						element.set(i, e);
					    }
					ElementInfo e1 = (ElementInfo)element.get(level);
					mainPath = mainPath.concat("/" + elementName);
					if(paths.get(elementName) == null)
					    {
						e1.setPath(mainPath);
					    }
					else
					    {
						String temp = (String)paths.get(elementName);
						e1.setPath(temp);
					    }
					e1.setKey("0." + elementName + "," + e1.getPath());
					element.set(level, e1);
					index1 = line.indexOf("/>");
					ElementInfo e = (ElementInfo) element.get(level);
					if(index1 != -1)
					    {
					    moduleName.put(e, "");
					    }
					if(namespace != null)
					    {
						e.setKey("0." + namespace + ":" + elementName);
						if(index1 != -1)
						    {
							moduleName.put(e,"");
						    }
					    }
					if(index1 != -1)
					    {
						element.remove(level);
						int indexPath = mainPath.indexOf("/" + elementName);
						mainPath = mainPath.substring(0, indexPath);
						level --;
					    }
				    }
				else if((line.charAt(j) == 'r') && (line.charAt(j+1) == 'e') &&
					(line.charAt(j+2) == 'f')) //if element ref found
				    {
					index1 = line.indexOf('"');
					if (index1 == -1)
					    {
						index1 = line.indexOf("'");
					    }
					index2 = line.indexOf('"', index1+2);
					if(index2 == -1)
					    {
						index2 = line.indexOf("'", index1+2);
					    }
					elementName = line.substring(index1+1, index2);
					int indexPath = mainPath.lastIndexOf('/');
					String parent =mainPath.substring(indexPath+1, mainPath.length());
					String path = (String)paths.get(parent);
					if( path != null)
					    path = path.concat("/" + elementName);
					else
					    path = mainPath.concat("/" + elementName);
					paths.put(elementName, path);
					for(i=0; i<=level; i++)
					    {
						ElementInfo e = (ElementInfo) element.get(i);
						element.set(i, e);
					    }
				    }
			    }  //name not found
			    else if((line.indexOf("element>")) != -1)
			    {
				for(i=0; i<=level; i++)
				    {
					ElementInfo e = (ElementInfo) element.get(i);
					element.set(i, e);
				    }
				ElementInfo e = (ElementInfo) element.get(level);
				String key = e.getKey();
				moduleName.put(e,"");
				index = key.indexOf(':');
				index1 = key.indexOf("0.");
				if(index != -1) //namespace is not null
				    {
					elementName = key.substring(index+1, key.length());
					e.setKey("0." + elementName + "," + e.getPath());
					moduleName.put(e,"");
				    }
				else
				    {
					index = key.indexOf(',');
					elementName = key.substring(index1+2, index);
				    }
				int inNamespace = elementName.indexOf(':');
				String tagName = elementName.substring(inNamespace+1, elementName.length());
				int indexPath = mainPath.indexOf("/" + tagName);
				mainPath = mainPath.substring(0, indexPath);
				element.remove(level);
				level --;
			    }
		    } //element found
		else if(line.indexOf(" name=") != -1) //check for type
		    {
			String type = null;
			if(line.indexOf("type name=") != -1)
			    {
				index = line.indexOf("type name=");
				type = "type";
			    }
			else if(line.indexOf("complexType name=") != -1)
			    {
				index = line.indexOf("complexType name=");
				type = "complexType";
			    }
			else if(line.indexOf("simpleType name=") != -1)
			    {
				index = line.indexOf("simpleType name=");
				type = "simpleType";
			    }
			if(type != null) //avoid "attribute name"
			    {
				index1 = line.indexOf("<");
				index2 = line.indexOf(":");
				if((index1 != -1) && (index2 != -1) && (index2 < index))
				    {
					namespace = line.substring(index1+1, index2);
					namespace = namespace.trim();
				    }
				else
				    {
					namespace = null;
				    }
				index1 = line.indexOf('"');
				if (index1 == -1)
				    {
					index1 = line.indexOf("'");
				    }
				index2 = line.indexOf('"', index1+2);
				if(index2 == -1)
				    {
					index2 = line.indexOf("'", index1+2);
				    }
				typeName = line.substring(index1+1, index2);
				level = level + 1;
				element.add(level, new ElementInfo());
				for(i=0; i<=level; i++)
				    {
					ElementInfo e = (ElementInfo) element.get(i);
					element.set(i, e);
				    }
				ElementInfo e1 = (ElementInfo)element.get(level);
				String key = null;
				if(namespace != null)
				    key = "0." + namespace + ":" + type + "=" + typeName;
				else
				    key = "0." + type + "=" + typeName;
				e1.setKey(key);
				element.set(level, e1);
				index1 = line.indexOf("/>");
				if(index1 != -1)
				    {
					ElementInfo e = (ElementInfo) element.get(level);
					moduleName.put(e,"");
				    }
			    }
			else
			    {
				for(i=0; i<=level; i++)
				    {
					ElementInfo e = (ElementInfo) element.get(i);
					element.set(i, e);
				    }
			    }
		    }
	        else if((line.indexOf("</type>") != -1) || (line.indexOf("simpleType>") != -1)
			|| (line.indexOf("complexType>")!= -1) || (line.indexOf(":type>") != -1))
		    {
			for(i=0; i<=level; i++)
			    {
				ElementInfo e = (ElementInfo) element.get(i);
				element.set(i, e);
			    }
			ElementInfo e = (ElementInfo) element.get(level);
			if(e.getKey().indexOf("ype=") != -1) //to avoid  <type> </type> kind of constructs
			    {
				moduleName.put(e,"");
				element.remove(level);
				level --;
			    }
		    }
	        else //element and type are not found
		    {
			for(i=0; i<=level; i++)
			    {
				ElementInfo e = (ElementInfo) element.get(i);
				element.set(i, e);
			    }
		    }
		    try
		        {
	            line = inLine.readLine();
	            }
	        catch(Exception ex)
	            {
	            printError(""+ex);
	            return "error";
	            }
	    }
	    String msg = askModuleInfo(moduleName);
	    if(msg != null)
	        return "error";
	    else
	        return null;
	}

	private void refreshScreen()
	{
	    addJTextField1.setText(" ");
	    messageJLabel.setText(" ");
	}

	/**
  	* This method is used to increase the version value for the tags.
  	*/

    public String modifyKeyValue(String key, int version)
    {
	int index = key.indexOf('.');
	version++;
	String newVersion = String.valueOf(version);
	newVersion = newVersion.trim();
	String newKey = newVersion + "." + key.substring(index+1, key.length());
	return newKey;
    }


	private String askModuleInfo(Hashtable main)
	    {
	    String msg = null;
	    Vector moduleVector = new Vector();
	    ElementInfo element = null;
	    String key = null;
	    String moduleInfo = null;
	    int ind = 0;
	    ElementInfo elementInfo = null;
	    for(Enumeration e = main.keys();e.hasMoreElements();)
	        {
	        element = (ElementInfo)e.nextElement();
	        key = element.getKey();
	        Object getDB = db.get(key);
	        if(getDB != null)
	            {
		        elementInfo = ElementInfo.getElementInfo((String)getDB);
		        element.setModuleInfo(elementInfo.getModuleInfo());
		        int version = elementInfo.getVersion();
				String newKey = modifyKeyValue(key, version);
				version++;
				elementInfo.setVersion(version);
				db.remove(key);
				elementInfo.setKey(newKey);
				db.put(newKey, elementInfo.toString());
	            }
	         moduleVector.add(ind, element);
	         ind++;
	        }
	        int row = 0;
	        for(Enumeration e = moduleVector.elements();e.hasMoreElements();)
	        {
	            e.nextElement();
	            row++;
	        }
	        Object[][] moduleArray;
	        moduleArray = new Object[row][4];
	        int i =0;
	        for(Enumeration e = moduleVector.elements();e.hasMoreElements();)
	        {
	            moduleArray[i][0] = e.nextElement();
	            i++;
	        }
	        ModuleTable.showTable(moduleArray, row);
		    return msg;
       	}

    public static void addToDB(Object[][] moduleInfo, int row)
    {
        ElementInfo element = null;
        String key = null;
        String msg = null;
        for(int i=0; i<row; i++)
        {
            element = (ElementInfo)moduleInfo[i][0];
            key = element.getKey();
            element.setModuleInfo(moduleInfo[i][1].toString());
            msg = db.put(key, element.toString());
	        if(msg != null)
	        {
		    printError(msg);
		    return;
		    }
		}
		msg = db.shutdown();
		if(msg != null)
		{
		    printError(msg);
		    return;
		}
		messageJLabel.setText("Tags are successfully added");
		return;
    }


    private String initialize()
        {
       	File file = new File("oracle.prop");
	    Properties property = new Properties();
	    String msg = null;
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
	    oraclePath = property.getProperty("oraclePath");
	    if(oraclePath == null || oraclePath.length() < 1)
	    {
		    printError("Parameter 'oraclePath' must be set in 'oracle.prop' file.");
		    return "error";
	    }
	    dbName = property.getProperty("dbName");
	    if(dbName == null || dbName.length() < 1)
	        {
		    printError("Parameter 'dbName' must be set in 'oracle.prop' file.");
		    return "error";
	        }
	    moduleDir = property.getProperty("moduleDir");
        if(moduleDir == null || moduleDir.length() < 1)
	        {
		    printError("Parameter 'moduleDir' must be set in 'oracle.prop' file.");
		    return "error";
	        }
	    moduleDir = oraclePath + File.separator + moduleDir;
	    db = null;
	    try
	        {
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

	   	static void printError(String msg)
	   	{
	   	    ErrorJDialog ed = new ErrorJDialog();
	        ed.setMessage(msg);
	   	}


}