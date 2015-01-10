/**
 * YAWLTree.java
 * Purpose: 
 * @author Stephan Breutel
 * @version 1.0
 * @created 3/03/2005
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

import org.jdom.output.XMLOutputter;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;


import java.util.List;
import model.backEnd.EmitterYAWL;
import model.frontEnd.SymbolTable;
import model.frontEnd.TreeBuilderVisitor;



public class YAWLTree extends XMLTree
   {   
      /**
       * MethodName   : YAWLTree
       * Purpose	  : Constructor which builds a skeleton YAWL-Tree including all the elements. 
       * Pre		  : TRUE
       * 				
       * Post	      : a YAWLTree object has been created.
       *	
       * @param	processName,	String the name of the workflow specification
       */
      public YAWLTree(String processName)
         {
            EmitterYAWL eY	= new EmitterYAWL();
            rootElement 	= eY.prologueElement(processName);            
            doc 			= new Document(rootElement);              
            treeHeight  	= calcTreeHeight(rootElement);
            System.out.println("YAWL-Tree skeleton successfully constructed.");        	   		
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
         	Element dataDecl 		= tbv.buildDataTypes(idTable,typeTable);
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
     	     
   }
