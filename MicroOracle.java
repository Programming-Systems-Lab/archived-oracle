package psl.oracle;

import org.jdom.*;
import org.jdom.input.SAXBuilder;

import java.util.*;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;


public class MicroOracle {

	static String configfile="oracle.properties";
	static String codebase = null;
	static String moduleDirURL;
	static String schemaDirURL;
	static String sienaURL;
	static String knowledgeURL;
	static String knowledgeSchemaURL;
	static String defaultModule;
	static String defaultSchema;
	
	static boolean debugFlag = true;
	
	OracleSienaInterface siena;
	Hashtable oracleKnowledgeBase;

	public MicroOracle () {
	//perhaps synchronize it?
	oracleKnowledgeBase = new Hashtable();
	fillKnowledgeBase(knowledgeURL);
	siena = new OracleSienaInterface(sienaURL);
	OracleSienaInterface.setOracleHandle(this);
	
	Thread t = new Thread(siena);
    t.start();
	}

	public String getSchemaURL (String key) {
		return ((SchemaFragmentEntry)oracleKnowledgeBase.get(key)).getSchema();	
	}
	
	public String getProcessorURL (String key) {
		return ((SchemaFragmentEntry)oracleKnowledgeBase.get(key)).getMetaJar();	
	}
	
	public SchemaFragmentEntry getSchemaFragment (String key) {
		return (SchemaFragmentEntry)oracleKnowledgeBase.get(key);
	}
	
	private SchemaFragmentEntry makeFragmentEntry (String schema, String processor) {
		if (schema == null || schema.equals("") || schema.equals("default")) {
			if (codebase != null)
				schema = codebase + "/" + defaultSchema;
			else
				schema = schemaDirURL + File.separator + defaultSchema;
		}
		if (processor == null || processor.equals("") || processor.equals("default")) {
			if (codebase != null)
				processor = codebase + "/" + defaultModule;
			else
				processor = moduleDirURL + File.separator + defaultModule;
		}
		return new SchemaFragmentEntry (schema, processor);	
	}
	
	private void fillKnowledgeBase (String sourceURL) {
		URL kbaseSchemaURL;
		URL kbaseURL;
		
		try {
			kbaseSchemaURL = new URL(MicroOracle.knowledgeSchemaURL);
		} catch (MalformedURLException urle) {
			System.err.println ("Cannot access schema file for FleXML Oracle knowledge base at URL: "
				+ MicroOracle.knowledgeSchemaURL);
			System.err.println ("Will not validate against schema ...");
			kbaseSchemaURL = null;			
		}
		try {
			//sourceURL ="file:C:\\pslcvs\\psl\\oracle\\SemanticLookup.xml";
    		kbaseURL = new URL (sourceURL);	
    	} catch (MalformedURLException urle) {
    		System.err.println ("Cannot access knowledge base file for FleXML Oracle at URL: " 
				+ sourceURL);
			urle.printStackTrace();
			kbaseURL = null;
			System.exit(1);    			
    	}
    	if (kbaseSchemaURL != null) // must validate against some schema
    	{
    		//implementation of schema-based validation based on kbaseSchemaURL 
    		//to  be done!!!
		}
		
		SAXBuilder builder = new SAXBuilder(true); //validates!
		try {
			InputStream is = kbaseURL.openStream();
          	Document doc = builder.build(is);
			System.err.println ("Valid knowledge base in " + kbaseURL);
			processDocument(doc);
        } catch (JDOMException jdomExc) {
        	System.err.println("Parsing error on " + kbaseURL);
        	System.err.println("Cannot read knowledge base in!!!");
        	System.err.println (jdomExc.getMessage());
        	System.exit(1);	
        }
        catch (IOException ioe) {
        	System.err.println ("error opening Stream for " +kbaseURL);
        	ioe.printStackTrace();
        	System.exit(1);	
        }
        
	}
	
