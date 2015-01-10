/**
 * ResultTree.java
 * Purpose:	 	ResultTree will build the resulting XML-Tree. Starting with an empty Tree
 * 				new information will be added incrementally to the Tree.
 * 				The implementation of all methods in this class are general. The specific content of the
 * 				Tree is defined by calling the corresponding Emitter-class methods.
 * 				 			  			
 * @author	 	Stephan Breutel
 * @version 	1.0
 * @created 	5/04/2005
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */


package model.data;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;


import model.backEnd.Emitter;
import model.backEnd.EmitterYAWL;
import model.frontEnd.SymbolTable;
import model.frontEnd.TreeBuilderVisitor;



public class ResultTree extends XMLTree
   {
   	private Emitter eY;
   	
      /**
       * MethodName   : ResultTree
       * Purpose	  : Constructor
       * Pre		  : TRUE
       * Post	      : a ResultTree object has been created.
       * 
       */
      public ResultTree()
         {
            super();
            eY = new EmitterYAWL(); /* default Emitter is PNML. */
         }
      
      /**
       * MethodName   : ResultTree
       * Purpose	  : Constructor
       * Pre		  : TRUE
       * Post	      : a ResultTree object has been created.
       * 
       * @param		Emitter, the corresponding emitter class.
       * 
       */          
      public ResultTree(Emitter eY)
         {
         	this.eY = eY;
         	System.out.println(">ResultTree generated.");
         }
      
      /**
       * MethodName   : genPrologue
       * Purpose	  : generation of the prologue element. 
       * Pre		  : TRUE
       * 				
       * Post	      : the ResultTree is enriched by the prologue.
       *	
       * @param	processName,	String the name of the workflow specification
       */
      public void genPrologue(String processName)
         {            
            rootElement 	= eY.prologueElement(processName);            
            doc 			= new Document(rootElement);              
            treeHeight  	= calcTreeHeight(rootElement);
            System.out.println(">ResultTree prologue added.");        	   		
         }
      
      /**       
       * MethodName		: addList
       * Purpose    	: add a number of elements directly under the rootElement
       * Pre		  	:
       * Post		  	: 
       * 
       * @param l
       */
      public void addList(List l,Element e)
         {
          for (int i=0;i<l.size();i++)
             e.addContent((Element) l.get(i)); 
         }
      
      /**
       * MethodName		: genDataDefinition
       * Purpose    	: To generate the Data Definition Part. (YAWL:<complexType>)
       * Pre		  	: TRUE
       * Post		  	: ResultTree = ResultTree + DataDefinition
       * 
       * 
       */
      public void genDataDefinition()
         {
         	
         }
      
      /**
       * MethodName		: genDataDecl
       * Purpose    	: Generate the Data Declaration (YAWL: <inputParam>,<outputParam>)
       * Pre		  	: TRUE
       * Post		  	: ResultTree = ResultTree + DataDecl
       * 
       * 
       */
      public void genDataDecl()
         {
         
         }
      
      /**
       * MethodName		: genTasks
       * Purpose    	: Generates the Task-Code (without Links) 
       * 					YAWL:<task id=>
       * 						<flowsInto></flowsInto>
       *   						<join code/>
       *   						<split code/>
       *   						<decomposesTo id="replyrequest" />
       * 						</task>
       * Pre		  	: TRUE	
       * Post		  	: ResultTree = ResultTree + taskList  
       *        
       * @param		List, taskList the list of all tasks.
       */
      public void genTasks(List taskList)
         {
         
         }
      
      /**
       * MethodName		: genTaskLink
       * Purpose    	: Generates the code to link the Task (YAWL:<flowsInto> <nextElementRef...> <join/split code > )
       * Pre		  	: TRUE
       * Post		  	: ResultTree = ResultTree + links
       * 
       * 
       */
      public void genTaskLink()
         {
         
         }
      
      /**
       * MethodName		: genTaskData
       * Purpose    	: Generates the code to associate data with a task (YAWL:<decompositionId>, <IOparam>)
       * Pre		  	: TRUE
       * Post		  	: ResultTree = ResultTree + data
       * 
       * 
       */
      public void genTaskData()
         {
         
         }
           
      
      /**
       * MethodName		: addDataTypes
       * Purpose    	: Process the idTable and the typeTable to generate "complexType" definitions.
       * Pre		  	: 
       * Post		  	: 
       * 
       * @param idTable
       * @param typeTable
       */
      public void addDataTypes(SymbolTable idTable,SymbolTable typeTable)
         {
         	// Algorithm for all variables in the idTable check if they are of complexType.
         	String [] s = new String[1];
  	   		s[0]		= "schema";
  	   		Element e	= findElement(rootElement,s);
  	   		// nicer would be a replacement function on an XML-Tree.
         	TreeBuilderVisitor tbv	=	new TreeBuilderVisitor();         	
         	Element dataDecl = tbv.buildDataTypes(idTable,typeTable);
         	if (dataDecl!=null)        
         	   {         	   	
         	   	Namespace n0	= Namespace.getNamespace("http://www.w3.org/2001/XMLSchema");
        	    e.setNamespace(n0);   
        	    e.addContent(dataDecl);
        	    dataDecl.setNamespace(n0);
         	   }	         	   
         }
      
      public void addNetIO(SymbolTable idTable)
         {
           // Algorithm to add the net Input and Output variables.
           // Currently: simple solution: everything is IO.
           // TODO refine it when possible (task for the optimizer)
         	String [] s = new String[1];
	   		s[0]		= "decomposition";
	   		Element e	= findElement(rootElement,s);	   	
	   		TreeBuilderVisitor tbv	=	new TreeBuilderVisitor();    
	   		List [] ioList = tbv.buildNetIO(idTable,e.getNamespace());	   		
	   		if (ioList!=null && !ioList[0].isEmpty())
	   		   {
	   		    e.addContent(ioList[0]);
	   		    e.addContent(ioList[1]);
	   		   }	   		
         }
      
      public void addProcesControlElements(List taskList)
         {
         	EmitterYAWL eY	= new EmitterYAWL();
         	String [] s = new String[1];
	   		s[0]		= "decomposition";
	   		Element e	= findElement(rootElement,s);	
         	Element pC 	= eY.getProcessControllTemplate(taskList,e.getNamespace());
         	pC.setNamespace(e.getNamespace());
         	e.addContent(pC);
         }
      
      public void addTaskDecomposition(List taskList)
         {
         	EmitterYAWL eY	= new EmitterYAWL();
         	String [] s 	= new String[1];
	   		s[0]			= "specification";
	   		Element e		= findElement(rootElement,s);	
	   		List decomp		= eY.getTaskDecomposition(taskList,e.getNamespace());	   		
	   		e.addContent(decomp);
         }
      
      /**
       * MethodName		: addTasks
       * Purpose    	: Add all tasks under the processControlElements Node.
       * Pre		  	: the YAWL-skeleton Tree exists i.e. YAWLTree(procesName) was called before.
       * Post		  	: Stub-tasks are added to the Tree in order of the List.
       * 
       * @param l	, List of Elements of the XML-Tree specifying tasks.
       */
      public void addTasks(List l)
         {
          // add all task under the specification Node.
         
         
         }
      
      
      public String toString()
         {         
         return new XMLOutputter().outputString(doc.getRootElement()).trim();	
         }

     	
      /**
       * MethodName		: toFile
       * Purpose    	: write the YAWL-Tree into an XML-file.
       * Pre		  	: 
       * Post		  	: 
       * 
       * @param fileName
       */
      public void toFile(String fileName)
         {
         	try{
         	   FileWriter writer = new FileWriter(fileName);
         	   new XMLOutputter(Format.getPrettyFormat()).output(doc, writer);
         	   writer.close();
         	}catch(IOException e)
         	{
         	  e.printStackTrace(); 
         	}
         }

   }
