/**
 * Title: AddFrame
 * Description: This class provides a frame to add the tags and schema
 *              definitions to the Oracle database
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

public class AddFrame extends Frame
{
    String moduleDir = null;
    String defaultModuleInfo = null;
    static String moduleInfo = null;
    String dbName = null;
    DBInterface db = null;

	public AddFrame()
	{
		setLayout(null);
		setBackground(new java.awt.Color(181,186,204));
		setSize(418,209);
		setVisible(false);
		addLabel1.setText("Enter a name of ");
		addLabel1.setAlignment(java.awt.Label.CENTER);
		add(addLabel1);
		addLabel1.setBounds(12,48,96,24);
		addLabel2.setText("the .xsd file");
		addLabel2.setAlignment(java.awt.Label.CENTER);
		add(addLabel2);
		addLabel2.setBounds(12,72,96,24);
		add(addText1);
		addText1.setBounds(120,60,216,24);
		add(addMessageLabel);
		addMessageLabel.setBounds(84,144,312,24);
		addParseButton.setLabel("Parse");
		add(addParseButton);
		addParseButton.setBackground(new java.awt.Color(0,64,128));
		addParseButton.setForeground(java.awt.Color.white);
		addParseButton.setBounds(180,108,86,24);
		setTitle("Add Frame");
		SymWindow aSymWindow = new SymWindow();
		this.addWindowListener(aSymWindow);
		SymMouse aSymMouse = new SymMouse();
		addParseButton.addMouseListener(aSymMouse);

	}

	public AddFrame(String title)
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
		(new AddFrame()).setVisible(true);
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

    boolean fComponentsAdjusted = false;

	java.awt.Label addLabel1 = new java.awt.Label();
	java.awt.Label addLabel2 = new java.awt.Label();
	java.awt.TextField addText1 = new java.awt.TextField();
	java.awt.Label addMessageLabel = new java.awt.Label();
	java.awt.Button addParseButton = new java.awt.Button();

	class SymWindow extends java.awt.event.WindowAdapter
	{
		public void windowClosing(java.awt.event.WindowEvent event)
		{
			Object object = event.getSource();
			if (object == AddFrame.this)
				AddFrame_WindowClosing(event);
		}
	}

	void AddFrame_WindowClosing(java.awt.event.WindowEvent event)
	{
		setVisible(false);		 // hide the Frame
	}

	class SymMouse extends java.awt.event.MouseAdapter
	{
		public void mouseClicked(java.awt.event.MouseEvent event)
		{
			Object object = event.getSource();
			if (object == addParseButton)
				addParseButton_MouseClicked(event);
		}
	}

	void addParseButton_MouseClicked(java.awt.event.MouseEvent event)
	{
		// to do: code goes here.
        String fileName = addText1.getText();
        File file = new File(fileName);
        if(file.exists() == false)
        {
            addMessageLabel.setText("File " + fileName + " does not exist");
        }
        else
        {
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
			    addMessageLabel.setText("Error on file read/write:" + e.getMessage());
			    e.printStackTrace();
	        }
	        String mainBuffer = "";
	        if(line == null)
    	    {
        	    addMessageLabel.setText("Format of .xsd file is not valid. Tag <schema> "
                   + "is expected.");
    	    }
    	    else
    	    {
    	        line = line.trim();
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
    	            FileInputStream is = null;
                    //Verify the format of schema file.
        	        is = new FileInputStream("oracletemp.txt");
            	    SAXParser parser = new SAXParser();
    	            parser.parse(new InputSource(is));
           	        is.close();
                }
                catch(Exception e)
                {
                    addMessageLabel.setText(""+e);
                }
        	    addMessageLabel.setText("Schema document validation is successfull.");
        	    processFile("oracletemp.txt", mainBuffer);
    	    }
        }
	}


	/**
	* .xsd file is processed by this method. It will generate a proper
	* key value by computing a path for each element. It also parses
	* a file to store a schema fragment for each element.
	*/

	public void processFile(String fileName, String mainBuffer)
	{
	    String msg = initializeParam();//initialize DB, default parameters
	    if(msg == null)
	    {
            File file = new File(fileName);
            if(file.exists() == false)
            {
                addMessageLabel.setText("File " + fileName + " does not exist");
            }
            else
            {
                BufferedReader inLine = null;
                String line = null;
                try
                {
    	            inLine = new BufferedReader(new FileReader(fileName));
    	            line = inLine.readLine();
    	        }
    	        catch(Exception ex)
    	        {
    	            addMessageLabel.setText(""+ex);
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
                    	            //addMessageLabel.setText("adding "+e.toString());
                            	    addToDB(e);
                            	}
    	                        if(namespace != null)
        	                    {
            	                    e.setKey("0." + namespace + ":" + elementName);
                	                if(index1 != -1)
                    	            {
                    	               // addMessageLabel.setText("adding "+e.toString());
                        	            addToDB(e);
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
                              addToDB(e);
                              //addMessageLabel.setText("adding "+e.toString());
                              index = key.indexOf(':');
                	          index1 = key.indexOf("0.");
                	          if(index != -1) //namespace is not null
                       	      {
                        	    elementName = key.substring(index+1, key.length());
                            	e.setKey("0." + elementName + "," + e.getPath());
    	                        addToDB(e);
    	                        //addMessageLabel.setText("adding "+e.toString());
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
            	        catch(Exception e)
            	        {
            	            addMessageLabel.setText(""+e);
        	            }
            	  }
             }
             shutdownDB();
        }
    }


	String initializeParam()
	{
	    //code to read parameters from the property file
	    moduleInfo = "";
        File file = new File("oracle.prop");
        if(file.exists() ==  false)
        {
            addMessageLabel.setText("File 'oracle.prop' must exist");
            return "error";
        }
        Properties property = new Properties();
        try
        {
            property.load(new FileInputStream(file));
        }
        catch(FileNotFoundException ffe)
        {
            addMessageLabel.setText("Exception: " + ffe);
        }
        catch(IOException ioe)
        {
            addMessageLabel.setText("Exception: "+ ioe);
        }
        defaultModuleInfo = property.getProperty("defaultModuleInfo");
        if(defaultModuleInfo == null || defaultModuleInfo.length() < 1)
        {
            addMessageLabel.setText("Parameter 'defaultModuleInfo' must be set in 'oracle.prop' file.");
            return "error";
        }
        //defaultModuleInfo = moduleInfo;
        dbName = property.getProperty("dbName");
        if(dbName == null || dbName.length() < 1)
        {
            addMessageLabel.setText("Parameter 'dbName' must be set in 'oracle.prop' file.");
            return "error";
        }
        moduleDir = property.getProperty("moduleDir");
        if(moduleDir == null || moduleDir.length() < 1)
        {
       	    addMessageLabel.setText("Parameter 'moduleDir' must be set in 'oracle.prop' file.");
            return "error";
    	}
        //property file code end here
        try
        {
            db = new DBInterface(dbName);
        }
        catch(Exception e)
        {
            addMessageLabel.setText("Exception in database: " + e);
            shutdownDB();
        }
        return null;
   }


   void shutdownDB()
   {
       db.shutdown();
   }

   void addToDB(ElementInfo e)
   {

       //must be removed by a module input method
       e.setModuleInfo("default.class,false");
       addMessageLabel.setText("Adding " + e.toString());
       String key = e.getKey();
       db.put(key, e.toString());
       return;
    }

}

