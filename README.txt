Description: This file provides a brief insruction to use Oracle system.
Copyright (c) 2001: The Trustees of Columbia University and the City of New York.
              All Rights Reserved.
author: Kanan Naik



Oracle uses Siena to receive a query from Metaparser and to send back areply (schema fragment and module information). The query and reply arerepresented in XML format. (siena.jar should be included in the classpath).Oracle properties like defaultModuleLocation, dbName must be included inoracle.prop file.There are two modes of operations for Oracle:1: To add a SchemaFragment to database:	Here Hypersonic SQL(HSQL) database is used. To use this database         we need to include hsql.jar in the classpath.		Once database is installed a user can add schemafragments by	giving following command at the command prompt:	java psl.oracle.impl.SchemaInterface		All fragments must be stored in a .xsd file and the format of this file        must be valid.2: To get schema definition from database: 	An oracle can receive a request for a schema definition using Siena. Method	getFragment(String query) from Oracle.java is called.	The query must be represented in XML format:			<FleXML:schemaQuery version="1.0" name="NAMESPACE:ELEMENT">   		<xPath>      			PATH		</xPath>	</FleXML:schemaQuery>	where NAMESPACE and PATH are optional but at least one of them is required.