/**
 * Babel.java
 * Purpose: Main java class. Checks first for a correct command line syntax and calls to the 
 * 			appropriate "control" method.
 * 			IMPORTANT. Due to time constraints and changes in the original spec. we introduced
 * 			a "hack": bpel2PNML. This method will be obsolete later. As written in the research
 * 			proposal YAWL will work as intermediate language. Therefore a direct translation of
 * 			BPEL into PNML will not be supported in future versions.
 * 			TODO/Desirable:
 * 				The core-idea for the design of the Babel implementation is to provide a
 * 				specification driven translation (refered to as mapFile in the source code).
 * 				Due to time constraints this is not implemented yet.  
 * @author Stephan Breutel
 * @version 1.0
 * 
 * 11.10.2005, versionString for information 
 * 28.08.2006, updated version to 1.1 after bug-Fixes with respect to the 
 * 			   termination Handler and the propagation of termination  between
 * 			   different scopes.
 * @created 27/02/2005
 * Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package controller;

import model.data.ResultTree;
import model.data.XMLTree;
import model.data.YAWLTree;
import model.frontEnd.*;

import org.jdom.Element;
import java.util.List;

import model.backEnd.EmitterPNML;
import controller.BabelEngine;
import model.languageDefinition.*;



public class Babel
   {
   	public static SymbolTable idTable;
   	public static SymbolTable typeTable;
   	public static SymbolTable taskTable;
   	public static XMLTree xmlBPELTree;
   	public static boolean graphicsOption;
   	public static boolean BPEL11 = false;
   	public static String BabelVersion="BPEL2PNML Version 1.1"; 
   	
   	
   	/**
   	 * MethodName	: bpel2YAWL
   	 * Purpose    	: translation of bpel files into yawl.
   	 * Pre		  	: The two files are correct BPEL-files written in
   	 * 				  well-formed XML.   	  
   	 * Post		  	: The files have been successfully translated into an XML YAWL File named:
   	 * 					<BPELProcessName>BPEL2YAWL.xml
   	 * 				  additionally a logFile has been generated logging the translation process.
   	 * 				  XOR
   	 * 				  an error occured and has been reported in the LogFile.
   	 * 		
   	 * @param wsdlFileName	String, name of the WSDL file	(Service Description)
   	 * @param bpelFileName	String, name of the BPEL file 	(Process)
   	 * @param mapFileName	String, name of the file specifying the BPEL-YAWL mapping.	
   	 */
   	public static void bpel2YAWL(String wsdlFileName,String bpelFileName,String mapFileName)
   	   {
   	    frontEndProcessing(wsdlFileName,bpelFileName,mapFileName);
   	      	       	    
   	    /* Processing Part and Back End */
   	    
   	    // 3. build the YAWL-Tree which will be used as the ResultTree.
   	    // Exploit the property that everything is centered around YAWL.
   	    String processName 	=  bpelFileName.substring(0,bpelFileName.indexOf('.')) + "BPEL2YAWL";   	       	    
   	    YAWLTree yawlTree 	= new YAWLTree(processName);   	   
   	    yawlTree.addDataTypes(idTable,typeTable);
   	    // update of idTable: replace with primitive Type whereEver possible.
   	    // would need a sort of update visitor, but for now : keep it simple.
   	    idTable.updateAllValues(typeTable);
   	    yawlTree.addNetIO(idTable);
   	    //TODO remove Hack
   	    List taskList = xmlBPELTree.getChildren("flow");  
	    System.out.println("TASKLIST");
	    // remove links
	    taskList.remove(0);
	    for (int i=0;i<taskList.size();i++)
	       {
	        Element e_ = (Element) taskList.get(i);
	        System.out.println(e_.getName());
	       }
   	    yawlTree.addProcesControlElements(taskList);
   	    yawlTree.addTaskDecomposition(taskList);
      	// TODO build the ResultTree as intermediate Tree     	   	
      	// TODO get the MappingRules and build the Mapper
      	// TODO traverse the ResultTree and build the ResultTree (YAWLTree) using the MappingRules
   	    
      	// 4.Output the ResultTree into a XML file
   	    yawlTree.toFile(processName+".xml");
      	// TODO call the Optimizer on the XML file   
   	   }
   	
   	/**
   	 * MethodName	: bpel2PNML
   	 * 
   	 * Purpose    	: Translation of bpel files into PNML. 
   	 * 				  IMPORTANT: this method will break the original logic of using YAWL as intermediate
   	 * 				  language. The reason is that a fast implementation is required for a paper submission.
   	 * 				  This method is in main parts a copy of the bpel2YAWL method.
   	 * 
   	 * Pre		  	: The two input files  (wsdl,bpel) are correct BPEL-files written in
   	 * 				  well-formed XML.
   	 *    	  
   	 * Post		  	: The files have been successfully translated into an XML PNML File named:
   	 * 					<BPELProcessName>BPEL2PNML.xml
   	 * 				  additionally a logFile has been generated logging the translation process.
   	 * 				  XOR
   	 * 				  an error occured and has been reported in the LogFile.
   	 * 		
   	 * @param wsdlFileName	String, name of the WSDL file	(Service Description)
   	 * @param bpelFileName	String, name of the BPEL file 	(Process)
   	 * @param mapFileName	String, name of the file specifying the BPEL-YAWL mapping.	
   	 */
   	public static void bpel2PNML(String wsdlFileName,String bpelFileName,String mapFileName)
   	   	   {
   	   		frontEndProcessing(wsdlFileName,bpelFileName,mapFileName);
     	    /* Processing Part and Back End */
     	    
     	    // 3. build the YAWL-Tree which will be used as the ResultTree.
     	    // Exploit the property that everything is centered around YAWL.
     	    String processName 	= bpelFileName.substring(0,bpelFileName.indexOf('.')) + "BPEL2PNML";
     	    BabelEngine bE 		= new BabelEngine(xmlBPELTree,
     	          								  new EmitterPNML(),idTable,typeTable,taskTable,processName,
     	          								  "BPEL","PNML");     	    
     	    bE.translateBPEL();     	    
     	    ResultTree resTree 	= bE.getResultTree();     	    
     	    resTree.toFile(processName+".xml");
     	    
     	    System.out.println("Translation Protocol:\n" + bE.getProtocol());
     	    /* initial ideas : obsolete soon ... */
        	// TODO build the ResultTree as intermediate Tree     	   	
        	// TODO get the MappingRules and build the Mapper
        	// TODO traverse the ResultTree and build the ResultTree (YAWLTree) using the MappingRules     	    
        	// TODO call the Optimizer on the XML file   
     	   }
   	
   	
   	public static void infoG()
   	   {
   	    System.out.println("Graphical Option: ON.");
   	   }
   	
   	public static void infoBPEL11()
   	   {
   	    System.out.println("BPEL file version is 1.1.");
   	   }
   	
   	public static void infoCompile()
   	   {
   	    System.out.println("Translation of BPEL into PNML.");
	    System.out.println("If you use control links then it could take some time, because" +
	    				   " the algorithm scales exponential with the number of variables for" +
	    				   " the boolean expression. ");
   	   }
   private static void frontEndProcessing(String wsdlFileName,String bpelFileName,String mapFileName)
  	   {
 	    XMLFileHandler[] xmlFiles	= new XMLFileHandler[3];
	   	String [] xmlFileString 	= new String[3];
	   	
	   	// FRONT-END
	   	// 1. read the file content
	   	xmlFiles[0] 		= new XMLFileHandler(wsdlFileName);
	   	xmlFiles[1] 		= new XMLFileHandler(bpelFileName);
	   	// Future: Specification Driven Translation (SDT) xmlFiles[2]			= new XMLFileHandler(mapFileName);
	   	// Obsolete soon ? xmlFileString[0] 	= xmlFiles[0].getFileContent();
	   	// Obsolete soon ? xmlFileString[1] 	= xmlFiles[1].getFileContent();
	   	// Obsolete soon ? xmlFileString[2]	= xmlFiles[2].getFileContent();
	   	
	   	// 2. build the XML-Tree for the WSDL and the BPEL file and the MappingRules
	   	XMLTree xmlWSDLTree = new XMLTree(wsdlFileName,"WSDL-Tree");
	   	xmlBPELTree 		= new XMLTree(bpelFileName,"BPEL-Tree",BPEL.dataActivity);   	   	   	 
	   	//XMLTree xmlresTree	= new XMLTree(xmlFileString[2]);
	   	XMLTree xmlresTree	= null;
	   	// 3. traverse xml-tree for WSDL file and build the SymbolTable,TypeTable and TaskTable.
	   	BuildSymbolTableVisitor builder = new BuildSymbolTableVisitor();
	   	idTable 			= builder.buildIdTable(xmlBPELTree,null);   	   	
	   	typeTable			= builder.buildTypeTable(xmlWSDLTree,null);   	   	
	   	taskTable			= builder.buildTaskTable(xmlWSDLTree,null);
	   	
	   	idTable.setName("VariableTable");
	   	typeTable.setName("TypeTable");
	   	taskTable.setName("TaskTable");
	   	
	   	//>>> Test the SymbolTable content
	   	Tester t = new Tester();	   	
	   	t.testSymbolTable(idTable);
	   	t.testSymbolTable(typeTable);
	   	t.testSymbolTable(taskTable);   	   	
	    //<<< Test the SymbolTable
	    
	    // >>> Print the XML-Trees.
	   	xmlWSDLTree.printTree();
	   	xmlBPELTree.printTree();   	   
	    // <<< Print the  XML-Trees.
	    // >>> Test the XML-Tree funtionality
	    System.out.println("TreeHeight (BPEL-TREE):" + xmlBPELTree.getTreeHeight());
	    String [] searchStrings=new String[2];
	    searchStrings[0]="flow";
	    searchStrings[1]="sequence";
	    Element e = xmlBPELTree.findElement(xmlBPELTree.getRoot(),searchStrings);
	    if (e==null)
	       System.out.println("Element not found.");
	    else
	       System.out.println("Element found: " + e.getName());
	    List l = xmlBPELTree.getLevelNodes(xmlBPELTree.getRoot(),2);
	    for(int i=0;i<l.size();i++)
	       System.out.println(((Element) l.get(i)).getName());
	    System.out.println("---");
	    l =  xmlBPELTree.getLevelNodes(xmlBPELTree.getRoot(),1);
	    for(int i=0;i<l.size();i++)
	       System.out.println(((Element) l.get(i)).getName());
	    // <<<
  	   }
   
   	public static void main(String[] args)
      {            
      	System.out.println("WELCOME TO THE BABEL PROJECT\n");
      	
      	//04.04.2005: allow direct mapping into PNML.
      	if (args.length==1)
      	   {
      	    Babel.graphicsOption=false;
      	    Babel.infoCompile();      	   
      	    bpel2PNML(args[0],args[0],"2doMapFile");
      	   }
      	else if (args.length==2 && (args[0].compareTo("g")==0 || args[0].compareTo("B1.1")==0) )
      	   {
      	    if (args[0].compareTo("g")==0)
      	       {
      	        Babel.graphicsOption = true;
      	        Babel.infoG();
      	       }
      	    else 
      	       {
      	        Babel.BPEL11 = true;
      	        Babel.infoBPEL11();
      	       }
      	    Babel.infoCompile();      	   
    	    bpel2PNML(args[1],args[1],"2doMapFile");      	    
      	   }
      	else if (args.length==3 && ( (args[0].compareTo("g")==0 && args[1].compareTo("B1.1")==0) 
      	                          || (args[0].compareTo("B1.1")==0 && args[1].compareTo("g")==0) ) )
      	   {
      	   	Babel.graphicsOption = true;
      	   	Babel.infoG();
      	   	Babel.infoBPEL11();
      	   	Babel.infoCompile();
      	   	bpel2PNML(args[2],args[2],"2doMapFile");     
      	   }
      	else  
      	   {
      	    System.out.println("CALL-STRUCTURE: BPEL2PNML [g | B1.1] <nameofBPELFile>");      	          	    
      	    System.exit(1);
      	   }
      	
      	/* 06.06.05: excluded for first Version of BPEL2PNML.
      	// command line checks.
      	if (args.length<4)     
      	   {
      	   	System.out.println(BabelMessages.error(0,""));
      	   	System.out.println(BabelMessages.info(0,""));
      	   	System.out.println(args.length);
      	   	System.exit(1);
      	   }      
      	
      	if (args[0].compareTo("BPEL")==0)
      	   {
      	   if (args.length!=4 )
      	      {
      	      	System.out.println(BabelMessages.error(2,args[1]));
      	      	System.out.println(BabelMessages.info(2,args[1])); 
      	      	System.exit(1);
      	      }      	   
      	   else if (args[1].compareTo("fromYAWL")==0)
      	      {
      	       System.out.println(BabelMessages.info(1001,args[2]));
      	       System.exit(1);
      	      } 
      	   }
      	else
      	   {
      	   	System.out.println(BabelMessages.error(1,""));
      	   	System.exit(1);
      	   }
      	
      	// Start of the Translation Process for toYAWL         	
   	   	System.out.println(BabelMessages.info(3,args[0])); 
      	if (args[0].compareTo("BPEL")==0 )
      	   if (args[1].compareTo("toYAWL")==0)
      	      bpel2YAWL(args[2],args[3],"2doMapFile");
      	   else
      	      bpel2PNML(args[2],args[3],"2doMapFile");
       */	      
      }
     
   }
