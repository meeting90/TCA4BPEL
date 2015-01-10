/**
* Emitter Interface
* Purpose: Interface defining methods to emitt code
* @author Stephan Breutel
* @version 1.0
* @created 24/02/2005
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
package model.backEnd;

import org.jdom.Element;
import java.util.List;

import model.data.Block;


public interface Emitter
   {
   /**
    * 
    * MethodName	: setProperties
    * Purpose    	: sets general properties for the Emitter. 
    * Pre		  	: propertyList is a list of valid properties for a specific Emitter.
    * Post		  	: properties for the Emitter according to the propertyList have 
    * 				  been set.
    * 
    * @param o, property to set.
    * @param i, i-th property.
    */
   public void setProperty(Object o,int i);
   
   /**
    * 
    * MethodName	: getProperties
    * Purpose    	: returns the propertyList of an Emitter. 
    * Pre		  	: 
    * Post		  	: 
    * 
    * @return
    */
   public List getProperties();
   
   
   /**
    * MethodName	: header
    * Purpose    	: contains a "constant" string defining the header of an
    * 				  XML-output file. The header is not part of the XML-Tree.
    * Pre		  	: TRUE	
    * Post		  	: the header for an XML-File is returned. This could also
    * 				  be an empty String (e.g. for WSDL,BPEL files).
    * 
    * @return	String, the XML-Header
    */
   public  String header();
   
   /**
    * MethodName : prologue
    * Purpose    : Generation of the Prologue
    * Pre		 : processName is the name of the process without suffix.
    * 			   Implementations of this method append the file suffix automatically. 
    * Post		 : String is returned containing the Prologue of an file.
    * 
    * @param	processName, String name of the business process  
    *
    * @return	String, containig the prologue of the emitted file
    */
   public String prologue(String processName);
   
   /**
    * MethodName : prologue
    * Purpose    : Generation of an Prologue Element
    * Pre		 : processName is the name of the process without suffix.
    * 			   Implementations of this method append the file suffix automatically. 
    * Post		 : JDOM-Element is returned containing the prologue.
    * 
    * @param	processName, String name of the business process  
    *
    * @return	Element, containig the prologue of the emitted file
    */
   public Element prologueElement(String processName);
   
   
   
   /**
    * MethodName : genTaskElement
    * Purpose    : Generation of an Element representing a task.
    * Pre		 : TRUE	    
    * Post		 : JDOM-Element is returned containing the representation of a task
    * 
    * @param	info, List of additional information about the task.
    *
    * @return	List[], Array of Lists of Elements.
    */
   public Block genTaskElement(List info);
      
   public List[] genStructureCode(String operation,List eList,int treeLevel);
      
   public void setTreeHeight(int h);
 
   public void preProcessNode(List l);
   
   public String genCaseName(Element e,int breadth, int treeDepth);
   
   public List genCatchList(Element e,int breadth,int treeDepth);
   
   public Block genSwitch(List[] l,List info);
   
   public Block genFlow(List[] l,List info);
   
   public Block genWhile(List[] l,List info);
   
   public Block genSequence(List[] l,List info);
   
   public Block genScope(List [] l, List info);
  
   public Block genHandler(List[] l,List info);
   
   /**
    * 
    * MethodName	: preProcessing
    * Purpose    	: do things BEFORE the generation of Code 
    * Pre		  	:
    * Post		  	: 
    * 
    * @param preList
    */
   public void preProcessing(List preList);
   
   
   /**
    * 
    * MethodName	: postProcessing
    * Purpose    	: do things AFTER the generation of Code  
    * Pre		  	:
    * Post		  	: 
    * 
    * @param code
    */
   public void postProcessing(List [] code);
   /**
    * MethodName : epilogue
    * Purpose    : Generation of the Epilogue
    * Pre		 : TRUE
    * Post		 : String is returned containing the Epilogue of an file.
    * 
    * @return	String, containig the Epilogue of the emitted file
    */
   public String epilogue();
   
   /**
    * MethodName : taskControl
    * Purpose    : Emitting the code for a single taskControl item
    * Pre		 : TODO
    * Post		 : String is returned containing the control definition of a task.
    * 
    * @return      String, control part of a task
    */
   public String taskControl();
   
   /**
    * MethodName	: taskDecomposition
    * Purpose    	: Emitting the code for a single task decompostion
    * Pre		  	: TODO
    * Post		  	: String is returned containing the decompostion of a task
    *  
    * @return		String, decomposition part of a task
    */
   public String taskDecomposition();
   
   
   /**
    * MethodName	: netDecompostion
    * Purpose    	: Emitting the code for a net decomposition 
    * Pre		  	: TODO
    * Post		  	: String is returned containing the decompostion of a task
    * 
    * @param		netName, String name of the net or subnet
    * 
    * @return		String, net decomposition part of a task
    */
   public String  netDecomposition(String netName);
   
      
   }