	// document is 3-level deep 
	//<SemanticLookup>
	//**** containing <Row> +
	//******** each <Row> contains 1 <Hint>, 1 <SchemaURL>, 1 <TagProcessorURL>
	private void processDocument (Document doc) {
		Element root = doc.getRootElement(); //	<SemanticLookup>
		List rows = root.getChildren("Row"); // all <Row>
		ListIterator iter = rows.listIterator();
		while (iter.hasNext()) {
			Element entry = (Element)iter.next();
			String hintKey = entry.getChildText("Hint");
			oracleKnowledgeBase.put(hintKey, makeFragmentEntry(entry.getChildText("SchemaURL"),
				entry.getChildText("TagProcessorURL")));
			if (debugFlag)
				System.out.println (hintKey +"\t --> " + ((SchemaFragmentEntry)oracleKnowledgeBase.get(hintKey)).toString());
		}
	}
		
	
	public static void main (String[] args) {
		System.out.println ("Starting FleXML Oracle ...");
		
		if((args.length > 1)) {
     	 System.out.println("Usage: java psl.oracle.MicroOracle <configfile>");
     	 System.exit(1);
     	} 
     	 if (args.length == 1)
     	 	configfile = args[0];
     	 else
     	 	System.out.println ("Using default config file");
     	
     	Properties p = new Properties();
    	try {
      			FileInputStream fis = new FileInputStream(configfile);
      			p.load(fis);
    		} catch (IOException ioe) {
     		 	System.out.println(": Warning: can't open properties file " +
      			configfile + " - Using defaults.");
      			System.out.println(ioe);
    		} 
    	// get out property info	
     	String sienaHost = p.getProperty("sienaHost", "localhost");
     	int sienaPort = Integer.valueOf(p.getProperty("sienaPort", "7130")).intValue();
     	String rootdir = p.getProperty("rootdir", System.getProperty("user.dir"));
     	String moduleDir = p.getProperty("moduleDir", null);
     	String schemaDir = p.getProperty("schemaDir", null);
     	codebase = p.getProperty("defaultURL", codebase);
     	defaultModule = p.getProperty("defaultModule", "tagprocessor.jar");
     	defaultSchema = p.getProperty("defaultSchema", "schema.xsd");
     	String knowledgeFile = p.getProperty("defaultSemanticLookup", "SemanticLookup.xml");
     	String knowledgeSchema = p.getProperty("defaultLookupSchema", null);
     	
     	// establish useful global variables
     	String sienaURL = "senp://" + sienaHost + ":" + sienaPort;
		if (moduleDir != null)
	     	moduleDirURL = "file://" + rootdir + File.separator + moduleDir;
	     else 
	     	moduleDirURL = "file://" + rootdir;
	    if (schemaDir != null)
	     	schemaDirURL = "file://" + rootdir + File.separator + schemaDir;
	     else 
	     	schemaDirURL = "file://" + rootdir;
	    
	    if (codebase != null)
	    	knowledgeURL = codebase + "/" +  knowledgeFile;
	    else
	    	knowledgeURL = "file:" + rootdir + File.separator + knowledgeFile; 
	    
	    if (knowledgeSchema != null) {
		    if (codebase != null)
		    	knowledgeSchemaURL = codebase + "/" +  knowledgeSchema;
	    	else
	    		knowledgeSchemaURL = "file:" + rootdir + File.separator + knowledgeSchema; 	
	    }
	    else
	    	knowledgeSchemaURL = null;
     	
     	MicroOracle mo = new MicroOracle ();
     	
/*   	 	mo.oracleKnowledgeBase.put ("schema", mo.makeFragment(codebase + "schema.xsd", codebase + "tagprocessor.jar"));
   	 	mo.oracleKnowledgeBase.put ("schema2", mo.makeFragment(codebase + "schema2.xsd", codebase + "tagprocessor.jar"));   	 
*/
	}

	class SchemaFragmentEntry {
		String metaJar;
		String schema;
		
		public String getMetaJar() { return metaJar ; }
		public void setMetaJar(String s)  {metaJar = s; }
		public String getSchema() { return schema; }
		public void setSchema(String s) { schema = s; }
		
		public SchemaFragmentEntry (String schemaURL, String jarURL) {
			schema = schemaURL;
			metaJar = jarURL;	
		}
		
		public String toString() {
			return ("Schema is: " + schema + "\t Processor is: " + metaJar);	
		}
		
	}
	
}
