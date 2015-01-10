/**
 * BuildSymbolTableVisitor.java
 * Purpose: Construction of the SymbolTables for identifiers, types and functions.
 * 			Implements the Visitor interface.
 * TODO : 	-> cleanUp: this class does not use the Visitor functionality.
 * 		   	-> move to frontEnd package ???
 * @author Stephan Breutel
 * @version 1.0
 * @created 4/03/2005
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

package model.frontEnd;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

import org.jdom.Element;

import controller.Visitor;

import model.languageDefinition.*;

import model.data.XMLTree;


public class BuildSymbolTableVisitor implements Visitor
   {

      /**
       * MethodName : BuildSymbolTableVisitor
       * Purpose	  : Constructor
       * Pre		  : TRUE
       * Post	      : a BuildSymbolTableVisitor object has been created.
       * 
       */
      public BuildSymbolTableVisitor()
         {
            super();
            // TODO Auto-generated constructor stub
         }
      
      /**
       * MethodName		: buildIdTable
       * Purpose    	: Constructs the SymbolTable for Variables (Id,Type)	 
       * Pre		  	: scanTree is the file which contains the information about the variables AND 
       * 				  mapTree is a file specifying how to detect Variables and its Types in the 
       * 				  scanTree		
       * Post		  	: a SymbolTable is constructed. This is a HashMap of (Id,Type) pairs.
       * 
       * @param scanTree	, XMLTree containing the information about variable/type-decl.  
       * @param mapTree		, XMLTree defining how to extract this information ("identificationTags").
       * @return	SymbolTable for the Variables.
       */
      public SymbolTable buildIdTable(XMLTree scanTree,XMLTree mapTree)
         {
         	SymbolTable s= new SymbolTable();
         	String variableTopElement; 	/* the name of the variables element: scope of var-decl. */
         	String varNameAttribute;	/* the attribute name to detect the name of the variable */
         	String varType = "UndefType";
              	         	
         	// TODO 1. getName of VariableElement of the mapTree.
         	// for now "stubbing" we use the BPEL convention.
         	variableTopElement		= "variables";
         	varNameAttribute		= "name";         	
         	// 2.  get all Variable Elements of the Tree.
         	List varList = scanTree.getChildren(variableTopElement);
         	// 3. iterate through the Element List and insert (name,type) into the symbolTable.
         	if (varList==null)
         	   return s;
         	for(int i=0;i<varList.size();i++)
     		  {
     		   Element e = (Element) varList.get(i);  
     		   
     		   for (int j=0;j<BPEL.varTypes.length;j++)
     		      {
     		       if ( e.getAttributeValue(BPEL.varTypes[j])!=null)
     		          {     		          
     		           varType = e.getAttributeValue(BPEL.varTypes[j]);
     		           break;
     		          }
     		      }   
     		   System.out.println("Enter into SymbolTable: (" + e.getAttributeValue(varNameAttribute) +
        			  ","  + varType +  ")");
     		   s.insert(e.getAttributeValue(varNameAttribute),varType);
     		  }
         	return s;
         }
      /**
       * MethodName		: buildTypeTable
       * Purpose    	: Constructs the SymbolTable for Types (TypeName, n-D-List (AttributeNames,TypeNames))	 
       * Pre		  	: scanTree is the tree which contains the information about the Types + TypeDef AND 
       * 				  mapTree is a tree specifying how to detect Types and TypeDefs. in the scanTree        				 
       * Post		  	: a SymbolTable is constructed. This is a HashMap of 
       * 				  (TypeName, n-D-List (AttributeNames,TypeNames))pairs.
       * 
       * @param scanTree	, XMLTree containing the information about type-decl/type-def  
       * @param mapTree		, XMLTree defining how to extract this information ("identificationTags").
       * @return	SymbolTable for the Types.
       */
      public SymbolTable buildTypeTable(XMLTree scanTree,XMLTree mapTree)
         {
            SymbolTable s= new SymbolTable();
	      	String typeTopElement; 	/* tagName, to identify the node for the DataTypeDefinition 	*/
	      	String dataTypeName;	/* attributeTagName, to identify the name of the DataType 		*/ 
	      	String attrName;		/* attributeTagName, to identify the name for the attribute 	*/		
	      	String attrType;		/* attributeTagName, to identify the name for the type			*/
	      	         	
	      	// TODO 1. getName of VariableElement of the mapTree.
	      	// for now "stubbing" we use the BPEL convention.
	      	typeTopElement	= "message";
	      	dataTypeName	= "name";
	      	attrName		= "name";
	      	attrType		= "type";
	      	// 2.  get all Type-Nodes 
	      	List varList = scanTree.getNodes(typeTopElement);
	      	// 3. iterate through the Element List and insert (name,type) into the symbolTable.
	      	if (varList==null)
	      	   return s;
	      	for(int i=0;i<varList.size();i++)
	  		  {
	  		   Element e 		= (Element) varList.get(i);  	  		   
	  		   List typeList 	= e.getChildren();
	  		   List[] infoList 	= new List[2];
	  		   infoList[0] 		= new ArrayList();
	  		   infoList[1]		= new ArrayList();
	  		   for (int j=0;j<typeList.size();j++)
	  		      {
	  		      	Element eChild = (Element) typeList.get(j);
	  		      	infoList[0].add(eChild.getAttributeValue(attrName));
	  		      	//TODO remove Hack
	  		      	if (eChild.getAttributeValue(attrType)!=null)
	  		      	   infoList[1].add(eChild.getAttributeValue(attrType));	  		      	
	  		      	else
	  		      	   infoList[1].add(eChild.getAttributeValue("element"));
	  		      }	  		      	  		      	  		    
	  		   System.out.println("enter into SymbolTable: (" + e.getAttributeValue(dataTypeName) +
	  		         			  ", (AttrNames: " + infoList[0].toString() + ",AttrTypes: " 
	  		         			  + infoList[1].toString() + " ))" );
	  		   s.insert(e.getAttributeValue(dataTypeName),infoList);     		   
	  		  }
	      	return s;
         }
      
      /**
       * MethodName		: buildTaskTable
       * Purpose    	: Constructs the SymbolTable for Tasks (which can be viewe as functions).
       * 				  We use the name task to be "close to" YAWL.  
       * Pre		  	: scanTree is the file which contains the information about the Functions + IO-Parameter AND 
       * 				  mapTree is a file specifying how to detect Functions + IO Parameters.        				 
       * Post		  	: a SymbolTable is constructed. This is a HashMap of 
       * 				  (TaskName,2-D List of formalInput and formalOutputParameter)pairs.
       * 
       * @param scanTree	, XMLTree containing the information about type-decl/type-def  
       * @param mapTree		, XMLTree defining how to extract this information ("identificationTags").
       * @return	SymbolTable for the Tasks.
       */
      public SymbolTable buildTaskTable(XMLTree scanTree,XMLTree mapTree)
         {
         	SymbolTable s= new SymbolTable();
         	String topElement;			      	      
	      	String inputPara;		/* elementTag, to identify the node for the input parameter					*/		
	      	String outputPara;		/* elementTag, to identify the node for the output parameter 				*/	      		      	
	      	String faultName;		/* attrTag, to identify the name of the fault error							*/
	      	String faultData;		/* attrTag, to identify the date of the fault case							*/
	      	
	      
	      	         	
	      	// TODO 1. getName of VariableElement of the mapTree.
	      	// for now "stubbing" we write it directly into a String.
	      	topElement		= "portType";	      	     	
	      	inputPara		= "message";
	      	outputPara		= "message";
	      	faultName		= "name";	      	
	      	faultData		= "message";	      	
	      	// 2.  get all Type-Nodes 
	      	List varList = scanTree.getNodes(topElement);
	      	// 3. iterate through the Element List and insert (name,type) into the symbolTable.
	      	if (varList==null)
	      	   return s;
	      	for(int i=0;i<varList.size();i++)
	  		  {
	  		   Element e 		= (Element) varList.get(i);
	  		   String libName	= e.getAttributeValue("name");
	  		   String taskName	= "";
	  		   List taskList 	= e.getChildren(); // portType/operation
	  		   
	  		   List[] infoList 	= new List[3];
	  		   
	  		   infoList[0] 		= new ArrayList(); // input Parameter
	  		   infoList[1]		= new ArrayList(); // output Parameter	
	  		   infoList[2]		= new ArrayList(); // Exception Handling
	  		   for (int j=0;j<taskList.size();j++)
	  		      {
	  		      	Element eChild 		= (Element) taskList.get(j);
	  		      	taskName 			= eChild.getAttributeValue("name");
	  		      	List taskChildren 	= eChild.getChildren();
	  		      	for (int k=0;k<taskChildren.size();k++)
	  		      	   {
	  		      	    Element e2 = (Element) taskChildren.get(k);
	  		      	    if (e2.getName().compareTo("input")==0)
	  		      	   		infoList[0].add(e2.getAttributeValue(inputPara));
	  		      	    else if (e2.getName().compareTo("output")==0)
	  		      	   		infoList[1].add(e2.getAttributeValue(outputPara));
	  		      	    else if (e2.getName().compareTo("fault")==0)
	  		      	   		infoList[2].add("("+ e2.getAttributeValue(faultName) + "," 
	  		      	      			   + e2.getAttributeValue(faultData) + ")");
	  		      	    else
	  		      	       System.out.println("Error in building SymbolTable.");
	  		      	   }
	  		      }	  		      	  		      	  		    
	  		   System.out.println("enter into SymbolTable: (" + libName + "." + taskName +
	  		         			  ", (Input: " + infoList[0].toString() + ",Output: " 
	  		         			  + infoList[1].toString() + "," 
	  		         			  + infoList[2].toString() + " ))" );
	  		   s.insert(libName+ "." + taskName,infoList);     		   
	  		  }
	      	return s;
         }
      /* (non-Javadoc)
       * @see controller.Visitor#dispatchVisit(java.lang.Object)
       */
      public void dispatchVisit(Object o)
         {
            // TODO Auto-generated method stub

         }

      /* (non-Javadoc)
       * @see controller.Visitor#visit(model.frontEnd.XMLTree)
       */
      public void visit(XMLTree x)
         {
            // TODO Auto-generated method stub
         }

      /* (non-Javadoc)
       * @see controller.Visitor#visit(model.frontEnd.SymbolTable)
       */
      public void visit(SymbolTable s)
         {
            // TODO Auto-generated method stub

         }
      public void visit(Collection c)
         {
         
         }
      
      
      /* (non-Javadoc)
       * @see controller.Visitor#visit(org.jdom.Element, int)
       */
      public void visit(Element e,int breadth, int depth)
         {
         
         }
      
      /* (non-Javadoc)
       * @see controller.Visitor#visit(java.lang.String, java.lang.Object)
       */
      public void visit(String key,Object item)
         {
         
         }
      
      /* (non-Javadoc)
       * @see controller.Visitor#visit(java.lang.Object)
       */
      public void visit(Object o)
         {
         
         }
      

   }
